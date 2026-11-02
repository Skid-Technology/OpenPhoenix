package xyz.refinedev.phoenix.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DateUtils {

    @GsonExclude
    public static String isoCompactPattern =
            "e5c8e6adbd8930c39bc753e3616aa03b810f30750b1303d00ab835a1683d65ed";

    private static final Pattern timePattern =
            Pattern.compile(
                    "(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?",
                    2);

    private static SimpleDateFormat dateFormat;

    public static final long INVALID = -5L;

    public static final long MALFORMED = -3L;

    public static final long PERMANENT = -5L;

    public static long getDuration(String text) {
        if (Character.isLetter(text.toLowerCase().charAt(0))) {
            return -5L;
        }
        long value = 0L;
        StringBuilder stringBuilder = new StringBuilder();
        for (int index = 0; index < text.length(); index++) {
            char value2;
            if (Character.isDigit(value2 = text.charAt(index))) {
                stringBuilder.append(value2);
            } else {
                String text2 = stringBuilder.toString();
                if (Character.isLetter(value2) && !text2.isEmpty()) {
                    value += convert(Integer.parseInt(text2), value2);
                    stringBuilder = new StringBuilder();
                }
            }
        }
        return value;
    }

    static long convert(int value, char value2) {
        switch (value2) {
            case 'D':
            case 'd':
                return value * TimeUnit.DAYS.toMillis(1L);
            case 'H':
            case 'h':
                return value * TimeUnit.HOURS.toMillis(1L);
            case 'M':
                return value * TimeUnit.DAYS.toMillis(30L);
            case 'S':
            case 's':
                return value * TimeUnit.SECONDS.toMillis(1L);
            case 'W':
            case 'w':
                return value * TimeUnit.DAYS.toMillis(7L);
            case 'Y':
            case 'y':
                return value * TimeUnit.DAYS.toMillis(365L);
            case 'm':
                return value * TimeUnit.MINUTES.toMillis(1L);
            default:
                return -1L;
        }
    }

    public static String formatTimeMillis(long value) {
        long value2;
        if ((value2 = value / 1000L) <= 0L) {
            return "0s";
        }
        long value3 = value2 / 60L;
        value2 %= 60L;
        long value4 = value3 / 60L;
        value3 %= 60L;
        long value5 = value4 / 24L;
        value4 %= 24L;
        long value6 = value5 / 365L;
        value5 %= 365L;
        StringBuilder stringBuilder = new StringBuilder();
        if (value6 != 0L) {
            stringBuilder.append(value6).append("y ");
        }
        if (value5 != 0L) {
            stringBuilder.append(value5).append("d ");
        }
        if (value4 != 0L) {
            stringBuilder.append(value4).append("h ");
        }
        if (value3 != 0L) {
            stringBuilder.append(value3).append("m ");
        }
        if (value2 != 0L) {
            stringBuilder.append(value2).append("s ");
        }
        return stringBuilder.toString().trim();
    }

    public static long parseTime(String text) {
        long value = 0L;
        boolean matches = false;
        Matcher matcher2 = Pattern.compile("\\d+\\D+").matcher(text);
        while (matcher2.find()) {
            String text2;
            Long value2 =
                    Long.parseLong(
                            (text2 = matcher2.group()).split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
            switch (text2.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1]) {
                case "s":
                    value += value2;
                    matches = true;
                    break;
                case "m":
                    value += value2 * 60L;
                    matches = true;
                    break;
                case "h":
                    value += value2 * 60L * 60L;
                    matches = true;
                    break;
                case "d":
                    value += value2 * 60L * 60L * 24L;
                    matches = true;
                    break;
                case "w":
                    value += value2 * 60L * 60L * 24L * 7L;
                    matches = true;
                    break;
                case "mo":
                    value += value2 * 60L * 60L * 24L * 30L;
                    matches = true;
                    break;
                case "y":
                    value += value2 * 60L * 60L * 24L * 365L;
                    matches = true;
            }
        }
        return matches ? value * 1000L + 1000L : -1L;
    }

    public static String getDate(long value) {
        return dateFormat.format(new Date(value));
    }

    public static String formatHourRange(int value) {
        return value >= 0 && value <= 23
                ? String.format("%02d:00 - %02d:00", value, (value + 1) % 24)
                : "N/A";
    }

    public static int dateDiff(int value, Calendar calendar, Calendar calendar2, boolean matches) {
        int value2 = 0;
        long timeInMillis = calendar.getTimeInMillis();
        while (matches && !calendar.after(calendar2) || !matches && !calendar.before(calendar2)) {
            timeInMillis = calendar.getTimeInMillis();
            calendar.add(value, matches ? 1 : -1);
            value2++;
        }
        calendar.setTimeInMillis(timeInMillis);
        return value2 - 1;
    }

    public static String formatDateDiff(long value) {
        GregorianCalendar gregorianCalendar;
        (gregorianCalendar = new GregorianCalendar()).setTimeInMillis(value);
        return formatDateDiff(new GregorianCalendar(), gregorianCalendar);
    }

    public static String formatDateDiff(Calendar calendar, Calendar calendar2) {
        boolean matches = false;
        if (calendar2.equals(calendar)) {
            return "now";
        }
        if (calendar2.after(calendar)) {
            matches = true;
        }
        StringBuilder stringBuilder = new StringBuilder();
        int[] values = new int[] {1, 2, 5, 11, 12, 13};
        String[] texts = new String[] {"y", "y", "M", "M", "d", "d", "h", "h", "m", "m", "s", "s"};
        int value = 0;
        for (int index = 0; index < values.length && value <= 2; index++) {
            int value2;
            if ((value2 = dateDiff(values[index], calendar, calendar2, matches)) > 0) {
                value++;
                stringBuilder
                        .append(" ")
                        .append(value2)
                        .append(texts[index * 2 + (value2 > 1 ? 1 : 0)]);
            }
        }
        return stringBuilder.length() == 0 ? "now" : stringBuilder.toString().trim();
    }

    public static String formatSimplifiedDateDiff(long value) {
        GregorianCalendar gregorianCalendar;
        (gregorianCalendar = new GregorianCalendar()).setTimeInMillis(value);
        return formatSimplifiedDateDiff(new GregorianCalendar(), gregorianCalendar);
    }

    public static String formatSimplifiedDateDiff(Calendar calendar, Calendar calendar2) {
        boolean matches = false;
        if (calendar2.equals(calendar)) {
            return "now";
        }
        if (calendar2.after(calendar)) {
            matches = true;
        }
        StringBuilder stringBuilder = new StringBuilder();
        int[] values = new int[] {1, 2, 5, 11, 12, 13};
        String[] texts = new String[] {"y", "y", "m", "m", "d", "d", "h", "h", "m", "m", "s", "s"};
        int value = 0;
        for (int index = 0; index < values.length && value <= 2; index++) {
            int value2;
            if ((value2 = dateDiff(values[index], calendar, calendar2, matches)) > 0) {
                value++;
                stringBuilder
                        .append(" ")
                        .append(value2)
                        .append(texts[index * 2 + (value2 > 1 ? 1 : 0)]);
            }
        }
        return stringBuilder.length() == 0 ? "now" : stringBuilder.toString().trim();
    }

    public static String readableTime(BigDecimal bigDecimal) {
        String text = "";
        if (bigDecimal.doubleValue() <= 60.0) {
            bigDecimal = bigDecimal.add(BigDecimal.valueOf(0.1));
            return text + " " + bigDecimal + "s";
        } else if (bigDecimal.doubleValue() <= 3600.0) {
            int value = bigDecimal.intValue() / 60;
            int value2 = bigDecimal.intValue() % 60;
            DecimalFormat decimalFormat = new DecimalFormat("00");
            return text
                    + " "
                    + decimalFormat.format(value)
                    + ":"
                    + decimalFormat.format(value2)
                    + "m";
        } else {
            return null;
        }
    }

    private DateUtils() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }

    public static void setDateFormat(SimpleDateFormat simpleDateFormat) {
        dateFormat = simpleDateFormat;
    }
}
