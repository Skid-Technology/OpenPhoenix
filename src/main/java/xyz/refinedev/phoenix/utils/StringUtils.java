package xyz.refinedev.phoenix.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class StringUtils {

    public static String joinCustomSpacer(Collection values, String text) {
        return niceBuilder(values, text, text, "");
    }

    public static String join(Collection values) {
        return niceBuilder(values, ", ", ", ", ".");
    }

    public static String join(Collection values, String text) {
        return niceBuilder(values, text + ", " + text, text + ", " + text, text + ".");
    }

    public static String join(Collection values, String text, String text2) {
        return niceBuilder(values, text + ", " + text2, text + ", " + text2, text + ".");
    }

    public static String niceBuilder(Collection values) {
        return niceBuilder(values, ", ", " and ", ".");
    }

    public static String niceBuilder(Collection values, String text) {
        return niceBuilder(values, text + ", ", text + " and ", text + '.');
    }

    public static String niceBuilder(Collection values, String text, String text2) {
        return niceBuilder(values, text + ", " + text2, text + " and " + text2, text + '.');
    }

    public static String niceBuilderWithAnd(
            Collection values, String text, String text2, String text3) {
        return niceBuilder(values, text + ", " + text2, text + text3 + text2, text + '.');
    }

    public static String niceBuilder(Collection values, String text, String text2, String text3) {
        if (values != null && !values.isEmpty()) {
            ArrayList<String> values2 = new ArrayList<>(values);
            String text4 = null;
            if (values2.size() > 1) {
                text4 = (String) values2.remove(values2.size() - 1);
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (String text5 : values2) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(text);
                }
                stringBuilder.append(text5);
            }
            if (text4 != null) {
                stringBuilder.append(text2).append(text4);
            }
            return stringBuilder.append(text3 != null ? text3 : "").toString();
        } else {
            return "";
        }
    }

    public static long parse(String text) {
        if (text != null && !text.isEmpty()) {
            long value = 0L;
            StringBuilder stringBuilder = new StringBuilder();
            for (int index = 0; index < text.length(); index++) {
                char value2;
                if (Character.isDigit(value2 = text.charAt(index))) {
                    stringBuilder.append(value2);
                } else {
                    String text2;
                    if (Character.isLetter(value2)
                            && !(text2 = stringBuilder.toString()).isEmpty()) {
                        value += convert(Integer.parseInt(text2), value2);
                        stringBuilder = new StringBuilder();
                    }
                }
            }
            return value;
        } else {
            return -1L;
        }
    }

    private static long convert(int value, char value2) {
        switch (value2) {
            case 'M':
                return value * TimeUnit.DAYS.toMillis(30L);
            case 'd':
                return value * TimeUnit.DAYS.toMillis(1L);
            case 'h':
                return value * TimeUnit.HOURS.toMillis(1L);
            case 'm':
                return value * TimeUnit.MINUTES.toMillis(1L);
            case 's':
                return value * TimeUnit.SECONDS.toMillis(1L);
            case 'y':
                return value * TimeUnit.DAYS.toMillis(365L);
            default:
                return -1L;
        }
    }

    public static String formatInt(int value) {
        int value2;
        int value3 = (value2 = value * 1000) / 1000 % 60;
        int value4 = value2 / 60000 % 60;
        int value5 = value2 / 3600000 % 24;
        return (value5 > 0 ? value5 + ":" : "")
                + (value4 < 10 ? "0" + value4 : value4)
                + ":"
                + (value3 < 10 ? "0" + value3 : value3);
    }

    public static Object getTime(int value) {
        if (value < 60) {
            return value + "seconds";
        }
        int value2 = value / 60;
        int value3 = 60 * value2;
        int value4 = value - value3;
        if (value2 < 60) {
            return value4 > 0 ? value2 + "minutes " + value4 + "seconds" : value2 + "minutes";
        }
        if (value2 < 1440) {
            int value5 = value2 / 60;
            String text = value5 + "hours";
            int value6 = 60 * value5;
            int value7;
            if ((value7 = value2 - value6) >= 1) {
                text = text + " " + value7 + "minutes";
            }
            if (value4 > 0) {
                text = text + " " + value4 + "seconds";
            }
            return text;
        } else {
            int value8 = value2 / 1440;
            String text2 = value8 + "days";
            int value9 = 1440 * value8;
            int value10;
            if ((value10 = value2 - value9) >= 1) {
                if (value10 < 60) {
                    text2 = text2 + " " + value10 + "minutes";
                } else {
                    int value11 = value10 / 60;
                    text2 = text2 + " " + value11 + "hours";
                    int value12 = 60 * value11;
                    int value13 = value10 - value12;
                    if (value10 >= 1) {
                        text2 = text2 + " " + value13 + "minutes";
                    }
                }
            }
            if (value4 > 0) {
                text2 = text2 + " " + value4 + "seconds";
            }
            return text2;
        }
    }

    public static String getStringFromList(Set<String> values) {
        if (values.size() == 0) {
            return "Empty";
        }
        StringBuilder stringBuilder = new StringBuilder();
        int value = 0;
        for (String text : values) {
            stringBuilder.append(text.replace(",", "#%&$"));
            if (value < values.size() - 1) {
                stringBuilder.append(", ");
            }
            value++;
        }
        return stringBuilder.toString();
    }

    public static String getStringFromList(List<String> values) {
        if (values.size() == 0) {
            return "Empty";
        }
        StringBuilder stringBuilder = new StringBuilder();
        int value = 0;
        for (String text : values) {
            stringBuilder.append(text.replace(",", "#%&$"));
            if (value < values.size() - 1) {
                stringBuilder.append(", ");
            }
            value++;
        }
        return stringBuilder.toString();
    }

    public static List<String> getListFromString(String text) {
        return text.equals("Empty")
                ? new ArrayList<>()
                : FastStreams.mapArrayList(
                        Arrays.asList(text.split(", ")), result -> result.replace("#%&$", ","));
    }

    public static String convertFirstUpperCase(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
