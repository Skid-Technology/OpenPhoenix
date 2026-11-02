package xyz.refinedev.phoenix.nametag.line;

public final class ConditionParser {

    private static final String[] strings = new String[] {"<=", ">=", "==", "!=", "<", ">"};

    private ConditionParser() {}

    static boolean matchesEnabled(String text, ConditionContext conditionContext) {
        if (text != null && !text.trim().isEmpty()) {
            String[] texts = strings;
            int value = strings.length;
            for (int index = 0; index < value; index++) {
                String text2 = texts[index];
                int value2;
                if ((value2 = text.indexOf(text2)) >= 0) {
                    String text3 = conditionContext.placeholders(text.substring(0, value2).trim());
                    String text4 =
                            conditionContext.placeholders(
                                    text.substring(value2 + text2.length()).trim());
                    Double empty = isEmpty(text3);
                    Double empty2 = isEmpty(text4);
                    if (empty != null && empty2 != null) {
                        return hashCode(empty.compareTo(empty2), text2);
                    }
                    if ("==".equals(text2)) {
                        return text3.equalsIgnoreCase(text4);
                    }
                    if ("!=".equals(text2)) {
                        return !text3.equalsIgnoreCase(text4);
                    }
                    return false;
                }
            }
            String text5;
            return !(text5 = conditionContext.placeholders(text).trim()).isEmpty()
                    && !text5.equalsIgnoreCase("false")
                    && !text5.equals("0");
        } else {
            return false;
        }
    }

    private static boolean hashCode(int value, String text) {
        switch (text) {
            case "<":
                return value < 0;
            case ">":
                return value > 0;
            case "<=":
                return value <= 0;
            case ">=":
                return value >= 0;
            case "==":
                return value == 0;
            case "!=":
                return value != 0;
            default:
                return false;
        }
    }

    private static Double isEmpty(String text) {
        if (text != null && !text.isEmpty()) {
            try {
                return Double.valueOf(text);
            } catch (NumberFormatException numberFormatException) {
                return null;
            }
        } else {
            return null;
        }
    }
}
