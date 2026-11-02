package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Default;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.commands.api.annotations.DurationInput;
import xyz.refinedev.phoenix.commands.provider.DurationProvider;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatLog;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PaginatedListClickable;
import xyz.refinedev.phoenix.utils.chat.Clickable;

import java.util.ArrayList;
import java.util.List;

@Help
@Register(name = "chatlogs")
@Requires("core.command.chatlogs")
public class ChatlogsCommand {

    private final CommonPlatform platform;

    @Command(name = "search", async = true)
    public void search(
            @Sender CommandSender sender,
            @Option({"disguise", "multiple"}) IProfile profile,
            @Named("message") String text,
            @Default("1") @Named("page") int value) {
        List<IChatLog> values =
                this.platform.getChatLogHandler().getByPlayer(profile.getUuid(), text).join();
        ArrayList<Clickable> values2 = new ArrayList<>();
        String nameWithColorNoDisguise = profile.getNameWithColorNoDisguise();
        String prefix = profile.getGeneralRank().getPrefix();
        for (IChatLog chatLog : values) {
            Clickable clickable =
                    new Clickable(
                            LocaleUtil.tr(sender, MessageConfig.CHAT_LOGS.FORMAT)
                                    .replace("<id>", chatLog.getId())
                                    .replace("<prefix>", prefix)
                                    .replace("<sender>", nameWithColorNoDisguise)
                                    .replace("<message>", chatLog.getMessage())
                                    .replace("<date>", chatLog.getNiceCreatedOn()));
            values2.add(clickable);
        }
        new PaginatedListClickable(
                        "Chat Logs",
                        profile.getNameWithColorNoDisguise(),
                        "/chatlogs search " + profile.getName() + " " + text)
                .display(sender, value, values2);
    }

    @Command(name = "view", async = true)
    public void view(
            @Sender CommandSender sender,
            @Option({"disguise", "multiple"}) IProfile profile,
            @Optional @Named("time") @DurationInput String text,
            @Default("1") @Named("page") int value) {
        long value2 = text != null ? DurationProvider.tr(sender, text, true).getTimeTime() : -5L;
        List<IChatLog> values =
                text != null
                        ? this.platform
                                .getChatLogHandler()
                                .getByPlayer(
                                        profile.getUuid(),
                                        value2 == -5L ? -5L : System.currentTimeMillis() - value2)
                                .join()
                        : this.platform.getChatLogHandler().getByPlayer(profile.getUuid()).join();
        ArrayList<Clickable> values2 = new ArrayList<>();
        String nameWithColorNoDisguise = profile.getNameWithColorNoDisguise();
        String prefix = profile.getGeneralRank().getPrefix();
        for (IChatLog chatLog : values) {
            Clickable clickable =
                    new Clickable(
                            LocaleUtil.tr(sender, MessageConfig.CHAT_LOGS.FORMAT)
                                    .replace("<id>", chatLog.getId())
                                    .replace("<prefix>", prefix)
                                    .replace("<sender>", nameWithColorNoDisguise)
                                    .replace("<message>", chatLog.getMessage())
                                    .replace("<date>", chatLog.getNiceCreatedOn()));
            values2.add(clickable);
        }
        new PaginatedListClickable(
                        "Chat Logs",
                        profile.getNameWithColorNoDisguise(),
                        "/chatlogs view "
                                + profile.getName()
                                + (text != null ? " " + text : " perm"))
                .display(sender, value, values2);
    }

    @Command(name = "clear")
    @Requires("core.command.chatlogs.clear")
    public void clear(
            @Sender CommandSender sender, @Option({"disguise", "multiple"}) IProfile profile) {
        this.platform.getChatLogHandler().clearLogs(profile.getUuid());
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.CHAT_LOGS.CLEAR)
                                .replace("<target>", profile.getNameWithColorNoDisguise())));
    }

    public ChatlogsCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
