package xyz.refinedev.phoenix.utils;

import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.profile.hop.IServerHop;
import xyz.refinedev.phoenix.profile.login.ILogin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SessionTimeline {

    private static final String text = "This is a utility class and cannot be instantiated";

    public static long calculateTime(List<IServerHop> values) {
        long value = 0L;
        Iterator<IServerHop> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            IServerHop serverHop;
            if ((serverHop = (IServerHop) iterator2.next()).getFrom() != null
                    && !serverHop.getFrom().isEmpty()) {
                value++;
            }
        }
        return value;
    }

    @Nullable
    public static String formatString(List<ILogin> values, List<IServerHop> values2) {
        Map<String, Long> entries = new HashMap();
        for (SessionTimeline.Session session : findList(values, values2)) {
            entries.merge(
                    SessionTimeline.Session.getText(session),
                    SessionTimeline.Session.getTimeTime(session)
                            - SessionTimeline.Session.getTime(session),
                    Long::sum);
        }
        return entries.entrySet().stream()
                .max(Entry.comparingByValue())
                .map(Entry::getKey)
                .orElse(null);
    }

    public static int calculateCount(List<ILogin> values, List<IServerHop> values2) {
        HashMap<Integer, Long> entries = new HashMap();
        Iterator<xyz.refinedev.phoenix.utils.SessionTimeline.Session> iterator2 =
                findList(values, values2).iterator();
        while (iterator2.hasNext()) {
            SessionTimeline.Session session;
            long time =
                    SessionTimeline.Session.getTime(
                            session = (SessionTimeline.Session) iterator2.next());
            while (time < SessionTimeline.Session.getTimeTime(session)) {
                Calendar calendar;
                (calendar = Calendar.getInstance()).setTimeInMillis(time);
                int value = calendar.get(11);
                calendar.set(12, 0);
                calendar.set(13, 0);
                calendar.set(14, 0);
                calendar.add(11, 1);
                long value2 =
                        Math.min(
                                calendar.getTimeInMillis(),
                                SessionTimeline.Session.getTimeTime(session));
                entries.merge(value, value2 - time, Long::sum);
                time = value2;
            }
        }
        return entries.entrySet().stream()
                .max(Entry.comparingByValue())
                .map(Entry::getKey)
                .orElse(-1);
    }

    private static List<SessionTimeline.Session> findList(
            List<ILogin> values, List<IServerHop> values2) {
        ArrayList<SessionTimeline.Session> values3 = new ArrayList<>();
        long now = System.currentTimeMillis();
        Iterator<ILogin> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            ILogin login;
            long value =
                    (login = (ILogin) iterator2.next()).hasLoggedOut() ? login.getLogout() : now;
            Stream<IServerHop> stream2 = values2.stream();
            ILogin login2 = login;
            List values4 =
                    stream2.filter(
                                    result ->
                                            result.getTimestamp() >= login2.getLogin()
                                                    && result.getTimestamp() <= value)
                            .sorted(Comparator.comparingLong(IServerHop::getTimestamp))
                            .collect(Collectors.toList());
            for (int index = 0; index < values4.size(); index++) {
                long timestamp = ((IServerHop) values4.get(index)).getTimestamp();
                long value2;
                if ((value2 =
                                index + 1 < values4.size()
                                        ? ((IServerHop) values4.get(index + 1)).getTimestamp()
                                        : value)
                        > timestamp) {
                    values3.add(
                            new SessionTimeline.Session(
                                    ((IServerHop) values4.get(index)).getTo(), timestamp, value2));
                }
            }
        }
        return values3;
    }

    private SessionTimeline() {
        throw new UnsupportedOperationException(text);
    }

    static final class Session {

        private final String text;

        private final long time;

        private final long timeTime;

        private Session(String text, long time, long timeTime) {
            this.text = text;
            this.time = time;
            this.timeTime = timeTime;
        }

        static String getText(SessionTimeline.Session session) {
            return session.text;
        }

        static long getTimeTime(SessionTimeline.Session session) {
            return session.timeTime;
        }

        static long getTime(SessionTimeline.Session session) {
            return session.time;
        }
    }
}
