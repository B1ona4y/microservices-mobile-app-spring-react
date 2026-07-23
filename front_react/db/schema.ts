import { SQLiteDatabase } from "expo-sqlite";

const DATABASE_VERSION = 1;

export async function migration(db:SQLiteDatabase) {
    const row = await db.getFirstAsync<{ user_version: number }>( 'PRAGMA user_version' )
    let current = row?.user_version ?? 0;

    if (current >= DATABASE_VERSION) return;

    if (current == 0) {
        await db.execAsync(`
        PRAGMA journal_mode = WAL;
        CREATE TABLE notebooks (...);
        CREATE TABLE pages (...);
        CREATE TABLE sync_state (...);
        `);
        current = 1;
    }
    
    await db.execAsync(`PRAGMA user_version = ${DATABASE_VERSION}`);
}