package xyz.refinedev.phoenix.profile;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.utils.Exclude;
import xyz.refinedev.phoenix.utils.GsonExclusionStrategy;
import xyz.refinedev.phoenix.utils.repository.updates.api.Updates;
import xyz.refinedev.phoenix.utils.repository.updates.impl.SetFieldsUpdateOption;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public abstract class TrackedEntity {

    @Exclude private transient volatile Map<String, String> snapshot = Collections.emptyMap();

    @Exclude private transient volatile Set<String> lastDrained = Collections.emptySet();

    @Exclude private transient volatile Set<String> inFlight = Collections.emptySet();

    @Exclude private transient volatile CompletableFuture<Void> writeTail;

    @Exclude
    private static final Map<Class<?>, List<TrackedEntity.Entry>> FIELD_CACHE =
            new ConcurrentHashMap<>();

    @Exclude private static final Map<Class<?>, Set<String>> NAME_CACHE = new ConcurrentHashMap<>();

    private static final ThreadLocal<MessageDigest> DIGESTS =
            ThreadLocal.withInitial(
                    () -> {
                        try {
                            return MessageDigest.getInstance("SHA-256");
                        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                            throw new IllegalStateException(
                                    "SHA-256 unavailable", noSuchAlgorithmException);
                        }
                    });

    public synchronized CompletableFuture<Void> enqueueWrite(Runnable runnable, Executor executor) {
        CompletableFuture<Void> completableFuture =
                (this.writeTail == null ? CompletableFuture.completedFuture(null) : this.writeTail)
                        .thenRunAsync(runnable, executor);
        this.writeTail = completableFuture.handle((result, result2) -> null);
        return completableFuture;
    }

    public boolean isNew() {
        return this.baseline().isEmpty();
    }

    private Map<String, String> baseline() {
        return this.snapshot == null ? Collections.emptyMap() : this.snapshot;
    }

    private Set<String> lastDrainedOrEmpty() {
        return this.lastDrained == null ? Collections.emptySet() : this.lastDrained;
    }

    private Set<String> inFlightOrEmpty() {
        return this.inFlight == null ? Collections.emptySet() : this.inFlight;
    }

    public Set<String> persistedFieldNames() {
        return persistedFieldNames(this.getClass());
    }

    public static Set<String> persistedFieldNames(Class<?> classValue) {
        Set<String> values;
        if ((values = NAME_CACHE.get(classValue)) != null) {
            return values;
        }
        HashSet<String> values2 = new HashSet<>();
        for (TrackedEntity.Entry entry : persistedFields(classValue)) {
            values2.add(entry.text);
        }
        Set<String> values3 = Collections.unmodifiableSet(values2);
        NAME_CACHE.put(classValue, values3);
        return values3;
    }

    public Map<String, String> fieldJson(Set<String> values, Gson gson) {
        LinkedHashMap<String, String> entries = new LinkedHashMap<>();
        for (TrackedEntity.Entry entry : persistedFields(this.getClass())) {
            if (values.contains(entry.text)) {
                entries.put(entry.text, gson.toJson(entry.findObject(this)));
            }
        }
        return entries;
    }

    public synchronized void snapshot(Gson gson) {
        this.snapshot = digestAll(this.serializeFields(gson));
    }

    public static <T> T snapshotIfTracked(T t, Gson gson) {
        if (t instanceof TrackedEntity) {
            ((TrackedEntity) t).snapshot(gson);
        }
        return (T) t;
    }

    public synchronized void snapshotFromJson(Map<String, String> entries) {
        Set<String> values = this.persistedFieldNames();
        HashMap entries2 = new HashMap();
        for (Map.Entry entry : entries.entrySet()) {
            if (values.contains(entry.getKey())) {
                entries2.put((String) entry.getKey(), digest((String) entry.getValue()));
            }
        }
        this.snapshot = entries2;
    }

    public synchronized void snapshotFields(Set<String> values, Gson gson) {
        if (values != null && !values.isEmpty()) {
            HashMap entries = new HashMap<>(this.baseline());
            for (Map.Entry entry : this.fieldJson(values, gson).entrySet()) {
                entries.put((String) entry.getKey(), digest((String) entry.getValue()));
            }
            this.snapshot = entries;
        }
    }

    public Set<String> drainDirty(Gson gson) {
        return this.drain(gson).strings;
    }

    private synchronized TrackedEntity.ChangeSet drain(Gson gson) {
        Map<String, String> entries;
        Map<String, String> entries2 = digestAll(entries = this.serializeFields(gson));
        Set<String> values = this.changedSince(entries2);
        this.lastDrained = values;
        this.snapshot = entries2;
        if (!values.isEmpty()) {
            HashSet values2;
            (values2 = new HashSet<>(this.inFlightOrEmpty())).addAll(values);
            this.inFlight = values2;
        }
        return new TrackedEntity.ChangeSet(entries, values);
    }

    public synchronized void writeSettled(Set<String> values) {
        if (values != null && !values.isEmpty()) {
            Set<String> values2;
            if (!(values2 = this.inFlightOrEmpty()).isEmpty()) {
                HashSet values3;
                (values3 = new HashSet(values2)).removeAll(values);
                this.inFlight = values3;
            }
        }
    }

    public Set<String> peekDirty(Gson gson) {
        return this.changedSince(digestAll(this.serializeFields(gson)));
    }

    public Set<String> lastDrained() {
        return this.lastDrainedOrEmpty();
    }

    public static Set<String> changedFieldsFor(Object result, Gson gson) {
        if (!(result instanceof TrackedEntity)) {
            return null;
        }
        TrackedEntity trackedEntity;
        Set<String> values;
        return (values = (trackedEntity = (TrackedEntity) result).peekDirty(gson)).isEmpty()
                ? trackedEntity.lastDrained()
                : values;
    }

    private Set<String> changedSince(Map<String, String> entries) {
        return changedSince(entries, this.baseline());
    }

    private static Set<String> changedSince(
            Map<String, String> entries, Map<String, String> entries2) {
        HashSet<String> values = new HashSet<>();
        Iterator<Map.Entry<String, String>> iterator2 = entries.entrySet().iterator();
        while (iterator2.hasNext()) {
            Map.Entry entry;
            if (!((String) (entry = (Map.Entry) iterator2.next()).getValue())
                    .equals(entries2.get(entry.getKey()))) {
                values.add((String) entry.getKey());
            }
        }
        return values;
    }

    private static String digest(String text) {
        byte[] values = DIGESTS.get().digest(text.getBytes(StandardCharsets.UTF_8));
        return new String(values, 0, 16, StandardCharsets.ISO_8859_1);
    }

    private static Map<String, String> digestAll(Map<String, String> entries) {
        HashMap<String, String> entries2 = new HashMap<>();
        for (Map.Entry entry : entries.entrySet()) {
            entries2.put((String) entry.getKey(), digest((String) entry.getValue()));
        }
        return entries2;
    }

    public SetFieldsUpdateOption toUpdate(Gson gson) {
        TrackedEntity.ChangeSet changeSet;
        if ((changeSet = this.drain(gson)).strings.isEmpty()) {
            return null;
        }
        LinkedHashMap entries = new LinkedHashMap();
        for (String text : changeSet.strings) {
            entries.put(text, changeSet.stringsByName.get(text));
        }
        return Updates.setFields(entries);
    }

    public synchronized void markDirty(Set<String> values) {
        if (values != null && !values.isEmpty()) {
            HashMap entries = new HashMap<>(this.baseline());
            for (String text : values) {
                entries.remove(text);
            }
            this.snapshot = entries;
        }
    }

    public void copyFieldsFrom(TrackedEntity trackedEntity, Set<String> values) {
        if (trackedEntity.getClass() != this.getClass()) {
            throw new IllegalArgumentException(
                    "Cannot copy from "
                            + trackedEntity.getClass().getName()
                            + " into "
                            + this.getClass().getName());
        }
        for (TrackedEntity.Entry entry : persistedFields(this.getClass())) {
            if (values.contains(entry.text)) {
                try {
                    entry.field.set(this, detach(entry.field, entry.findObject(trackedEntity)));
                } catch (IllegalAccessException illegalAccessException) {
                    throw new IllegalStateException(
                            "Could not copy " + entry.text + " on " + this.getClass().getName(),
                            illegalAccessException);
                }
            }
        }
    }

    public synchronized void applyFieldsFrom(
            TrackedEntity trackedEntity, Set<String> values, Gson gson) {
        if (values != null && !values.isEmpty()) {
            HashSet values2;
            (values2 = new HashSet(values)).removeAll(this.inFlightOrEmpty());
            if (!values2.isEmpty()) {
                this.copyFieldsFrom(trackedEntity, values2);
                this.snapshotFields(values2, gson);
            }
        }
    }

    public synchronized Set<String> mergeFrom(TrackedEntity trackedEntity, Gson gson) {
        if (trackedEntity.getClass() != this.getClass()) {
            throw new IllegalArgumentException(
                    "Cannot merge from "
                            + trackedEntity.getClass().getName()
                            + " into "
                            + this.getClass().getName());
        }
        Map<String, String> entries = this.serializeFields(gson);
        Set<String> values = this.locallyEdited(digestAll(entries));
        Set<String> values2 = this.inFlightOrEmpty();
        HashSet<String> values3 = new HashSet<>();
        for (TrackedEntity.Entry entry : persistedFields(this.getClass())) {
            if (!values.contains(entry.text) && !values2.contains(entry.text)) {
                try {
                    Object result = entry.findObject(trackedEntity);
                    if (!((String) entries.get(entry.text)).equals(gson.toJson(result))) {
                        entry.field.set(this, detach(entry.field, result));
                        values3.add(entry.text);
                    }
                } catch (IllegalAccessException illegalAccessException) {
                    throw new IllegalStateException(
                            "Could not merge " + entry.text + " on " + this.getClass().getName(),
                            illegalAccessException);
                }
            }
        }
        this.snapshotFields(values3, gson);
        return values3;
    }

    private Set<String> locallyEdited(Map<String, String> entries) {
        Map<String, String> entries2 = this.baseline();
        Set<String> values;
        (values = changedSince(entries, entries2)).retainAll(entries2.keySet());
        return values;
    }

    private static Object detach(Field field, Object result) {
        Class type = field.getType();
        if (result instanceof List && type.isAssignableFrom(ArrayList.class)) {
            return new ArrayList((List) result);
        } else if (result instanceof Set && type.isAssignableFrom(LinkedHashSet.class)) {
            return new LinkedHashSet((Set) result);
        } else {
            return result instanceof Map && type.isAssignableFrom(LinkedHashMap.class)
                    ? new LinkedHashMap((Map) result)
                    : result;
        }
    }

    private Map<String, String> serializeFields(Gson gson) {
        return this.fieldJson(this.persistedFieldNames(), gson);
    }

    private static List<TrackedEntity.Entry> persistedFields(Class<?> classValue) {
        List<TrackedEntity.Entry> values;
        if ((values = FIELD_CACHE.get(classValue)) != null) {
            return values;
        }
        ArrayList<xyz.refinedev.phoenix.profile.TrackedEntity.Entry> values2 = new ArrayList<>();
        for (Class classValue2 = classValue;
                classValue2 != null && classValue2 != Object.class;
                classValue2 = classValue2.getSuperclass()) {
            Field[] fields;
            int value = (fields = classValue2.getDeclaredFields()).length;
            for (int index = 0; index < value; index++) {
                Field field;
                if (!GsonExclusionStrategy.skips(
                        Modifier.isTransient((field = fields[index]).getModifiers()),
                        Modifier.isStatic(field.getModifiers()),
                        field.getAnnotation(Exclude.class) != null)) {
                    field.setAccessible(true);
                    values2.add(new TrackedEntity.Entry(field, serializedName(field)));
                }
            }
        }
        List<TrackedEntity.Entry> values3 = Collections.unmodifiableList(values2);
        FIELD_CACHE.put(classValue, values3);
        return values3;
    }

    private static String serializedName(Field field) {
        SerializedName serializedName2;
        return (serializedName2 = field.getAnnotation(SerializedName.class)) == null
                ? field.getName()
                : serializedName2.value();
    }

    static final class ChangeSet {

        final Map<String, String> stringsByName;

        final Set<String> strings;

        ChangeSet(Map<String, String> entries, Set<String> strings) {
            this.stringsByName = entries;
            this.strings = strings;
        }
    }

    static final class Entry {

        final Field field;

        final String text;

        Entry(Field field2, String text) {
            this.field = field2;
            this.text = text;
        }

        Object findObject(TrackedEntity trackedEntity) {
            try {
                return this.field.get(trackedEntity);
            } catch (IllegalAccessException illegalAccessException) {
                throw new IllegalStateException(
                        "Could not read " + this.text + " on " + trackedEntity.getClass().getName(),
                        illegalAccessException);
            }
        }
    }
}
