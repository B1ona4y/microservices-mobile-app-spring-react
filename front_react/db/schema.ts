import { SQLiteDatabase } from "expo-sqlite";

const DATABASE_VERSION = 1;

export async function migration(db: SQLiteDatabase) {
    const row = await db.getFirstAsync<{ user_version: number }>('PRAGMA user_version');
    let current = row?.user_version ?? 0;

    if (current >= DATABASE_VERSION) return;

    if (current === 0) {
        await db.execAsync(`
            PRAGMA journal_mode = WAL;

            CREATE TABLE notebooks (
                id         TEXT    PRIMARY KEY NOT NULL,
                name       TEXT    NOT NULL,
                version    INTEGER NOT NULL DEFAULT 0,
                deleted    INTEGER NOT NULL DEFAULT 0,
                updated_at TEXT,
                dirty      INTEGER NOT NULL DEFAULT 0
            );

            CREATE TABLE pages (
                id          TEXT    PRIMARY KEY NOT NULL,
                notebook_id TEXT    NOT NULL,
                content     TEXT,
                version     INTEGER NOT NULL DEFAULT 0,
                deleted     INTEGER NOT NULL DEFAULT 0,
                updated_at  TEXT,
                dirty       INTEGER NOT NULL DEFAULT 0
            );

            CREATE INDEX idx_pages_notebook ON pages(notebook_id);

            CREATE TABLE sync_state (
                entity TEXT PRIMARY KEY NOT NULL,
                since  TEXT NOT NULL
            );
        `);
        current = 1;
    }

    // будущие миграции: if (current === 1) { ...; current = 2; }

    await db.execAsync(`PRAGMA user_version = ${DATABASE_VERSION}`);
}
