package xyz.refinedev.phoenix.utils.repository.mongo.abstraction;

import com.google.gson.Gson;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.FindIterable;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.lib.mongodb.client.MongoCursor;
import xyz.refinedev.lib.mongodb.client.model.Filters;
import xyz.refinedev.lib.mongodb.client.model.Projections;
import xyz.refinedev.lib.mongodb.client.model.ReplaceOptions;
import xyz.refinedev.lib.mongodb.client.result.UpdateResult;
import xyz.refinedev.phoenix.profile.TrackedEntity;
import xyz.refinedev.phoenix.utils.repository.EntityGateway;
import xyz.refinedev.phoenix.utils.repository.EntityVanishedException;
import xyz.refinedev.phoenix.utils.repository.filters.api.FilterOptions;
import xyz.refinedev.phoenix.utils.repository.sorting.api.SortingOptions;
import xyz.refinedev.phoenix.utils.repository.translate.MongoQueryTranslator;
import xyz.refinedev.phoenix.utils.repository.updates.api.UpdateOptions;
import xyz.refinedev.phoenix.utils.repository.updates.impl.SetFieldsUpdateOption;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class MongoCollectionGateway<V> implements EntityGateway<V> {

    private final Type type;

    private final MongoCollection<Document> collection;

    private final String idFieldName;

    private final Gson gson;

    public MongoCollectionGateway(
            Type type2, MongoCollection<Document> mongoCollection, String idFieldName, Gson gson2) {
        this.type = type2;
        this.collection = mongoCollection;
        this.idFieldName = idFieldName;
        this.gson = gson2;
    }

    @Override
    public String toJson(V v) {
        return this.gson.toJson(v, this.type);
    }

    public String getCollectionName() {
        return this.collection.getNamespace().getCollectionName();
    }

    @Override
    public String describe() {
        return this.getCollectionName();
    }

    @Override
    public V deserialize(String text) {
        return TrackedEntity.snapshotIfTracked((V) this.gson.fromJson(text, this.type), this.gson);
    }

    @Override
    public void replace(String text, String text2) {
        this.collection.replaceOne(
                Filters.eq(this.idFieldName, text),
                Document.parse(text2),
                new ReplaceOptions().upsert(true));
    }

    @Override
    public void updateFields(String text, UpdateOptions updateOptions, String text2) {
        boolean matches = coversEveryField(updateOptions, this.type);
        UpdateResult updateResult =
                this.collection.updateOne(
                        Filters.eq(this.idFieldName, text),
                        MongoQueryTranslator.update(updateOptions),
                        new xyz.refinedev.lib.mongodb.client.model.UpdateOptions().upsert(matches));
        if (!matches && updateResult.getMatchedCount() <= 0L) {
            throw new EntityVanishedException(
                    "No document to apply a field-scoped update to in "
                            + this.collection.getNamespace().getCollectionName()
                            + " (Key: "
                            + text
                            + ")");
        }
    }

    static boolean coversEveryField(UpdateOptions updateOptions, Type type) {
        return updateOptions instanceof SetFieldsUpdateOption && type instanceof Class
                ? ((SetFieldsUpdateOption) updateOptions)
                        .getFieldJson()
                        .keySet()
                        .containsAll(TrackedEntity.persistedFieldNames((Class<?>) type))
                : true;
    }

    @Override
    public boolean needsFullJson() {
        return false;
    }

    @Override
    public void saveAll(List<V> values, Function<V, ?> function) {
        if (!values.isEmpty()) {
            ArrayList<Document> values2 = new ArrayList<>();
            for (Object result : values) {
                values2.add(Document.parse(this.toJson((V) result)));
            }
            this.collection.insertMany(values2);
        }
    }

    @Override
    public Optional<V> findByKey(String text) {
        Optional<Document> optional =
                Optional.ofNullable(
                        (Document)
                                this.collection.find(Filters.eq(this.idFieldName, text)).first());
        return optional.map(result -> this.deserialize(result.toJson()));
    }

    @Override
    public Optional<V> findOne(FilterOptions filterOptions, SortingOptions sortingOptions) {
        FindIterable findIterable =
                this.collection.find(MongoQueryTranslator.filter(filterOptions));
        if (sortingOptions != null) {
            findIterable = findIterable.sort(MongoQueryTranslator.sort(sortingOptions));
        }
        Optional<Document> optional = Optional.ofNullable((Document) findIterable.first());
        return optional.map(result -> this.deserialize(result.toJson()));
    }

    @Override
    public List<V> findAll() {
        FindIterable findIterable = this.collection.find();
        MongoCollectionGateway<V> mongoCollectionGateway = this;
        return this.collect(findIterable, mongoCollectionGateway::deserialize);
    }

    @Override
    public List<V> findAll(FilterOptions filterOptions, SortingOptions sortingOptions) {
        FindIterable findIterable = this.find(filterOptions, sortingOptions);
        MongoCollectionGateway<V> mongoCollectionGateway = this;
        return this.collect(findIterable, mongoCollectionGateway::deserialize);
    }

    @Override
    public List<V> findPage(FilterOptions filterOptions, SortingOptions sortingOptions, int value) {
        FindIterable findIterable = this.find(filterOptions, sortingOptions).limit(value);
        MongoCollectionGateway<V> mongoCollectionGateway = this;
        return this.collect(findIterable, mongoCollectionGateway::deserialize);
    }

    @Override
    public List<V> findAllDetached(FilterOptions filterOptions, SortingOptions sortingOptions) {
        FindIterable findIterable = this.find(filterOptions, sortingOptions);
        return this.collect(findIterable, result -> (V) this.gson.fromJson(result, this.type));
    }

    @Override
    public List<String> findFieldValues(
            String text, FilterOptions filterOptions, SortingOptions sortingOptions) {
        ArrayList<String> values = new ArrayList<>();
        MongoCursor mongoCursor =
                this.find(filterOptions, sortingOptions)
                        .projection(Projections.include(new String[] {text}))
                        .iterator();
        while (mongoCursor.hasNext()) {
            Document document;
            Object result;
            if ((document = (Document) mongoCursor.next()) != null
                    && (result = document.get(text)) != null) {
                values.add(result.toString());
            }
        }
        return values;
    }

    private FindIterable<Document> find(
            FilterOptions filterOptions, SortingOptions sortingOptions) {
        FindIterable findIterable =
                this.collection.find(MongoQueryTranslator.filter(filterOptions));
        return sortingOptions == null
                ? findIterable
                : findIterable.sort(MongoQueryTranslator.sort(sortingOptions));
    }

    private List<V> collect(Iterable<Document> iterable, Function<String, V> function) {
        ArrayList<V> values = new ArrayList<>();
        Iterator<Document> iterator2 = iterable.iterator();
        while (iterator2.hasNext()) {
            Document document;
            if ((document = (Document) iterator2.next()) != null) {
                values.add(function.apply(document.toJson()));
            }
        }
        return Collections.synchronizedList(values);
    }

    @Override
    public void deleteByKey(String text) {
        this.collection.deleteOne(Filters.eq(this.idFieldName, text));
    }

    @Override
    public long deleteWhere(FilterOptions filterOptions) {
        return this.collection
                .deleteMany(MongoQueryTranslator.filter(filterOptions))
                .getDeletedCount();
    }

    @Override
    public void deleteAll() {
        this.collection.deleteMany(new Document());
    }

    @Override
    public boolean exists(String text) {
        return this.collection.find(Filters.eq(this.idFieldName, text)).first() != null;
    }

    @Override
    public long count() {
        return this.collection.countDocuments();
    }

    @Override
    public long count(FilterOptions filterOptions) {
        return this.collection.countDocuments(MongoQueryTranslator.filter(filterOptions));
    }

    @Override
    public void updateWhere(FilterOptions filterOptions, UpdateOptions updateOptions) {
        this.collection.updateMany(
                MongoQueryTranslator.filter(filterOptions),
                MongoQueryTranslator.update(updateOptions));
    }

    public Type getType() {
        return this.type;
    }

    public MongoCollection<Document> getCollection() {
        return this.collection;
    }

    public String getIdFieldName() {
        return this.idFieldName;
    }

    @Override
    public Gson getGson() {
        return this.gson;
    }
}
