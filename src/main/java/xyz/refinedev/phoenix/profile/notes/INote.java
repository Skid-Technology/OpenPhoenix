package xyz.refinedev.phoenix.profile.notes;

import xyz.refinedev.lib.jetbrains.NotNull;

import java.util.UUID;

public interface INote {

    @NotNull
    String getNiceIssuedBy();

    @Override
    int hashCode();

    @Override
    boolean equals(Object result);

    UUID getUuid();

    UUID getIssuedBy();

    long getIssuedAt();

    String getNote();

    void setUuid(UUID uUID);

    void setIssuedBy(UUID uUID);

    void setIssuedAt(long value);

    void setNote(String text);
}
