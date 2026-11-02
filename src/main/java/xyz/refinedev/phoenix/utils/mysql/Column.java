package xyz.refinedev.phoenix.utils.mysql;

public interface Column<T> extends Attribute {

    T get(Object result);

    String getName();

    String getFullName();

    String getSqlType();

    boolean isNullable();

    Column<T> table(Table table2);
}
