import { getAccess, getRefresh, saveTokens, clearTokens } from './tokens';

const API_URL = process.env.EXPO_PUBLIC_API_URL;

//single-flight
let refreshPromise: Promise<string> | null = null;

async function doRefresh(): Promise<string> {
    const refresh = await getRefresh();
    if(!refresh) throw new Error('no refresh token');

    const res = await fetch(`${API_URL}/auth/refresh`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken: refresh }),
    });
    if (!res.ok) {
        await clearTokens();
        throw new Error('session expired');
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
    try {
        await refreshAccess();
        return true;
    } catch {
        return false;
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