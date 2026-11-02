package xyz.refinedev.phoenix.profile.punishment.banwave;

import xyz.refinedev.phoenix.utils.DateUtils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

public final class BanwaveCommands {

    public static long calculateTime(String text) {
        if (text == null) {
            return 0L;
        }
        String text2;
        if ((text2 = text.trim()).isEmpty()) {
            return 0L;
        }
        String[] texts;
        if ((texts = text2.split("\\s+")).length == 2) {
            DayOfWeek dayOfWeek = findDayOfWeek(texts[0]);
            LocalTime localTime = parse(texts[1]);
            if (dayOfWeek != null && localTime != null) {
                LocalDateTime localDateTime;
                LocalDateTime localDateTime2;
                if (!(localDateTime2 =
                                (localDateTime = LocalDateTime.now())
                                        .with(TemporalAdjusters.nextOrSame(dayOfWeek))
                                        .with(localTime)
                                        .withSecond(0)
                                        .withNano(0))
                        .isAfter(localDateTime)) {
                    localDateTime2 =
                            localDateTime2.with(TemporalAdjusters.next(dayOfWeek)).with(localTime);
                }
                return localDateTime2.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            } else {
                return 0L;
            }
        } else {
            long value;
            return (value = DateUtils.getDuration(text2)) <= 0L
                    ? 0L
                    : System.currentTimeMillis() + value;
        }
    }

    private static DayOfWeek findDayOfWeek(String text) {
        DayOfWeek[] dayOfWeeks;
        int value = (dayOfWeeks = DayOfWeek.values()).length;
        for (int index = 0; index < value; index++) {
            DayOfWeek dayOfWeek;
            if ((dayOfWeek = dayOfWeeks[index]).name().equalsIgnoreCase(text)) {
                return dayOfWeek;
            }
        }
        return null;
    }

    private static LocalTime parse(String text) {
        try {
            return LocalTime.parse(text);
        } catch (Exception exception) {
            return null;
        }
    }

    private BanwaveCommands() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
