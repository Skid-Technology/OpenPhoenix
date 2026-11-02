package xyz.refinedev.phoenix.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.disguise.IDisguiseData;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.general.Tasks;

@Register(
        name = "redisguise",
        aliases = {"red", "redis", "renick"})
@Requires("core.command.redisguise")
public class RedisguiseCommand {

    private final Phoenix plugin;

    @Command(name = "", async = true)
    public void execute(@Sender Player player) {
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                .getDisguiseData()
                .isDisguised()) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.DISGUISE.ALREADY_DISGUISED)
                            .replace("<color>", profile.getNameColor())
                            .replace("<name>", profile.getPlayerName()));
        }
        if (!this.plugin.getDisguiseHandler().hasPreviouslyDisguised(player.getUniqueId())) {
            throw new ExitMessage(LocaleUtil.tr(player, MessageConfig.DISGUISE.NO_PREV_DISGUISE));
        }
        if (!player.hasPermission("core.cooldowns.bypass")
                && profile.hasGlobalCooldown("redisguise")) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.COOLDOWNS.REDISGUISE_COOLDOWN)
                            .replace(
                                    "<remaining>",
                                    DateUtils.formatTimeMillis(
                                            profile.getGlobalRemaining("redisguise"))));
        }
        IDisguiseData disguiseData;
        String text;
        if (Bukkit.getPlayer(
                        text =
                                (disguiseData =
                                                this.plugin
                                                        .getDisguiseHandler()
                                                        .getPreviousDisguiseData(
                                                                player.getUniqueId()))
                                        .getDisguiseName())
                != null) {
            throw new ExitMessage(LocaleUtil.tr(player, MessageConfig.DISGUISE.NOT_ALLOWED));
        }
        if (this.plugin.getDisguiseHandler().isNameInUse(text).join()) {
            throw new ExitMessage(LocaleUtil.tr(player, MessageConfig.DISGUISE.NOT_ALLOWED));
        }
        if (!SettingsConfig.DISGUISE.NAMES.contains(text)) {
            throw new ExitMessage(LocaleUtil.tr(player, MessageConfig.DISGUISE.NOT_ALLOWED));
        }
        IDisguiseData disguiseData2 = disguiseData;
        IProfile profile2 = profile;
        Tasks.run(() -> this.plugin.getDisguiseHandler().disguise(profile2, disguiseData2, true));
    }

    public RedisguiseCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
