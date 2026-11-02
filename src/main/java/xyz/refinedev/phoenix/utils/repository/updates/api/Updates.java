package xyz.refinedev.phoenix.utils.repository.updates.api;

import xyz.refinedev.phoenix.utils.repository.updates.impl.SetFieldsUpdateOption;
import xyz.refinedev.phoenix.utils.repository.updates.impl.SetUpdateOption;

import java.util.Map;

public final class Updates {

    private static final String text = "This is a utility class and cannot be instantiated";

    public static <T> SetUpdateOption<T> set(String text, T t) {
        return new SetUpdateOption<>(text, (T) t);
    }

    public static SetFieldsUpdateOption setFields(Map<String, String> entries) {
        return new SetFieldsUpdateOption(entries);
    }

    private Updates() {
        throw new UnsupportedOperationException(text);
    }
}
