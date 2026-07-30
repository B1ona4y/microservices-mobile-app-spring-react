import { useCallback, useEffect, useState } from 'react';
import { useSQLiteContext } from 'expo-sqlite';
import * as db from './db';
import { Notebook, Page } from './types';
import { runSync } from './sync';

export function useNotebooks() {
    const database = useSQLiteContext();
    const [notebooks, setNotebooks] = useState<Notebook[]>([]);
    const [loading, setLoading] = useState(true);

    const refresh = useCallback(async () => {
        const rows = await db.listNotebooks(database);
        setNotebooks(rows);
    }, [database]);

    useEffect(() => {
        refresh().finally(() => setLoading(false));
    }, [refresh]);

    const create = useCallback(async (name: string) => {
        await db.createNotebook(database, name);
        await refresh();
    }, [refresh, database])

    const rename = useCallback(async (id: string, name: string) => {
        await db.renameNotebook(database, id, name);
        await refresh();
    }, [refresh, database])

    const remove = useCallback(async (id: string) => {
        await db.deleteNotebook(database, id);
        await refresh();
    }, [refresh, database])

    return { notebooks, loading, create, rename, remove };
}

export function usePages(notebookId: string) {
    const database = useSQLiteContext();
    const [pages, setPages] = useState<Page[]>([]);
    const [loading, setLoading] = useState(true);

    const refresh = useCallback(async () => {
        const rows = await db.listPages(database, notebookId);
        setPages(rows);
    }, [database, notebookId]);

    useEffect(() => {
        refresh().finally(() => setLoading(false));
    }, [refresh]);

    const create = useCallback(async () => {
        const id = await db.createPage(database, notebookId);
        await refresh();
        return id;
    }, [database, notebookId, refresh]);

    const updateContent = useCallback(async (pageId: string, content: string) => {
        await db.updatePageContent(database, pageId, content);
        await refresh();
    }, [database, refresh]);

    const remove = useCallback(async (pageId: string) => {
        await db.deletePage(database, pageId);
        await refresh();
    }, [database, refresh]);

    return { pages, loading, create, updateContent, remove };
}

export function useSync(){
    const database = useSQLiteContext();
    const [syncing, setSyncing] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const sync = useCallback(async () => {
        setSyncing(true);
        setError(null);
        try {
            await runSync(database);
        } catch (e) {
            setError(e instanceof Error ? e.message : 'sync faild');
        } finally {
            setSyncing(false);
        }
    }, [database]);

    return {syncing, error, sync};
}