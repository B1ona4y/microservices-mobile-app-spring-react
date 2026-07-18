import * as SecureStore from 'expo-secure-store';

const ACCESS = 'auth_token';
const REFRESH = 'refresh_token';

export async function saveTokens(accessToken: string, refreshToken: string) {
    await SecureStore.setItemAsync(ACCESS, accessToken);
    await SecureStore.setItemAsync(REFRESH, refreshToken);
}

export async function getAccess() {
    return SecureStore.getItemAsync(ACCESS);
}

export async function getRefresh() {
    return SecureStore.getItemAsync(REFRESH);
}

export async function clearTokens() {
    await SecureStore.deleteItemAsync(ACCESS);
    await SecureStore.deleteItemAsync(REFRESH);
}