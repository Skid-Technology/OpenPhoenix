package xyz.refinedev.phoenix.utils.repository.translate;

import xyz.refinedev.phoenix.utils.mysql.IndexedColumn;
import xyz.refinedev.phoenix.utils.mysql.IndexedColumns;
import xyz.refinedev.phoenix.utils.repository.filters.api.FilterOptions;
import xyz.refinedev.phoenix.utils.repository.filters.api.FilterVisitor;
import xyz.refinedev.phoenix.utils.repository.sorting.api.SortVisitor;
import xyz.refinedev.phoenix.utils.repository.sorting.api.SortingOptions;
import xyz.refinedev.phoenix.utils.repository.updates.api.UpdateOptions;
import xyz.refinedev.phoenix.utils.repository.updates.api.UpdateVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class MySQLQueryTranslator
        implements FilterVisitor<ParameterizedFragment>,
                SortVisitor<String>,
                UpdateVisitor<ParameterizedFragment> {

    private static final MySQLQueryTranslator UNROUTED =
            new MySQLQueryTranslator(IndexedColumns.none());

    private static final String EXTRACT = "JSON_UNQUOTE(JSON_EXTRACT(data, ?))";

    private final IndexedColumns columns;

    private MySQLQueryTranslator(IndexedColumns indexedColumns) {
        this.columns = indexedColumns;
    }

    public static MySQLQueryTranslator routing(IndexedColumns indexedColumns) {
        return indexedColumns.isEmpty() ? UNROUTED : new MySQLQueryTranslator(indexedColumns);
    }

    public static MySQLQueryTranslator unrouted() {
        return UNROUTED;
    }

    public ParameterizedFragment filter(FilterOptions filterOptions) {
        return filterOptions.accept(this);
    }

    public String sort(SortingOptions sortingOptions) {
        return sortingOptions.accept(this);
    }

    public ParameterizedFragment update(UpdateOptions updateOptions) {
        return updateOptions.accept(this);
    }

    private static String path(String text) {
        return "$." + text;
    }

    private static String str(Object result) {
        return result == null ? null : String.valueOf(result);
    }

    private ParameterizedFragment comparison(String text, Object result, String text2) {
        String text3 = this.columns.columnFor(text);
        boolean matches;
        Object result2 = (matches = result instanceof Number) ? result : str(result);
        if (text3 != null) {
            return new ParameterizedFragment(text3 + " " + text2 + " ?", result2);
        }
        String text4 =
                matches
                        ? "CAST(JSON_UNQUOTE(JSON_EXTRACT(data, ?)) AS DECIMAL(65, 10))"
                        : "JSON_UNQUOTE(JSON_EXTRACT(data, ?))";
        return new ParameterizedFragment(text4 + " " + text2 + " ?", path(text), result2);
    }

    public ParameterizedFragment visitEquals(String text, Object result) {
        return this.comparison(text, result, "=");
    }

    public ParameterizedFragment visitNotEquals(String text, Object result) {
        return this.comparison(text, result, "<>");
    }

    public ParameterizedFragment visitGreaterThan(String text, Object result) {
        return this.comparison(text, result, ">");
    }

    public ParameterizedFragment visitGreaterThanEquals(String text, Object result) {
        return this.comparison(text, result, ">=");
    }

    public ParameterizedFragment visitLowerThan(String text, Object result) {
        return this.comparison(text, result, "<");
    }

    public ParameterizedFragment visitIn(String text, Collection<String> values) {
        return this.membership(text, values, "IN", ParameterizedFragment.alwaysFalse());
    }

    public ParameterizedFragment visitNotIn(String text, Collection<String> values) {
        return this.membership(text, values, "NOT IN", ParameterizedFragment.alwaysTrue());
    }

    private ParameterizedFragment membership(
            String text,
            Collection<String> values,
            String text2,
            ParameterizedFragment parameterizedFragment) {
        if (values != null && !values.isEmpty()) {
            String text3 = this.columns.columnFor(text);
            ArrayList<String> values2 = new ArrayList<>();
            StringBuilder stringBuilder = new StringBuilder();
            if (text3 != null) {
                stringBuilder.append(text3);
            } else {
                stringBuilder.append("JSON_UNQUOTE(JSON_EXTRACT(data, ?))");
                values2.add(path(text));
            }
            stringBuilder.append(' ').append(text2).append(" (");
            boolean matches = true;
            for (String text4 : values) {
                if (!matches) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append('?');
                values2.add(text4);
                matches = false;
            }
            return new ParameterizedFragment(stringBuilder.append(')').toString(), values2);
        } else {
            return parameterizedFragment;
        }
    }

    public ParameterizedFragment visitExists(String text) {
        return new ParameterizedFragment("JSON_CONTAINS_PATH(data, 'one', ?)", path(text));
    }

    public ParameterizedFragment visitAnd(List<FilterOptions> values) {
        return this.composite(values, " AND ", ParameterizedFragment.alwaysTrue());
    }

    public ParameterizedFragment visitOr(List<FilterOptions> values) {
        return this.composite(values, " OR ", ParameterizedFragment.alwaysFalse());
    }

    private ParameterizedFragment composite(
            List<FilterOptions> values, String text, ParameterizedFragment parameterizedFragment) {
        if (values != null && !values.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder("(");
            ArrayList values2 = new ArrayList();
            for (int index = 0; index < values.size(); index++) {
                if (index > 0) {
                    stringBuilder.append(text);
                }
                ParameterizedFragment parameterizedFragment2 =
                        ((FilterOptions) values.get(index)).accept(this);
                stringBuilder.append(parameterizedFragment2.getSql());
                values2.addAll(parameterizedFragment2.getArgs());
            }
            return new ParameterizedFragment(stringBuilder.append(')').toString(), values2);
        } else {
            return parameterizedFragment;
        }
    }

    public String visitDescending(List<String> values) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String text : values) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }
            String text2;
            if ((text2 = this.columns.columnFor(text)) != null) {
                stringBuilder.append(text2).append(" DESC");
            } else {
                stringBuilder.append(IndexedColumn.jsonExtract(text)).append(" DESC");
            }
        }
        return stringBuilder.toString();
    }

    public ParameterizedFragment visitSet(String text, Object result) {
        return new ParameterizedFragment("data = JSON_SET(data, ?, ?)", path(text), str(result));
    }

    public ParameterizedFragment visitSetFields(Map<String, String> entries) {
        StringBuilder stringBuilder = new StringBuilder("data = JSON_SET(data");
        ArrayList<String> values = new ArrayList<>();
        for (Entry<String, String> entry : entries.entrySet()) {
            stringBuilder.append(", ?, JSON_EXTRACT(?, '$')");
            values.add(path((String) entry.getKey()));
            values.add(entry.getValue());
        }
        return new ParameterizedFragment(stringBuilder.append(')').toString(), values);
    }
}
