package xyz.refinedev.phoenix.profile.log;

import xyz.refinedev.lib.jetbrains.NotNull;

import java.util.UUID;

public interface ILogable extends IExpirable {

    @NotNull
    String getNiceIssuedBy();

    @NotNull
    String getNiceRemovedBy();

    @NotNull
    String getNiceTarget();

    @Override
    int hashCode();

    @Override
    boolean equals(Object result);

    UUID getUuid();

    UUID getTarget();

    UUID getIssuedBy();

    UUID getRemovedBy();

    String getIssuedOn();

    String getRemovedOn();

    String getReason();

    String getRemovedReason();

    void setUuid(UUID uUID);

    void setTarget(UUID uUID);

    void setIssuedBy(UUID uUID);

    void setRemovedBy(UUID uUID);

    void setIssuedOn(String text);

    void setRemovedOn(String text);

    void setReason(String text);

    void setRemovedReason(String text);

    void remove(UUID uUID, String text);
}
