package xyz.refinedev.phoenix.scope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class ScopeParser {

    public static final String CUSTOM_TEXT = "CUSTOM";

    public static final Set<String> strings;

    private static final String text = ";";

    public static ScopeExpression create(String text) {
        if (text == null) {
            return null;
        }
        String text2;
        if ((text2 = text.trim()).isEmpty()) {
            return null;
        }
        String[] texts;
        if ((texts = text2.split("\\|", 3)).length < 2) {
            return null;
        }
        String text3 = texts[0].trim().toUpperCase();
        if ("CUSTOM".equals(text3)) {
            if (texts.length != 3) {
                return null;
            }
            String text4 = texts[1].trim();
            String text5 = texts[2].trim();
            return !text4.isEmpty() && !text5.isEmpty()
                    ? new ScopeExpression(text3, text4, text5)
                    : null;
        } else {
            if (!strings.contains(text3)) {
                return null;
            }
            String text6;
            return (text6 = (texts.length == 3 ? texts[2] : texts[1]).trim()).isEmpty()
                    ? null
                    : new ScopeExpression(text3, null, text6);
        }
    }

    public static List<ScopeExpression> findList(List<String> values) {
        ArrayList<ScopeExpression> values2 = new ArrayList<>();
        if (values == null) {
            return values2;
        }
        Iterator<String> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            ScopeExpression scopeExpression;
            if ((scopeExpression = create((String) iterator2.next())) != null) {
                values2.add(scopeExpression);
            }
        }
        return values2;
    }

    public static List<ScopeExpression> findList(String text) {
        return text != null && !text.trim().isEmpty()
                ? findList(Arrays.asList(text.split(";")))
                : new ArrayList<>();
    }

    public static List<ScopeExpression> addAll(
            List<ScopeExpression> values, List<ScopeExpression> values2) {
        ArrayList<ScopeExpression> values3 = new ArrayList<>();
        if (values != null) {
            values3.addAll(values);
        }
        if (values2 != null) {
            values3.addAll(values2);
        }
        return values3;
    }

    private ScopeParser() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }

    static {
        String[] texts = new String[10];
        texts[0] = "BUG_REPORT";
        texts[1] = "COMMUNITY_GUIDELINES";
        texts[2] = "SUPPORT";
        texts[3] = "STATUS";
        texts[4] = "FEEDBACK";
        texts[5] = "COMMUNITY";
        texts[6] = "WEBSITE";
        texts[7] = "FORUMS";
        texts[8] = "NEWS";
        texts[9] = "ANNOUNCEMENTS";
        strings = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(texts)));
    }
}
