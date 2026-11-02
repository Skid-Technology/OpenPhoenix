package xyz.refinedev.phoenix.rank.permission;

import xyz.refinedev.phoenix.rank.scopeable.IScopeable;

public interface IPermission extends IScopeable {

    @Override
    int hashCode();

    @Override
    boolean equals(Object result);

    String getPermission();
}
