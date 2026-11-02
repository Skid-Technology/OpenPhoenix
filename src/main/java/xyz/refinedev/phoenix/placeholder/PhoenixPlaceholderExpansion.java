package xyz.refinedev.phoenix.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.TimeConstants;

import java.util.List;
import java.util.Set;

public class PhoenixPlaceholderExpansion extends PlaceholderExpansion {

    private final Phoenix plugin;

    private static Player findPlayer(IProfile profile) {
        return profile == null ? null : Bukkit.getPlayer(profile.getUniqueId());
    }

    @NotNull
    public String getIdentifier() {
        return "phoenix";
    }

    @NotNull
    public String getAuthor() {
        return "Refine Development";
    }

    public boolean persist() {
        return true;
    }

    @NotNull
    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Nullable
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String text) {
        if (offlinePlayer == null) {
            return this.formatString(text, null);
        }
        IProfile profile;
        return (profile =
                                this.plugin
                                        .getProfileHandler()
                                        .getCachedProfile(offlinePlayer.getUniqueId()))
                        == null
                ? this.formatString(text, null)
                : this.formatString(text, profile);
    }

    public String onPlaceholderRequest(Player player, @NotNull String text) {
        if (player == null) {
            return this.formatString(text, null);
        }
        IProfile profile;
        return (profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                        == null
                ? this.formatString(text, null)
                : this.formatString(text, profile);
    }

    private String formatString(@NotNull String text, IProfile profile) {
        if (text.startsWith("server_")) {
            if (text.startsWith("server_whitelisted_")) {
                String text2 = text.toLowerCase().replace("server_whitelisted_", "");
                IServerData serverData;
                if ((serverData = this.plugin.getNetworkHandler().getServerByName(text2)) == null) {
                    return "";
                }
                return serverData.isWhitelisted() ? "&atrue" : "&cfalse";
            }
            if (text.startsWith("server_online_")) {
                String text3 = text.toLowerCase().replace("server_online_", "");
                IServerData serverData2;
                if ((serverData2 = this.plugin.getNetworkHandler().getServerByName(text3))
                        == null) {
                    return "";
                }
                return String.valueOf(
                        this.plugin
                                .getNetworkHandler()
                                .getNonVanishedOnlinePlayers(serverData2)
                                .size());
            }
            if (text.startsWith("server_status_")) {
                String text4 = text.toLowerCase().replace("server_status_", "");
                IServerData serverData3;
                if ((serverData3 = this.plugin.getNetworkHandler().getServerByName(text4))
                        == null) {
                    return Color.translate(
                            LocaleUtil.tr(
                                    findPlayer(profile),
                                    MessageConfig.GENERAL.SERVER_STATUS.OFFLINE));
                }
                if (!serverData3.isOnline()) {
                    return Color.translate(
                            LocaleUtil.tr(
                                    findPlayer(profile),
                                    MessageConfig.GENERAL.SERVER_STATUS.OFFLINE));
                }
                return serverData3.isWhitelisted()
                        ? Color.translate(
                                LocaleUtil.tr(
                                        findPlayer(profile),
                                        MessageConfig.GENERAL.SERVER_STATUS.WHITELISTED))
                        : Color.translate(
                                LocaleUtil.tr(
                                        findPlayer(profile),
                                        MessageConfig.GENERAL.SERVER_STATUS.ONLINE));
            }
            if (text.startsWith("server_max_")) {
                String text5 = text.toLowerCase().replace("server_max_", "");
                IServerData serverData4;
                if ((serverData4 = this.plugin.getNetworkHandler().getServerByName(text5))
                        == null) {
                    return "";
                }
                return String.valueOf(serverData4.getMaxPlayers());
            }
            if (text.startsWith("server_displayname_")) {
                String text6 = text.toLowerCase().replace("server_displayname_", "");
                IServerData serverData5;
                if ((serverData5 = this.plugin.getNetworkHandler().getServerByName(text6))
                        == null) {
                    return "";
                }
                return serverData5.getDisplayName();
            }
        } else if (text.startsWith("group_")) {
            if (text.startsWith("group_online_")) {
                String text7 = text.replace("group_online_", "");
                Set<IServerData> values;
                if ((values = this.plugin.getNetworkHandler().getServersInGroup(text7)).isEmpty()) {
                    return "0";
                }
                int value = 0;
                for (IServerData serverData6 : values) {
                    value +=
                            this.plugin
                                    .getNetworkHandler()
                                    .getNonVanishedOnlinePlayers(serverData6)
                                    .size();
                }
                return String.valueOf(value);
            }
            if (text.startsWith("group_max_")) {
                String text8 = text.replace("group_max_", "");
                Set<IServerData> values2;
                if ((values2 = this.plugin.getNetworkHandler().getServersInGroup(text8))
                        .isEmpty()) {
                    return "0";
                }
                int value2 = 0;
                for (IServerData serverData7 : values2) {
                    value2 += serverData7.getMaxPlayers();
                }
                return String.valueOf(value2);
            }
        }
        if (profile == null) {
            return "";
        }
        if (text.toLowerCase().startsWith("setting_")) {
            String text9 = text.toLowerCase().substring("setting_".length());
            return Boolean.toString(profile.isSetting(text9));
        }
        switch (text.toLowerCase()) {
            case "player_rank":
                return Color.translate(profile.getHighestRank().getDisplayName());
            case "player_rank_name":
                return profile.getHighestRank().getName();
            case "player_rank_color":
                return Color.translate(profile.getHighestRank().getColor());
            case "player_rank_chatcolor":
                return Color.translate(profile.getHighestRank().getChatColor());
            case "player_rank_prefix":
                return Color.translate(profile.getHighestRank().getPrefix());
            case "player_rank_playerlist_prefix":
                return Color.translate(profile.getHighestRank().getPlayerListPrefix());
            case "player_rank_suffix":
                return Color.translate(profile.getHighestRank().getSuffix());
            case "player_rank_priority":
                return String.valueOf(profile.getHighestRank().getPriority());
            case "player_rank_icon":
                return Phoenix.getInstance()
                        .getRankIconHandler()
                        .formatString(profile.getHighestRank());
            case "player_rank_duration":
                IGrant grant;
                if ((grant = profile.getBestGrant()) == null) {
                    return TimeConstants.PERMANENT;
                }
                return grant.getNiceDuration();
            case "player_rank_expiration":
                IGrant grant2;
                if ((grant2 = profile.getBestGrant()) == null) {
                    return TimeConstants.PERMANENT;
                }
                return grant2.getNiceExpire();
            case "player_real_rank":
                return Color.translate(profile.getGeneralRank().getDisplayName());
            case "player_real_rank_name":
                return profile.getGeneralRank().getName();
            case "player_real_rank_prefix":
                return Color.translate(profile.getGeneralRank().getPrefix());
            case "player_real_rank_playerlist_prefix":
                return Color.translate(profile.getGeneralRank().getPlayerListPrefix());
            case "player_real_rank_suffix":
                return Color.translate(profile.getGeneralRank().getSuffix());
            case "player_real_rank_color":
                return Color.translate(profile.getGeneralRank().getColor());
            case "player_real_rank_chatcolor":
                return Color.translate(profile.getGeneralRank().getChatColor());
            case "player_real_rank_priority":
                return String.valueOf(profile.getGeneralRank().getPriority());
            case "player_real_rank_duration":
                IGrant grant3;
                if ((grant3 = profile.getBestGeneralGrant()) == null) {
                    return TimeConstants.PERMANENT;
                }
                return grant3.getNiceDuration();
            case "player_real_rank_expiration":
                IGrant grant4;
                if ((grant4 = profile.getBestGeneralGrant()) == null) {
                    return TimeConstants.PERMANENT;
                }
                return grant4.getNiceExpire();
            case "player_name":
                return profile.getPlayerName();
            case "player_real_name":
                return profile.getName();
            case "player_tag":
                return BukkitAPI.getTagPrefix(profile);
            case "player_channel":
                return profile.getSetting("channel", "Default");
            case "player_hidingstaff":
                return !profile.isSetting("show-staff") ? "Hiding Staff" : "Showing Staff";
            case "player_vanished":
                return profile.isVanished() ? "Vanished" : "Visible";
            case "player_disguised":
                return profile.getDisguiseData().isDisguised() ? "Disguised" : "Not Disguised";
            case "player_level_name":
                return this.plugin.getLevelHook().getLevelNameWithColor(profile);
            case "player_level_displayname":
                return this.plugin.getLevelHook().getLevelDisplayName(profile);
            case "player_exp":
                return String.valueOf(this.plugin.getLevelHook().getExperience(profile));
            case "player_modmode":
                Player player;
                if ((player = Bukkit.getPlayer(profile.getUniqueId())) == null) {
                    return "Offline";
                }
                return player.hasMetadata("ModMode") ? "In ModMode" : "Not In ModMode";
            case "player_playtime_seconds":
                List<xyz.refinedev.phoenix.profile.login.ILogin> values3;
                if ((values3 = this.plugin.getLoginHandler().getLogins(profile.getUuid()))
                        .isEmpty()) {
                    return "0";
                }
                return String.valueOf(profile.getPlayTime(values3) / 1000L);
            case "player_playtime":
                List<xyz.refinedev.phoenix.profile.login.ILogin> values4;
                if ((values4 = this.plugin.getLoginHandler().getLogins(profile.getUuid()))
                        .isEmpty()) {
                    return "0s";
                }
                return DateUtils.formatTimeMillis(profile.getPlayTime(values4));
            case "player_afktime_seconds":
                return String.valueOf(profile.getAfkTime() / 1000L);
            case "player_afktime":
                return DateUtils.formatTimeMillis(profile.getAfkTime());
            case "server_whitelisted":
                return this.plugin.getWhitelistHandler().isWhitelisted() ? "&atrue" : "&cfalse";
            case "server_online":
                return String.valueOf(
                        this.plugin
                                .getNetworkHandler()
                                .getNonVanishedOnlinePlayers(
                                        this.plugin
                                                .getNetworkHandler()
                                                .getPresence(profile.getUuid()))
                                .size());
            case "server_status":
                IServerData serverData8;
                if ((serverData8 =
                                this.plugin
                                        .getNetworkHandler()
                                        .getServerByName(GlobalConfig.SERVER.NAME))
                        == null) {
                    return Color.translate(
                            LocaleUtil.tr(
                                    findPlayer(profile),
                                    MessageConfig.GENERAL.SERVER_STATUS.OFFLINE));
                } else {
                    if (!serverData8.isOnline()) {
                        return Color.translate(
                                LocaleUtil.tr(
                                        findPlayer(profile),
                                        MessageConfig.GENERAL.SERVER_STATUS.OFFLINE));
                    }
                    return serverData8.isWhitelisted()
                            ? Color.translate(
                                    LocaleUtil.tr(
                                            findPlayer(profile),
                                            MessageConfig.GENERAL.SERVER_STATUS.WHITELISTED))
                            : Color.translate(
                                    LocaleUtil.tr(
                                            findPlayer(profile),
                                            MessageConfig.GENERAL.SERVER_STATUS.ONLINE));
                }
            case "server_global_online":
                return String.valueOf(this.plugin.getNetworkHandler().getOnline());
            case "server_name":
                return this.plugin.getNetworkHandler().getServerName();
            case "server_displayname":
                return this.plugin.getNetworkHandler().getDisplayName();
            default:
                return "";
        }
    }

    public PhoenixPlaceholderExpansion(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
