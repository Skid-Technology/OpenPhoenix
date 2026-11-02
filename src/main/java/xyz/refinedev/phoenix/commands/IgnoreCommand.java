package xyz.refinedev.phoenix.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.StringUtils;

import java.util.Iterator;

@Help
@Register(name = "ignore")
public class IgnoreCommand {

    private final CommonPlatform platform;

    @Command(name = "add", description = "Ignore a player")
    public void add(@Sender Player player, @Option("coverdisguise") IProfile profile) {
        IProfile cachedProfile =
                this.platform.getProfileHandler().getCachedProfile(player.getUniqueId());
        if (profile.getPlayerName().equals(player.getName())) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.GENERAL.IGNORE.CANT_IGNORE_SELF));
        }
        if (cachedProfile.isIgnoring(profile.getUniqueId())) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.GENERAL.IGNORE.ALREADY_IGNORING)
                            .replace("<player>", profile.getNameWithColor()));
        }
        cachedProfile.getIgnoring().add(profile.getUniqueId());
        cachedProfile.saveAndUpdate(false);
        player.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player, MessageConfig.GENERAL.IGNORE.IGNORE_ADDED)
                                .replace("<player>", profile.getNameWithColor())));
    }

    @Command(name = "remove", description = "Unignore a player")
    public void remove(@Sender Player player, IProfile profile) {
        IProfile profile2;
        if (!(profile2 = this.platform.getProfileHandler().getCachedProfile(player.getUniqueId()))
                .isIgnoring(profile.getUniqueId())) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.GENERAL.IGNORE.NOT_IGNORING)
                            .replace("<player>", profile.getNameWithColor()));
        }
        profile2.getIgnoring().remove(profile.getUniqueId());
        profile2.saveAndUpdate(false);
        player.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player, MessageConfig.GENERAL.IGNORE.IGNORE_REMOVED)
                                .replace("<player>", profile.getNameWithColor())));
    }

    @Command(name = "list", description = "List the players ignored by you")
    public void list(@Sender Player player) {
        IProfile profile;
        if ((profile = this.platform.getProfileHandler().getCachedProfile(player.getUniqueId()))
                .getIgnoring()
                .isEmpty()) {
            throw new ExitMessage(LocaleUtil.tr(player, MessageConfig.GENERAL.IGNORE.NO_IGNORES));
        }
        Iterator<String> iterator2 =
                LocaleUtil.tr(player, MessageConfig.GENERAL.IGNORE.FORMAT).iterator();
        while (iterator2.hasNext()) {
            String text;
            if ((text = (String) iterator2.next()).contains("<names>")) {
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.GENERAL.IGNORE.NAMES)
                                        .replace(
                                                "<names>",
                                                StringUtils.niceBuilderWithAnd(
                                                        profile.getNiceIgnoreNames(),
                                                        ChatColor.GRAY.toString(),
                                                        ChatColor.GREEN.toString(),
                                                        " "
                                                                + LocaleUtil.tr(
                                                                        player,
                                                                        MessageConfig.GENERAL
                                                                                .GENERIC_LIST_AND)
                                                                + " "))));
            } else {
                player.sendMessage(Color.translate(text));
            }
        }
    }

    public IgnoreCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
