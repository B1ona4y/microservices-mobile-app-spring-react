import { SQLiteDatabase } from "expo-sqlite";
import { getDirtyNotebooks, getDirtyPages, getSince, setSince, NotebookPayload, PagePayload, upsertNotebookFromServer, upsertPageFromServer, clearDirty} from "./db";
import { apiFetch } from "../../auth/apiClient";

export async function runSync(db: SQLiteDatabase) {
    const dirtyNotebooks = await getDirtyNotebooks(db);
    const sinceNotebooks = await getSince(db, 'notebooks');

    const dirtyPages = await getDirtyPages(db);
    const sincePages = await getSince(db, 'pages');

    const changesNotebooks = dirtyNotebooks.map(NotebookPayload);
    const changesPages = dirtyPages.map(PagePayload);

    const body = JSON.stringify({
        notebooks: { changes: changesNotebooks, since: sinceNotebooks },
        pages: { changes: changesPages, since: sincePages },
    });

    const res = await apiFetch('/sync', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body
    })

    if (!res.ok){
        throw new Error(`sync failed: ${res.status}`);
    }

    const result = await res.json();

    for (const serverNotebook of result.notebooks.changes){
        await upsertNotebookFromServer(db, serverNotebook)
    }

    for (const serverPage of result.pages.changes){
        await upsertPageFromServer(db, serverPage)
    }

    const rejectedIdsNotebooks = new Set(result.notebooks.rejected.map((r: { id: string }) => r.id));
    const acceptedNotebookIds = dirtyNotebooks
        .filter((n) => !rejectedIdsNotebooks.has(n.id))
        .map((n) => n.id);

    const rejectedIdsPages = new Set(result.pages.rejected.map((r: { id: string }) => r.id));
    const acceptedPagesIds = dirtyPages
        .filter((p) => !rejectedIdsPages.has(p.id))
        .map((p) => p.id);

    await clearDirty(db, 'notebooks', acceptedNotebookIds);
    await clearDirty(db, 'pages', acceptedPagesIds);

    await setSince(db, 'notebooks', result.notebooks.newSince);
    await setSince(db, 'pages', result.pages.newSince);

    return result;
}