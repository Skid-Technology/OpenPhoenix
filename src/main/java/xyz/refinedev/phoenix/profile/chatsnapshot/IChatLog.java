package xyz.refinedev.phoenix.profile.chatsnapshot;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;

import java.util.UUID;

public interface IChatLog {

    @NotNull
    String getNiceSender();

    String getNiceCreatedOn();

    static String getRandomSaltedString(int value) {
        return IPunishment.getRandomSaltedString(value);
    }

    @Override
    int hashCode();

    @Override
    boolean equals(Object result);

    String getId();

    long getTimestamp();

    UUID getSender();

    String getMessage();
}
