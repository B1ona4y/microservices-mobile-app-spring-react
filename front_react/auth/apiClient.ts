import { getAccess, getRefresh, saveTokens, clearTokens } from './tokens';

export class SessionExpiredError extends Error {}
export class NetworkError extends Error {}

const API_URL = process.env.EXPO_PUBLIC_API_URL;

//single-flight
let refreshPromise: Promise<string> | null = null;

async function doRefresh(): Promise<string> {
    const refresh = await getRefresh();
    if(!refresh) throw new SessionExpiredError('no refresh token');

    let res: Response;
    try {
        res = await fetch(`${API_URL}/auth/refresh`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken: refresh }),
    });
    } catch {
        throw new NetworkError('offline');
    }
    if (!res.ok) {
        await clearTokens();
        if(!refresh) throw new SessionExpiredError('refresh rejected');
    }
    const { accessToken, refreshToken } = await res.json();
    await saveTokens(accessToken, refreshToken);
    return accessToken;
}

function refreshAccess(): Promise<string> {
    if (!refreshPromise) {
        refreshPromise = doRefresh().finally(() => { refreshPromise = null; });
    }
    return refreshPromise;
}

export async function restoreSession(): Promise<boolean> {
    const refresh = await getRefresh();
    if (!refresh) return false;

    try {
        await refreshAccess();
        return true;
    } catch (e) {
        return e instanceof NetworkError;
    }
}

export async function apiFetch(path: string, init: RequestInit = {}): Promise<Response> {
    const withAuth = (token: string | null): RequestInit => ({
    ...init,
    headers: {
        ...(init.headers ?? {}),
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    });

    let res = await fetch(`${API_URL}${path}`, withAuth(await getAccess()));
    if (res.status === 401) {
        const fresh = await refreshAccess();
        res = await fetch(`${API_URL}${path}`, withAuth(fresh));
    }
    return res;
}