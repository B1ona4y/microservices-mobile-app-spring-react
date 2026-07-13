import { StatusBar } from 'expo-status-bar';
import * as SecureStore from 'expo-secure-store';
import { useState } from 'react';
import { Button, StyleSheet, Text, View } from 'react-native';
import { useGoogleLogin } from './auth/useGoogleLogin';

export default function App() {
  const [status, setStatus] = useState<'idle' | 'loading' | 'loggedIn' | 'error'>('idle');

  const { promptAsync, ready } = useGoogleLogin(async (idToken) => {
    setStatus('loading');
    try {
      const res = await fetch(`${process.env.EXPO_PUBLIC_API_URL}/auth/google`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ token: idToken }),
      });
      if (!res.ok) throw new Error(`Backend rejected token: ${res.status}`);
      const { token } = await res.json();
      await SecureStore.setItemAsync('auth_token', token);
      setStatus('loggedIn');
    } catch (e) {
      console.error(e);
      setStatus('error');
    }
  });

  return (
    <View style={styles.container}>
      {status === 'loggedIn' ? (
        <Text>Вы вошли через Google</Text>
      ) : (
        <Button
          title={status === 'loading' ? 'Вход...' : 'Войти через Google'}
          disabled={!ready || status === 'loading'}
          onPress={() => promptAsync().catch(() => setStatus('error'))}
        />
      )}
      {status === 'error' && <Text style={styles.error}>Не удалось войти, попробуйте снова</Text>}
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
