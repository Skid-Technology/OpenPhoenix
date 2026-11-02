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
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.Collection;

@Register(name = "heal")
@Requires("core.command.heal")
public class HealCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(
            @Sender Player player, @Optional @Requires("core.command.heal.others") Player player2) {
        if (player2 != null) {
            player2.setHealth(player2.getMaxHealth());
            player2.setFoodLevel(20);
            if (SettingsConfig.BASICS.HEAL_RESET_POTION_EFFECTS) {
                Collection<org.bukkit.potion.PotionEffect> activePotionEffects =
                        player2.getActivePotionEffects();
                activePotionEffects.forEach(result -> player2.removePotionEffect(result.getType()));
            }
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.HEAL.OTHER)
                                    .replace(
                                            "<player>",
                                            this.plugin
                                                    .getProfileHandler()
                                                    .getProfile(player2.getUniqueId())
                                                    .getNameWithColorNoDisguise())));
            player2.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player2, MessageConfig.BASIC.ADMIN.HEAL.R_OTHER)
                                    .replace(
                                            "<player>",
                                            this.plugin
                                                    .getProfileHandler()
                                                    .getProfile(player.getUniqueId())
                                                    .getNameWithColorNoDisguise())));
            BukkitAPI.sendAdminAlert(
                    player,
                    MessageConfig.BASIC.ADMIN.ADMIN_ALERTS.HEALED_SOMEONE.replace(
                            "<target>",
                            this.plugin
                                    .getProfileHandler()
                                    .getProfile(player2.getUniqueId())
                                    .getNameWithColorNoDisguise()));
        } else {
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            if (SettingsConfig.BASICS.HEAL_RESET_POTION_EFFECTS) {
                Collection<org.bukkit.potion.PotionEffect> activePotionEffects2 =
                        player.getActivePotionEffects();
                activePotionEffects2.forEach(result -> player.removePotionEffect(result.getType()));
            }
            player.sendMessage(
                    Color.translate(LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.HEAL.SELF)));
            BukkitAPI.sendAdminAlert(player, MessageConfig.BASIC.ADMIN.ADMIN_ALERTS.SELF_HEAL);
        }
    }

    public HealCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
