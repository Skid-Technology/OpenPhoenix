package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(
        name = "modmode",
        aliases = {"staffmode", "mod", "h", "staff"})
@Requires("core.command.modmode")
public class ModmodeCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(
            @Sender Player player,
            @Optional @Requires("core.command.modmode.others") Player player2) {
        if (player2 != null) {
            IProfile profile = this.plugin.getProfileHandler().getProfile(player2.getUniqueId());
            if (player2.hasMetadata("ModMode")) {
                this.plugin.getModModeHandler().sendMessageByFORCE_MODMODE(player2);
                player2.sendMessage(
                        Color.translate(LocaleUtil.tr(player2, MessageConfig.MODMODE.DISABLED)));
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.MODMODE.DISABLED_OTHER)
                                        .replace("<player>", player2.getName())));
                BukkitAPI.sendAdminAlert(
                        player,
                        MessageConfig.STAFF.ADMIN_ALERTS.MOD_OTHER_OFF.replace(
                                "<target>", profile.getNameWithColorNoDisguise()));
            } else {
                this.plugin.getModModeHandler().sendMessage(player2);
                player2.sendMessage(
                        Color.translate(LocaleUtil.tr(player2, MessageConfig.MODMODE.ENABLED)));
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.MODMODE.ENABLED_OTHER)
                                        .replace("<player>", player2.getName())));
                BukkitAPI.sendAdminAlert(
                        player,
                        MessageConfig.STAFF.ADMIN_ALERTS.MOD_OTHER_ON.replace(
                                "<target>", profile.getNameWithColorNoDisguise()));
            }
        } else {
            if (player.hasMetadata("ModMode")) {
                this.plugin.getModModeHandler().sendMessageByFORCE_MODMODE(player);
                player.sendMessage(
                        Color.translate(LocaleUtil.tr(player, MessageConfig.MODMODE.DISABLED)));
                BukkitAPI.sendAdminAlert(player, MessageConfig.STAFF.ADMIN_ALERTS.SELF_MOD_OFF);
            } else {
                this.plugin.getModModeHandler().sendMessage(player);
                player.sendMessage(
                        Color.translate(LocaleUtil.tr(player, MessageConfig.MODMODE.ENABLED)));
                BukkitAPI.sendAdminAlert(player, MessageConfig.STAFF.ADMIN_ALERTS.SELF_MOD_ON);
            }
        }
    }

    public ModmodeCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
