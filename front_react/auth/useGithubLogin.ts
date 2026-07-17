import { useEffect, useMemo, useRef } from 'react';
import * as AuthSession from 'expo-auth-session';
import * as WebBrowser from 'expo-web-browser';

WebBrowser.maybeCompleteAuthSession();

const discovery = { authorizationEndpoint: 'https://github.com/login/oauth/authorize' }

export function useGithubLogin(onCode: (code: string) => void) {
    const config = useMemo(() => ({
        clientId: process.env.EXPO_PUBLIC_GITHUB_CLIENT_ID!,
        scopes: ['read:user', 'user:email'],
        usePKCE: false,
        redirectUri: AuthSession.makeRedirectUri({ scheme: 'frontreact' }),
    }), []);

    const [request, response, promptAsync] = AuthSession.useAuthRequest(config, discovery);

    const handledCode = useRef<string | null>(null);

    useEffect(() => {
        if (response?.type === 'success' && response.params.code !== handledCode.current) {
            handledCode.current = response.params.code;
            onCode(response.params.code);
        }
    }, [response, onCode])

    return {promptAsync, ready: !!request }
}
