package xyz.refinedev.phoenix.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "hidestaff", aliases = "showstaff")
@Requires("core.command.hidestaff")
public class HidestaffCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(@Sender Player player) {
        IProfile profile;
        (profile = this.plugin.getProfileHandler().getProfile(player.getUniqueId()))
                .updateMetadata("show-staff", !profile.isSetting("show-staff"), false);
        if (profile.isSetting("show-staff")) {
            for (Player player2 : Bukkit.getOnlinePlayers()) {
                if (this.plugin.getVanishHandler().isVanished(player2)
                        && Phoenix.getInstance().getVisibilityHandler().canSee(player, player2)) {
                    player.showPlayer(player2);
                }
            }
            player.sendMessage(
                    Color.translate(LocaleUtil.tr(player, MessageConfig.VANISH.SHOW_STAFF)));
        } else {
            for (Player player3 : Bukkit.getOnlinePlayers()) {
                if (this.plugin.getVanishHandler().isVanished(player3)) {
                    player.hidePlayer(player3.getPlayer());
                }
            }
            player.sendMessage(
                    Color.translate(LocaleUtil.tr(player, MessageConfig.VANISH.HIDE_STAFF)));
        }
    }

    public HidestaffCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
