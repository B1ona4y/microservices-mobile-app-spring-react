import { StatusBar } from 'expo-status-bar';
import * as SecureStore from 'expo-secure-store';
import { useState, useEffect } from 'react';
import { Button, StyleSheet, Text, View } from 'react-native';
import { useGoogleLogin } from './auth/useGoogleLogin';
import { RegisterForm } from './auth/RegisterForm';
import { LoginForm } from './auth/LoginForm';
import { useGithubLogin } from './auth/useGithubLogin';
import { saveTokens, clearTokens, getRefresh } from './auth/tokens';
import { restoreSession } from './auth/apiClient';

type Screen = 'loading' | 'choice' | 'register' | 'login' | 'loggedIn';

export default function App() {
  const [screen, setScreen] = useState<Screen>('loading');
  const [googleStatus, setGoogleStatus] = useState<'idle' | 'loading' | 'error'>('idle');
  const [githubStatus, setGithubStatus] = useState<'idle' | 'loading' | 'error'>('idle');

  useEffect(() => {
    restoreSession().then((ok) => setScreen(ok ? 'loggedIn' : 'choice'));
  }, []);

  const handleAuthSuccess = async (accessToken: string, refreshToken: string) => {
    await saveTokens(accessToken, refreshToken);
    setScreen('loggedIn');
  };

  const { promptAsync, ready } = useGoogleLogin(async (idToken) => {
    setGoogleStatus('loading');
    try {
      const res = await fetch(`${process.env.EXPO_PUBLIC_API_URL}/auth/google`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ token: idToken }),
      });
      if (!res.ok) throw new Error(`Backend rejected token: ${res.status}`);
      const { accessToken, refreshToken } = await res.json();
      await handleAuthSuccess(accessToken, refreshToken);
      setGoogleStatus('idle');
    } catch (e) {
      console.error(e);
      setGoogleStatus('error');
    }
  });

  const { promptAsync: githubPrompt, ready: githubReady } = useGithubLogin(async (code) => {
    setGithubStatus('loading');
    try{
      const res = await fetch(`${process.env.EXPO_PUBLIC_API_URL}/auth/github`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ code }),
      });
      if (!res.ok) throw new Error(`Backend rejected token: ${res.status}`);
      const { accessToken, refreshToken } = await res.json();
      await handleAuthSuccess(accessToken, refreshToken);
      setGithubStatus('idle');
    } catch (e) {
      console.error(e);
      setGithubStatus('error');
    }
  });

  const handleLogout = async () => {
    const refresh = await getRefresh();
    if (refresh) {
      await fetch(`${process.env.EXPO_PUBLIC_API_URL}/auth/logout`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken: refresh }),
      }).catch(() => {});
    }
    await clearTokens();
    setScreen('choice');
  };

  return (
    <View style={styles.container}>
      {screen === 'loading' && <Text>Loading...</Text>}
      {screen === 'loggedIn' && (
        <>
        <Text>You loggedIn</Text>
        <Button title='Logout' onPress={handleLogout}/>
        </>
      )}

      {screen === 'choice' && (
        <>
          <Button
            title={googleStatus === 'loading' ? 'Login in...' : 'Login using Google'}
            disabled={!ready || googleStatus === 'loading'}
            onPress={() => promptAsync().catch(() => setGoogleStatus('error'))}
          />
          {googleStatus === 'error' && (
            <Text style={styles.error}>Can&apos;t login using google, try again</Text>
          )}
          <Button
            title={googleStatus === 'loading' ? 'Login in...' : 'Login using GitHub'}
            disabled={!githubReady}
            onPress={() => githubPrompt()}
          />
          {githubStatus === 'error' && (
            <Text style={styles.error}>Can&apos;t login using github, try again</Text>
          )}
          <Button title="Create account" onPress={() => setScreen('register')} />
        </>
      )}

      {screen === 'register' && (
        <RegisterForm
          onSuccess={handleAuthSuccess}
          onSwitchToLogin={() => setScreen('login')}
          onBack={() => setScreen('choice')}
        />
      )}

      {screen === 'login' && (
        <LoginForm
          onSuccess={handleAuthSuccess}
          onSwitchToRegister={() => setScreen('register')}
          onBack={() => setScreen('choice')}
        />
      )}

      <StatusBar style="auto" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 12,
  },
  error: {
    color: 'red',
  },
});
