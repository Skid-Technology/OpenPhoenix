package xyz.refinedev.phoenix.profile.permission;

import xyz.refinedev.phoenix.rank.scopeable.IScopeable;

public interface IProfilePermission extends IScopeable {

    boolean isPermanent();

    boolean hasExpired();

    @Override
    int hashCode();

    @Override
    boolean equals(Object result);

    String getPermission();

    long getExpiration();
}
