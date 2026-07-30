import { useState } from 'react';
import {
    ActivityIndicator, Button, FlatList, Pressable,
    StyleSheet, Text, TextInput, View,
} from 'react-native';
import { useNotebooks, usePages, useSync } from '../hooks';
import { runSync } from '../sync';

export function NotebooksTestScreen({ onBack }: { onBack: () => void }) {
    const [openId, setOpenId] = useState<string | null>(null);

    if (openId) {
        return <PagesView notebookId={openId} onBack={() => setOpenId(null)} />;
    }
    return <NotebookListView onOpen={setOpenId} onBack={onBack} />;
}

function NotebookListView({ onOpen, onBack }: { onOpen: (id: string) => void; onBack: () => void }) {
    const { notebooks, loading, create, remove } = useNotebooks();
    const [name, setName] = useState('');
    const [createError, setCreateError] = useState<string | null>(null);
    const { syncing, error: syncError, sync } = useSync();

    const add = async () => {
        try {
            await create(name);
            setName('');
            setCreateError(null);
        } catch (e) {
            setCreateError(e instanceof Error ? e.message : 'Ошибка');
        }
    };

    if (loading) return <ActivityIndicator style={styles.center} />;

    return (
        <View style={styles.screen}>
            <View style={styles.header}>
                <Button title="← Назад" onPress={onBack} />
                <Text style={styles.title}>Тетради ({notebooks.length})</Text>
            </View>

            <View style={styles.row}>
                <TextInput
                    style={styles.input}
                    placeholder="Название тетради"
                    value={name}
                    onChangeText={setName}
                />
                <Button title="Создать" onPress={add} disabled={!name.trim()} />
                <Button title="Sync" onPress={sync} disabled={!name.trim()} />
            </View>
            {createError && <Text style={styles.error}>{createError}</Text>}

            <FlatList
                style={styles.list}
                data={notebooks}
                keyExtractor={(n) => n.id}
                ListEmptyComponent={<Text style={styles.muted}>Пусто — создайте тетрадь.</Text>}
                renderItem={({ item }) => (
                    <View style={styles.item}>
                        <Pressable style={styles.grow} onPress={() => onOpen(item.id)}>
                            <Text style={styles.itemText}>{item.name}</Text>
                            <Text style={styles.meta}>v{item.version}{item.dirty ? ' • не синхр.' : ''}</Text>
                        </Pressable>
                        <Button title="Удалить" color="#c0392b" onPress={() => remove(item.id)} />
                    </View>
                )}
            />
        </View>
    );
}

function PagesView({ notebookId, onBack }: { notebookId: string; onBack: () => void }) {
    const { pages, loading, create, updateContent, remove } = usePages(notebookId);
    const [editingId, setEditingId] = useState<string | null>(null);
    const [draft, setDraft] = useState('');

    const openEditor = (id: string, content: string | null) => {
        setEditingId(id);
        setDraft(content ?? '');
    };

    const save = async () => {
        if (editingId) await updateContent(editingId, draft);
        setEditingId(null);
    };

    if (loading) return <ActivityIndicator style={styles.center} />;

    if (editingId) {
        return (
            <View style={styles.screen}>
                <Text style={styles.title}>Редактор страницы</Text>
                <TextInput
                    style={styles.editor}
                    multiline
                    value={draft}
                    onChangeText={setDraft}
                    placeholder="Текст страницы..."
                />
                <View style={styles.row}>
                    <Button title="Сохранить" onPress={save} />
                    <Button title="Отмена" color="#888" onPress={() => setEditingId(null)} />
                </View>
            </View>
        );
    }

    return (
        <View style={styles.screen}>
            <View style={styles.header}>
                <Button title="← К тетрадям" onPress={onBack} />
                <Text style={styles.title}>Страницы ({pages.length})</Text>
            </View>

            <Button
                title="+ Новая страница"
                onPress={async () => { const id = await create(); openEditor(id, ''); }}
            />

            <FlatList
                style={styles.list}
                data={pages}
                keyExtractor={(p) => p.id}
                ListEmptyComponent={<Text style={styles.muted}>Нет страниц.</Text>}
                renderItem={({ item }) => (
                    <View style={styles.item}>
                        <Pressable style={styles.grow} onPress={() => openEditor(item.id, item.content)}>
                            <Text style={styles.itemText} numberOfLines={1}>
                                {item.content || '(пусто)'}
                            </Text>
                            <Text style={styles.meta}>v{item.version}{item.dirty ? ' • не синхр.' : ''}</Text>
                        </Pressable>
                        <Button title="Удалить" color="#c0392b" onPress={() => remove(item.id)} />
                    </View>
                )}
            />
        </View>
    );
}

const styles = StyleSheet.create({
    screen: { flex: 1, padding: 16, gap: 12, width: '100%' },
    center: { marginTop: 40 },
    header: { flexDirection: 'row', alignItems: 'center', gap: 12 },
    list: { flex: 1 },
    title: { fontSize: 20, fontWeight: '600' },
    row: { flexDirection: 'row', alignItems: 'center', gap: 8 },
    grow: { flex: 1 },
    input: {
        flex: 1, borderWidth: 1, borderColor: '#ccc', borderRadius: 8, padding: 10,
    },
    editor: {
        borderWidth: 1, borderColor: '#ccc', borderRadius: 8, padding: 10,
        minHeight: 160, textAlignVertical: 'top',
    },
    item: {
        flexDirection: 'row', alignItems: 'center', gap: 8,
        paddingVertical: 10, borderBottomWidth: 1, borderBottomColor: '#eee',
    },
    itemText: { fontSize: 16 },
    meta: { fontSize: 12, color: '#888' },
    muted: { color: '#888', textAlign: 'center', marginTop: 20 },
    error: { color: 'red' },
});
