export class ValidationError extends Error {}

const NAME_PATTERN = /^[\p{L}\p{N} _\-.,!?()]+$/u;   // флаг u — для кириллицы!

export function validateNotebookName(name: string): string | null {
    const t = name.trim();
    if (!t)                    return 'Cant be empty';
    if (t.length > 256)        return 'longer then 256';
    if (!NAME_PATTERN.test(t)) return 'Invalid characters';
    return null;   // null - valid
}