package xyz.refinedev.phoenix.profile.grant;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.profile.log.Logable;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.scope.Scope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Grant extends Logable implements IGrant {

    @SerializedName("rankId")
    private UUID rankId;

    @SerializedName("scopes")
    private List<IScope> niceScopes = new ArrayList<>();

    @Override
    public boolean isActive() {
        return !this.isScope() ? false : this.isActiveOnOneScope();
    }

    @Override
    public boolean isActiveOnOneScope() {
        return this.getRank() == null ? false : super.isActive();
    }

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

    @NotNull
    @Override
    public List<String> getNiceActiveScopes() {
        ArrayList<String> values = new ArrayList<>();
        for (IScope scope : this.getActiveScopes()) {
            values.add(scope.getDisplayName());
        }
        return values;
    }

    @NotNull
    @Override
    public List<IScope> getActiveScopes() {
        IRank rank;
        if ((rank = this.getRank()) == null) {
            return Collections.emptyList();
        }
        if (rank.isGlobal() && this.isGlobal()) {
            return Collections.singletonList(Scope.scopeScope);
        }
        if (rank.isGlobal() && !this.isGlobal()) {
            return this.getScopes();
        }
        if (!rank.isGlobal() && this.isGlobal()) {
            return rank.getScopes();
        }
        ArrayList<IScope> values = new ArrayList<>();
        for (IScope scope : this.niceScopes) {
            if (rank.hasScope(scope)) {
                values.add(scope);
            }
        }
        return values;
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
        return Scope.findList(this.niceScopes);
    }

    @Override
    public long getRemainingDuration() {
        return this.getIssuedAt() + this.getDuration() - System.currentTimeMillis();
    }

    @Nullable
    @Override
    public IRank getRank() {
        return PlatformGetter.getInstance().getRankHandler().getRank(this.rankId);
    }

    @Override
    public UUID getRankId() {
        return this.rankId;
    }

    @Override
    public List<IScope> getScopes() {
        return this.niceScopes;
    }

    @Override
    public void setRankId(UUID rankId) {
        this.rankId = rankId;
    }

    @Override
    public void setScopes(List<IScope> niceScopes) {
        this.niceScopes = niceScopes;
    }
}
