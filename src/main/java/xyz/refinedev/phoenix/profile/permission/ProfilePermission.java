package xyz.refinedev.phoenix.profile.permission;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.rank.scopeable.Scopeable;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.scope.Scope;

import java.util.Collections;
import java.util.List;

public class ProfilePermission extends Scopeable implements IProfilePermission {

    @SerializedName("permission")
    private final String permission;

    @SerializedName("expiration")
    private final long expiration;

    public ProfilePermission(String permission, long value) {
        this.permission = permission;
        this.expiration = value == -5L ? -5L : System.currentTimeMillis() + value;
        this.setScopes(Collections.singletonList(Scope.scopeScope));
    }

    public ProfilePermission(String permission, long expiration, List<IScope> values) {
        this.permission = permission;
        this.expiration = expiration;
        this.setScopes(values);
    }

    @Override
    public boolean isPermanent() {
        return this.expiration == -5L;
    }

    @Override
    public boolean hasExpired() {
        return this.expiration == -5L ? false : System.currentTimeMillis() > this.expiration;
    }

    @Override
    public int hashCode() {
        return this.permission.hashCode();
    }

    @Override
    public boolean equals(Object result) {
        if (this == result) {
            return true;
        } else if (result != null && this.getClass() == result.getClass()) {
            ProfilePermission profilePermission = (ProfilePermission) result;
            return this.permission.equals(profilePermission.permission);
        } else {
            return false;
        }
    }

    @Override
    public String getPermission() {
        return this.permission;
    }

    @Override
    public long getExpiration() {
        return this.expiration;
    }
}
