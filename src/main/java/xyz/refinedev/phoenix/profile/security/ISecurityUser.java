package xyz.refinedev.phoenix.profile.security;

import xyz.refinedev.lib.jetbrains.NotNull;

import java.util.UUID;

public interface ISecurityUser {

    @NotNull
    String getNiceIssuedBy();

    @Override
    int hashCode();

    @Override
    boolean equals(Object result);

    UUID getUuid();

    UUID getIssuedBy();

    boolean isAdmin();
}
