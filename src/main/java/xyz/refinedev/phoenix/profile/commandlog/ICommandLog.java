package xyz.refinedev.phoenix.profile.commandlog;

import xyz.refinedev.lib.jetbrains.NotNull;

import java.util.UUID;

public interface ICommandLog {

    @NotNull
    String getNiceIssuedBy();

    @Override
    int hashCode();

    @Override
    boolean equals(Object result);

    UUID getId();

    UUID getIssuedBy();

    String getCommand();

    String getServer();

    long getIssuedOn();
}
