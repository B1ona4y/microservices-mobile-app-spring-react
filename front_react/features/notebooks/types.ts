export type Notebook = {
    id: string;
    name: string;
    version: number;
    deleted: number;      // SQLite: 0/1, не boolean
    updated_at: string | null;
    dirty: number;
};

export type Page = {
    id: string;
    notebook_id: string;
    content: string | null;
    version: number;
    deleted: number;
    updated_at: string | null;
    dirty: number;
};