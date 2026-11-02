package xyz.refinedev.phoenix.utils.repository.mysql.abstraction;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.utils.mysql.Column;
import xyz.refinedev.phoenix.utils.mysql.JSONColumn;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.repository.GatewayMapRepository;

import java.lang.reflect.Type;
import java.util.function.Function;

public abstract class AbstractMySQLMapRepository<K, V> extends GatewayMapRepository<K, V> {

    public static final JSONColumn DATA_COLUMN = MySQLTableGateway.DATA_COLUMN;

    public AbstractMySQLMapRepository(
            Type type, Table table, Column<?> column, Function<V, K> function) {
        super(
                new MySQLTableGateway<>(
                        type, table, column, PlatformGetter.getInstance().getGson()),
                function);
    }

    public MySQLTableGateway<V> getGateway() {
        return (MySQLTableGateway<V>) super.getGateway();
    }
}
