package xyz.refinedev.phoenix.utils.mysql;

public abstract class AbstractColumn<T> implements Column<T> {

    private final String name;

    private final String sqlType;

    private final boolean nullable;

    private final String defaultValue;

    private final String generatedAs;

    private Table table;

    public AbstractColumn(
            String name,
            String sqlType,
            boolean nullable,
            String defaultValue,
            String generatedAs) {
        this.name = name;
        this.sqlType = sqlType;
        this.nullable = nullable;
        this.defaultValue = defaultValue;
        this.generatedAs = generatedAs;
    }

    public AbstractColumn(String text, String text2) {
        this(text, text2, false, null, null);
    }

    @Override
    public String getFullName() {
        return this.table == null ? this.getName() : this.table.getName() + "." + this.getName();
    }

    @Override
    public String toDefinition() {
        StringBuilder stringBuilder;
        (stringBuilder = new StringBuilder())
                .append(this.getName())
                .append(" ")
                .append(this.getSqlType());
        if (!this.isNullable()) {
            stringBuilder.append(" NOT NULL");
        }
        if (this.defaultValue != null) {
            stringBuilder.append(" DEFAULT ").append(this.defaultValue);
        }
        if (this.generatedAs != null) {
            stringBuilder
                    .append(" GENERATED ALWAYS AS (")
                    .append(this.generatedAs)
                    .append(") PERSISTENT");
        }
        return stringBuilder.toString();
    }

    @Override
    public Column<T> table(Table table2) {
        this.table = table2;
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getSqlType() {
        return this.sqlType;
    }

    @Override
    public boolean isNullable() {
        return this.nullable;
    }
}
