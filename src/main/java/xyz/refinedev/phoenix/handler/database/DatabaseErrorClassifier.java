package xyz.refinedev.phoenix.handler.database;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import javax.net.ssl.SSLException;

public final class DatabaseErrorClassifier {

    private static final List<DatabaseErrorClassifier.Entry> entries =
            Arrays.asList(
                    new DatabaseErrorClassifier.Entry(
                            result ->
                                    matchesEnabled(
                                            result,
                                            "wrongpass",
                                            "noauth",
                                            "access denied for user",
                                            "authentication failed",
                                            "exception authenticating"),
                            "The username or password is wrong."),
                    new DatabaseErrorClassifier.Entry(
                            result -> matchesEnabled(result, "unknown database"),
                            "That database doesn't exist on the server. Create it, or fix the database name."),
                    new DatabaseErrorClassifier.Entry(
                            result -> result instanceof UnknownHostException,
                            "The address doesn't point at any server. Check it for typos."),
                    new DatabaseErrorClassifier.Entry(
                            result ->
                                    result instanceof ConnectException
                                            || matchesEnabled(result, "connection refused"),
                            "Nothing answered on that address and port. Check the database is running and the port is right."),
                    new DatabaseErrorClassifier.Entry(
                            result -> result instanceof SSLException,
                            "The SSL handshake failed. Check the ssl settings match what the server expects."),
                    new DatabaseErrorClassifier.Entry(
                            result ->
                                    result instanceof SocketTimeoutException
                                            || matchesEnabled(result, "timed out", "timeout"),
                            "The connection timed out. A firewall may be blocking the port, or the address is wrong."));

    private DatabaseErrorClassifier() {}

    public static String formatString(Throwable throwable) {
        ArrayList values = new ArrayList();
        for (Throwable throwable2 = throwable;
                throwable2 != null && !values.contains(throwable2);
                throwable2 = throwable2.getCause()) {
            values.add(throwable2);
        }
        for (DatabaseErrorClassifier.Entry entry : entries) {
            for (Throwable throwable3 : (Iterable<Throwable>) ((Iterable) values)) {
                if (DatabaseErrorClassifier.Entry.getPredicate(entry).test(throwable3)) {
                    return DatabaseErrorClassifier.Entry.getText(entry);
                }
            }
        }
        return null;
    }

    private static boolean matchesEnabled(Throwable throwable, String... text) {
        String text2;
        if ((text2 = throwable.getMessage()) == null) {
            return false;
        }
        String text3 = text2.toLowerCase(Locale.ROOT);
        String[] texts = text;
        int value = text.length;
        for (int index = 0; index < value; index++) {
            String text4 = texts[index];
            if (text3.contains(text4)) {
                return true;
            }
        }
        return false;
    }

    static final class Entry {

        private final Predicate<Throwable> predicate;

        private final String text;

        private Entry(Predicate<Throwable> predicate2, String text) {
            this.predicate = predicate2;
            this.text = text;
        }

        static Predicate<Throwable> getPredicate(DatabaseErrorClassifier.Entry entry) {
            return entry.predicate;
        }

        static String getText(DatabaseErrorClassifier.Entry entry) {
            return entry.text;
        }
    }
}
