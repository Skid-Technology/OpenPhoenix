package xyz.refinedev.phoenix.utils.mysql;

import java.util.UUID;

public class UUIDColumn extends AbstractColumn<UUID> {

    public static UUIDColumn of(String text) {
        return new UUIDColumn(text);
    }

    public static UUIDColumn ofNullable(String text) {
        return new UUIDColumn(text, true, null);
    }

    public static UUIDColumn ofDefault(String text, String text2) {
        return new UUIDColumn(text, false, text2);
    }

    public UUIDColumn(String text) {
        super(text, "VARCHAR(36)");
    }

    public UUIDColumn(String text, boolean matches, String text2) {
        super(text, "VARCHAR(36)", matches, text2, null);
    }

    public UUID get(Object result) {
        return result instanceof String ? UUID.fromString((String) result) : null;
    }
}
