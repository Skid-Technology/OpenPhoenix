package xyz.refinedev.phoenix.editor;

import xyz.refinedev.phoenix.utils.repository.IListRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;

public final class EditorRequestContext {

    private EditorRequestContext() {}

    public static <K, V> void applyListRepository(
            IListRepository<K, V> listRepository,
            Collection<V> values,
            Collection<V> values2,
            BiConsumer<V, Boolean> biConsumer) {
        ArrayList<V> values3 = new ArrayList();
        for (V result : values) {
            if (!values2.contains(result)) {
                values3.add(result);
            }
        }
        for (V result2 : values2) {
            listRepository.saveSync(result2);
            listRepository.addToCache(result2);
            biConsumer.accept(result2, false);
        }
        for (V result3 : values3) {
            listRepository.deleteSync(result3);
            listRepository.removeFromCache(result3);
            biConsumer.accept(result3, true);
        }
    }
}
