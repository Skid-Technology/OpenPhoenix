package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.packet.impl.server.SendToStaffPacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "panic")
@Requires("core.command.panic")
public class PanicCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(@Sender Player player) {
        if (this.plugin.getPanicHandler().isPanic(player.getUniqueId())) {
            throw new ExitMessage(LocaleUtil.tr(player, MessageConfig.PANIC.ALREADY_PANIC));
        }
        IProfile profile = this.plugin.getProfileHandler().getProfile(player.getUniqueId());
        this.plugin.getPanicHandler().setPanic(player.getUniqueId(), true);
        player.sendMessage(Color.translate(LocaleUtil.tr(player, MessageConfig.PANIC.PANICKED)));
        for (String text : LocaleUtil.tr(player, MessageConfig.PANIC.MESSAGE)) {
            player.sendMessage(Color.translate(text));
        }
        if (profile.isStaff()) {
            this.plugin.getAnomalyHandler().alertPanic(player);
        }
        this.plugin
                .getPacketHandler()
                .send(
                        new SendToStaffPacket(
                                MessageConfig.STAFF.PANIC_ALERT,
                                LocaleUtil.replacements(
                                        "<server>",
                                        this.plugin.getNetworkHandler().getDisplayName(),
                                        "<player>",
                                        profile.getNameWithColorNoDisguise()),
                                false,
                                true));
    }

    public PanicCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
