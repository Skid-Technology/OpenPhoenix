package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "kill")
@Requires("core.command.kill")
public class KillCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(@Sender Player player, Player player2) {
        IProfile profile = this.plugin.getProfileHandler().getProfile(player.getUniqueId());
        IProfile profile2 = this.plugin.getProfileHandler().getProfile(player2.getUniqueId());
        player2.damage(1000.0);
        player.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.KILL.PLAYER_KILL)
                                .replace("<player>", profile2.getNameWithColor())));
        if (!player.equals(player2)) {
            player2.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player2, MessageConfig.BASIC.ADMIN.KILL.KILL_RESPONSE)
                                    .replace("<player>", profile.getNameWithColor())));
        }
        BukkitAPI.sendAdminAlert(
                player,
                MessageConfig.BASIC.ADMIN.ADMIN_ALERTS.KILL_OTHER.replace(
                        "<target>", profile2.getNameWithColorNoDisguise()));
    }

    public KillCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
