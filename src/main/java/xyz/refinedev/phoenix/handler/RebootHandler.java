package xyz.refinedev.phoenix.handler;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.folialib.wrapper.task.WrappedTask;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.internal.RebootTask;
import xyz.refinedev.phoenix.packet.api.IPacketHandler;
import xyz.refinedev.phoenix.packet.impl.server.SendToStaffPacket;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.general.Tasks;

public class RebootHandler {

    private final Phoenix plugin;

    private RebootTask rebootTask;

    private WrappedTask wrappedTask = null;

    public void reboot(CommandSender sender, long value) {
        String text =
                sender instanceof Player
                        ? this.plugin
                                .getProfileHandler()
                                .getCachedProfile(((Player) sender).getUniqueId())
                                .getNameWithColorNoDisguise()
                        : GlobalConfig.SERVER.CONSOLE_NAME;
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.REBOOT.SCHEDULED)
                                .replace(
                                        "<server>",
                                        this.plugin.getNetworkHandler().getDisplayName())
                                .replace("<player>", text)
                                .replace("<time>", DateUtils.formatTimeMillis(value))));
        IPacketHandler packetHandler = this.plugin.getPacketHandler();
        String text2 = MessageConfig.REBOOT.SCHEDULED_STAFF;
        String[] texts = new String[6];
        texts[0] = "<server>";
        texts[1] = this.plugin.getNetworkHandler().getDisplayName();
        texts[2] = "<player>";
        texts[3] = text;
        texts[4] = "<time>";
        texts[5] = DateUtils.formatTimeMillis(value);
        packetHandler.send(
                new SendToStaffPacket(text2, LocaleUtil.replacements(texts), true, false));
        this.rebootTask = new RebootTask(this.plugin, value);
        if (this.plugin.getFoliaLib().isFolia()) {
            this.wrappedTask = Tasks.runWrappedTimer(this.rebootTask, 20L, 20L);
        } else {
            this.rebootTask.runTaskTimer(this.plugin, 20L, 20L);
        }
    }

    public void cancel(CommandSender sender) {
        if (this.rebootTask != null) {
            String text =
                    sender instanceof Player
                            ? this.plugin
                                    .getProfileHandler()
                                    .getCachedProfile(((Player) sender).getUniqueId())
                                    .getNameWithColorNoDisguise()
                            : GlobalConfig.SERVER.CONSOLE_NAME;
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.REBOOT.CANCELLED)
                                    .replace(
                                            "<server>",
                                            this.plugin.getNetworkHandler().getDisplayName())
                                    .replace("<player>", text)));
            this.plugin
                    .getPacketHandler()
                    .send(
                            new SendToStaffPacket(
                                    MessageConfig.REBOOT.CANCELLED_STAFF,
                                    LocaleUtil.replacements(
                                            "<server>",
                                            this.plugin.getNetworkHandler().getDisplayName(),
                                            "<player>",
                                            text),
                                    true,
                                    false));
            if (this.wrappedTask != null) {
                this.wrappedTask.cancel();
            } else {
                this.rebootTask.cancel();
            }
            this.rebootTask = null;
        }
    }

    public long getTimeRemaining() {
        return this.rebootTask == null ? -1L : this.rebootTask.getTime();
    }

    public boolean isRebootScheduled() {
        return this.rebootTask != null;
    }

    public RebootHandler(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
