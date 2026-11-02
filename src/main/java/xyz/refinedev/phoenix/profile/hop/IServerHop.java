package xyz.refinedev.phoenix.profile.hop;

import xyz.refinedev.lib.jetbrains.Nullable;

import java.util.UUID;

public interface IServerHop {

    UUID getId();

    UUID getTarget();

    @Nullable
    String getFrom();

    String getTo();

    long getTimestamp();
}
