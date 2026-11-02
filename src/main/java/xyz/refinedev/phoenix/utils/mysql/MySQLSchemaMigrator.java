package xyz.refinedev.phoenix.utils.mysql;

import xyz.refinedev.phoenix.PlatformGetter;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public final class MySQLSchemaMigrator {

    private MySQLSchemaMigrator() {}

    public static IndexedColumns apply(Table table, IndexedColumns indexedColumns) {
        if (indexedColumns.isEmpty()) {
            return IndexedColumns.none();
        }
        String name = table.getName();
        HashSet<String> values = new HashSet<>();
        Set<String> values2;
        Set<String> values3;
        try {
            values2 = fetchExistingColumns(name);
            values3 = fetchExistingIndexes(name);
        } catch (SQLException sQLException) {
            PlatformGetter.getInstance()
                    .getLogger()
                    .log(
                            Level.SEVERE,
                            "Could not read information_schema for "
                                    + name
                                    + "; no index columns will be routed.",
                            sQLException);
            return IndexedColumns.none();
        }
        for (IndexedColumn indexedColumn : indexedColumns.all()) {
            try {
                if (ensureColumn(name, indexedColumn, values2)) {
                    ensureIndex(name, indexedColumn, values3);
                    values.add(indexedColumn.getColumn());
                }
            } catch (SQLException sQLException2) {
                PlatformGetter.getInstance()
                        .getLogger()
                        .log(
                                Level.SEVERE,
                                "Could not add index column "
                                        + name
                                        + "."
                                        + indexedColumn.getColumn()
                                        + "; queries on "
                                        + indexedColumn.getField()
                                        + " will scan.",
                                sQLException2);
            }
        }
        return indexedColumns.retaining(values);
    }

    private static boolean ensureColumn(
            String text, IndexedColumn indexedColumn, Set<String> values) throws SQLException {
        if (indexedColumn.isGenerated() && !values.contains(indexedColumn.getColumn())) {
            PlatformGetter.getInstance()
                    .getLogger()
                    .info(
                            "Adding generated column "
                                    + text
                                    + "."
                                    + indexedColumn.getColumn()
                                    + " ...");
            long now = System.currentTimeMillis();
            execute(addColumnStatement(text, indexedColumn));
            PlatformGetter.getInstance()
                    .getLogger()
                    .info(
                            "Added generated column "
                                    + text
                                    + "."
                                    + indexedColumn.getColumn()
                                    + " in "
                                    + (System.currentTimeMillis() - now)
                                    + "ms.");
            values.add(indexedColumn.getColumn());
        }
        if (!values.contains(indexedColumn.getColumn())) {
            PlatformGetter.getInstance()
                    .getLogger()
                    .warning(
                            "Index column "
                                    + text
                                    + "."
                                    + indexedColumn.getColumn()
                                    + " is absent; queries on "
                                    + indexedColumn.getField()
                                    + " will scan.");
            return false;
        } else {
            return true;
        }
    }

    private static void ensureIndex(String text, IndexedColumn indexedColumn, Set<String> values)
            throws SQLException {
        if (indexedColumn.isGenerated() && !values.contains(indexedColumn.getIndexName(text))) {
            String indexName = indexedColumn.getIndexName(text);
            PlatformGetter.getInstance()
                    .getLogger()
                    .info(
                            "Building index "
                                    + indexName
                                    + " on "
                                    + text
                                    + "."
                                    + indexedColumn.getColumn()
                                    + " (~"
                                    + approximateRows(text)
                                    + " rows); startup waits for this ...");
            long now = System.currentTimeMillis();
            execute(addIndexStatement(text, indexedColumn));
            PlatformGetter.getInstance()
                    .getLogger()
                    .info(
                            "Built index "
                                    + indexName
                                    + " on "
                                    + text
                                    + "."
                                    + indexedColumn.getColumn()
                                    + " in "
                                    + (System.currentTimeMillis() - now)
                                    + "ms.");
            values.add(indexName);
        }
    }

    static String addColumnStatement(String text, IndexedColumn indexedColumn) {
        return "ALTER TABLE "
                + text
                + " ADD COLUMN "
                + indexedColumn.getColumn()
                + " VARCHAR("
                + 255
                + ") CHARACTER SET utf8mb4 COLLATE utf8mb4_bin GENERATED ALWAYS AS ("
                + indexedColumn.generatedFrom()
                + ") VIRTUAL";
    }

    static String addIndexStatement(String text, IndexedColumn indexedColumn) {
        return "CREATE INDEX "
                + indexedColumn.getIndexName(text)
                + " ON "
                + text
                + " ("
                + indexedColumn.getColumn()
                + ")";
    }

    private static void execute(String text) throws SQLException {
        SQLBuilder sQLBuilder = new SQLBuilder();
        try {
            sQLBuilder.setStatement(text).executeUpdate();
        } catch (Throwable throwable) {
            try {
                sQLBuilder.close();
            } catch (Throwable throwable2) {
                throwable.addSuppressed(throwable2);
            }
            throw throwable;
        }
        sQLBuilder.close();
    }

    private static Set<String> fetchExistingColumns(String text) throws SQLException {
        return fetchNames(
                "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?",
                "COLUMN_NAME",
                text);
    }

    private static Set<String> fetchExistingIndexes(String text) throws SQLException {
        return fetchNames(
                "SELECT INDEX_NAME FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?",
                "INDEX_NAME",
                text);
    }

    private static String approximateRows(String text) {
        try {
            SQLBuilder sQLBuilder = new SQLBuilder();
            String text2;
            try {
                sQLBuilder
                        .setStatement(
                                "SELECT TABLE_ROWS FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?")
                        .executeQuery(text);
                text2 =
                        sQLBuilder.next()
                                ? String.valueOf(sQLBuilder.getLong("TABLE_ROWS"))
                                : "unknown";
            } catch (Throwable throwable) {
                try {
                    sQLBuilder.close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            sQLBuilder.close();
            return text2;
        } catch (SQLException | RuntimeException exception) {
            return "unknown";
        }
    }

    private static Set<String> fetchNames(String text, String text2, String text3)
            throws SQLException {
        HashSet<String> values = new HashSet<>();
        SQLBuilder sQLBuilder = new SQLBuilder();
        try {
            sQLBuilder.setStatement(text).executeQuery(text3);
            while (sQLBuilder.next()) {
                values.add(sQLBuilder.getString(text2));
            }
        } catch (Throwable throwable) {
            try {
                sQLBuilder.close();
            } catch (Throwable throwable2) {
                throwable.addSuppressed(throwable2);
            }
            throw throwable;
        }
        sQLBuilder.close();
        return values;
    }
}
