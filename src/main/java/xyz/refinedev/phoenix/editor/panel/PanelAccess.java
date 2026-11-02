package xyz.refinedev.phoenix.editor.panel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.LongSupplier;

public class PanelAccess {

    private static final int count = 5;

    private static final long time = 60000L;

    private static final int countCount = 60;

    private final Map<String, PanelAccess.Entry> entriesByName = new ConcurrentHashMap<>();

    private final AtomicInteger counter = new AtomicInteger();

    private final LongSupplier longSupplier;

    private volatile long time3;

    public PanelAccess(LongSupplier longSupplier2) {
        this.longSupplier = longSupplier2;
        this.time3 = longSupplier2.getAsLong();
    }

    public boolean matchesEnabled(String text) {
        long value;
        if ((value = this.longSupplier.getAsLong()) - this.time3 >= 60000L) {
            this.time3 = value;
            this.counter.set(0);
        }
        PanelAccess.Entry entry;
        return this.counter.get() >= 60
                ? false
                : (entry = this.entriesByName.get(toLowerCase(text))) == null
                        || value >= PanelAccess.Entry.getTime(entry);
    }

    public void applyString(String text) {
        long asLong = this.longSupplier.getAsLong();
        this.counter.incrementAndGet();
        PanelAccess.Entry entry;
        synchronized (
                entry =
                        this.entriesByName.computeIfAbsent(
                                toLowerCase(text), result -> new PanelAccess.Entry())) {
            PanelAccess.Entry.calculateCount(entry);
            if (PanelAccess.Entry.getCount(entry) >= 5) {
                PanelAccess.Entry.calculateCountByCountCount(entry);
                PanelAccess.Entry.calculateCount(entry, 0);
                PanelAccess.Entry.calculateTime(
                        entry, asLong + min(PanelAccess.Entry.getCountCount(entry)));
            }
        }
    }

    public void applyStringByEntriesByName(String text) {
        this.entriesByName.remove(toLowerCase(text));
    }

    static long min(int value) {
        if (value <= 1) {
            return 60000L;
        } else {
            return value >= 5 ? 900000L : Math.min(60000L << value - 1, 900000L);
        }
    }

    private static String toLowerCase(String text) {
        return text == null ? "" : text.toLowerCase();
    }

    static final class Entry {

        private int count;

        private int countCount;

        private long time;

        private Entry() {}

        static long getTime(PanelAccess.Entry entry) {
            return entry.time;
        }

        static int calculateCount(PanelAccess.Entry entry) {
            return entry.count++;
        }

        static int getCount(PanelAccess.Entry entry) {
            return entry.count;
        }

        static int calculateCountByCountCount(PanelAccess.Entry entry) {
            return entry.countCount++;
        }

        static int calculateCount(PanelAccess.Entry entry, int value) {
            return entry.count = value;
        }

        static long calculateTime(PanelAccess.Entry entry, long value) {
            return entry.time = value;
        }

        static int getCountCount(PanelAccess.Entry entry) {
            return entry.countCount;
        }
    }
}
