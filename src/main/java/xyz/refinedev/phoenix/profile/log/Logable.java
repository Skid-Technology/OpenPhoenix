package xyz.refinedev.phoenix.profile.log;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.profile.IProfile;

import java.util.UUID;

public class Logable extends Expirable implements ILogable {

    @SerializedName("_id")
    private UUID uuid = UUID.randomUUID();

    @SerializedName("target")
    private UUID target;

    @SerializedName("issuedBy")
    private UUID issuedBy;

    @SerializedName("removedBy")
    private UUID removedBy;

    @SerializedName("issuedOn")
    private String issuedOn;

    @SerializedName("removedOn")
    private String removedOn;

    @SerializedName("reason")
    private String reason;

    @SerializedName("removedReason")
    private String removedReason;

    @Override
    public void remove(UUID uUID, String text) {
        this.setActive(false);
        this.setRemovedBy(uUID);
        this.setRemovedReason(text);
        this.setRemovedAt(System.currentTimeMillis());
        this.setRemovedOn(GlobalConfig.SERVER.NAME);
    }

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

    @NotNull
    @Override
    public String getNiceRemovedBy() {
        IProfile profile;
        return (profile =
                                PlatformGetter.getInstance()
                                        .getProfileHandler()
                                        .getProfile(this.removedBy))
                        == null
                ? this.removedBy.toString()
                : profile.getNameWithColorNoDisguise();
    }

    @NotNull
    @Override
    public String getNiceTarget() {
        IProfile profile;
        return (profile = PlatformGetter.getInstance().getProfileHandler().getProfile(this.target))
                        == null
                ? this.target.toString()
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
            Logable logable = (Logable) result;
            return this.uuid.equals(logable.uuid);
        } else {
            return false;
        }
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public UUID getTarget() {
        return this.target;
    }

    @Override
    public UUID getIssuedBy() {
        return this.issuedBy;
    }

    @Override
    public UUID getRemovedBy() {
        return this.removedBy;
    }

    @Override
    public String getIssuedOn() {
        return this.issuedOn;
    }

    @Override
    public String getRemovedOn() {
        return this.removedOn;
    }

    @Override
    public String getReason() {
        return this.reason;
    }

    @Override
    public String getRemovedReason() {
        return this.removedReason;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void setTarget(UUID target) {
        this.target = target;
    }

    @Override
    public void setIssuedBy(UUID issuedBy) {
        this.issuedBy = issuedBy;
    }

    @Override
    public void setRemovedBy(UUID removedBy) {
        this.removedBy = removedBy;
    }

    @Override
    public void setIssuedOn(String issuedOn) {
        this.issuedOn = issuedOn;
    }

    @Override
    public void setRemovedOn(String removedOn) {
        this.removedOn = removedOn;
    }

    @Override
    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public void setRemovedReason(String removedReason) {
        this.removedReason = removedReason;
    }
}
