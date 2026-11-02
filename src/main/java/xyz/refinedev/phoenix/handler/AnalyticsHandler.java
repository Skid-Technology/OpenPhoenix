package xyz.refinedev.phoenix.handler;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.profile.login.ILogin;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class AnalyticsHandler implements IAnalyticsHandler {

    private final CommonPlatform platform;

    private final Map<UUID, Long> longsById;

    private final List<Long> longs;

    public AnalyticsHandler(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
        this.longsById = new ConcurrentHashMap<>();
        this.longs = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void cleanUp() {
        HashSet<String> values = new HashSet();
        long value = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis());
        Iterator<Entry<String, String>> iterator2 =
                this.platform.getMetaData().entrySet().iterator();
        while (iterator2.hasNext()) {
            Entry entry;
            if (((String) (entry = (Entry) iterator2.next()).getKey()).startsWith("peakPlayers-")
                    && Long.parseLong(((String) entry.getKey()).replace("peakPlayers-", ""))
                            < value - 7L) {
                values.add((String) entry.getKey());
            }
            if (((String) entry.getKey()).startsWith("averagePlaytime-")) {
                long value2;
                if ((value2 =
                                Long.parseLong(
                                        ((String) entry.getKey()).replace("averagePlaytime-", "")))
                        == value) {
                    this.longs.add(Long.parseLong((String) entry.getValue()));
                }
                if (value2 < value - 7L) {
                    values.add((String) entry.getKey());
                }
            }
            if (((String) entry.getKey()).startsWith("serverSwitches-")
                    && Long.parseLong(((String) entry.getKey()).replace("serverSwitches-", ""))
                            < value - 7L) {
                values.add((String) entry.getKey());
            }
        }
        for (String text : values) {
            this.platform.getMetaData().remove(text);
        }
    }

    @Override
    public void addToLoggedIn(UUID uUID) {
        this.longsById.put(uUID, System.currentTimeMillis());
    }

    @Override
    public void saveLoggedInTime(UUID uUID) {
        long value = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis());
        Long value2;
        if ((value2 = this.longsById.getOrDefault(uUID, null)) != null) {
            long value3 = System.currentTimeMillis() - value2;
            this.longs.add(value3);
            this.longsById.remove(uUID);
            ArrayList<Long> values;
            synchronized (this.longs) {
                values = new ArrayList<>(this.longs);
            }
            long value4 =
                    Math.round(values.stream().mapToLong(Long::longValue).average().orElse(0.0));
            this.platform.getMetaData().put("averagePlaytime-" + value, String.valueOf(value4));
        }
    }

    @Override
    public int getTotalPeakPlayers() {
        long value = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis());
        int value2 = 0;
        for (IServerData serverData : this.platform.getNetworkHandler().getProxies()) {
            value2 +=
                    Integer.parseInt(
                            serverData.getMetaData().getOrDefault("peakPlayers-" + value, "0"));
        }
        return value2;
    }

    @Override
    public int getTotalPeakPlayers(int value) {
        long value2 = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()) - value;
        int value3 = 0;
        for (IServerData serverData : this.platform.getNetworkHandler().getProxies()) {
            value3 +=
                    Integer.parseInt(
                            serverData.getMetaData().getOrDefault("peakPlayers-" + value2, "0"));
        }
        return value3;
    }

    @Override
    public int getPeakPlayers() {
        return this.getPeakPlayers(0);
    }

    @Override
    public int getPeakPlayers(IServerData serverData) {
        return this.getPeakPlayers(serverData, 0);
    }

    @Override
    public int getPeakPlayers(int value) {
        long value2 = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()) - value;
        return Integer.parseInt(
                this.platform.getMetaData().getOrDefault("peakPlayers-" + value2, "0"));
    }

    @Override
    public int getPeakPlayers(IServerData serverData, int value) {
        long value2 = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()) - value;
        return Integer.parseInt(
                serverData.getMetaData().getOrDefault("peakPlayers-" + value2, "0"));
    }

    @Override
    public String getTotalAveragePlayTime(int value) {
        long value2 = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()) - value;
        ArrayList<Long> values = new ArrayList<>();
        for (IServerData serverData : this.platform.getNetworkHandler().getProxies()) {
            values.add(
                    Long.parseLong(
                            serverData
                                    .getMetaData()
                                    .getOrDefault("averagePlaytime-" + value2, "0")));
        }
        double value3;
        return (value3 = values.stream().mapToLong(Long::longValue).average().orElse(0.0)) == 0.0
                ? "0s"
                : DateUtils.formatTimeMillis(Math.round(value3));
    }

    @Override
    public String getTotalAveragePlayTimeDifference(int value) {
        long value2 = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis());
        long value3 = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()) - value;
        ArrayList<Long> values = new ArrayList<>();
        ArrayList<Long> values2 = new ArrayList<>();
        for (IServerData serverData : this.platform.getNetworkHandler().getProxies()) {
            values.add(
                    Long.parseLong(
                            serverData
                                    .getMetaData()
                                    .getOrDefault("averagePlaytime-" + value2, "0")));
            values2.add(
                    Long.parseLong(
                            serverData
                                    .getMetaData()
                                    .getOrDefault("averagePlaytime-" + value3, "0")));
        }
        double value4 = values.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double value5 = values2.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double value6 = value4 - value5;
        return value4 >= value5
                ? "&a+" + DateUtils.formatTimeMillis(Math.round(value6))
                : "&c-" + DateUtils.formatTimeMillis(Math.round(value6 * -1.0));
    }

    @Override
    public String getAveragePlayTime() {
        return this.getAveragePlayTime(0);
    }

    @Override
    public String getAveragePlayTime(IServerData serverData) {
        return this.getAveragePlayTime(serverData, 0);
    }

    @Override
    public String getAveragePlayTime(int value) {
        long value2 = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()) - value;
        return DateUtils.formatTimeMillis(
                Long.parseLong(
                        this.platform
                                .getMetaData()
                                .getOrDefault("averagePlaytime-" + value2, "0")));
    }

    @Override
    public String getAveragePlayTime(IServerData serverData, int value) {
        long value2 = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()) - value;
        return DateUtils.formatTimeMillis(
                Long.parseLong(
                        serverData.getMetaData().getOrDefault("averagePlaytime-" + value2, "0")));
    }

    @Override
    public String getAveragePlayTimeDifference(IServerData serverData, int value) {
        long value2 = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis());
        long value3 = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()) - value;
        long value4 =
                Long.parseLong(
                        serverData.getMetaData().getOrDefault("averagePlaytime-" + value2, "0"));
        long value5 =
                Long.parseLong(
                        serverData.getMetaData().getOrDefault("averagePlaytime-" + value3, "0"));
        long value6 = value4 - value5;
        return value4 >= value5
                ? "&a+" + DateUtils.formatTimeMillis(value6)
                : "&c-" + DateUtils.formatTimeMillis(value6 * -1L);
    }

    @Override
    public void updatePeakPlayers(int value) {
        long value2 = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis());
        int value3 =
                Integer.parseInt(
                        this.platform.getMetaData().getOrDefault("highest-peakPlayers", "0"));
        int value4 =
                Integer.parseInt(
                        this.platform.getMetaData().getOrDefault("peakPlayers-" + value2, "0"));
        if (value > value4) {
            this.platform.getMetaData().put("peakPlayers-" + value2, String.valueOf(value));
            if (value4 > value3) {
                this.platform.getMetaData().put("highest-peakPlayers", String.valueOf(value4));
            }
        }
    }

    @Override
    public long getLogins(int value) {
        return this.platform
                .getLoginHandler()
                .getRepository()
                .count(
                        Filters.gte(
                                "login",
                                Filters.storedLong(
                                        System.currentTimeMillis()
                                                - TimeUnit.DAYS.toMillis(value))));
    }

    @Override
    public Map<String, String> getRetentionPlaceholders() {
        long now = System.currentTimeMillis();
        long value = TimeUnit.MILLISECONDS.toDays(now);
        long value2 = now - TimeUnit.DAYS.toMillis(30L);
        Map<Long, Set<UUID>> entries = new HashMap();
        Map<UUID, Long> entries2 = new HashMap();
        Iterator<ILogin> iterator2 =
                this.platform
                        .getLoginHandler()
                        .getRepository()
                        .fetchAllSync(Filters.gte("login", Filters.storedLong(value2)))
                        .iterator();
        while (iterator2.hasNext()) {
            ILogin login;
            if ((login = (ILogin) iterator2.next()) != null && login.getTarget() != null) {
                long value3 = TimeUnit.MILLISECONDS.toDays(login.getLogin());
                entries.computeIfAbsent(value3, result -> new HashSet<>()).add(login.getTarget());
                entries2.merge(login.getTarget(), value3, Math::min);
            }
        }
        int size2 = entries.getOrDefault(value, Collections.emptySet()).size();
        int value4 = this.calculateCount(entries2, value);
        HashMap<String, String> entries3;
        (entries3 = new HashMap()).put("<uniquePlayers>", String.valueOf(size2));
        entries3.put(
                "<uniquePlayersYesterday>",
                String.valueOf(entries.getOrDefault(value - 1L, Collections.emptySet()).size()));
        entries3.put("<newPlayers>", String.valueOf(value4));
        entries3.put(
                "<newPlayersYesterday>", String.valueOf(this.calculateCount(entries2, value - 1L)));
        entries3.put("<returningPlayers>", String.valueOf(Math.max(0, size2 - value4)));
        entries3.put("<d1Retention>", this.formatString(entries2, entries, value - 2L, 1));
        entries3.put("<d7Retention>", this.formatString(entries2, entries, value - 8L, 7));
        return entries3;
    }

    private int calculateCount(Map<UUID, Long> entries, long value) {
        int value2 = 0;
        Iterator<Long> iterator2 = entries.values().iterator();
        while (iterator2.hasNext()) {
            if ((Long) iterator2.next() == value) {
                value2++;
            }
        }
        return value2;
    }

    private String formatString(
            Map<UUID, Long> entries, Map<Long, Set<UUID>> entries2, long value, int value2) {
        HashSet<UUID> values = new HashSet<>();
        Iterator<Entry<UUID, Long>> iterator2 = entries.entrySet().iterator();
        while (iterator2.hasNext()) {
            Entry entry;
            if ((Long) (entry = (Entry) iterator2.next()).getValue() == value) {
                values.add((UUID) entry.getKey());
            }
        }
        if (values.isEmpty()) {
            return "n/a";
        }
        HashSet<UUID> values2 = new HashSet<>();
        for (int index = 1; index <= value2; index++) {
            for (UUID uUID : entries2.getOrDefault(value + index, Collections.emptySet())) {
                if (values.contains(uUID)) {
                    values2.add(uUID);
                }
            }
        }
        return Math.round(values2.size() * 100.0 / values.size()) + "% of " + values.size();
    }

    @Override
    public void recordServerSwitch() {
        long value = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis());
        long value2 =
                Long.parseLong(
                        this.platform.getMetaData().getOrDefault("serverSwitches-" + value, "0"));
        this.platform.getMetaData().put("serverSwitches-" + value, String.valueOf(value2 + 1L));
    }

    @Override
    public long getTotalServerSwitches(int value) {
        long value2 = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()) - value;
        long value3 = 0L;
        for (IServerData serverData : this.platform.getNetworkHandler().getProxies()) {
            value3 +=
                    Long.parseLong(
                            serverData.getMetaData().getOrDefault("serverSwitches-" + value2, "0"));
        }
        return value3;
    }
}
