import { useState } from 'react';
import { ActivityIndicator, Button, StyleSheet, Text, TextInput, View } from 'react-native';

type Props = {
  onSuccess: (accessToken: string, refreshToken: string) => void;
  onSwitchToLogin: () => void;
  onBack: () => void;
};

export function RegisterForm({ onSuccess, onSwitchToLogin, onBack }: Props) {
  const [email, setEmail] = useState('');
  const [name, setName] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const submit = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await fetch(`${process.env.EXPO_PUBLIC_API_URL}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, name, password }),
      });
      if (res.status === 409) throw new Error('Такой email уже зарегистрирован');
      if (!res.ok) throw new Error(`Ошибка регистрации: ${res.status}`);
      const { accessToken, refreshToken } = await res.json();
      onSuccess(accessToken, refreshToken);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Не удалось зарегистрироваться');
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.form}>
      <Text style={styles.title}>Создать аккаунт</Text>
      <TextInput
        style={styles.input}
        placeholder="Имя"
        value={name}
        onChangeText={setName}
        autoCapitalize="words"
      />
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
        <Button
          title="Зарегистрироваться"
          onPress={submit}
          disabled={!email || !password}
        />
      )}
      {error && <Text style={styles.error}>{error}</Text>}
      <Text style={styles.link} onPress={onSwitchToLogin}>
        Уже есть аккаунт? Войти
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
