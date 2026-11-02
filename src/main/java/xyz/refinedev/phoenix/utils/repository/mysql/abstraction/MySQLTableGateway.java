package xyz.refinedev.phoenix.utils.repository.mysql.abstraction;

import com.google.gson.Gson;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.profile.TrackedEntity;
import xyz.refinedev.phoenix.utils.mysql.Column;
import xyz.refinedev.phoenix.utils.mysql.IndexedColumns;
import xyz.refinedev.phoenix.utils.mysql.JSONColumn;
import xyz.refinedev.phoenix.utils.mysql.MySQLSchemaMigrator;
import xyz.refinedev.phoenix.utils.mysql.SQLBuilder;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.repository.DataAccessException;
import xyz.refinedev.phoenix.utils.repository.DataAccessFailures;
import xyz.refinedev.phoenix.utils.repository.EntityGateway;
import xyz.refinedev.phoenix.utils.repository.filters.api.FilterOptions;
import xyz.refinedev.phoenix.utils.repository.sorting.api.SortingOptions;
import xyz.refinedev.phoenix.utils.repository.translate.MySQLQueryTranslator;
import xyz.refinedev.phoenix.utils.repository.translate.ParameterizedFragment;
import xyz.refinedev.phoenix.utils.repository.updates.api.UpdateOptions;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;

public final class MySQLTableGateway<V> implements EntityGateway<V> {

    public static final JSONColumn DATA_COLUMN = new JSONColumn("data");

    private final Type type;

    private final Table table;

    private final Column<?> idColumn;

    private final Gson gson;

    private final IndexedColumns indexedColumns;

    private final MySQLQueryTranslator translator;

    public MySQLTableGateway(Type type2, Table table2, Column<?> column, Gson gson2) {
        this.type = type2;
        this.table = table2;
        this.idColumn = column;
        this.gson = gson2;
        try {
            SQLBuilder sQLBuilder = new SQLBuilder();
            try {
                sQLBuilder.createTable(table2, true).executeUpdate();
            } catch (Throwable throwable) {
                try {
                    sQLBuilder.close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            sQLBuilder.close();
        } catch (SQLException sQLException) {
            PlatformGetter.getInstance()
                    .getLogger()
                    .log(Level.SEVERE, "Error while creating table:", sQLException);
        }
        this.indexedColumns =
                MySQLSchemaMigrator.apply(table2, IndexedColumns.forTable(table2.getName()));
        this.translator = MySQLQueryTranslator.routing(this.indexedColumns);
    }

    private <T> T run(String text, String text2, T t, MySQLTableGateway.SQLBody<T> sQLBody) {
        try {
            SQLBuilder sQLBuilder = new SQLBuilder();
            Object result;
            try {
                T result2 = sQLBody.execute(sQLBuilder);
                DataAccessFailures.recordSuccess("mysql");
                result = result2;
            } catch (Throwable throwable) {
                try {
                    sQLBuilder.close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            sQLBuilder.close();
            return (T) result;
        } catch (Exception exception) {
            DataAccessFailures.record("mysql", text, this.table.getName(), text2, exception);
            return (T) t;
        }
    }

    private <T> T read(String text, String text2, MySQLTableGateway.SQLBody<T> sQLBody) {
        try {
            SQLBuilder sQLBuilder = new SQLBuilder();
            Object result;
            try {
                T result2 = sQLBody.execute(sQLBuilder);
                DataAccessFailures.recordSuccess("mysql");
                result = result2;
            } catch (Throwable throwable) {
                try {
                    sQLBuilder.close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            sQLBuilder.close();
            return (T) result;
        } catch (Exception exception) {
            DataAccessFailures.record("mysql", text, this.table.getName(), text2, exception);
            throw new DataAccessException(
                    "Read of "
                            + this.table.getName()
                            + " failed ("
                            + text
                            + ", Key: "
                            + text2
                            + ")",
                    exception);
        }
    }

    private <T> T saving(String text, String text2, MySQLTableGateway.SQLBody<T> sQLBody) {
        try {
            SQLBuilder sQLBuilder = new SQLBuilder();
            Object result;
            try {
                T result2 = sQLBody.execute(sQLBuilder);
                DataAccessFailures.recordSuccess("mysql");
                result = result2;
            } catch (Throwable throwable) {
                try {
                    sQLBuilder.close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            sQLBuilder.close();
            return (T) result;
        } catch (Exception exception) {
            DataAccessFailures.record("mysql", text, this.table.getName(), text2, exception);
            throw new DataAccessException(
                    "Bulk write to " + this.table.getName() + " failed", exception);
        }
    }

    @Override
    public String toJson(V v) {
        return this.gson.toJson(v, this.type);
    }

    @Override
    public V deserialize(String text) {
        return TrackedEntity.snapshotIfTracked((V) this.gson.fromJson(text, this.type), this.gson);
    }

    @Override
    public String describe() {
        return this.table.getName();
    }

    @Override
    public boolean needsFullJson() {
        return true;
    }

    @Override
    public void replace(String text, String text2) {
        this.run(
                "save",
                text,
                null,
                result -> {
                    result.insert(this.table, this.idColumn, DATA_COLUMN)
                            .values("?, ?")
                            .onDuplicateKeyUpdate("data = ?")
                            .executeUpdate(text, text2, text2);
                    return null;
                });
    }

    @Override
    public void updateFields(String text, UpdateOptions updateOptions, String text2) {
        ParameterizedFragment parameterizedFragment = this.translator.update(updateOptions);
        ArrayList<Object> values2;
        (values2 = new ArrayList()).add(text);
        values2.add(text2);
        values2.addAll(parameterizedFragment.getArgs());
        try {
            SQLBuilder sQLBuilder = new SQLBuilder();
            try {
                sQLBuilder
                        .insert(this.table, this.idColumn, DATA_COLUMN)
                        .values("?, ?")
                        .onDuplicateKeyUpdate(parameterizedFragment.getSql())
                        .executeUpdate(values2.toArray());
                DataAccessFailures.recordSuccess("mysql");
            } catch (Throwable throwable) {
                try {
                    sQLBuilder.close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            sQLBuilder.close();
        } catch (SQLException sQLException) {
            DataAccessFailures.record("mysql", "update", this.table.getName(), text, sQLException);
            throw new DataAccessException(
                    "Field-scoped update to "
                            + this.table.getName()
                            + " failed (Key: "
                            + text
                            + ")",
                    sQLException);
        }
    }

    @Override
    public void saveAll(List<V> values2, Function<V, ?> function) {
        this.saving(
                "save",
                null,
                result -> {
                    result.insert(this.table, this.idColumn, DATA_COLUMN)
                            .values("?,?")
                            .onDuplicateKeyUpdate("data = ?");
                    for (V result2 : values2) {
                        String text = this.toJson((V) result2);
                        result.queue(function.apply(result2), text, text);
                    }
                    result.executeBatch();
                    return null;
                });
    }

    @Override
    public Optional<V> findByKey(String text) {
        return this.read(
                "fetch",
                text,
                result -> {
                    result.select(this.table, DATA_COLUMN)
                            .whereColumnIs(this.idColumn)
                            .limit(1)
                            .executeQuery(text);
                    return !result.next()
                            ? Optional.empty()
                            : Optional.of(this.deserialize(result.get(DATA_COLUMN)));
                });
    }

    @Override
    public Optional<V> findOne(FilterOptions filterOptions, SortingOptions sortingOptions) {
        ParameterizedFragment parameterizedFragment = this.translator.filter(filterOptions);
        String text = sortingOptions == null ? null : this.translator.sort(sortingOptions);
        String sql = parameterizedFragment.getSql();
        return this.read(
                "fetch",
                sql,
                result -> {
                    result.select(this.table, DATA_COLUMN).where(parameterizedFragment.getSql());
                    if (text != null) {
                        result.order(text);
                    }
                    result.limit(1).executeQuery(parameterizedFragment.argsArray());
                    return !result.next()
                            ? Optional.empty()
                            : Optional.of(this.deserialize(result.get(DATA_COLUMN)));
                });
    }

    @Override
    public List<V> findAll() {
        return this.read(
                "fetchAll",
                null,
                result -> {
                    ArrayList<V> values = new ArrayList<>();
                    result.select(this.table, DATA_COLUMN).executeQuery();
                    while (result.next()) {
                        values.add(this.deserialize(result.get(DATA_COLUMN)));
                    }
                    return Collections.synchronizedList(values);
                });
    }

    @Override
    public List<V> findAll(FilterOptions filterOptions, SortingOptions sortingOptions) {
        ParameterizedFragment parameterizedFragment = this.translator.filter(filterOptions);
        String text = sortingOptions == null ? null : this.translator.sort(sortingOptions);
        String sql = parameterizedFragment.getSql();
        return this.read(
                "fetchAll",
                sql,
                result -> {
                    ArrayList<V> values = new ArrayList<>();
                    result.select(this.table, DATA_COLUMN).where(parameterizedFragment.getSql());
                    if (text != null) {
                        result.order(text);
                    }
                    result.executeQuery(parameterizedFragment.argsArray());
                    while (result.next()) {
                        values.add(this.deserialize(result.get(DATA_COLUMN)));
                    }
                    return Collections.synchronizedList(values);
                });
    }

    @Override
    public List<V> findPage(FilterOptions filterOptions, SortingOptions sortingOptions, int value) {
        ParameterizedFragment parameterizedFragment = this.translator.filter(filterOptions);
        String text = sortingOptions == null ? null : this.translator.sort(sortingOptions);
        String sql = parameterizedFragment.getSql();
        return this.read(
                "fetchPage",
                sql,
                result -> {
                    ArrayList<V> values = new ArrayList<>();
                    result.select(this.table, DATA_COLUMN).where(parameterizedFragment.getSql());
                    if (text != null) {
                        result.order(text);
                    }
                    result.limit(value).executeQuery(parameterizedFragment.argsArray());
                    while (result.next()) {
                        values.add(this.deserialize(result.get(DATA_COLUMN)));
                    }
                    return Collections.synchronizedList(values);
                });
    }

    @Override
    public void deleteByKey(String text) {
        this.run(
                "delete",
                text,
                null,
                result -> {
                    result.delete(this.table).whereColumnIs(this.idColumn).executeUpdate(text);
                    return null;
                });
    }

    @Override
    public long deleteWhere(FilterOptions filterOptions) {
        ParameterizedFragment parameterizedFragment = this.translator.filter(filterOptions);
        String sql = parameterizedFragment.getSql();
        return this.run(
                "delete",
                sql,
                (Long) 0L,
                result ->
                        (long)
                                result.delete(this.table)
                                        .where(parameterizedFragment.getSql())
                                        .executeUpdate(parameterizedFragment.argsArray()));
    }

    @Override
    public void deleteAll() {
        this.run(
                "delete",
                null,
                null,
                result -> {
                    result.delete(this.table).executeUpdate();
                    return null;
                });
    }

    @Override
    public boolean exists(String text) {
        return this.read(
                "fetch",
                text,
                result -> {
                    result.select(this.table).whereColumnIs(this.idColumn).executeQuery(text);
                    return result.next();
                });
    }

    @Override
    public long count() {
        return this.read(
                "count",
                null,
                result -> {
                    result.count(this.table).executeQuery();
                    return !result.next() ? 0L : result.getLong(1);
                });
    }

    @Override
    public long count(FilterOptions filterOptions) {
        ParameterizedFragment parameterizedFragment = this.translator.filter(filterOptions);
        String sql = parameterizedFragment.getSql();
        return this.read(
                "count",
                sql,
                result -> {
                    result.selectCount(this.table)
                            .where(parameterizedFragment.getSql())
                            .executeQuery(parameterizedFragment.argsArray());
                    return !result.next() ? 0L : result.getLong(1);
                });
    }

    @Override
    public void updateWhere(FilterOptions filterOptions, UpdateOptions updateOptions) {
        ParameterizedFragment parameterizedFragment = this.translator.update(updateOptions);
        ParameterizedFragment parameterizedFragment2 = this.translator.filter(filterOptions);
        ArrayList values;
        (values = new ArrayList<>(parameterizedFragment.getArgs()))
                .addAll(parameterizedFragment2.getArgs());
        String sql = parameterizedFragment2.getSql();
        ArrayList<Object> values2 = values;
        this.run(
                "update",
                sql,
                null,
                result -> {
                    result.updateRaw(this.table)
                            .set()
                            .manualAppend(parameterizedFragment.getSql())
                            .where(parameterizedFragment2.getSql())
                            .executeUpdate(values2.toArray());
                    return null;
                });
    }

    public Type getType() {
        return this.type;
    }

    public Table getTable() {
        return this.table;
    }

    public Column<?> getIdColumn() {
        return this.idColumn;
    }

    @Override
    public Gson getGson() {
        return this.gson;
    }

    public IndexedColumns getIndexedColumns() {
        return this.indexedColumns;
    }

    public MySQLQueryTranslator getTranslator() {
        return this.translator;
    }

    interface SQLBody<T> {

        T execute(SQLBuilder sQLBuilder) throws SQLException;
    }
}
