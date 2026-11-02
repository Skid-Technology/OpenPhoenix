package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Default;
import xyz.refinedev.lib.commands.annotation.parameter.Flag;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.commands.api.annotations.DurationInput;
import xyz.refinedev.phoenix.commands.provider.DurationProvider;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.commandlog.ICommandLog;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.Duration;
import xyz.refinedev.phoenix.utils.EncryptedPasteBuilder;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Help
@Register(name = "commandlogs", aliases = "clogs")
@Requires("core.command.commandlogs")
public class CommandlogsCommand {

    private final CommonPlatform platform;

    @Command(name = "player")
    public void player(
            @Sender CommandSender sender,
            @Option("multiple") @Named("player") IProfile profile,
            @Named("time") @Default("perm") @DurationInput String text,
            @Flag("password") String text2,
            @Flag("delete-after-open") boolean matches) {
        Duration duration = DurationProvider.tr(sender, text, true);
        CompletableFuture<java.util.List<ICommandLog>> completableFuture =
                this.platform
                        .getCommandLogHandler()
                        .findByIssuedBy(
                                profile.getUniqueId(),
                                System.currentTimeMillis() - duration.getTimeTime(),
                                duration.isEnabled());
        completableFuture.whenComplete(
                (result, result2) -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (ICommandLog commandLog : (Iterable<ICommandLog>) ((Iterable) result)) {
                        stringBuilder
                                .append("[")
                                .append(DateUtils.getDate(commandLog.getIssuedOn()))
                                .append(" @ ")
                                .append(commandLog.getServer())
                                .append("] ")
                                .append(commandLog.getNiceIssuedBy())
                                .append(": ")
                                .append(commandLog.getCommand())
                                .append(" - ")
                                .append(this.formatString(commandLog.getIssuedOn()))
                                .append("\n");
                    }
                    EncryptedPasteBuilder encryptedPasteBuilder;
                    (encryptedPasteBuilder = new EncryptedPasteBuilder())
                            .findEncryptedPasteBuilderByTextString(stringBuilder.toString());
                    if (text2 != null && !text2.isEmpty()) {
                        encryptedPasteBuilder.findEncryptedPasteBuilder(text2);
                    }
                    encryptedPasteBuilder.findEncryptedPasteBuilder(matches);
                    encryptedPasteBuilder.getEncryptedPasteBuilder();
                    try {
                        sender.sendMessage(
                                Color.translate(
                                        Phoenix.SECONDARY_COLOR
                                                + "Link: "
                                                + encryptedPasteBuilder.getString()));
                    } catch (Exception exception) {
                        sender.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(sender, MessageConfig.ERROR.PASTE_FAILED)));
                        Phoenix.getInstance()
                                .getLogger()
                                .log(
                                        Level.SEVERE,
                                        "An error occurred while creating a command logs paste:",
                                        exception);
                    }
                });
    }

    @Command(name = "server")
    public void server(
            @Sender CommandSender sender,
            IServerData serverData,
            @Named("time") @Default("perm") @DurationInput String text,
            @Flag("password") String text2,
            @Flag("delete-after-open") boolean matches) {
        Duration duration = DurationProvider.tr(sender, text, true);
        CompletableFuture<java.util.List<ICommandLog>> completableFuture =
                this.platform
                        .getCommandLogHandler()
                        .findByServer(
                                serverData.getServerName(),
                                System.currentTimeMillis() - duration.getTimeTime(),
                                duration.isEnabled());
        completableFuture.whenComplete(
                (result, result2) -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (ICommandLog commandLog : (Iterable<ICommandLog>) ((Iterable) result)) {
                        stringBuilder
                                .append("[")
                                .append(DateUtils.getDate(commandLog.getIssuedOn()))
                                .append(" @ ")
                                .append(commandLog.getServer())
                                .append("] ")
                                .append(commandLog.getNiceIssuedBy())
                                .append(": ")
                                .append(commandLog.getCommand())
                                .append(" - ")
                                .append(this.formatString(commandLog.getIssuedOn()))
                                .append("\n");
                    }
                    EncryptedPasteBuilder encryptedPasteBuilder;
                    (encryptedPasteBuilder = new EncryptedPasteBuilder())
                            .findEncryptedPasteBuilderByTextString(stringBuilder.toString());
                    if (text2 != null && !text2.isEmpty()) {
                        encryptedPasteBuilder.findEncryptedPasteBuilder(text2);
                    }
                    encryptedPasteBuilder.findEncryptedPasteBuilder(matches);
                    encryptedPasteBuilder.getEncryptedPasteBuilder();
                    try {
                        sender.sendMessage(
                                Color.translate(
                                        Phoenix.SECONDARY_COLOR
                                                + "Link: "
                                                + encryptedPasteBuilder.getString()));
                    } catch (Exception exception) {
                        sender.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(sender, MessageConfig.ERROR.PASTE_FAILED)));
                        Phoenix.getInstance()
                                .getLogger()
                                .log(
                                        Level.SEVERE,
                                        "An error occurred while creating a command logs paste:",
                                        exception);
                    }
                });
    }

    public String formatString(long value) {
        Calendar instance = Calendar.getInstance();
        Calendar instance2 = Calendar.getInstance();
        instance.setTime(new Date(value));
        instance2.setTime(new Date(System.currentTimeMillis()));
        String text = DateUtils.formatDateDiff(instance, instance2);
        return text + (text.equals("now") ? "" : " ago");
    }

    public CommandlogsCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
