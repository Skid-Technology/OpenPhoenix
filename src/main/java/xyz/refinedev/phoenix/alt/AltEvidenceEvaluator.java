package xyz.refinedev.phoenix.alt;

import xyz.refinedev.phoenix.profile.login.ILogin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class AltEvidenceEvaluator {

    private static final long time = 24L;

    private AltEvidenceEvaluator() {}

    public static int calculateCount(List<ILogin> values, List<ILogin> values2, long value) {
        return calculateCountByCount(values, values2, value)
                + calculateCountByCount(values2, values, value);
    }

    private static int calculateCountByCount(
            List<ILogin> values, List<ILogin> values2, long value) {
        long[] values3 = new long[values.size()];
        int value2 = 0;
        Iterator<ILogin> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            ILogin login;
            if ((login = (ILogin) iterator2.next()).hasLoggedOut()) {
                values3[value2++] = login.getLogout();
            }
        }
        long[] values4 = new long[values2.size()];
        for (int index = 0; index < values2.size(); index++) {
            values4[index] = ((ILogin) values2.get(index)).getLogin();
        }
        Arrays.sort(values3, 0, value2);
        Arrays.sort(values4);
        int value3 = 0;
        int value4 = 0;
        for (int index2 = 0; index2 < value2; index2++) {
            while (value4 < values4.length && values4[value4] < values3[index2]) {
                value4++;
            }
            if (value4 < values4.length && values4[value4] - values3[index2] <= value) {
                value3++;
            }
        }
        return value3;
    }

    public static int calculateCountByList(List<ILogin> values, List<ILogin> values2, long value) {
        long[] values3 = new long[values2.size()];
        long[] values4 = new long[values2.size()];
        for (int index = 0; index < values2.size(); index++) {
            ILogin login = (ILogin) values2.get(index);
            values3[index] = login.getLogin();
            values4[index] = login.hasLoggedOut() ? login.getLogout() : value;
        }
        Arrays.sort(values3);
        Arrays.sort(values4);
        int value2 = 0;
        Iterator<ILogin> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            ILogin login2;
            long login3 = (login2 = (ILogin) iterator2.next()).getLogin();
            long value3 = login2.hasLoggedOut() ? login2.getLogout() : value;
            value2 += calculateCount(values3, value3) - calculateCountByCount(values4, login3);
        }
        return value2;
    }

    private static int calculateCount(long[] values, long value) {
        int value2 = 0;
        int value3 = values.length;
        while (value2 < value3) {
            int value4 = value2 + value3 >>> 1;
            if (values[value4] < value) {
                value2 = value4 + 1;
            } else {
                value3 = value4;
            }
        }
        return value2;
    }

    private static int calculateCountByCount(long[] values, long value) {
        int value2 = 0;
        int value3 = values.length;
        while (value2 < value3) {
            int value4 = value2 + value3 >>> 1;
            if (values[value4] <= value) {
                value2 = value4 + 1;
            } else {
                value3 = value4;
            }
        }
        return value2;
    }

    public static double calculateValue(List<ILogin> values, List<ILogin> values2) {
        double[] values3 = findValueArray(values);
        double[] values4 = findValueArray(values2);
        return calculateValue(values3, values4);
    }

    private static double[] findValueArray(List<ILogin> values) {
        double[] values2 = new double[24];
        Iterator<ILogin> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            int value;
            if ((value =
                            (int)
                                    (((ILogin) iterator2.next()).getLogin()
                                            / TimeUnit.HOURS.toMillis(1L)
                                            % time))
                    < 0) {
                value += 24;
            }
            values2[value]++;
        }
        return values2;
    }

    private static double calculateValue(double[] values, double[] values2) {
        int value;
        if ((value = Math.min(values.length, values2.length)) == 0) {
            return 0.0;
        }
        double value2 = calculateValue(values);
        double value3 = calculateValue(values2);
        double value4 = 0.0;
        double value5 = 0.0;
        double value6 = 0.0;
        for (int index = 0; index < value; index++) {
            double value7 = values[index] - value2;
            double value8 = values2[index] - value3;
            value4 += value7 * value8;
            value5 += value7 * value7;
            value6 += value8 * value8;
        }
        return !(value5 <= 0.0) && !(value6 <= 0.0) ? value4 / Math.sqrt(value5 * value6) : 0.0;
    }

    private static double calculateValue(double[] values) {
        double value = 0.0;
        double[] values2 = values;
        int value2 = values.length;
        for (int index = 0; index < value2; index++) {
            double value3 = values2[index];
            value += value3;
        }
        return values.length == 0 ? 0.0 : value / values.length;
    }

    public static int calculateCount(List<Integer> values) {
        if (values != null && !values.isEmpty()) {
            ArrayList values2;
            Collections.sort(values2 = new ArrayList(values));
            return (Integer) values2.get(values2.size() / 2);
        } else {
            return 0;
        }
    }

    public static double calculateValue(String text, String text2) {
        if (text != null && text2 != null) {
            String text3 = formatString(text.toLowerCase());
            String text4 = formatString(text2.toLowerCase());
            if (text3.isEmpty() || text4.isEmpty()) {
                return 0.0;
            } else if (text3.equals(text4)) {
                return 1.0;
            } else {
                double value = max(text3, text4);
                int value2;
                if ((value2 = calculateCountByCount(text3, text4)) >= 4) {
                    double value3 = (double) value2 / Math.min(text3.length(), text4.length());
                    return Math.max(value, value3);
                } else {
                    return value;
                }
            }
        } else {
            return 0.0;
        }
    }

    private static String formatString(String text) {
        int value = 0;
        while (value < text.length()
                && !Character.isLetterOrDigit(text.charAt(value))
                && text.charAt(value) != '_') {
            value++;
        }
        return value == 0 ? text : text.substring(value);
    }

    private static double max(String text, String text2) {
        int value;
        return (value = Math.max(text.length(), text2.length())) == 0
                ? 1.0
                : 1.0 - (double) calculateCount(text, text2) / value;
    }

    private static int calculateCount(String text, String text2) {
        int[] values = new int[text2.length() + 1];
        int[] values2 = new int[text2.length() + 1];
        int value = 0;
        while (value <= text2.length()) {
            values[value] = value++;
        }
        for (int index = 1; index <= text.length(); index++) {
            values2[0] = index;
            for (int index2 = 1; index2 <= text2.length(); index2++) {
                int value2 =
                        values[index2 - 1]
                                + (text.charAt(index - 1) == text2.charAt(index2 - 1) ? 0 : 1);
                values2[index2] =
                        Math.min(Math.min(values2[index2 - 1] + 1, values[index2] + 1), value2);
            }
            int[] values3 = values;
            values = values2;
            values2 = values3;
        }
        return values[text2.length()];
    }

    private static int calculateCountByCount(String text, String text2) {
        int[] values = new int[text2.length() + 1];
        int[] values2 = new int[text2.length() + 1];
        int value = 0;
        for (int index = 1; index <= text.length(); index++) {
            for (int index2 = 1; index2 <= text2.length(); index2++) {
                if (text.charAt(index - 1) == text2.charAt(index2 - 1)) {
                    values2[index2] = values[index2 - 1] + 1;
                    value = Math.max(value, values2[index2]);
                } else {
                    values2[index2] = 0;
                }
            }
            int[] values3 = values;
            values = values2;
            values2 = values3;
            Arrays.fill(values3, 0);
        }
        return value;
    }
}
