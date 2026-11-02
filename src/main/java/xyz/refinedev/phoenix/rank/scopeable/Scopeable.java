package xyz.refinedev.phoenix.rank.scopeable;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.profile.TrackedEntity;
import xyz.refinedev.phoenix.scope.IScope;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Scopeable extends TrackedEntity implements IScopeable {

    @SerializedName("scopes")
    private List<IScope> niceScopes = new ArrayList<>();

    @Override
    public boolean isGlobal() {
        Iterator<IScope> iterator2 = this.niceScopes.iterator();
        while (iterator2.hasNext()) {
            if (((IScope) iterator2.next()).isGlobal()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isScope(String text, Set<String> values) {
        if (this.niceScopes.isEmpty()) {
            return true;
        }
        if (this.isGlobal()) {
            return true;
        }
        if (this.hasServer(text)) {
            return true;
        }
        for (String text2 : values) {
            if (this.hasGroup(text2)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isScope() {
        return this.isScope(GlobalConfig.SERVER.NAME, GlobalConfig.SERVER.GROUPS);
    }

    @Override
    public boolean hasGroup(String text) {
        Iterator<IScope> iterator2 = this.niceScopes.iterator();
        while (iterator2.hasNext()) {
            IScope scope;
            if ((scope = (IScope) iterator2.next()).isGroup()
                    && scope.getGroupName().equalsIgnoreCase(text)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasServer(String text) {
        Iterator<IScope> iterator2 = this.niceScopes.iterator();
        while (iterator2.hasNext()) {
            IScope scope;
            if ((scope = (IScope) iterator2.next()).isServer()
                    && scope.getServerName().equalsIgnoreCase(text)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasScope(IScope scope) {
        Iterator<IScope> iterator2 = this.niceScopes.iterator();
        while (iterator2.hasNext()) {
            if (((IScope) iterator2.next()).getScope().equals(scope.getScope())) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    @Override
    public List<String> getNiceScopes() {
        return IScope.toNiceScopes(this.niceScopes);
    }

    @Override
    public List<IScope> getScopes() {
        return this.niceScopes;
    }

    @Override
    public void setScopes(List<IScope> niceScopes) {
        this.niceScopes = niceScopes;
    }
}
