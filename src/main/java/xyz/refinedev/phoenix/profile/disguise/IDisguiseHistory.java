package xyz.refinedev.phoenix.profile.disguise;

import java.util.UUID;

public interface IDisguiseHistory {

    @Override
    int hashCode();

    @Override
    boolean equals(Object result);

    UUID getId();

    long getDate();

    UUID getPlayer();

    UUID getRankId();

    String getName();

    ISkin getSkin();
}
