import { useEffect } from 'react';
import * as AuthSession from 'expo-auth-session';
import * as WebBrowser from 'expo-web-browser';

WebBrowser.maybeCompleteAuthSession();

const discovery = { authorizationEndpoint: 'https://github.com/login/oauth/authorize' }

export function useGithubLogin(onCode: (code: string) => void) {
    const [request, response, promtAsync] = AuthSession.useAuthRequest(
        {
            clientId: process.env.EXPO_PUBLIC_GITHUB_CLIENT_ID!,
            scopes: ['read:user', 'user:email'],
            usePKCE: false,
            redirectUri: AuthSession.makeRedirectUri({ scheme: 'frontreact'})
        },
        discovery
    );

    useEffect(() => {
        if(response?.type === 'success'){
            onCode(response?.params.code);
        }
    }, [response])

    return {promtAsync, ready: !!request }
}