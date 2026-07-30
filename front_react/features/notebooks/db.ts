import * as Crypto from 'expo-crypto';
import { SQLiteDatabase } from 'expo-sqlite';
import { Notebook, Page } from './types';
import { validateNotebookName, ValidationError } from './validation';

export type SyncEntity = 'notebooks' | 'pages';

export async function listNotebooks(db: SQLiteDatabase): Promise<Notebook[]> {
    return db.getAllAsync<Notebook>(
        'SELECT * FROM notebooks WHERE deleted = 0 ORDER BY name'
    );
}

export async function listPages(db: SQLiteDatabase, notebookId: string): Promise<Page[]> {
    return db.getAllAsync<Page>(
        'SELECT * FROM pages WHERE notebook_id = ? AND deleted = 0 ORDER BY updated_at',
        notebookId
    );
}

//create notebook
export async function createNotebook(db:SQLiteDatabase, name: string) {
    const error = validateNotebookName(name);
    if (error) throw new ValidationError(error);
    const id = Crypto.randomUUID();
    await db.runAsync(
        'INSERT INTO notebooks (id, name, version, deleted, dirty) VALUES (?, ?, 1, 0, 1)',
        id, name
    );
    return id;
}

//rename
export async function renameNotebook(db:SQLiteDatabase, id: string, name: string) {
    const error = validateNotebookName(name);
    if (error) throw new ValidationError(error);
    await db.runAsync(
        'UPDATE notebooks SET name = ?, version = version + 1, dirty = 1 WHERE id = ?',
        name, id
    );
}

//delete
export async function deleteNotebook(db:SQLiteDatabase, id: string) {
    await db.withTransactionAsync(async () => {
        await db.runAsync(
            'UPDATE notebooks SET deleted = 1, version = version + 1, dirty = 1 WHERE id = ?',
            id
        );

        await db.runAsync(
            'UPDATE pages SET deleted = 1, version = version + 1, dirty = 1 WHERE notebook_id = ?'
        );
    });
}

//create page
export async function createPage(db: SQLiteDatabase, notebookId: string) {
    const id = Crypto.randomUUID();
    await db.runAsync(
        'INSERT INTO pages (id, notebook_id, version, deleted, dirty) VALUES (?, ?, 1, 0, 1)',
        id, notebookId
    );
    return id;
}

//content page update
export async function updatePageContent(db: SQLiteDatabase, pageId: string, content: string) {
    await db.runAsync(
        'UPDATE pages SET content = ?, version = version + 1, dirty = 1 WHERE id = ?',
        content, pageId
    );
}

//delete page
export async function deletePage(db: SQLiteDatabase, pageId: string) {
    await db.runAsync(
        'UPDATE pages SET deleted = 1, version = version + 1, dirty = 1 WHERE id = ?',
        pageId
    );
}


export async function getDirtyNotebooks(db: SQLiteDatabase) {
    return await db.getAllAsync<Notebook>(
        'SELECT * FROM notebooks WHERE dirty = 1',
    );
}

export async function getDirtyPages(db: SQLiteDatabase) {
    return await db.getAllAsync<Page>(
        'SELECT * FROM pages WHERE dirty = 1',
    );
}

export async function getSince(db: SQLiteDatabase, entity: SyncEntity): Promise<string | null> {
    const row = await db.getFirstAsync<{ since: string }>(
        'SELECT since FROM sync_state WHERE entity = ?', entity
    );
    return row?.since ?? null;
}

export async function setSince(db: SQLiteDatabase, entity: SyncEntity, iso: string) {
    await db.runAsync(
        'INSERT OR REPLACE INTO sync_state (entity, since) VALUES (?, ?)', entity, iso
    );
}   

export async function NotebookPayload(row: Notebook) {
    return {
        id: row.id,
        name: row.name,
        version: row.version,
        deleted: row.deleted === 1,
    };
}

export async function PagePayload(row: Page) {
    return {
        id: row.id,
        notebookId: row.notebook_id,
        content: row.content,
        version: row.version,
        deleted: row.deleted === 1,
    };
}

async function upsertNotebookFromServer(db: SQLiteDatabase, n: Notebook) {
    await db.runAsync(
        `INSERT INTO notebooks (id, name, version, deleted, updated_at, dirty)
         VALUES (?, ?, ?, ?, ?, 0)
         ON CONFLICT(id) DO UPDATE SET
           name = excluded.name,
           version = excluded.version,
           deleted = excluded.deleted,
           updated_at = excluded.updated_at,
           dirty = 0`,
        n.id, n.name, n.version, n.deleted ? 1 : 0, n.updated_at
    );
}


async function upsertPageFromServer(db: SQLiteDatabase, p: Page) {
    await db.runAsync(
        `INSERT INTO pages (id, notebook_id, content, version, deleted, updated_at, dirty)
         VALUES (?, ?, ?, ?, ?, ?, 0)
         ON CONFLICT(id) DO UPDATE SET
           notebook_id = excluded.notebook_id,
           content = excluded.content,
           version = excluded.version,
           deleted = excluded.deleted,
           updated_at = excluded.updated_at,
           dirty = 0`,
        p.id, p.notebook_id, p.content, p.version, p.deleted ? 1 : 0, p.updated_at
    );
}

async function clearDirty(db: SQLiteDatabase, table: 'notebooks'|'pages', ids: string[]) {
    if (ids.length === 0) return;
    const placeholders = ids.map(() => '?').join(',');
    await db.runAsync(`UPDATE ${table} SET dirty = 0 WHERE id IN (${placeholders})`, ids);
}