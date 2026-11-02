package xyz.refinedev.phoenix.profile.chatsnapshot;

import xyz.refinedev.lib.jetbrains.NotNull;

import java.util.List;
import java.util.UUID;

public interface IChatSnapshot {

    @NotNull
    String getNiceRequestedBy();

    String getNiceCreatedOn();

    @Override
    int hashCode();

    @Override
    boolean equals(Object result);

    UUID getUuid();

    UUID getRequestedBy();

    List<IChatEntry> getChat();

    boolean isWholeChat();

    String getNiceId();

    long getCreatedOn();
}
