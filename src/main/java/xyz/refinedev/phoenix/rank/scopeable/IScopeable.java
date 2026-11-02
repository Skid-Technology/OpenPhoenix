package xyz.refinedev.phoenix.rank.scopeable;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.scope.IScope;

import java.util.List;
import java.util.Set;

public interface IScopeable {

    boolean isGlobal();

    boolean isScope(String text, Set<String> values);

    boolean isScope();

    boolean hasGroup(String text);

    boolean hasServer(String text);

    boolean hasScope(IScope scope);

    @NotNull
    List<String> getNiceScopes();

    List<IScope> getScopes();

    void setScopes(List<IScope> values);
}
