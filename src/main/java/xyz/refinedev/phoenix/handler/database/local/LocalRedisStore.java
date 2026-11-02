package xyz.refinedev.phoenix.handler.database.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class LocalRedisStore {

    private final Map<String, Object> objectsByName = new HashMap<>();

    private final Map<String, Long> longsByName = new HashMap<>();

    private final LongSupplier longSupplier;

    public LocalRedisStore() {
        this(System::currentTimeMillis);
    }

    public LocalRedisStore(LongSupplier longSupplier2) {
        this.longSupplier = longSupplier2;
    }

    public synchronized String formatString(String text) {
        Object result;
        if ((result = this.findObject(text)) == null) {
            return null;
        } else if (!(result instanceof String)) {
            throw create(text);
        } else {
            return (String) result;
        }
    }

    public synchronized String formatString(
            String text, String text2, long value, boolean matches) {
        if (matches && this.findObject(text) != null) {
            return null;
        }
        this.objectsByName.put(text, text2);
        if (value > 0L) {
            this.longsByName.put(text, this.longSupplier.getAsLong() + value);
        } else {
            this.longsByName.remove(text);
        }
        return "OK";
    }

    public synchronized long remove(String... text) {
        long value = 0L;
        String[] texts = text;
        int value2 = text.length;
        for (int index = 0; index < value2; index++) {
            String text2 = texts[index];
            if (this.findObject(text2) != null) {
                value++;
            }
            this.objectsByName.remove(text2);
            this.longsByName.remove(text2);
        }
        return value;
    }

    public synchronized long calculateTime(String... text) {
        long value = 0L;
        String[] texts = text;
        int value2 = text.length;
        for (int index = 0; index < value2; index++) {
            String text2 = texts[index];
            if (this.findObject(text2) != null) {
                value++;
            }
        }
        return value;
    }

    public synchronized List<String> findList(String text) {
        Pattern pattern = findPattern(text);
        ArrayList<String> values = new ArrayList<>();
        for (String text2 :
                (Iterable<String>) ((Iterable) new ArrayList<>(this.objectsByName.keySet()))) {
            if (this.findObject(text2) != null && pattern.matcher(text2).matches()) {
                values.add(text2);
            }
        }
        return values;
    }

    public synchronized boolean getAsLong(String text, long value) {
        if (this.findObject(text) == null) {
            return false;
        }
        this.longsByName.put(text, this.longSupplier.getAsLong() + value);
        return true;
    }

    public synchronized String clear() {
        this.objectsByName.clear();
        this.longsByName.clear();
        return "OK";
    }

    public synchronized String formatString(String text, String text2) {
        Map<String, String> entries;
        return (entries = this.findMap(text, false)) == null ? null : (String) entries.get(text2);
    }

    public synchronized boolean matchesEnabled(String text, String text2, String text3) {
        return this.findMap(text, true).put(text2, text3) == null;
    }

    public synchronized long calculateTime(String text, Map<String, String> entries) {
        Map<String, String> entries2 = this.findMap(text, true);
        long value = 0L;
        for (Entry<String, String> entry : entries.entrySet()) {
            if (entries2.put((String) entry.getKey(), (String) entry.getValue()) == null) {
                value++;
            }
        }
        return value;
    }

    public synchronized boolean putIfAbsent(String text, String text2, String text3) {
        return this.findMap(text, true).putIfAbsent(text2, text3) == null;
    }

    public synchronized long calculateTime(String text, String... text2) {
        Map<String, String> entries;
        if ((entries = this.findMap(text, false)) == null) {
            return 0L;
        }
        long value = 0L;
        String[] texts = text2;
        int value2 = text2.length;
        for (int index = 0; index < value2; index++) {
            String text3 = texts[index];
            if (entries.remove(text3) != null) {
                value++;
            }
        }
        if (entries.isEmpty()) {
            this.objectsByName.remove(text);
            this.longsByName.remove(text);
        }
        return value;
    }

    public synchronized Map<String, String> findMap(String text) {
        Map<String, String> entries;
        return (entries = this.findMap(text, false)) == null
                ? new HashMap<>()
                : new HashMap<>(entries);
    }

    public synchronized List<String> keySet(String text) {
        Map<String, String> entries;
        return (entries = this.findMap(text, false)) == null
                ? new ArrayList<>()
                : new ArrayList<>(entries.keySet());
    }

    public synchronized long calculateTime(String text) {
        Map<String, String> entries;
        return (entries = this.findMap(text, false)) == null ? 0L : entries.size();
    }

    public synchronized boolean containsKey(String text, String text2) {
        Map<String, String> entries;
        return (entries = this.findMap(text, false)) != null && entries.containsKey(text2);
    }

    public synchronized int getCount() {
        long asLong = this.longSupplier.getAsLong();
        int value = 0;
        Iterator<Entry<String, Long>> iterator2 = this.longsByName.entrySet().iterator();
        while (iterator2.hasNext()) {
            Entry entry;
            if ((Long) (entry = (Entry) iterator2.next()).getValue() <= asLong) {
                this.objectsByName.remove(entry.getKey());
                iterator2.remove();
                value++;
            }
        }
        return value;
    }

    private Map<String, String> findMap(String text, boolean matches) {
        Object result;
        if ((result = this.findObject(text)) == null) {
            if (!matches) {
                return null;
            }
            HashMap<String, String> entries = new HashMap<>();
            this.objectsByName.put(text, entries);
            return entries;
        } else if (!(result instanceof Map)) {
            throw create(text);
        } else {
            return (Map<String, String>) result;
        }
    }

    private Object findObject(String text) {
        Long value;
        if ((value = this.longsByName.get(text)) != null
                && value <= this.longSupplier.getAsLong()) {
            this.objectsByName.remove(text);
            this.longsByName.remove(text);
            return null;
        } else {
            return this.objectsByName.get(text);
        }
    }

    private static IllegalStateException create(String text) {
        return new IllegalStateException("WRONGTYPE " + text + " holds a different kind of value");
    }

    private static Pattern findPattern(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] values;
        int value = (values = text.toCharArray()).length;
        for (int index = 0; index < value; index++) {
            char value2;
            if ((value2 = values[index]) == '*') {
                stringBuilder.append(".*");
            } else if (value2 == '?') {
                stringBuilder.append('.');
            } else {
                stringBuilder.append(Pattern.quote(String.valueOf(value2)));
            }
        }
        return Pattern.compile(stringBuilder.toString(), 32);
    }

    public synchronized LocalStorePersistence create(Predicate<String> predicate) {
        LocalStorePersistence localStorePersistence = new LocalStorePersistence();
        for (String text :
                (Iterable<String>) ((Iterable) new ArrayList<>(this.objectsByName.keySet()))) {
            Object result;
            if ((result = this.findObject(text)) != null && predicate.test(text)) {
                if (result instanceof String) {
                    localStorePersistence.getStrings().put(text, (String) result);
                } else {
                    localStorePersistence
                            .getHashes()
                            .put(
                                    text,
                                    new HashMap<>(
                                            (Map<? extends String, ? extends String>) result));
                }
                Long value;
                if ((value = this.longsByName.get(text)) != null) {
                    localStorePersistence.getExpiries().put(text, value);
                }
            }
        }
        return localStorePersistence;
    }

    public synchronized void applyLocalStorePersistence(
            LocalStorePersistence localStorePersistence) {
        if (localStorePersistence != null) {
            HashSet values = new HashSet();
            Iterator iterator2 = localStorePersistence.getStrings().entrySet().iterator();
            while (iterator2.hasNext()) {
                Entry entry;
                if ((entry = (Entry) iterator2.next()).getValue() != null) {
                    this.objectsByName.put((String) entry.getKey(), entry.getValue());
                    values.add((String) entry.getKey());
                }
            }
            iterator2 = localStorePersistence.getHashes().entrySet().iterator();
            while (iterator2.hasNext()) {
                Entry entry2;
                if ((entry2 = (Entry) iterator2.next()).getValue() != null) {
                    HashMap entries = new HashMap();
                    Iterator iterator3 = ((Map) entry2.getValue()).entrySet().iterator();
                    while (iterator3.hasNext()) {
                        Entry entry3;
                        if ((entry3 = (Entry) iterator3.next()).getValue() != null) {
                            entries.put((String) entry3.getKey(), (String) entry3.getValue());
                        }
                    }
                    if (!entries.isEmpty()) {
                        this.objectsByName.put((String) entry2.getKey(), entries);
                        values.add((String) entry2.getKey());
                    }
                }
            }
            for (String text : (Iterable<String>) ((Iterable) values)) {
                Long value;
                if ((value = localStorePersistence.getExpiries().get(text)) != null) {
                    this.longsByName.put(text, value);
                } else {
                    this.longsByName.remove(text);
                }
            }
            this.getCount();
        }
    }
}
