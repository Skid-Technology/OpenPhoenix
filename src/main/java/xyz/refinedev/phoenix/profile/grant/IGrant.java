package xyz.refinedev.phoenix.profile.grant;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.profile.log.ILogable;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.utils.Exclude;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IGrant extends ILogable {

    @Exclude GrantComparator COMPARATOR = new GrantComparator();

    @Override
    boolean isActive();

    boolean isActiveOnOneScope();

    boolean isScope(String text, Set<String> values);

    boolean isScope();

    boolean isGlobal();

    boolean hasGroup(String text);

    boolean hasServer(String text);

    @NotNull
    List<String> getNiceActiveScopes();

    @NotNull
    List<IScope> getActiveScopes();

    boolean hasScope(IScope scope);

    @NotNull
    List<String> getNiceScopes();

    long getRemainingDuration();

    @Nullable
    IRank getRank();

    UUID getRankId();

    List<IScope> getScopes();

    void setRankId(UUID uUID);

    void setScopes(List<IScope> values);
}
