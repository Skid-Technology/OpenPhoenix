package xyz.refinedev.phoenix.scope;

import java.util.List;

public class ScopeSet {

    private final List<IScope> scopes;

    public List<IScope> getScopes() {
        return this.scopes;
    }

    public ScopeSet(List<IScope> scopes) {
        this.scopes = scopes;
    }
}
