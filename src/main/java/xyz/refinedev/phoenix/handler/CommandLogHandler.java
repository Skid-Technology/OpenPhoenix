package xyz.refinedev.phoenix.handler;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.profile.commandlog.CommandLog;
import xyz.refinedev.phoenix.profile.commandlog.ICommandLog;
import xyz.refinedev.phoenix.repository.ICommandLogRepository;
import xyz.refinedev.phoenix.utils.repository.RepositoryFactory;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CommandLogHandler implements ICommandLogHandler {

    private final CommonPlatform platform;

    private final ICommandLogRepository commandLogRepository;

    public CommandLogHandler(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
        this.commandLogRepository = new RepositoryFactory(commonPlatform).commandLogs();
    }

    @Override
    public void saveCommandLog(UUID uUID, String text, String text2) {
        CommandLog commandLog = new CommandLog(uUID, text2, text);
        this.commandLogRepository.save(commandLog);
        String text3;
        if ((text3 = text2 == null ? "unknown" : text2.trim()).startsWith("/")) {
            text3 = text3.substring(1);
        }
        int value;
        if ((value = text3.indexOf(32)) > 0) {
            text3 = text3.substring(0, value);
        }
        if (text3.isEmpty()) {
            text3 = "unknown";
        }
        this.platform
                .getMetricsHandler()
                .metric("phoenix_command_executed")
                .findMetric("command", text3.toLowerCase())
                .findMetric("server", text == null ? "unknown" : text)
                .initialize();
    }

    @Override
    public CompletableFuture<List<ICommandLog>> findByIssuedBy(
            UUID uUID, long value, boolean matches) {
        CompletableFuture<List<ICommandLog>> completableFuture =
                this.commandLogRepository.fetchAll(Filters.eq("issuedBy", uUID.toString()));
        return completableFuture.handleAsync(
                (result, result2) -> {
                    ArrayList<ICommandLog> values = new ArrayList<>();
                    for (ICommandLog commandLog : (Iterable<ICommandLog>) ((Iterable) result)) {
                        if (matches || commandLog.getIssuedOn() >= value) {
                            values.add(commandLog);
                        }
                    }
                    Collections.reverse(values);
                    return values;
                });
    }

    @Override
    public CompletableFuture<List<ICommandLog>> findByServer(
            String text, long value, boolean matches) {
        CompletableFuture<List<ICommandLog>> completableFuture =
                this.commandLogRepository.fetchAll(Filters.eq("server", text));
        return completableFuture.handleAsync(
                (result, result2) -> {
                    ArrayList<ICommandLog> values = new ArrayList<>();
                    for (ICommandLog commandLog : (Iterable<ICommandLog>) ((Iterable) result)) {
                        if (matches || commandLog.getIssuedOn() >= value) {
                            values.add(commandLog);
                        }
                    }
                    Collections.reverse(values);
                    return values;
                });
    }
}
