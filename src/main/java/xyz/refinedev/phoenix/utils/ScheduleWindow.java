package xyz.refinedev.phoenix.utils;

import java.time.LocalDate;

public final class ScheduleWindow {

    private ScheduleWindow() {}

    public static boolean isActive(String text, String text2) {
        return isActive(text, text2, LocalDate.now());
    }

    public static boolean isActive(String text, String text2, LocalDate localDate) {
        if (!isBlank(text) && !isBlank(text2)) {
            String text3 = text.trim();
            String text4 = text2.trim();
            try {
                if (text3.length() == 5 && text4.length() == 5) {
                    int value = recurringKey(text3);
                    int value2 = recurringKey(text4);
                    int value3 = localDate.getMonthValue() * 100 + localDate.getDayOfMonth();
                    return value <= value2
                            ? value3 >= value && value3 <= value2
                            : value3 >= value || value3 <= value2;
                } else {
                    LocalDate localDate2 = LocalDate.parse(text3);
                    LocalDate localDate3 = LocalDate.parse(text4);
                    return !localDate.isBefore(localDate2) && !localDate.isAfter(localDate3);
                }
            } catch (Exception exception) {
                return true;
            }
        } else {
            return true;
        }
    }

    public static boolean isValid(String text) {
        if (isBlank(text)) {
            return false;
        }
        String text2 = text.trim();
        try {
            if (text2.length() == 5) {
                recurringKey(text2);
                return true;
            } else {
                LocalDate.parse(text2);
                return true;
            }
        } catch (Exception exception) {
            return false;
        }
    }

    private static int recurringKey(String text) {
        String[] texts;
        if ((texts = text.split("-")).length != 2) {
            throw new IllegalArgumentException("Not MM-dd: " + text);
        } else {
            int value = Integer.parseInt(texts[0]);
            int value2 = Integer.parseInt(texts[1]);
            if (value >= 1 && value <= 12 && value2 >= 1 && value2 <= 31) {
                return value * 100 + value2;
            } else {
                throw new IllegalArgumentException("Not a real date: " + text);
            }
        }
    }

    private static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }
}
