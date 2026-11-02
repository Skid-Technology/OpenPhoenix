package xyz.refinedev.phoenix.profile.notes;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.profile.IProfile;

import java.util.UUID;

public class Note implements INote {

    @SerializedName("uuid")
    private UUID uuid = UUID.randomUUID();

    @SerializedName("issuedBy")
    private UUID issuedBy;

    @SerializedName("issuedAt")
    private long issuedAt;

    @SerializedName("note")
    private String note;

    @NotNull
    @Override
    public String getNiceIssuedBy() {
        IProfile profile;
        return (profile =
                                PlatformGetter.getInstance()
                                        .getProfileHandler()
                                        .getProfile(this.issuedBy))
                        == null
                ? this.issuedBy.toString()
                : profile.getNameWithColorNoDisguise();
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }

    @Override
    public boolean equals(Object result) {
        if (this == result) {
            return true;
        } else if (result != null && this.getClass() == result.getClass()) {
            Note note = (Note) result;
            return this.uuid.equals(note.uuid);
        } else {
            return false;
        }
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public UUID getIssuedBy() {
        return this.issuedBy;
    }

    @Override
    public long getIssuedAt() {
        return this.issuedAt;
    }

    @Override
    public String getNote() {
        return this.note;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void setIssuedBy(UUID issuedBy) {
        this.issuedBy = issuedBy;
    }

    @Override
    public void setIssuedAt(long issuedAt) {
        this.issuedAt = issuedAt;
    }

    @Override
    public void setNote(String note) {
        this.note = note;
    }
}
