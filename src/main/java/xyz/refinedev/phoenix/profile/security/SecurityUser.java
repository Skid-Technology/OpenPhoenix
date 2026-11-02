package xyz.refinedev.phoenix.profile.security;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.TrackedEntity;

import java.util.UUID;

public class SecurityUser extends TrackedEntity implements ISecurityUser {

    @SerializedName("_id")
    private final UUID uuid;

    @SerializedName("issuedBy")
    private final UUID issuedBy;

    @SerializedName("admin")
    private final boolean admin;

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
            SecurityUser securityUser = (SecurityUser) result;
            return this.uuid.equals(securityUser.uuid);
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
    public boolean isAdmin() {
        return this.admin;
    }

    public SecurityUser(UUID uuid, UUID issuedBy, boolean admin) {
        this.uuid = uuid;
        this.issuedBy = issuedBy;
        this.admin = admin;
    }
}
