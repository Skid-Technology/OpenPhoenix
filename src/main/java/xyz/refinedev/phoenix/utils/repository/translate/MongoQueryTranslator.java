package xyz.refinedev.phoenix.utils.repository.translate;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.bson.conversions.Bson;
import xyz.refinedev.lib.mongodb.client.model.Filters;
import xyz.refinedev.lib.mongodb.client.model.Sorts;
import xyz.refinedev.lib.mongodb.client.model.Updates;
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

public final class MongoQueryTranslator
        implements FilterVisitor<Bson>, SortVisitor<Bson>, UpdateVisitor<Bson> {

    private static final MongoQueryTranslator INSTANCE = new MongoQueryTranslator();

    private static final String text = "{\"v\":";

    private MongoQueryTranslator() {}

    public static Bson filter(FilterOptions filterOptions) {
        return filterOptions.accept(INSTANCE);
    }

    public static Bson sort(SortingOptions sortingOptions) {
        return sortingOptions.accept(INSTANCE);
    }

    public static Bson update(UpdateOptions updateOptions) {
        return updateOptions.accept(INSTANCE);
    }

    public Bson visitEquals(String text, Object result) {
        return Filters.eq(text, result);
    }

    public Bson visitNotEquals(String text, Object result) {
        return Filters.ne(text, result);
    }

    public Bson visitGreaterThan(String text, Object result) {
        return Filters.gt(text, result);
    }

    public Bson visitGreaterThanEquals(String text, Object result) {
        return Filters.gte(text, result);
    }

    public Bson visitLowerThan(String text, Object result) {
        return Filters.lt(text, result);
    }

    public Bson visitIn(String text, Collection<String> values) {
        return Filters.in(text, values);
    }

    public Bson visitNotIn(String text, Collection<String> values) {
        return Filters.nin(text, values);
    }

    public Bson visitExists(String text) {
        return Filters.exists(text);
    }

    public Bson visitAnd(List<FilterOptions> values) {
        return Filters.and(this.translate(values));
    }

    public Bson visitOr(List<FilterOptions> values) {
        return Filters.or(this.translate(values));
    }

    private List<Bson> translate(List<FilterOptions> values) {
        ArrayList<Bson> values2 = new ArrayList<>();
        for (FilterOptions filterOptions : values) {
            values2.add(filterOptions.accept(this));
        }
        return values2;
    }

    public Bson visitDescending(List<String> values) {
        return Sorts.descending(values);
    }

    public Bson visitSet(String text, Object result) {
        return Updates.set(text, result);
    }

    public Bson visitSetFields(Map<String, String> entries) {
        ArrayList<Bson> values = new ArrayList<>();
        for (Entry<String, String> entry : entries.entrySet()) {
            values.add(Updates.set((String) entry.getKey(), parseValue((String) entry.getValue())));
        }
        return Updates.combine(values);
    }

    private static Object parseValue(String text2) {
        return Document.parse(text + text2 + "}").get("v");
    }
}
