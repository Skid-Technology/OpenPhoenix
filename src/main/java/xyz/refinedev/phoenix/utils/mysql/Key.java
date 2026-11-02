package xyz.refinedev.phoenix.utils.mysql;

import java.util.StringJoiner;

public interface Key extends Attribute {

    static Key primary(Column<?>... column) {
        return new Key.PrimaryKey(column);
    }

    static Key index(Column<?>... column) {
        return new Key.IndexKey(false, column);
    }

    static Key index(boolean matches, Column<?>... column) {
        return new Key.IndexKey(matches, column);
    }

    static Key unique(Column<?>... column) {
        return new Key.Unique(column);
    }

    Column<?>[] getColumns();

    class IndexKey implements Key {

        Column<?>[] columns;

        boolean unique;

        public IndexKey(boolean unique, Column<?>... column) {
            this.unique = unique;
            this.columns = column;
        }

        @Override
        public Column<?>[] getColumns() {
            return this.columns;
        }

        @Override
        public String toDefinition() {
            StringJoiner stringJoiner = new StringJoiner(",");
            Column[] columns2 = this.columns;
            int value = this.columns.length;
            for (int index = 0; index < value; index++) {
                Column column = columns2[index];
                stringJoiner.add(column.getName());
            }
            return (this.unique ? "UNIQUE " : "") + "KEY (" + stringJoiner.toString() + ")";
        }
    }

    class PrimaryKey implements Key {

        Column<?>[] columns;

        private static final String text = "PRIMARY KEY (";

        public PrimaryKey(Column<?>... column) {
            this.columns = column;
        }

        @Override
        public Column<?>[] getColumns() {
            return this.columns;
        }

        @Override
        public String toDefinition() {
            StringJoiner stringJoiner = new StringJoiner(",");
            Column[] columns2 = this.columns;
            int value = this.columns.length;
            for (int index = 0; index < value; index++) {
                Column column = columns2[index];
                stringJoiner.add(column.getName());
            }
            return text + stringJoiner.toString() + ")";
        }
    }

    class Unique implements Key {

        Column<?>[] columns;

        private static final String text = "UNIQUE (";

        public Unique(Column<?>... column) {
            this.columns = column;
        }

        @Override
        public Column<?>[] getColumns() {
            return this.columns;
        }

        @Override
        public String toDefinition() {
            StringJoiner stringJoiner = new StringJoiner(",");
            Column[] columns2 = this.columns;
            int value = this.columns.length;
            for (int index = 0; index < value; index++) {
                Column column = columns2[index];
                stringJoiner.add(column.getName());
            }
            return text + stringJoiner.toString() + ")";
        }
    }
}
