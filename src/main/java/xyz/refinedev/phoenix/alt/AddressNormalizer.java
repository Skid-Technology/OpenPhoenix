package xyz.refinedev.phoenix.alt;

public final class AddressNormalizer {

    public static final String UNKNOWN_TEXT = "unknown";

    private AddressNormalizer() {}

    public static String formatString(String text) {
        if (text == null || text.isEmpty()) {
            return "unknown";
        }
        if (text.indexOf(58) >= 0) {
            return formatStringByString(text);
        }
        int value;
        return (value = text.lastIndexOf(46)) <= 0 ? text : text.substring(0, value);
    }

    private static String formatStringByString(String text) {
        int value;
        if ((value = text.indexOf("::")) >= 0) {
            String[] texts;
            String text2;
            return (texts =
                                            (text2 = text.substring(0, value)).isEmpty()
                                                    ? new String[0]
                                                    : text2.split(":"))
                                    .length
                            >= 4
                    ? formatString(texts, 4)
                    : text2 + "::";
        } else {
            String[] texts2;
            return (texts2 = text.split(":")).length < 4 ? text : formatString(texts2, 4);
        }
    }

    private static String formatString(String[] texts, int value) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int index = 0; index < value && index < texts.length; index++) {
            if (index > 0) {
                stringBuilder.append(':');
            }
            stringBuilder.append(texts[index]);
        }
        return stringBuilder.toString();
    }
}
