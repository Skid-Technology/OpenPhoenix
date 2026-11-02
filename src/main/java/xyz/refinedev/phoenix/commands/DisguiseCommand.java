package xyz.refinedev.phoenix.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.commands.api.annotations.ValidName;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.menu.impl.DisguiseRankMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(
        name = "disguise",
        aliases = {"d", "dis", "nick"})
@Requires("core.command.disguise")
public class DisguiseCommand {

    private final Phoenix plugin;

    @Command(name = "", async = true)
    public void execute(
            @Sender Player player,
            @ValidName @Requires("core.command.disguise.name") @Named("name") @Optional
                    String text) {
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                .getDisguiseData()
                .isDisguised()) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.DISGUISE.ALREADY_DISGUISED)
                            .replace("<color>", profile.getNameColor())
                            .replace("<name>", profile.getPlayerName()));
        }
        if (!player.hasPermission("core.cooldowns.bypass")
                && profile.hasGlobalCooldown("disguise")) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.COOLDOWNS.DISGUISE_COOLDOWN)
                            .replace(
                                    "<remaining>",
                                    DateUtils.formatTimeMillis(
                                            profile.getGlobalRemaining("disguise"))));
        }
        String text2;
        if (text != null) {
            if (SettingsConfig.DISGUISE.BLACKLISTED_NAMES.contains(text.toLowerCase())) {
                throw new ExitMessage(LocaleUtil.tr(player, MessageConfig.DISGUISE.NOT_ALLOWED));
            }
            if (Bukkit.getPlayer(text) != null
                    || this.plugin.getProfileHandler().isInOnlineCache(text)) {
                throw new ExitMessage(LocaleUtil.tr(player, MessageConfig.DISGUISE.NOT_ALLOWED));
            }
            if (this.plugin.getDisguiseHandler().isNameInUse(text).join()) {
                throw new ExitMessage(LocaleUtil.tr(player, MessageConfig.DISGUISE.NOT_ALLOWED));
            }
            if (!SettingsConfig.DISGUISE.DISGUISE_AS_REAL_PLAYER
                    && !player.hasPermission("core.disguise.real.bypass")
                    && this.plugin.getProfileHandler().hasProfile(text)) {
                throw new ExitMessage(LocaleUtil.tr(player, MessageConfig.DISGUISE.NOT_ALLOWED));
            }
            text2 = text;
        } else {
            if (this.plugin.getDisguiseHandler().getAvailableNames().isEmpty()) {
                throw new ExitMessage(
                        LocaleUtil.tr(player, MessageConfig.DISGUISE.NO_AVAILABLE_NAMES));
            }
            String text3;
            if ((text3 = this.plugin.getDisguiseHandler().getRandomName()) == null) {
                throw new ExitMessage(
                        LocaleUtil.tr(player, MessageConfig.DISGUISE.NO_AVAILABLE_NAMES));
            }
            text2 = text3;
        }
        this.plugin.getMenuHandler().openMenuSync(new DisguiseRankMenu(text2), player);
    }

    public DisguiseCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
