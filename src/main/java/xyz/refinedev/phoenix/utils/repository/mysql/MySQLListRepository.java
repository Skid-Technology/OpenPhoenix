package xyz.refinedev.phoenix.utils.repository.mysql;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.utils.mysql.JSONColumn;
import xyz.refinedev.phoenix.utils.mysql.StringColumn;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.repository.GatewayListRepository;
import xyz.refinedev.phoenix.utils.repository.mysql.abstraction.MySQLTableGateway;

import java.lang.reflect.Type;
import java.util.function.Function;

public class MySQLListRepository<K, V> extends GatewayListRepository<K, V> {

    public static final JSONColumn DATA_COLUMN = MySQLTableGateway.DATA_COLUMN;

    private static final String ID_TEXT = "id";

    public MySQLListRepository(Type type, Table table, Function<V, K> function) {
        super(
                new MySQLTableGateway<>(
                        type,
                        table,
                        new StringColumn(ID_TEXT, 255),
                        PlatformGetter.getInstance().getGson()),
                function);
    }

    public MySQLTableGateway<V> getGateway() {
        return (MySQLTableGateway<V>) super.getGateway();
    }
}
