package xyz.refinedev.phoenix.utils.mysql;

public class StringColumn extends AbstractColumn<String> {

    public static StringColumn of(String text, int value) {
        return new StringColumn(text, value);
    }

    public static StringColumn ofNullable(String text, int value) {
        return new StringColumn(text, value, true, null);
    }

    public static StringColumn ofDefault(String text, int value, String text2) {
        return new StringColumn(text, value, false, text2);
    }

    public StringColumn(String text, int value) {
        super(text, "VARCHAR(" + value + ")");
    }

    public StringColumn(String text, int value, boolean matches, String text2) {
        super(text, "VARCHAR(" + value + ")", matches, text2, null);
    }

    public String get(Object result) {
        return (String) result;
    }
}
