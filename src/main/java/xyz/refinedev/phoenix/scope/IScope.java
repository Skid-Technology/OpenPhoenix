package xyz.refinedev.phoenix.scope;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public interface IScope {

    Pattern SCOPE_REGEX = Pattern.compile("g:[A-Za-z0-9-_]+|s:[A-Za-z0-9-_]+|global");

    static boolean isValid(String text) {
        return SCOPE_REGEX.matcher(text).matches();
    }

    static List<String> toNiceScopes(List<IScope> values) {
        ArrayList<String> values2 = new ArrayList<>();
        for (IScope scope : values) {
            values2.add(scope.getDisplayName());
        }
        return values2;
    }

    static boolean isValidMultiple(String text) {
        String[] texts;
        if ((texts = text.split(";")).length == 0) {
            return false;
        }
        String[] texts2 = texts;
        int value = texts.length;
        for (int index = 0; index < value; index++) {
            String text2 = texts2[index];
            if (!SCOPE_REGEX.matcher(text2).matches()) {
                return false;
            }
        }
        return true;
    }

    String getDisplayName();

    boolean isGlobal();

    boolean isServer();

    boolean isGroup();

    String getServerName();

    String getGroupName();

    @Override
    int hashCode();

    @Override
    boolean equals(Object result);

    String getScope();
}
