package xyz.refinedev.phoenix.utils.repository.mysql;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.utils.mysql.Column;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.repository.GatewayCacheListRepository;
import xyz.refinedev.phoenix.utils.repository.mysql.abstraction.MySQLTableGateway;

import java.lang.reflect.Type;
import java.util.function.Function;

public class MySQLCacheListRepository<K, V> extends GatewayCacheListRepository<K, V> {

    public MySQLCacheListRepository(
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
