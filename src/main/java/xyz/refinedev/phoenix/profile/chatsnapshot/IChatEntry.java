package xyz.refinedev.phoenix.profile.chatsnapshot;

import java.util.UUID;

public interface IChatEntry {

    String getNiceTime();

    @Override
    int hashCode();

    @Override
    boolean equals(Object result);

    UUID getUuid();

    long getTime();

    String getMessage();
}
