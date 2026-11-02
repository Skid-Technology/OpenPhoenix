package xyz.refinedev.phoenix.utils.repository.translate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ParameterizedFragment {

    private static final ParameterizedFragment ALWAYS_TRUE = new ParameterizedFragment("1 = 1");

    private static final ParameterizedFragment ALWAYS_FALSE = new ParameterizedFragment("1 = 0");

    private final String sql;

    private final List<Object> args;

    public ParameterizedFragment(String text, Object... result) {
        this(text, Arrays.asList(result));
    }

    public ParameterizedFragment(String sql, List<Object> values) {
        this.sql = sql;
        this.args = Collections.unmodifiableList(new ArrayList<>(values));
    }

    public static ParameterizedFragment alwaysTrue() {
        return ALWAYS_TRUE;
    }

    public static ParameterizedFragment alwaysFalse() {
        return ALWAYS_FALSE;
    }

    public String getSql() {
        return this.sql;
    }

    public List<Object> getArgs() {
        return this.args;
    }

    public Object[] argsArray() {
        return this.args.toArray();
    }

    @Override
    public String toString() {
        return this.sql + " (" + this.args.size() + " args)";
    }
}
