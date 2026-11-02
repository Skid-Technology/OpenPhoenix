package xyz.refinedev.phoenix;

import me.creaxx.api.NMSHandler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.chat.api.ChatChannelType;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.packet.impl.profile.ChatChannelMessagePacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.tag.ITag;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.Duration;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.events.player.PlayerChatInChannelEvent;
import xyz.refinedev.phoenix.utils.general.Tasks;
import xyz.refinedev.phoenix.whitelist.WhitelistAccess;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class BukkitAPI {

    public static CommonPlatform api = Phoenix.getInstance();

    public static Pattern UUID_PATTERN =
            Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");

    public static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_$]{1,16}$");

    public static final Pattern DISGUISE_VALID_NAME_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_$]{3,16}$");

    public static boolean isUuid(String text) {
        return UUID_PATTERN.matcher(text).find();
    }

    public static boolean isDisguiseValidName(String text) {
        return text != null
                && !text.isEmpty()
                && DISGUISE_VALID_NAME_PATTERN.matcher(text).matches();
    }

    public static boolean isValidName(String text) {
        return text != null
                && !text.isEmpty()
                && VALID_NAME_PATTERN.matcher(text.replace("*", "").replace(".", "")).matches();
    }

    public static void sendAdminAlert(Player player, String text) {
        IProfile cachedProfile = api.getProfileHandler().getCachedProfile(player.getUniqueId());
        Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
        while (iterator2.hasNext()) {
            Player player2;
            IProfile profile;
            if (!(player2 = (Player) iterator2.next()).getName().equals(player.getName())
                    && player2.hasPermission("core.admin")
                    && (profile =
                                    Phoenix.getInstance()
                                            .getProfileHandler()
                                            .getCachedProfile(player2.getUniqueId()))
                            != null
                    && profile.isSetting("admin-alerts")) {
                player2.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player2, MessageConfig.STAFF.ADMIN_ALERT)
                                        .replace(
                                                "<player>",
                                                cachedProfile.getNameWithColorNoDisguise())
                                        .replace("<action>", text)));
            }
        }
    }

    public static void broadcastToStaff(String text) {
        Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
        while (iterator2.hasNext()) {
            Player player;
            if ((player = (Player) iterator2.next()).hasPermission("core.admin")
                    && api.getProfileHandler()
                            .getCachedProfile(player.getUniqueId())
                            .isSetting("admin-alerts")) {
                player.sendMessage(Color.translate(text));
            }
        }
    }

    public static void broadcastToStaff(Function<Player, String> function) {
        Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
        while (iterator2.hasNext()) {
            Player player;
            if ((player = (Player) iterator2.next()).hasPermission("core.admin")
                    && api.getProfileHandler()
                            .getCachedProfile(player.getUniqueId())
                            .isSetting("admin-alerts")) {
                player.sendMessage((String) function.apply(player));
            }
        }
    }

    public static boolean isInModMode(Player player) {
        return player.hasMetadata("ModMode");
    }

    public static long parseDuration(String text) {
        if (!text.equalsIgnoreCase("perm") && !text.equalsIgnoreCase("permanent")) {
            try {
                return getDuration(text);
            } catch (Exception exception) {
            }
        }
        return -5L;
    }

    public static long parseDurationPunishment(String text) {
        Duration duration;
        return (duration = Duration.of(text)) == null ? -3L : duration.getTimeTime();
    }

    public static long getDuration(String text) {
        return DateUtils.getDuration(text);
    }

    public static void callEvent(Event event) {
        if (event.isAsynchronous()) {
            if (Bukkit.isPrimaryThread()) {
                Tasks.runAsync(() -> Bukkit.getPluginManager().callEvent(event));
            } else {
                Bukkit.getPluginManager().callEvent(event);
            }
        } else if (Bukkit.isPrimaryThread()) {
            Bukkit.getPluginManager().callEvent(event);
        } else {
            Tasks.run(() -> Bukkit.getPluginManager().callEvent(event));
        }
    }

    public static String getKickMessage(List<String> values) {
        if (values.isEmpty()) {
            return "Empty";
        }
        int value = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (String text : values) {
            value++;
            stringBuilder.append(text);
            if (value < values.size()) {
                stringBuilder.append("\n");
            }
        }
        return Color.translate(stringBuilder.toString());
    }

    public static String getUntranslatedStringFromList(List<String> values) {
        if (values.isEmpty()) {
            return "Empty";
        }
        int value = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (String text : values) {
            value++;
            stringBuilder.append(text);
            if (value < values.size()) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    @NotNull
    public static String getTagPrefix(IProfile profile) {
        ITag tag;
        if ((tag = profile.getTag()) != null
                && (hasTag(profile, tag) || profile.getCustomTag() != null)) {
            return !tag.isScheduleActive()
                    ? ""
                    : Color.translate(
                            SettingsConfig.FORMATS
                                    .TAGS_FORMAT
                                    .replace("<color>", tag.getColor())
                                    .replace("<tag>", tag.getPrefix()));
        } else {
            return "";
        }
    }

    @NotNull
    public static String getTagPrefix(Player player) {
        IProfile profile;
        ITag tag;
        if ((tag =
                                (profile =
                                                api.getProfileHandler()
                                                        .getCachedProfile(player.getUniqueId()))
                                        .getTag())
                        != null
                && (hasTag(player, tag) || profile.getCustomTag() != null)) {
            return !tag.isScheduleActive()
                    ? ""
                    : Color.translate(
                            SettingsConfig.FORMATS
                                    .TAGS_FORMAT
                                    .replace("<color>", tag.getColor())
                                    .replace("<tag>", tag.getPrefix()));
        } else {
            return "";
        }
    }

    public static boolean hasTag(IProfile profile, ITag tag) {
        return profile.checkPermission("core.tags." + tag.getName(), false)
                || profile.checkPermission("core.tags.*", false);
    }

    public static boolean hasTag(Player player, ITag tag) {
        return player.hasPermission("core.tags." + tag.getName())
                || player.hasPermission("core.tags.*");
    }

    public static void sendSomeStaffMessage(
            IProfile profile, ChatChannelType chatChannelType, String text) {
        if (!chatChannelType.canSendMessage(Bukkit.getPlayer(profile.getUniqueId()))) {
            profile.updateMetadata("channel", "Default", true);
        } else {
            callEvent(new PlayerChatInChannelEvent(chatChannelType, profile, text));
            api.getPacketHandler()
                    .send(
                            new ChatChannelMessagePacket(
                                    profile.getUuid(),
                                    chatChannelType.getName(),
                                    profile.getNameWithColorNoDisguise(),
                                    GlobalConfig.SERVER.DISPLAYNAME,
                                    text));
        }
    }

    public static void sendGlobalChatChannel(
            IProfile profile, ChatChannelType chatChannelType, String text) {
        if (!chatChannelType.canSendMessage(Bukkit.getPlayer(profile.getUniqueId()))) {
            profile.updateMetadata("channel", "Default", true);
        } else {
            callEvent(new PlayerChatInChannelEvent(chatChannelType, profile, text));
            api.getPacketHandler()
                    .send(
                            new ChatChannelMessagePacket(
                                    profile.getUuid(),
                                    chatChannelType.getName(),
                                    profile.getNameWithColorNoDisguise(),
                                    GlobalConfig.SERVER.DISPLAYNAME,
                                    text));
        }
    }

    public static void sendSomeStaffMessageConsole(ChatChannelType chatChannelType, String text) {
        api.getPacketHandler()
                .send(
                        new ChatChannelMessagePacket(
                                IProfile.getConsoleUuid(),
                                chatChannelType.getName(),
                                GlobalConfig.SERVER.CONSOLE_NAME,
                                GlobalConfig.SERVER.DISPLAYNAME,
                                text));
    }

    @Nullable
    public static IProfile getProfileByName(String text) {
        IProfile profile;
        String text2;
        if ((profile = api.getProfileHandler().getLocalProfileByName(text)) == null
                && (text2 = Phoenix.getInstance().getDisguiseHandler().getUuidByFakeName(text))
                        != null) {
            UUID uUID = UUID.fromString(text2);
            profile = api.getProfileHandler().getProfile(uUID);
        }
        if (profile == null) {
            UUID uUID2;
            if ((uUID2 = api.getUuidCache().getUuidNoMojang(text)) != null) {
                profile = api.getProfileHandler().getProfile(uUID2);
            } else {
                UUID uUID3;
                if ((uUID3 = api.getProfileHandler().getUuidFromName(text)) != null) {
                    profile = api.getProfileHandler().getProfile(uUID3);
                }
            }
        }
        return profile;
    }

    @Nullable
    public static IProfile getProfileByNameNoDisguise(String text) {
        IProfile profile;
        if ((profile = api.getProfileHandler().getLocalProfileByName(text)) == null) {
            UUID uUID;
            if ((uUID = api.getUuidCache().getUuidNoMojang(text)) != null) {
                profile = api.getProfileHandler().getProfile(uUID);
            } else if ((uUID = api.getProfileHandler().getUuidFromName(text)) != null) {
                profile = api.getProfileHandler().getProfile(uUID);
            }
        }
        return profile;
    }

    public static void kickUnwhitelistedUsers() {
        if (api.getWhitelistHandler().isWhitelisted()) {
            IRank rank = api.getRankHandler().getRank("WhitelistBypass");
            Set<WhitelistAccess> accessTypes = api.getWhitelistHandler().getAccessTypes();
            IServerData serverByName =
                    api.getNetworkHandler().getServerByName(GlobalConfig.SERVER.NAME);
            IRank whitelistRank = api.getWhitelistHandler().getWhitelistRank();
            for (Player player : Bukkit.getOnlinePlayers()) {
                IProfile profile;
                if ((profile = api.getProfileHandler().getCachedProfile(player.getUniqueId()))
                                != null
                        && (rank == null || !profile.hasRank(rank))) {
                    boolean matches = false;
                    WhitelistAccess whitelistAccess = null;
                    WhitelistAccess[] whitelistAccesss;
                    int value = (whitelistAccesss = WhitelistAccess.values()).length;
                    for (int index = 0; index < value; index++) {
                        WhitelistAccess whitelistAccess2 = whitelistAccesss[index];
                        if (accessTypes.contains(whitelistAccess2)) {
                            if (whitelistAccess2.getFunction().apply(profile, serverByName)) {
                                matches = true;
                                break;
                            }
                            whitelistAccess = whitelistAccess2;
                        }
                    }
                    if (!matches) {
                        List<String> values2 =
                                whitelistAccess == WhitelistAccess.RANK
                                        ? MessageConfig.WHITELIST.RANK_KICK_MESSAGE
                                        : MessageConfig.WHITELIST.UUID_KICK_MESSAGE;
                        player.kickPlayer(
                                Color.translate(
                                        getKickMessage(values2)
                                                .replace(
                                                        "<rank>",
                                                        whitelistRank != null
                                                                ? whitelistRank.getDisplayName()
                                                                : "")));
                    }
                }
            }
        }
    }

    public static void teleport(Player player, Location location) {
        NMSHandler.getInstance().getTeleportAPI().teleport(player, location);
    }

    public static void teleport(Player player, Player player2) {
        teleport(player, player2.getLocation());
    }

    private BukkitAPI() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
