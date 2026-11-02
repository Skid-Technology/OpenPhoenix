package xyz.refinedev.phoenix.utils.repository;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.handler.database.AbstractDatabaseHandler;
import xyz.refinedev.phoenix.profile.TrackedEntity;
import xyz.refinedev.phoenix.utils.repository.updates.impl.SetFieldsUpdateOption;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;
import java.util.logging.Level;

public final class FieldWrites {

    private static final FieldWrites.AfterWrite IGNORED = (result, result2) -> {};

    private FieldWrites() {}

    static CompletableFuture<Boolean> writeAsync(
            TrackedEntity trackedEntity,
            SetFieldsUpdateOption setFieldsUpdateOption,
            Runnable runnable,
            String text) {
        Set<String> values = setFieldsUpdateOption.getFieldJson().keySet();
        long now = System.currentTimeMillis();
        CompletableFuture<Void> completableFuture =
                trackedEntity.enqueueWrite(
                        () -> writeSync(trackedEntity, setFieldsUpdateOption, runnable),
                        AbstractDatabaseHandler.DB_EXECUTOR);
        return completableFuture.handle(
                (result, result2) -> {
                    CommonPlatform instance = PlatformGetter.getInstance();
                    if (result2 != null) {
                        instance.getLogger().log(Level.SEVERE, "Failed to save " + text, result2);
                        return false;
                    }
                    if (instance.isDebugMode()) {
                        instance.getLogger()
                                .info(
                                        "Saved "
                                                + text
                                                + " in "
                                                + (System.currentTimeMillis() - now)
                                                + "ms: "
                                                + values);
                    }
                    return true;
                });
    }

    public static CompletableFuture<Boolean> landed(
            CompletableFuture<Void> completableFuture, String text) {
        return completableFuture.handle(
                (result, result2) -> {
                    if (result2 == null) {
                        return true;
                    }
                    PlatformGetter.getInstance().getLogger().log(Level.SEVERE, text, result2);
                    return false;
                });
    }

    public static <T> CompletableFuture<T> logged(
            CompletableFuture<T> completableFuture, String text) {
        return loggedOr(completableFuture, text, null);
    }

    public static <T> CompletableFuture<T> loggedOr(
            CompletableFuture<T> completableFuture, String text, T t) {
        return completableFuture.exceptionally(
                result -> {
                    PlatformGetter.getInstance().getLogger().log(Level.SEVERE, text, result);
                    return (T) t;
                });
    }

    static void writeSync(
            TrackedEntity trackedEntity,
            SetFieldsUpdateOption setFieldsUpdateOption,
            Runnable runnable) {
        restoringOnFailure(
                trackedEntity,
                setFieldsUpdateOption,
                () -> {
                    runnable.run();
                    return null;
                });
        trackedEntity.writeSettled(setFieldsUpdateOption.getFieldJson().keySet());
    }

    private static <T> T restoringOnFailure(
            TrackedEntity trackedEntity,
            SetFieldsUpdateOption setFieldsUpdateOption,
            Supplier<T> supplier) {
        try {
            return (T) supplier.get();
        } catch (RuntimeException runtimeException) {
            trackedEntity.markDirty(
                    runtimeException instanceof EntityVanishedException
                            ? trackedEntity.persistedFieldNames()
                            : setFieldsUpdateOption.getFieldJson().keySet());
            throw runtimeException;
        }
    }

    public static <V> CompletableFuture<Boolean> saveAsync(
            V v,
            EntityGateway<V> entityGateway,
            String text,
            Supplier<CompletableFuture<Boolean>> supplier) {
        return saveAsync(v, entityGateway, text, supplier, IGNORED);
    }

    public static <V> CompletableFuture<Boolean> saveAsync(
            V v,
            EntityGateway<V> entityGateway,
            String text,
            Supplier<CompletableFuture<Boolean>> supplier,
            FieldWrites.AfterWrite afterWrite) {
        if (!(v instanceof TrackedEntity)) {
            CompletableFuture<Boolean> completableFuture = (CompletableFuture) supplier.get();
            return completableFuture.thenApply(
                    result -> completed(afterWrite, Collections.emptyMap(), result));
        }
        TrackedEntity trackedEntity;
        SetFieldsUpdateOption setFieldsUpdateOption;
        if ((setFieldsUpdateOption =
                        (trackedEntity = (TrackedEntity) v).toUpdate(entityGateway.getGson()))
                == null) {
            afterWrite.completed(Collections.emptyMap(), true);
            return CompletableFuture.completedFuture(true);
        }
        Map<String, String> fieldJson = setFieldsUpdateOption.getFieldJson();
        String text2;
        try {
            text2 =
                    restoringOnFailure(
                            trackedEntity,
                            setFieldsUpdateOption,
                            () -> {
                                return entityGateway.fullJsonIfNeeded(
                                        () -> entityGateway.toJson(v));
                            });
        } catch (RuntimeException runtimeException) {
            afterWrite.completed(fieldJson, false);
            throw runtimeException;
        }
        String text3 = text2;
        SetFieldsUpdateOption setFieldsUpdateOption2 = setFieldsUpdateOption;
        CompletableFuture<Boolean> completableFuture2 =
                writeAsync(
                        trackedEntity,
                        setFieldsUpdateOption,
                        () -> entityGateway.updateFields(text, setFieldsUpdateOption2, text3),
                        "key " + text);
        return completableFuture2.thenApply(result2 -> completed(afterWrite, fieldJson, result2));
    }

    private static boolean completed(
            FieldWrites.AfterWrite afterWrite, Map<String, String> entries, boolean matches) {
        try {
            afterWrite.completed(entries, matches);
        } catch (RuntimeException runtimeException) {
            PlatformGetter.getInstance()
                    .getLogger()
                    .log(Level.SEVERE, "Could not finish a write", runtimeException);
        }
        return matches;
    }

    public static <V> void saveSync(V v, EntityGateway<V> entityGateway, String text) {
        saveSync(v, entityGateway, text, IGNORED);
    }

    public static <V> void saveSync(
            V v, EntityGateway<V> entityGateway, String text, FieldWrites.AfterWrite afterWrite) {
        if (!(v instanceof TrackedEntity)) {
            Map entries = Collections.emptyMap();
            notifying(
                    afterWrite,
                    entries,
                    () -> entityGateway.replace(text, entityGateway.toJson(v)));
        } else {
            TrackedEntity trackedEntity;
            SetFieldsUpdateOption setFieldsUpdateOption;
            if ((setFieldsUpdateOption =
                            (trackedEntity = (TrackedEntity) v).toUpdate(entityGateway.getGson()))
                    == null) {
                completed(afterWrite, Collections.emptyMap(), true);
            } else {
                Map<String, String> fieldJson = setFieldsUpdateOption.getFieldJson();
                SetFieldsUpdateOption setFieldsUpdateOption2 = setFieldsUpdateOption;
                TrackedEntity trackedEntity2 = trackedEntity;
                notifying(
                        afterWrite,
                        fieldJson,
                        () -> {
                            String text2 =
                                    restoringOnFailure(
                                            trackedEntity2,
                                            setFieldsUpdateOption2,
                                            () -> {
                                                return entityGateway.fullJsonIfNeeded(
                                                        () -> entityGateway.toJson(v));
                                            });
                            awaiting(
                                    trackedEntity2.enqueueWrite(
                                            () -> {
                                                writeSync(
                                                        trackedEntity2,
                                                        setFieldsUpdateOption2,
                                                        () ->
                                                                entityGateway.updateFields(
                                                                        text,
                                                                        setFieldsUpdateOption2,
                                                                        text2));
                                            },
                                            Runnable::run));
                        });
            }
        }
    }

    private static void notifying(
            FieldWrites.AfterWrite afterWrite, Map<String, String> entries, Runnable runnable) {
        try {
            runnable.run();
        } catch (RuntimeException runtimeException) {
            completed(afterWrite, entries, false);
            throw runtimeException;
        }
        completed(afterWrite, entries, true);
    }

    private static void awaiting(CompletableFuture<Void> completableFuture) {
        try {
            completableFuture.join();
        } catch (CompletionException completionException) {
            Throwable throwable;
            if ((throwable = completionException.getCause()) instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof Error) {
                throw (Error) throwable;
            } else {
                throw completionException;
            }
        }
    }

    public interface AfterWrite {

        void completed(Map<String, String> entries, boolean matches);
    }
}
