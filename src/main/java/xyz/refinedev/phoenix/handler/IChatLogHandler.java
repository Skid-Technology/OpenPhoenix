package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatLog;
import xyz.refinedev.phoenix.repository.IChatLogRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IChatLogHandler {

    CompletableFuture<Long> deleteOldLogs();

    void clearLogs(UUID uUID);

    void saveLogsToDatabase();

    void addChatLog(IChatLog chatLog);

    @NotNull
    CompletableFuture<List<IChatLog>> getByPlayer(UUID uUID);

    @NotNull
    CompletableFuture<List<IChatLog>> getByPlayer(UUID uUID, String text);

    @NotNull
    CompletableFuture<List<IChatLog>> getByPlayer(UUID uUID, long value);

    @Nullable
    IChatLog getById(String text);

    IChatLogRepository getRepository();

    List<IChatLog> getChatEntriesToSave();
}
