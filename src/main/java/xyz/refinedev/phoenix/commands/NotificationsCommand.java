package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Default;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.impl.notification.NotificationDetailsMenu;
import xyz.refinedev.phoenix.menu.impl.notification.NotificationListMenu;
import xyz.refinedev.phoenix.menu.impl.notification.NotificationTextEditorMenu;
import xyz.refinedev.phoenix.notification.INotification;
import xyz.refinedev.phoenix.notification.Notification;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.MetricsReporter;
import xyz.refinedev.phoenix.utils.PaginatedListClickable;
import xyz.refinedev.phoenix.utils.chat.Clickable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Help
@Register(
        name = "notifications",
        aliases = {"notification", "notif"})
public class NotificationsCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(@Sender Player player) {
        MetricsReporter.applyString("notifications");
        CompletableFuture<List<INotification>> unreadByPlayer =
                this.plugin.getNotificationHandler().getUnreadByPlayer(player.getUniqueId());
        unreadByPlayer.whenCompleteAsync(
                (result, result2) ->
                        this.plugin
                                .getMenuHandler()
                                .openMenuSync(
                                        new NotificationListMenu(
                                                (List<INotification>) result, true),
                                        player));
    }

    @Command(name = "all")
    public void all(@Sender Player player) {
        MetricsReporter.applyString("notifications_all");
        this.plugin
                .getMenuHandler()
                .openMenu(
                        new NotificationListMenu(
                                this.plugin.getNotificationHandler().getRepository().getCache(),
                                false),
                        player);
    }

    @Command(name = "list")
    @Requires("core.command.notifications.list")
    public void list(@Sender CommandSender sender, @Default("1") @Named("page") int value) {
        PaginatedListClickable paginatedListClickable =
                new PaginatedListClickable("Notifications", null, "/notifications list");
        ArrayList<Clickable> values = new ArrayList<>();
        for (INotification notification :
                this.plugin.getNotificationHandler().getRepository().getCache()) {
            Clickable clickable;
            (clickable = new Clickable())
                    .add(LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_LIST_FORMAT));
            clickable.add(
                    notification.getId() + " &7(" + notification.getTitle() + "&7)",
                    BukkitAPI.getKickMessage(notification.getDescription()),
                    "");
            values.add(clickable);
        }
        paginatedListClickable.display(sender, value, values);
    }

    @Command(name = "create")
    @Requires("core.command.notifications.create")
    public void create(@Sender Player player, @Named("id") String text) {
        if (this.plugin.getNotificationHandler().getFromCache(text) != null) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.NOTIFICATIONS.ALREADY_EXISTS)
                            .replace("<notification>", text));
        }
        Notification notification = new Notification(text, player.getUniqueId());
        this.plugin.getNotificationHandler().saveNotification(notification);
        player.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player, MessageConfig.NOTIFICATIONS.CREATED)
                                .replace("<notification>", notification.getId())));
    }

    @Command(name = "delete")
    @Requires("core.command.notifications.delete")
    public void delete(@Sender Player player, INotification notification) {
        this.plugin.getNotificationHandler().deleteNotification(notification);
        player.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player, MessageConfig.NOTIFICATIONS.DELETED)
                                .replace("<notification>", notification.getId())));
    }

    @Command(name = "title")
    @Requires("core.command.notifications.title")
    public void title(
            @Sender Player player, INotification notification, @Named("title") @Text String text) {
        notification.setTitle(text);
        this.plugin.getNotificationHandler().saveNotification(notification);
        player.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player, MessageConfig.NOTIFICATIONS.TITLE_UPDATED)
                                .replace("<notification>", notification.getId())
                                .replace("<title>", text)));
    }

    @Command(name = "lore")
    @Requires("core.command.notifications.lore")
    public void lore(@Sender Player player, INotification notification) {
        this.plugin.getMenuHandler().openMenu(new NotificationTextEditorMenu(notification), player);
    }

    @Command(name = "stats")
    @Requires("core.command.notifications.stats")
    public void stats(@Sender Player player, INotification notification) {
        CompletableFuture<Integer> completableFuture =
                CompletableFuture.supplyAsync(
                        () ->
                                this.plugin
                                        .getNotificationHandler()
                                        .getReadsForNotification(notification)
                                        .size());
        CompletableFuture<Integer> completableFuture2 =
                CompletableFuture.supplyAsync(
                        () ->
                                this.plugin
                                        .getNotificationHandler()
                                        .getViewsForNotification(notification)
                                        .size());
        completableFuture.thenAcceptBothAsync(
                completableFuture2,
                (result, result2) ->
                        this.plugin
                                .getMenuHandler()
                                .openMenuSync(
                                        new NotificationDetailsMenu(notification, result, result2),
                                        player));
    }

    public NotificationsCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
