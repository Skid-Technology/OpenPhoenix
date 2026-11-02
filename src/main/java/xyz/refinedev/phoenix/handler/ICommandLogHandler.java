package xyz.refinedev.phoenix.handler;

import xyz.refinedev.phoenix.profile.commandlog.ICommandLog;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ICommandLogHandler {

    void saveCommandLog(UUID uUID, String text, String text2);

    CompletableFuture<List<ICommandLog>> findByIssuedBy(UUID uUID, long value, boolean matches);

    CompletableFuture<List<ICommandLog>> findByServer(String text, long value, boolean matches);
}
