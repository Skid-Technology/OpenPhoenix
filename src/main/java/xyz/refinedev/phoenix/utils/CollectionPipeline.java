package xyz.refinedev.phoenix.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class CollectionPipeline {

    private final List<String> strings;

    private final boolean enabled;

    private CollectionPipeline(Collection<String> values, boolean enabled) {
        this.strings = new ArrayList<>(values);
        this.enabled = enabled;
    }

    public static CollectionPipeline of(Collection<String> values) {
        return new CollectionPipeline(values, true);
    }

    public static CollectionPipeline asList(String... text) {
        return of(Arrays.asList(text));
    }

    private CollectionPipeline of(Consumer<List<String>> consumer) {
        CollectionPipeline collectionPipeline =
                this.enabled ? new CollectionPipeline(this.strings, false) : this;
        consumer.accept(collectionPipeline.strings);
        return collectionPipeline;
    }

    public CollectionPipeline findCollectionPipeline(Function<String, String> function) {
        return this.of(
                result -> {
                    result.replaceAll(function::apply);
                });
    }

    public CollectionPipeline findCollectionPipeline(Predicate<String> predicate) {
        return this.of(result -> result.removeIf(predicate));
    }

    public CollectionPipeline findCollectionPipeline(String text, Objects objects) {
        return this.findCollectionPipeline(
                (Function<String, String>) (result -> result.replace(text, objects.toString())));
    }

    @Override
    public String toString() {
        return this.strings.isEmpty() ? "" : Color.translate(this.strings.get(0));
    }

    public List<String> getStrings() {
        return Color.translate(this.strings);
    }

    public List<String> getStringsByStrings() {
        return this.strings;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}
