package xyz.refinedev.phoenix.nametag.line;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public final class LineCondition {

    public static final LineCondition lineCondition = new LineCondition(new ArrayList<>(), "");

    private final List<LineCondition.ConditionResult> conditionResults;

    private final String text;

    private LineCondition(List<LineCondition.ConditionResult> conditionResults, String text) {
        this.conditionResults = conditionResults;
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static LineCondition of(String text) {
        if (text == null) {
            return lineCondition;
        }
        String text2;
        if ((text2 = text.trim()).isEmpty()) {
            return lineCondition;
        }
        ArrayList<xyz.refinedev.phoenix.nametag.line.LineCondition.ConditionResult> values =
                new ArrayList<>();
        int value = 0;
        boolean matches = false;
        while (value < text2.length()) {
            int value2 = text2.indexOf("&&", value);
            int value3 = text2.indexOf("||", value);
            int value4;
            String text3;
            if (!(text3 =
                            ((value4 = min(value2, value3)) < 0
                                            ? text2.substring(value)
                                            : text2.substring(value, value4))
                                    .trim())
                    .isEmpty()) {
                values.add(
                        new LineCondition.ConditionResult(
                                matches, LineCondition.Entry.findEntry(text3)));
            }
            if (value4 < 0) {
                break;
            }
            matches = value4 == value3;
            value = value4 + 2;
        }
        return values.isEmpty() ? lineCondition : new LineCondition(values, text2);
    }

    private static int min(int value, int value2) {
        if (value < 0) {
            return value2;
        } else {
            return value2 < 0 ? value : Math.min(value, value2);
        }
    }

    public boolean isEmpty(ConditionContext conditionContext) {
        if (this.conditionResults.isEmpty()) {
            return true;
        }
        boolean matches =
                LineCondition.Entry.matchesEnabled(
                        LineCondition.ConditionResult.getEntry(this.conditionResults.get(0)),
                        conditionContext);
        for (int index = 1; index < this.conditionResults.size(); index++) {
            LineCondition.ConditionResult conditionResult;
            boolean matches2 =
                    LineCondition.Entry.matchesEnabled(
                            LineCondition.ConditionResult.getEntry(
                                    conditionResult = this.conditionResults.get(index)),
                            conditionContext);
            matches =
                    LineCondition.ConditionResult.isEnabled(conditionResult)
                            ? matches || matches2
                            : matches && matches2;
        }
        return matches;
    }

    public List<String> getList() {
        ArrayList<String> values = new ArrayList<>();
        Iterator<xyz.refinedev.phoenix.nametag.line.LineCondition.ConditionResult> iterator2 =
                this.conditionResults.iterator();
        while (iterator2.hasNext()) {
            LineCondition.ConditionResult conditionResult;
            if (LineCondition.Entry.getKind(
                            LineCondition.ConditionResult.getEntry(
                                    conditionResult =
                                            (LineCondition.ConditionResult) iterator2.next()))
                    == LineCondition.Kind.UNKNOWN) {
                values.add(
                        LineCondition.Entry.getText(
                                LineCondition.ConditionResult.getEntry(conditionResult)));
            }
        }
        return values;
    }

    public boolean isEnabled() {
        Iterator<xyz.refinedev.phoenix.nametag.line.LineCondition.ConditionResult> iterator2 =
                this.conditionResults.iterator();
        while (iterator2.hasNext()) {
            LineCondition.ConditionResult conditionResult;
            if (LineCondition.Entry.getKind(
                                    LineCondition.ConditionResult.getEntry(
                                            conditionResult =
                                                    (LineCondition.ConditionResult)
                                                            iterator2.next()))
                            == LineCondition.Kind.SELF
                    || LineCondition.Entry.getKind(
                                    LineCondition.ConditionResult.getEntry(conditionResult))
                            == LineCondition.Kind.SAME_TEAM) {
                return true;
            }
        }
        return false;
    }

    static final class ConditionResult {

        private final boolean enabled;

        private final LineCondition.Entry entry;

        private ConditionResult(boolean enabled, LineCondition.Entry entry2) {
            this.enabled = enabled;
            this.entry = entry2;
        }

        static LineCondition.Entry getEntry(LineCondition.ConditionResult conditionResult) {
            return conditionResult.entry;
        }

        static boolean isEnabled(LineCondition.ConditionResult conditionResult) {
            return conditionResult.enabled;
        }
    }

    static final class Entry {

        private final LineCondition.Kind kind;

        private final String text;

        private final boolean enabled;

        private Entry(LineCondition.Kind kind2, String text, boolean enabled) {
            this.kind = kind2;
            this.text = text;
            this.enabled = enabled;
        }

        private static LineCondition.Entry of(String text) {
            boolean matches = false;
            String text2;
            for (text2 = text; text2.startsWith("!"); text2 = text2.substring(1).trim()) {
                matches = !matches;
            }
            String text3;
            if ((text3 = text2.toLowerCase(Locale.ROOT)).startsWith("permission:")) {
                return new LineCondition.Entry(
                        LineCondition.Kind.PERMISSION, text2.substring(11).trim(), matches);
            } else if (text3.startsWith("rank>=:")) {
                return new LineCondition.Entry(
                        LineCondition.Kind.RANK_AT_LEAST, text2.substring(7).trim(), matches);
            } else if (text3.startsWith("rank:")) {
                return new LineCondition.Entry(
                        LineCondition.Kind.RANK, text2.substring(5).trim(), matches);
            } else if (text3.startsWith("papi:")) {
                return new LineCondition.Entry(
                        LineCondition.Kind.PAPI, text2.substring(5).trim(), matches);
            } else if (text3.equals("has-prefix")) {
                return new LineCondition.Entry(LineCondition.Kind.HAS_PREFIX, text2, matches);
            } else if (text3.equals("vanished")) {
                return new LineCondition.Entry(LineCondition.Kind.VANISHED, text2, matches);
            } else if (text3.equals("modmode")) {
                return new LineCondition.Entry(LineCondition.Kind.MOD_MODE, text2, matches);
            } else if (text3.equals("self")) {
                return new LineCondition.Entry(LineCondition.Kind.SELF, text2, matches);
            } else {
                return text3.equals("same-team")
                        ? new LineCondition.Entry(LineCondition.Kind.SAME_TEAM, text2, matches)
                        : new LineCondition.Entry(LineCondition.Kind.UNKNOWN, text, matches);
            }
        }

        private boolean matchesEnabled(ConditionContext conditionContext) {
            boolean matches = this.matchesEnabledByKind(conditionContext);
            return this.enabled != matches;
        }

        private boolean matchesEnabledByKind(ConditionContext conditionContext) {
            switch (this.kind) {
                case PERMISSION:
                    return conditionContext.permission(this.text);
                case RANK:
                    return this.text.equalsIgnoreCase(conditionContext.rankName());
                case RANK_AT_LEAST:
                    int value;
                    if ((value = conditionContext.priorityOf(this.text)) == Integer.MAX_VALUE) {
                        return false;
                    }
                    return conditionContext.rankPriority() <= value;
                case HAS_PREFIX:
                    return conditionContext.hasPrefix();
                case VANISHED:
                    return conditionContext.vanished();
                case MOD_MODE:
                    return conditionContext.modMode();
                case SELF:
                    return conditionContext.self();
                case SAME_TEAM:
                    return conditionContext.sameTeam();
                case PAPI:
                    return ConditionParser.matchesEnabled(this.text, conditionContext);
                case UNKNOWN:
                default:
                    return false;
            }
        }

        static LineCondition.Entry findEntry(String text) {
            return of(text);
        }

        static boolean matchesEnabled(
                LineCondition.Entry entry, ConditionContext conditionContext) {
            return entry.matchesEnabled(conditionContext);
        }

        static LineCondition.Kind getKind(LineCondition.Entry entry) {
            return entry.kind;
        }

        static String getText(LineCondition.Entry entry) {
            return entry.text;
        }
    }

    enum Kind {
        PERMISSION,
        RANK,
        RANK_AT_LEAST,
        HAS_PREFIX,
        VANISHED,
        MOD_MODE,
        SELF,
        SAME_TEAM,
        PAPI,
        UNKNOWN;

        private static final LineCondition.Kind[] $VALUES = $values();

        private static LineCondition.Kind[] $values() {
            return new LineCondition.Kind[] {
                PERMISSION,
                RANK,
                RANK_AT_LEAST,
                HAS_PREFIX,
                VANISHED,
                MOD_MODE,
                SELF,
                SAME_TEAM,
                PAPI,
                UNKNOWN
            };
        }
    }
}
