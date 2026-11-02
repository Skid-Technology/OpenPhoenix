package xyz.refinedev.phoenix.scope;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.utils.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class Scope implements IScope {

    public static final Scope scopeScope = new Scope("global");

    @SerializedName("scope")
    private final String scope;

    public Scope(String text) {
        Preconditions.checkArgument(
                IScope.SCOPE_REGEX.matcher(text).matches(),
                text + " does not follow a valid scope format.");
        this.scope = text.trim().replace(" ", "");
    }

    public static IScope append(String text) {
        return new Scope("s:" + text);
    }

    public static IScope appendByScope(String text) {
        return new Scope("g:" + text);
    }

    public static List<String> findList(List<IScope> values) {
        ArrayList<String> values2 = new ArrayList<>();
        for (IScope scope : values) {
            values2.add(scope.getDisplayName());
        }
        return values2;
    }

    public static List<IScope> findList(String text) {
        text = text.trim().replace(" ", "");
        ArrayList<IScope> values = new ArrayList<>();
        String[] texts;
        int value = (texts = text.split(";")).length;
        for (int index = 0; index < value; index++) {
            String text2 = texts[index];
            values.add(new Scope(text2));
        }
        if (values.isEmpty()) {
            values.add(scopeScope);
        }
        return values;
    }

    public static List<IScope> findListByScopeScope(List<String> values) {
        ArrayList<IScope> values2 = new ArrayList<>();
        for (String text : values) {
            values2.add(new Scope(text));
        }
        if (values2.isEmpty()) {
            values2.add(scopeScope);
        }
        return values2;
    }

    @Override
    public String getDisplayName() {
        if (this.isGlobal()) {
            return "global";
        } else {
            return this.isServer() ? this.getServerName() : this.getGroupName() + " (Group)";
        }
    }

    @Override
    public boolean isGlobal() {
        return this.scope.toLowerCase().contains("global");
    }

    @Override
    public boolean isServer() {
        return this.scope.contains("s:");
    }

    @Override
    public boolean isGroup() {
        return this.scope.contains("g:");
    }

    @Override
    public String getServerName() {
        return this.scope.replace("s:", "");
    }

    @Override
    public String getGroupName() {
        return this.scope.replace("g:", "");
    }

    @Override
    public int hashCode() {
        return this.scope.hashCode();
    }

    @Override
    public boolean equals(Object result) {
        if (this == result) {
            return true;
        } else if (result != null && this.getClass() == result.getClass()) {
            Scope scope2 = (Scope) result;
            return this.scope.equals(scope2.scope);
        } else {
            return false;
        }
    }

    @Override
    public String getScope() {
        return this.scope;
    }
}
