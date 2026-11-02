package xyz.refinedev.phoenix.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.commands.api.annotations.PlayerNames;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.StringUtils;
import xyz.refinedev.phoenix.whitelist.WhitelistAccess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Help
@Register(name = "whitelist", aliases = "wl")
@Requires("core.command.whitelist")
public class WhitelistCommand {

    private final CommonPlatform platform;

    @Command(name = "toggle", description = "Enable/disable the whitelist")
    public void toggle(
            @Sender CommandSender sender, @Optional @Named("true/false") Boolean matches) {
        if (matches == null) {
            matches = !this.platform.getWhitelistHandler().isWhitelisted();
        }
        this.platform.getWhitelistHandler().setEnabled(matches);
        if (matches) {
            sender.sendMessage(
                    Color.translate(LocaleUtil.tr(sender, MessageConfig.WHITELIST.ENABLED)));
        } else {
            sender.sendMessage(
                    Color.translate(LocaleUtil.tr(sender, MessageConfig.WHITELIST.DISABLED)));
        }
        BukkitAPI.kickUnwhitelistedUsers();
    }

    @Command(name = "access", description = "Set the whitelist access type")
    public void access(
            @Sender CommandSender sender, WhitelistAccess whitelistAccess, @Optional IRank rank) {
        if (whitelistAccess == WhitelistAccess.RANK && rank != null) {
            this.platform.getWhitelistHandler().updateRank(rank);
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.WHITELIST.ACCESS_RANK)
                                    .replace("<rank>", rank.getDisplayName())));
        } else if (this.platform.getWhitelistHandler().hasAccessType(whitelistAccess)) {
            if (this.platform.getWhitelistHandler().getAccessTypes().size() <= 1) {
                throw new ExitMessage(
                        LocaleUtil.tr(sender, MessageConfig.WHITELIST.NEED_ACCESS_TYPE));
            }
            this.platform.getWhitelistHandler().removeAccessType(whitelistAccess);
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.WHITELIST.ACCESS_TYPE_OFF)
                                    .replace("<type>", whitelistAccess.getDisplayName())));
        } else {
            this.platform.getWhitelistHandler().addAccessType(whitelistAccess);
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.WHITELIST.ACCESS_TYPE_ON)
                                    .replace("<type>", whitelistAccess.getDisplayName())));
        }
        BukkitAPI.kickUnwhitelistedUsers();
    }

    @Command(name = "add", description = "Add a user to the whitelist")
    public void add(
            @Sender CommandSender sender,
            @Named("player") @PlayerNames String text,
            @Named("permanent [true/false]") boolean matches) {
        String text2 = text;
        if (!GlobalConfig.WHITELIST.NAME_BASED) {
            UUID uUID;
            if ((uUID = this.platform.getUuidCache().getUuid(text)) == null) {
                throw new ExitMessage(
                        LocaleUtil.tr(sender, MessageConfig.WHITELIST.DOESNT_EXIST)
                                .replace("<player>", text));
            }
            text2 = uUID.toString();
        }
        if (this.platform.getWhitelistHandler().isWhitelisted(text2, matches)) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.WHITELIST.ALREADY_ADDED)
                            .replace("<player>", text));
        }
        this.platform.getWhitelistHandler().whitelistPlayer(text2, matches);
        if (matches) {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.WHITELIST.ADDED_PERM)
                                    .replace("<player>", text)));
        } else {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.WHITELIST.ADDED_TEMP)
                                    .replace("<player>", text)));
        }
    }

    @Command(name = "remove", description = "Remove a user from the whitelist")
    public void remove(@Sender CommandSender sender, @PlayerNames @Named("player") String text) {
        String text2 = text;
        if (!GlobalConfig.WHITELIST.NAME_BASED) {
            UUID uUID;
            if ((uUID = this.platform.getUuidCache().getUuid(text)) == null) {
                throw new ExitMessage(
                        LocaleUtil.tr(sender, MessageConfig.WHITELIST.DOESNT_EXIST)
                                .replace("<player>", text));
            }
            text2 = uUID.toString();
        }
        if (!this.platform.getWhitelistHandler().isWhitelisted(text2, false)) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.WHITELIST.NOT_ADDED)
                            .replace("<player>", text));
        }
        this.platform.getWhitelistHandler().unwhitelistPlayer(text2);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.WHITELIST.REMOVED)
                                .replace("<player>", text)));
        BukkitAPI.kickUnwhitelistedUsers();
    }

    @Command(
            name = "info",
            aliases = {"list", "status"},
            description = "View information about the current whitelist",
            async = true)
    public void info(@Sender CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "Fetching status...");
        List<String> whitelistedUsers = this.platform.getWhitelistHandler().getWhitelistedUsers();
        ArrayList values =
                FastStreams.mapArrayList(
                        whitelistedUsers,
                        result -> {
                            if (GlobalConfig.WHITELIST.NAME_BASED) {
                                return GlobalConfig.WHITELIST.PLAYERS.contains(result)
                                        ? MessageConfig.WHITELIST.PLAYER_FORMAT.replace(
                                                "<player>", result)
                                        : MessageConfig.WHITELIST.PLAYER_TEMPORARY_FORMAT.replace(
                                                "<player>", result);
                            }
                            String nameFromString =
                                    this.platform.getUuidCache().getNameFromString(result);
                            return GlobalConfig.WHITELIST.PLAYERS.contains(result)
                                    ? MessageConfig.WHITELIST.PLAYER_FORMAT.replace(
                                            "<player>", nameFromString)
                                    : MessageConfig.WHITELIST.PLAYER_TEMPORARY_FORMAT.replace(
                                            "<player>", nameFromString);
                        });
        Iterator<String> iterator2 =
                LocaleUtil.tr(sender, MessageConfig.WHITELIST.STATUS).iterator();
        while (iterator2.hasNext()) {
            String text =
                    ((String) iterator2.next())
                            .replace(
                                    "<enabled>",
                                    this.platform.getWhitelistHandler().isWhitelisted()
                                            ? "&atrue"
                                            : "&cfalse")
                            .replace(
                                    "<rank>",
                                    this.platform
                                            .getWhitelistHandler()
                                            .getWhitelistRank()
                                            .getDisplayName())
                            .replace(
                                    "<accessTypes>",
                                    StringUtils.join(
                                            this.platform
                                                    .getWhitelistHandler()
                                                    .getAccessTypesAsStrings(),
                                            "&f"))
                            .replace("<players>", StringUtils.join(values, "&f"));
            sender.sendMessage(Color.translate(text));
        }
    }

    public WhitelistCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
