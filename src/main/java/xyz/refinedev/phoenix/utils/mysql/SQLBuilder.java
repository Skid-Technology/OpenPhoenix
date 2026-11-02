package xyz.refinedev.phoenix.utils.mysql;

import xyz.refinedev.lib.idb.DB;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.StringJoiner;

public class SQLBuilder implements AutoCloseable {

    private Connection connection;

    private PreparedStatement ps;

    private ResultSet set;

    private StringBuilder statement = new StringBuilder();

    public SQLBuilder() throws SQLException {
        this.connection = DB.getGlobalDatabase().getConnection();
    }

    public SQLBuilder(Connection connection2) {
        this.connection = connection2;
    }

    public String getStatement() {
        return this.statement.toString();
    }

    public SQLBuilder createTable(Table table, boolean matches) {
        this.statement
                .append("CREATE TABLE")
                .append(matches ? " IF NOT EXISTS " : " ")
                .append(table.getName())
                .append("(")
                .append(table.getTableAttributes())
                .append(")");
        return this;
    }

    public String getColumnsAsString(Column<?>... column) {
        String text;
        if (column.length == 0) {
            text = "*";
        } else {
            StringJoiner stringJoiner = new StringJoiner(",");
            Column[] columns = column;
            int value = column.length;
            for (int index = 0; index < value; index++) {
                Column column2 = columns[index];
                stringJoiner.add(column2.getFullName());
            }
            text = stringJoiner.toString();
        }
        return text;
    }

    public SQLBuilder select(Table table, Column<?>... column) {
        this.statement
                .append("SELECT ")
                .append(this.getColumnsAsString(column))
                .append(" FROM ")
                .append(table.getName());
        return this;
    }

    public SQLBuilder selectCount(Table table, Column<?>... column) {
        this.statement
                .append("SELECT COUNT(")
                .append(this.getColumnsAsString(column))
                .append(") FROM ")
                .append(table.getName());
        return this;
    }

    public SQLBuilder count(Table table) {
        this.statement.append("SELECT COUNT(*) FROM ").append(table.getName());
        return this;
    }

    public SQLBuilder affectedByLastUpdate() {
        this.statement.append("SELECT ROW_COUNT() AS deleted_count");
        return this;
    }

    public SQLBuilder insert(Table table, Column<?>... column) {
        this.statement.append("INSERT INTO ").append(table.getName());
        if (column.length > 0) {
            this.statement.append(" (").append(this.getColumnsAsString(column)).append(")");
        }
        return this;
    }

    public SQLBuilder insertIgnore(Table table, Column<?>... column) {
        this.statement.append("INSERT IGNORE INTO ").append(table.getName());
        if (column.length > 0) {
            this.statement.append(" (").append(this.getColumnsAsString(column)).append(")");
        }
        return this;
    }

    public SQLBuilder values(String text) {
        this.statement.append(" VALUES (").append(text).append(")");
        return this;
    }

    public SQLBuilder onDuplicateKeyUpdate(String text) {
        this.statement.append(" ON DUPLICATE KEY UPDATE ").append(text);
        return this;
    }

    public SQLBuilder update(Table table, Column<?>... column) {
        this.statement.append("UPDATE ").append(table.getName()).append(" SET ");
        for (int index = 0; index < column.length; index++) {
            String fullName = column[index].getFullName();
            this.statement.append(fullName).append(" = ?");
            if (index + 1 < column.length) {
                this.statement.append(", ");
            }
        }
        return this;
    }

    public SQLBuilder updateRaw(Table table) {
        this.statement.append("UPDATE ").append(table.getName());
        return this;
    }

    public SQLBuilder set() {
        this.statement.append(" SET ");
        return this;
    }

    public SQLBuilder manualAppend(String text) {
        this.statement.append(text);
        return this;
    }

    public SQLBuilder delete(Table table) {
        this.statement.append("DELETE FROM ").append(table.getName());
        return this;
    }

    public SQLBuilder where(String text) {
        if (!text.isEmpty()) {
            this.statement.append(" WHERE ").append(text);
        }
        return this;
    }

    public SQLBuilder whereColumnIs(Column<?> column) {
        return this.where(column.getFullName() + " = ?");
    }

    public SQLBuilder and(String text) {
        this.statement.append(" AND ").append(text);
        return this;
    }

    public SQLBuilder andColumnIs(Column<?> column) {
        return this.and(column.getFullName() + " = ?");
    }

    public SQLBuilder or(String text) {
        this.statement.append(" OR ").append(text);
        return this;
    }

    public SQLBuilder orColumnIs(Column<?> column) {
        return this.or(column.getFullName() + " = ?");
    }

    public SQLBuilder in(String text) {
        if (!text.isEmpty()) {
            this.statement.append(" IN (").append(text).append(")");
        }
        return this;
    }

    public SQLBuilder orderBy(Column<?> column, boolean matches) {
        this.statement
                .append(" ORDER BY ")
                .append(column.getFullName())
                .append(matches ? " ASC" : " DESC");
        return this;
    }

    public SQLBuilder order(String text, boolean matches) {
        this.statement.append(" ORDER BY ").append(text).append(matches ? " ASC" : " DESC");
        return this;
    }

    public SQLBuilder order(String text) {
        this.statement.append(" ORDER BY ").append(text);
        return this;
    }

    public SQLBuilder limit(int value) {
        this.statement.append(" LIMIT ").append(value);
        return this;
    }

    public SQLBuilder limit(int value, int value2) {
        this.statement.append(" LIMIT ").append(value).append(",").append(value2);
        return this;
    }

    public SQLBuilder join(Table table, String text) {
        this.statement.append(" JOIN ").append(table.getName()).append(" ON ").append(text);
        return this;
    }

    public SQLBuilder leftJoin(Table table, String text) {
        this.statement.append(" LEFT JOIN ").append(table.getName()).append(" ON ").append(text);
        return this;
    }

    public SQLBuilder incrInteger(Column<?> column, int value) {
        this.statement
                .append(column.getFullName())
                .append(" = ")
                .append(column.getFullName())
                .append(" + ")
                .append(value);
        return this;
    }

    public SQLBuilder setStatement(String text) {
        this.statement = new StringBuilder(text);
        return this;
    }

    public SQLBuilder addStatement(String text) throws SQLException {
        if (this.ps == null) {
            this.createStatement();
        }
        this.ps.addBatch(text);
        return this;
    }

    public SQLBuilder queue(Object... result) throws SQLException {
        if (this.ps == null) {
            this.createStatement();
        }
        this.fillParams(result);
        this.ps.addBatch();
        return this;
    }

    public SQLBuilder clearParameters() throws SQLException {
        this.ps.clearParameters();
        this.statement = new StringBuilder();
        return this;
    }

    public SQLBuilder createStatement() throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            this.ps = this.connection.prepareStatement(this.statement.toString());
            return this;
        } else {
            throw new SQLException("Connection already closed");
        }
    }

    public SQLBuilder createStatement(int value) throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            this.ps = this.connection.prepareStatement(this.statement.toString(), value);
            return this;
        } else {
            throw new SQLException("Connection already closed");
        }
    }

    public SQLBuilder setString(int value, String text) throws SQLException {
        this.getPreparedStatement().setString(value, text);
        return this;
    }

    public SQLBuilder setObject(int value, Object result) throws SQLException {
        this.getPreparedStatement().setObject(value, result);
        return this;
    }

    public int executeUpdate(Object... result) throws SQLException {
        if (this.ps == null) {
            this.createStatement();
        }
        this.fillParams(result);
        return this.ps.executeUpdate();
    }

    public int[] executeBatch() throws SQLException {
        return this.ps.executeBatch();
    }

    public SQLBuilder executeQuery(Object... result) throws SQLException {
        if (this.ps == null) {
            this.createStatement();
        }
        this.fillParams(result);
        this.set = this.ps.executeQuery();
        return this;
    }

    protected void fillParams(Object... result) throws SQLException {
        for (int index = 0; index < result.length; index++) {
            this.ps.setObject(index + 1, result[index]);
        }
    }

    public boolean next() throws SQLException {
        return this.set.next();
    }

    public Object getObject(int value) throws SQLException {
        return this.set.getObject(value);
    }

    public Object getObject(String text) throws SQLException {
        return this.set.getObject(text);
    }

    public String getString(String text) throws SQLException {
        return (String) this.getObject(text);
    }

    public String getString(int value) throws SQLException {
        return (String) this.getObject(value);
    }

    public Integer getInteger(String text) throws SQLException {
        Object result;
        return (result = this.getObject(text)) instanceof BigDecimal
                ? ((BigDecimal) result).intValue()
                : (Integer) result;
    }

    public Integer getInteger(int value) throws SQLException {
        Object result;
        return (result = this.getObject(value)) instanceof BigDecimal
                ? ((BigDecimal) result).intValue()
                : (Integer) result;
    }

    public Long getLong(String text) throws SQLException {
        Object result;
        return (result = this.getObject(text)) instanceof BigDecimal
                ? ((BigDecimal) result).longValue()
                : (Long) result;
    }

    public Long getLong(int value) throws SQLException {
        Object result;
        return (result = this.getObject(value)) instanceof BigDecimal
                ? ((BigDecimal) result).longValue()
                : (Long) result;
    }

    public Timestamp getTimestamp(String text) throws SQLException {
        return (Timestamp) this.getObject(text);
    }

    public Timestamp getTimestamp(int value) throws SQLException {
        return (Timestamp) this.getObject(value);
    }

    public Date getDate(String text) throws SQLException {
        return (Date) this.getObject(text);
    }

    public Date getDate(int value) throws SQLException {
        return (Date) this.getObject(value);
    }

    public <T> T get(Column<T> column) throws SQLException {
        return (T) column.get(this.getObject(column.getFullName()));
    }

    public PreparedStatement getPreparedStatement() {
        return this.ps;
    }

    public ResultSet getResultSet() {
        return this.set;
    }

    @Override
    public void close() {
        if (this.ps != null) {
            this.closeResource(this.ps);
        }
        if (this.set != null) {
            this.closeResource(this.set);
        }
        if (this.connection != null) {
            this.closeResource(this.connection);
        }
    }

    protected void closeResource(AutoCloseable autoCloseable) {
        try {
            autoCloseable.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public Connection getConnection() {
        return this.connection;
    }
}
