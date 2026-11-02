package xyz.refinedev.phoenix.rank.permission;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.rank.scopeable.Scopeable;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.scope.Scope;

import java.util.Collections;
import java.util.List;

public class Permission extends Scopeable implements IPermission {

    @SerializedName("permission")
    private final String permission;

    public Permission(String permission) {
        this.permission = permission;
        this.setScopes(Collections.singletonList(Scope.scopeScope));
    }

    public Permission(String permission, List<IScope> values) {
        this.permission = permission;
        this.setScopes(values);
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
            Permission permission2 = (Permission) result;
            return this.permission.equals(permission2.permission);
        } else {
            return false;
        }
    }

    @Override
    public String getPermission() {
        return this.permission;
    }
}
