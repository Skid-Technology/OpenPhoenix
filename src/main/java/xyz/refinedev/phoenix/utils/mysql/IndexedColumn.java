package xyz.refinedev.phoenix.utils.mysql;

public final class IndexedColumn {

    public static final int LENGTH = 255;

    private final String field;

    private final String column;

    private final boolean generated;

    private IndexedColumn(String field, String column, boolean generated) {
        this.field = field;
        this.column = column;
        this.generated = generated;
    }

    public static IndexedColumn generated(String text, String text2) {
        return new IndexedColumn(text, text2, true);
    }

    public static IndexedColumn existing(String text, String text2) {
        return new IndexedColumn(text, text2, false);
    }

    public String getIndexName(String text) {
        return "idx_" + text + "_" + this.column;
    }

    public String generatedFrom() {
        return jsonExtract(this.field);
    }

    public static String jsonExtract(String text) {
        return "JSON_UNQUOTE(JSON_EXTRACT(data, '$." + text + "'))";
    }

    public String getField() {
        return this.field;
    }

    public String getColumn() {
        return this.column;
    }

    public boolean isGenerated() {
        return this.generated;
    }
}
