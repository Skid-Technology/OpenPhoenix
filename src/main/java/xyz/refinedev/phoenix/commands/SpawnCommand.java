package xyz.refinedev.phoenix.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.CommandHandler;
import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.bukkit.actor.BukkitActor;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.server.LocationSerilization;

import java.util.List;

@Register(name = "spawn")
public class SpawnCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(
            @Sender Player player,
            CommandHandler commandHandler,
            @Optional @Requires("core.command.spawn.others") Player player2) {
        if (player2 != null) {
            try {
                this.plugin
                        .getNmsHandler()
                        .getTeleportAPI()
                        .teleport(
                                player2,
                                LocationSerilization.deserializeLocation(
                                        SettingsConfig.ON_JOIN.TELEPORT_LOCATION));
                player2.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player2, MessageConfig.GENERAL.SPAWN_TELEPORTED)));
            } catch (Exception exception) {
                Bukkit.getConsoleSender()
                        .sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(
                                                Bukkit.getConsoleSender(),
                                                MessageConfig.PHOENIX.SPAWN_NOT_SET)));
            }
        } else if (!player.hasPermission("core.command.spawn")) {
            this.applyPlayer(player, commandHandler);
        } else {
            IProfile cachedProfile =
                    this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId());
            if (!player.hasPermission("core.cooldowns.bypass")
                    && cachedProfile.hasLocalCooldown("spawn")) {
                throw new ExitMessage(
                        LocaleUtil.tr(player, MessageConfig.COOLDOWNS.SPAWN_COOLDOWN)
                                .replace(
                                        "<remaining>",
                                        DateUtils.formatTimeMillis(
                                                cachedProfile.getLocalRemaining("spawn"))));
            }
            try {
                this.plugin
                        .getNmsHandler()
                        .getTeleportAPI()
                        .teleport(
                                player,
                                LocationSerilization.deserializeLocation(
                                        SettingsConfig.ON_JOIN.TELEPORT_LOCATION));
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.GENERAL.SPAWN_TELEPORTED)));
                this.plugin
                        .getCooldownHandler()
                        .placeOnCooldown(
                                "spawn", player.getUniqueId(), BukkitAPI.getDuration("3s"));
            } catch (Exception exception2) {
                Bukkit.getConsoleSender()
                        .sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(
                                                Bukkit.getConsoleSender(),
                                                MessageConfig.PHOENIX.SPAWN_NOT_SET)));
            }
        }
    }

    private void applyPlayer(Player player, CommandHandler commandHandler) {
        BukkitActor bukkitActor = new BukkitActor(player, commandHandler);
        List<String> noPermission = commandHandler.getLocale().getNoPermission();
        noPermission.forEach(bukkitActor::sendMessage);
    }

    public SpawnCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
