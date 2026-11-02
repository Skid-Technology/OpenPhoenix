package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Default;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.menu.impl.ChatsnapshotMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatEntry;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatSnapshot;
import xyz.refinedev.phoenix.profile.cooldown.Cooldown;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.MetricsReporter;
import xyz.refinedev.phoenix.utils.PaginatedListClickable;
import xyz.refinedev.phoenix.utils.chat.Clickable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Help
@Register(name = "chatsnapshot", aliases = "chatsnapshots")
@Requires("core.command.chatsnapshot")
public class ChatsnapshotCommand {

    private final Phoenix plugin;

    @Command(name = "create")
    @Requires("core.command.chatsnapshot.create")
    public void create(@Sender CommandSender sender, @Optional Player player) {
        MetricsReporter.applyString("chatsnapshots");
        if (sender instanceof Player) {
            Player player2 = (Player) sender;
            IProfile cachedProfile =
                    this.plugin.getProfileHandler().getCachedProfile(player2.getUniqueId());
            if (!player2.hasPermission("core.cooldowns.bypass")
                    && cachedProfile.hasGlobalCooldown("chatSnapshot")) {
                throw new ExitMessage(
                        LocaleUtil.tr(sender, MessageConfig.COOLDOWNS.SNAPSHOT_COOLDOWN)
                                .replace(
                                        "<remaining>",
                                        DateUtils.formatTimeMillis(
                                                cachedProfile.getGlobalRemaining("chatSnapshot"))));
            }
            cachedProfile
                    .getGlobalCooldowns()
                    .add(
                            new Cooldown(
                                    BukkitAPI.parseDuration(SettingsConfig.COOLDOWNS.CHAT_SNAPSHOT),
                                    "chatSnapshot"));
            cachedProfile.saveProfile();
        }
        UUID uUID =
                sender instanceof Player
                        ? ((Player) sender).getUniqueId()
                        : IProfile.getConsoleUuid();
        IChatSnapshot chatSnapshot;
        if (player != null) {
            chatSnapshot =
                    this.plugin.getChatSnapshotHandler().createSnapshot(uUID, player.getUniqueId());
        } else {
            chatSnapshot = this.plugin.getChatSnapshotHandler().createSnapshot(uUID);
        }
        if (chatSnapshot != null) {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.CHAT_SNAPSHOT.CREATED)
                                    .replace("<id>", chatSnapshot.getNiceId())));
        } else {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.CHAT_SNAPSHOT.NO_CHAT_MESSAGES));
        }
    }

    @Command(name = "delete")
    @Requires("core.command.chatsnapshot.delete")
    public void delete(@Sender CommandSender sender, IChatSnapshot chatSnapshot) {
        this.plugin.getChatSnapshotHandler().deleteSnapshot(chatSnapshot);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.CHAT_SNAPSHOT.DELETED)
                                .replace("<id>", chatSnapshot.getNiceId())));
    }

    @Command(name = "own")
    @Requires("core.command.chatsnapshot.own")
    public void own(@Sender Player player) {
        CompletableFuture<List<IChatSnapshot>> byPlayer =
                this.plugin.getChatSnapshotHandler().getByPlayer(player.getUniqueId());
        byPlayer.whenCompleteAsync(
                (result, result2) ->
                        this.plugin
                                .getMenuHandler()
                                .openMenuAsync(
                                        new ChatsnapshotMenu((List<IChatSnapshot>) result, true),
                                        player));
    }

    @Command(name = "player")
    @Requires("core.command.chatsnapshot.player")
    public void player(
            @Sender Player player2,
            @Named("player") @Option({"disguise", "multiple"}) IProfile profile) {
        CompletableFuture<List<IChatSnapshot>> byPlayer =
                this.plugin.getChatSnapshotHandler().getByPlayer(profile.getUniqueId());
        byPlayer.whenCompleteAsync(
                (result, result2) ->
                        this.plugin
                                .getMenuHandler()
                                .openMenuAsync(
                                        new ChatsnapshotMenu((List<IChatSnapshot>) result, false),
                                        player2));
    }

    @Command(name = "view", async = true)
    @Requires("core.command.chatsnapshot.view")
    public void view(
            @Sender Player player,
            IChatSnapshot chatSnapshot,
            @Default("1") @Named("page") int value) {
        PaginatedListClickable paginatedListClickable =
                new PaginatedListClickable(
                        "Entries",
                        DateUtils.getDate(chatSnapshot.getCreatedOn()),
                        "/chatsnapshot view " + chatSnapshot.getNiceId());
        ArrayList<Clickable> values = new ArrayList<>();
        for (IChatEntry chatEntry : chatSnapshot.getChat()) {
            values.add(
                    new Clickable("&7[" + chatEntry.getNiceTime() + "] " + chatEntry.getMessage()));
        }
        paginatedListClickable.display(player, value, values);
    }

    public ChatsnapshotCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
