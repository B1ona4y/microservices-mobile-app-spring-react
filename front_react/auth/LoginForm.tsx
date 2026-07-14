import { useState } from 'react';
import { ActivityIndicator, Button, StyleSheet, Text, TextInput, View } from 'react-native';

type Props = {
  onSuccess: (token: string) => void;
  onSwitchToRegister: () => void;
  onBack: () => void;
};

export function LoginForm({ onSuccess, onSwitchToRegister, onBack }: Props) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const submit = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await fetch(`${process.env.EXPO_PUBLIC_API_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });
      if (res.status === 401) throw new Error('Неверный email или пароль');
      if (!res.ok) throw new Error(`Ошибка входа: ${res.status}`);
      const { token } = await res.json();
      onSuccess(token);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Не удалось войти');
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.form}>
      <Text style={styles.title}>Вход</Text>
      <TextInput
        style={styles.input}
        placeholder="Email"
        value={email}
        onChangeText={setEmail}
        autoCapitalize="none"
        keyboardType="email-address"
      />
      <TextInput
        style={styles.input}
        placeholder="Пароль"
        value={password}
        onChangeText={setPassword}
        secureTextEntry
      />
      {loading ? (
        <ActivityIndicator />
      ) : (
        <Button title="Войти" onPress={submit} disabled={!email || !password} />
      )}
      {error && <Text style={styles.error}>{error}</Text>}
      <Text style={styles.link} onPress={onSwitchToRegister}>
        Нет аккаунта? Создать
      </Text>
      <Text style={styles.link} onPress={onBack}>
        ← Назад
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  form: {
    width: '100%',
    maxWidth: 320,
    gap: 10,
  },
  title: {
    fontSize: 18,
    fontWeight: '600',
    textAlign: 'center',
    marginBottom: 8,
  },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 8,
    padding: 10,
  },
  error: {
    color: 'red',
    textAlign: 'center',
  },
  link: {
    color: '#007aff',
    textAlign: 'center',
    marginTop: 4,
  },
});
