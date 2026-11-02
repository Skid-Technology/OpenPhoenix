package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.handler.database.AbstractDatabaseHandler;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatLog;
import xyz.refinedev.phoenix.repository.IChatLogRepository;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.repository.RepositoryFactory;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ChatLogHandler implements IChatLogHandler {

    private final CommonPlatform platform;

    private final IChatLogRepository repository;

    private final List<IChatLog> chatEntriesToSave = new ArrayList<>();

    private final List<IChatLog> chatLogs = new ArrayList<>();

    private final ReentrantLock reentrantLock = new ReentrantLock();

    public ChatLogHandler(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
        this.repository = new RepositoryFactory(commonPlatform).chatLogs();
    }

    ChatLogHandler(IChatLogRepository chatLogRepository) {
        this.platform = null;
        this.repository = chatLogRepository;
    }

    @Override
    public CompletableFuture<Long> deleteOldLogs() {
        return CompletableFuture.supplyAsync(
                () ->
                        this.repository.deleteSync(
                                Filters.lt(
                                        "timestamp",
                                        Filters.storedLong(
                                                System.currentTimeMillis()
                                                        - TimeUnit.DAYS.toMillis(
                                                                GlobalConfig.SERVER
                                                                        .CHATLOGS
                                                                        .DAYS_KEEP)))),
                AbstractDatabaseHandler.DB_EXECUTOR);
    }

    @Override
    public void clearLogs(UUID uUID) {
        CompletableFuture.runAsync(
                () -> this.repository.deleteSync(Filters.eq("sender", uUID.toString())),
                AbstractDatabaseHandler.DB_EXECUTOR);
    }

    @Override
    public void saveLogsToDatabase() {
        this.reentrantLock.lock();
        ArrayList<IChatLog> values;
        try {
            (values = new ArrayList<>(this.chatEntriesToSave)).addAll(this.chatLogs);
            this.chatEntriesToSave.clear();
            this.chatLogs.clear();
        } finally {
            this.reentrantLock.unlock();
        }
        if (!values.isEmpty()) {
            try {
                this.repository.saveSync(values);
            } catch (RuntimeException runtimeException) {
                this.reentrantLock.lock();
                try {
                    this.chatEntriesToSave.addAll(0, values);
                } finally {
                    this.reentrantLock.unlock();
                }
                throw runtimeException;
            }
        }
    }

    @Override
    public void addChatLog(IChatLog chatLog) {
        if (this.reentrantLock.tryLock()) {
            try {
                this.chatEntriesToSave.add(chatLog);
            } finally {
                this.reentrantLock.unlock();
            }
        } else {
            synchronized (this.chatLogs) {
                this.chatLogs.add(chatLog);
            }
        }
    }

    @NotNull
    @Override
    public CompletableFuture<List<IChatLog>> getByPlayer(UUID uUID) {
        return CompletableFuture.supplyAsync(
                () -> {
                    List<IChatLog> values =
                            this.repository.fetchAllSync(Filters.eq("sender", uUID.toString()));
                    ArrayList<IChatLog> values2;
                    (values2 = new ArrayList<>(this.chatEntriesToSave)).addAll(this.chatLogs);
                    values2.removeIf(result -> !result.getSender().equals(uUID));
                    values.addAll(values2);
                    values.sort(Comparator.comparingLong(IChatLog::getTimestamp).reversed());
                    return values;
                },
                AbstractDatabaseHandler.DB_EXECUTOR);
    }

    @NotNull
    @Override
    public CompletableFuture<List<IChatLog>> getByPlayer(UUID uUID, String text) {
        return CompletableFuture.supplyAsync(
                () -> {
                    List<IChatLog> values =
                            this.repository.fetchAllSync(Filters.eq("sender", uUID.toString()));
                    ArrayList<IChatLog> values2 =
                            FastStreams.filterArrayList(
                                    values,
                                    result ->
                                            result.getMessage()
                                                    .toLowerCase()
                                                    .contains(text.toLowerCase()));
                    ArrayList<IChatLog> values3;
                    (values3 = new ArrayList<>(this.chatEntriesToSave)).addAll(this.chatLogs);
                    values3.removeIf(
                            result2 ->
                                    !result2.getSender().equals(uUID)
                                            || !result2.getMessage()
                                                    .toLowerCase()
                                                    .contains(text.toLowerCase()));
                    values2.addAll(values3);
                    values2.sort(Comparator.comparingLong(IChatLog::getTimestamp).reversed());
                    return values2;
                },
                AbstractDatabaseHandler.DB_EXECUTOR);
    }

    @NotNull
    @Override
    public CompletableFuture<List<IChatLog>> getByPlayer(UUID uUID, long value) {
        return CompletableFuture.supplyAsync(
                () -> {
                    List<IChatLog> values =
                            this.repository.fetchAllSync(Filters.eq("sender", uUID.toString()));
                    ArrayList<IChatLog> values2 =
                            FastStreams.filterArrayList(
                                    values,
                                    result -> value == -5L || result.getTimestamp() >= value);
                    ArrayList<IChatLog> values3;
                    (values3 = new ArrayList<>(this.chatEntriesToSave)).addAll(this.chatLogs);
                    values3.removeIf(
                            result2 ->
                                    !result2.getSender().equals(uUID)
                                            || value != -5L && result2.getTimestamp() < value);
                    values2.addAll(values3);
                    values2.sort(Comparator.comparingLong(IChatLog::getTimestamp).reversed());
                    return values2;
                },
                AbstractDatabaseHandler.DB_EXECUTOR);
    }

    @Nullable
    @Override
    public IChatLog getById(String text) {
        return this.repository.fetchSync(Filters.eq("_id", text), null).orElse(null);
    }

    public CommonPlatform getPlatform() {
        return this.platform;
    }

    @Override
    public IChatLogRepository getRepository() {
        return this.repository;
    }

    @Override
    public List<IChatLog> getChatEntriesToSave() {
        return this.chatEntriesToSave;
    }

    public List<IChatLog> getChatLogs() {
        return this.chatLogs;
    }

    public ReentrantLock getReentrantLock() {
        return this.reentrantLock;
    }
}
