package xyz.refinedev.phoenix.utils.mysql;

public class JSONColumn extends AbstractColumn<String> {

    public static JSONColumn of(String text) {
        return new JSONColumn(text);
    }

    public static JSONColumn ofNullable(String text) {
        return new JSONColumn(text, true, null);
    }

    public static JSONColumn ofDefault(String text, String text2) {
        return new JSONColumn(text, false, text2);
    }

    public JSONColumn(String text) {
        super(text, "JSON");
    }

    public JSONColumn(String text, boolean matches, String text2) {
        super(text, "JSON", matches, text2, null);
    }

    public String get(Object result) {
        return (String) result;
    }
}
