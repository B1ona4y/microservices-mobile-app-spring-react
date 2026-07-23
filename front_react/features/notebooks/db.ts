import * as Crypto from 'expo-crypto';
import { SQLiteDatabase } from 'expo-sqlite';
import { Notebook, Page } from './types';
import { validateNotebookName, ValidationError } from './validation';

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
