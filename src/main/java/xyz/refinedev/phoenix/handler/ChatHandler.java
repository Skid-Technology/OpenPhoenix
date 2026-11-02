package xyz.refinedev.phoenix.handler;

import me.clip.placeholderapi.PlaceholderAPI;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.lib.kyori.adventure.text.Component;
import xyz.refinedev.lib.kyori.adventure.text.event.ClickEvent;
import xyz.refinedev.lib.kyori.adventure.text.event.HoverEvent;
import xyz.refinedev.lib.kyori.adventure.text.format.ShadowColor;
import xyz.refinedev.lib.kyori.adventure.text.format.Style;
import xyz.refinedev.lib.kyori.adventure.text.object.ObjectContents;
import xyz.refinedev.lib.kyori.adventure.text.object.PlayerHeadObjectContents;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.chat.ChatChannelUtils;
import xyz.refinedev.phoenix.chat.api.ChatChannelType;
import xyz.refinedev.phoenix.chat.api.ChatLimitation;
import xyz.refinedev.phoenix.chat.channel.AdminChatChannel;
import xyz.refinedev.phoenix.chat.channel.BuilderChatChannel;
import xyz.refinedev.phoenix.chat.channel.DefaultChatChannel;
import xyz.refinedev.phoenix.chat.channel.ManagementChatChannel;
import xyz.refinedev.phoenix.chat.channel.StaffChatChannel;
import xyz.refinedev.phoenix.chat.limitation.AntiSpamChatLimitation;
import xyz.refinedev.phoenix.chat.limitation.AuthenticationChatLimitation;
import xyz.refinedev.phoenix.chat.limitation.ChatCooldownLimitation;
import xyz.refinedev.phoenix.chat.limitation.FilterChatLimitation;
import xyz.refinedev.phoenix.chat.limitation.PunishmentChatLimitation;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.disguise.ISkin;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LimitedSizeList;
import xyz.refinedev.phoenix.utils.MojangProfileUtils;
import xyz.refinedev.phoenix.utils.Pair;
import xyz.refinedev.phoenix.utils.version.ClientVersionUtil;
import xyz.refinedev.phoenix.utils.version.ServerVersion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ChatHandler {

    private final List<ChatChannelType> chatChannels;

    private final List<ChatLimitation> chatLimitations;

    private List<ChatLimitation> sortedChatLimitations;

    private final Phoenix plugin;

    private final Map<UUID, LimitedSizeList<Pair<UUID, Component>>> lastChatMessages =
            new ConcurrentHashMap<>();

    public static final String SHADOW_COLOR_METADATA_KEY = "shadow-color";

    public ChatHandler(Phoenix phoenix) {
        this.plugin = phoenix;
        this.chatChannels = new ArrayList<>();
        this.chatChannels.add(new DefaultChatChannel());
        this.addChannel(new BuilderChatChannel());
        this.addChannel(new StaffChatChannel());
        this.addChannel(new AdminChatChannel());
        this.addChannel(new ManagementChatChannel());
        this.chatLimitations = new ArrayList<>();
        this.sortedChatLimitations = new ArrayList<>();
        this.chatLimitations.add(new PunishmentChatLimitation(phoenix));
        this.chatLimitations.add(new AuthenticationChatLimitation(phoenix));
        this.chatLimitations.add(new FilterChatLimitation(phoenix));
        this.chatLimitations.add(new AntiSpamChatLimitation(phoenix));
        this.chatLimitations.add(new ChatCooldownLimitation(phoenix));
        this.sortChatLimitations();
    }

    @Nullable
    public ChatChannelType getChannelByQuickAccess(String text) {
        Iterator<ChatChannelType> iterator2 = this.chatChannels.iterator();
        while (iterator2.hasNext()) {
            ChatChannelType chatChannelType;
            if ((chatChannelType = (ChatChannelType) iterator2.next()).getQuickAccessPrefix()
                            != null
                    && text.toLowerCase()
                            .startsWith(chatChannelType.getQuickAccessPrefix().toLowerCase())) {
                return chatChannelType;
            }
        }
        return null;
    }

    @NotNull
    public String getReplacedChatFormat(Player player, String text) {
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                == null) {
            this.plugin
                    .getLogger()
                    .severe(
                            "Tried replacing chat format for "
                                    + player.getName()
                                    + " but the profile was not loaded.");
            return "";
        }
        String text2 =
                this.getChannelByName(profile.getChatChannelName())
                        .getFormat()
                        .replace(
                                "<prefix>",
                                (profile.hasSubscription() ? profile.getSubRanks() : "")
                                        + profile.getHighestRank().getPrefix())
                        .replace("<suffix>", profile.getHighestRank().getSuffix())
                        .replace("<player>", profile.getPlayerName())
                        .replace("<color>", profile.getHighestRank().getColor())
                        .replace("<chatcolor>", profile.getHighestRank().getChatColor())
                        .replace(
                                "<rankIcon>",
                                this.plugin
                                        .getRankIconHandler()
                                        .formatString(profile.getHighestRank()))
                        .replace("<tag>", BukkitAPI.getTagPrefix(player));
        text2 =
                Color.translate(
                        this.plugin.getPlaceholderHandler().replace(player.getUniqueId(), text2));
        text = text.replace("{0}", "");
        if (player.hasPermission("core.chat.colorcodes")) {
            text2 = text2.replace("<message>", Color.translate(text));
        } else {
            text2 = text2.replace("<message>", Color.stripColor(text));
        }
        return text2;
    }

    @NotNull
    public Component buildDefaultChatComponent(
            Player player, @Nullable Player player2, String text) {
        String replaced =
                this.getReplaced(
                        player,
                        BukkitAPI.getUntranslatedStringFromList(
                                SettingsConfig.FORMATS.HOVER_FORMAT));
        String text2 = SettingsConfig.FORMATS.CHAT_COMMAND.replace("<player>", player.getName());
        Player player3 = SettingsConfig.FORMATS.PARSE_RELATIONAL_PLACEHOLDERS ? player2 : null;
        return this.buildChatComponent(player, player3, text, replaced, text2);
    }

    @NotNull
    public Component buildChatComponent(
            Player player,
            @Nullable Player player2,
            String text,
            @Nullable String text2,
            @Nullable String text3) {
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                == null) {
            this.plugin
                    .getLogger()
                    .severe(
                            "Tried building chat component for "
                                    + player.getName()
                                    + " but the profile was not loaded.");
            return Component.empty();
        }
        ChatChannelType channelByName = this.getChannelByName(profile.getChatChannelName());
        IRank highestRank = profile.getHighestRank();
        String text4 =
                (profile.hasSubscription() ? profile.getSubRanks() : "") + highestRank.getPrefix();
        String text5 =
                (player.hasPermission("core.chat.colorcodes")
                                ? Color.translate(text)
                                : Color.stripColor(text))
                        .replace("{0}", "");
        String text6 =
                channelByName
                        .getFormat()
                        .replace("<suffix>", highestRank.getSuffix())
                        .replace("<player>", profile.getPlayerName())
                        .replace("<color>", highestRank.getColor())
                        .replace("<chatcolor>", highestRank.getChatColor())
                        .replace(
                                "<rankIcon>",
                                this.plugin.getRankIconHandler().formatString(highestRank))
                        .replace("<tag>", BukkitAPI.getTagPrefix(player));
        text6 =
                Color.translate(
                        this.plugin.getPlaceholderHandler().replace(player.getUniqueId(), text6));
        if (player2 != null) {
            text6 = PlaceholderAPI.setRelationalPlaceholders(player, player2, text6);
        }
        text6 = text6.replace("<message>", text5);
        Component component = this.buildRankPrefixComponent(highestRank, text4);
        Component component2 = this.buildHeadSprite(player, profile);
        int value;
        if ((value = text6.indexOf("<prefix>")) < 0) {
            Component component3 =
                    this.wrapWithChatHover(Color.translateComponent(text6), text2, text3);
            return component2 == null ? component3 : component2.append(component3);
        }
        String text7 = text6.substring(0, value);
        String text8 = text6.substring(value + "<prefix>".length());
        Component component4 =
                (Component)
                        (component2 == null
                                ? Component.empty()
                                : Component.empty().append(component2));
        if (!text7.isEmpty()) {
            component4 =
                    component4.append(
                            this.wrapWithChatHover(Color.translateComponent(text7), text2, text3));
        }
        component4 = component4.append(component);
        if (!text8.isEmpty()) {
            component4 =
                    component4.append(
                            this.wrapWithChatHover(
                                    Color.translateComponent(text4, text8), text2, text3));
        }
        return component4;
    }

    public static ShadowColor parseShadowColor(String text) {
        if (text == null) {
            return null;
        }
        String text2;
        if (!(text2 = text.trim()).startsWith("#")) {
            text2 = "#" + text2;
        }
        if (text2.length() == 7) {
            text2 = text2 + "ff";
        }
        return ShadowColor.fromHexString(text2);
    }

    private Component applyShadowColor(IRank rank, Component component) {
        if (!SettingsConfig.MODERN.CHAT.SHADOW_COLORS) {
            return component;
        }
        if (!ClientVersionUtil.serverSupports(ServerVersion.V_1_21_4)) {
            return component;
        }
        String text;
        if ((text = rank.getMetadata("shadow-color")) != null && !text.trim().isEmpty()) {
            ShadowColor shadowColor2;
            if ((shadowColor2 = parseShadowColor(text)) == null) {
                this.plugin
                        .getLogger()
                        .warning(
                                "Rank '"
                                        + rank.getName()
                                        + "' has an invalid "
                                        + "shadow-color"
                                        + " '"
                                        + text
                                        + "', ignoring it.");
                return component;
            } else {
                return component.style((Style) component.style().shadowColor(shadowColor2));
            }
        } else {
            return component;
        }
    }

    private Component buildHeadSprite(Player player, IProfile profile) {
        if (!SettingsConfig.MODERN.CHAT.HEAD_SPRITES) {
            return null;
        }
        if (!ClientVersionUtil.serverSupports(ServerVersion.V_1_21_5)) {
            return null;
        }
        String text;
        if ((text = profile.getPlayerName()) == null || text.isEmpty()) {
            text = player.getName();
        }
        ISkin skin;
        return (skin =
                                        profile.getDisguiseData().isDisguised()
                                                ? profile.getDisguiseData().getDisguiseSkin()
                                                : profile.getDisguiseData().getRealSkin())
                                != null
                        && skin.getValue() != null
                        && !skin.getValue().isEmpty()
                ? Component.object(
                        ObjectContents.playerHead()
                                .name(text)
                                .profileProperty(
                                        PlayerHeadObjectContents.property(
                                                "textures", skin.getValue(), skin.getSignature()))
                                .build())
                : Component.object(ObjectContents.playerHead(text));
    }

    private Component wrapWithChatHover(
            Component component, @Nullable String text, @Nullable String text2) {
        if (SettingsConfig.FORMATS.HOVER_ENABLED && text != null && !text.isEmpty()) {
            component = component.hoverEvent(HoverEvent.showText(Color.translateComponent(text)));
        }
        if (text2 != null && !text2.isEmpty()) {
            component = component.clickEvent(ClickEvent.suggestCommand(text2));
        }
        return component;
    }

    @NotNull
    public Component buildRankHoverPreview(IRank rank) {
        return this.buildRankPrefixComponent(rank, rank.getPrefix());
    }

    private Component buildRankPrefixComponent(IRank rank, String text) {
        Component component = this.applyShadowColor(rank, Color.translateComponent(text));
        if (!SettingsConfig.FORMATS.RANK_HOVER_ENABLED) {
            return component;
        }
        String text2;
        if ((text2 = this.resolveRankHoverText(rank)) != null && !text2.isEmpty()) {
            component = component.hoverEvent(HoverEvent.showText(Color.translateComponent(text2)));
        }
        String text3;
        if ((text3 = this.resolveRankHoverUrl(rank)) != null && !text3.isEmpty()) {
            component = component.clickEvent(ClickEvent.openUrl(text3));
        }
        return component;
    }

    @Nullable
    private String resolveRankHoverText(IRank rank) {
        if (!rank.hasMetadata("hover-description")) {
            return null;
        }
        String text;
        return (text = rank.getMetadata("hover-description")) != null && !text.isEmpty()
                ? text.replace("\\n", "\n")
                : null;
    }

    @Nullable
    private String resolveRankHoverUrl(IRank rank) {
        if (!rank.hasMetadata("hover-url")) {
            return null;
        }
        String text;
        return (text = rank.getMetadata("hover-url")) != null && !text.isEmpty() ? text : null;
    }

    @NotNull
    public String getReplaced(Player player, String text) {
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                == null) {
            this.plugin
                    .getLogger()
                    .severe(
                            "Tried replacing chat format for "
                                    + player.getName()
                                    + " but the profile was not loaded.");
            return "";
        } else {
            text =
                    text.replace(
                                    "<prefix>",
                                    (profile.hasSubscription() ? profile.getSubRanks() : "")
                                            + profile.getHighestRank().getPrefix())
                            .replace("<suffix>", profile.getHighestRank().getSuffix())
                            .replace("<player>", profile.getPlayerName())
                            .replace("<color>", profile.getHighestRank().getColor())
                            .replace("<chatcolor>", profile.getHighestRank().getChatColor())
                            .replace("<tag>", BukkitAPI.getTagPrefix(player));
            return Color.translate(
                    this.plugin.getPlaceholderHandler().replace(player.getUniqueId(), text));
        }
    }

    @NotNull
    public List<String> getAccessChannelsSorted(Player player) {
        ArrayList<String> values = new ArrayList<>();
        for (ChatChannelType chatChannelType : this.getSortedChatChannels()) {
            if (player.canSee(player)) {
                values.add(chatChannelType.getName());
            }
        }
        return values;
    }

    @NotNull
    public List<ChatChannelType> getSortedChatChannels() {
        ArrayList<ChatChannelType> values;
        (values = new ArrayList<>(this.chatChannels))
                .sort(Comparator.comparingInt(ChatChannelType::getPriority));
        return values;
    }

    public void addChannel(ChatChannelType chatChannelType) {
        this.chatChannels.add(chatChannelType);
        this.plugin
                .getCommandHandler()
                .register(
                        new ChatChannelUtils(this.plugin, chatChannelType),
                        chatChannelType.getName().toLowerCase() + "chat",
                        chatChannelType.getPermission(),
                        chatChannelType.getName().toCharArray()[0] + "c");
    }

    @NotNull
    public ChatChannelType getChannelByName(String text) {
        Iterator<ChatChannelType> iterator2 = this.chatChannels.iterator();
        while (iterator2.hasNext()) {
            ChatChannelType chatChannelType;
            if ((chatChannelType = (ChatChannelType) iterator2.next())
                    .getName()
                    .equalsIgnoreCase(text)) {
                return chatChannelType;
            }
        }
        return new DefaultChatChannel();
    }

    @Nullable
    public ChatChannelType getNullableChannelByName(String text) {
        Iterator<ChatChannelType> iterator2 = this.chatChannels.iterator();
        while (iterator2.hasNext()) {
            ChatChannelType chatChannelType;
            if ((chatChannelType = (ChatChannelType) iterator2.next())
                    .getName()
                    .equalsIgnoreCase(text)) {
                return chatChannelType;
            }
        }
        return null;
    }

    public void addLimitation(ChatLimitation chatLimitation) {
        this.chatLimitations.add(chatLimitation);
        this.sortChatLimitations();
    }

    @Nullable
    public ChatLimitation getLimitationByName(String text) {
        Iterator<ChatLimitation> iterator2 = this.chatLimitations.iterator();
        while (iterator2.hasNext()) {
            ChatLimitation chatLimitation;
            if ((chatLimitation = (ChatLimitation) iterator2.next())
                    .getName()
                    .equalsIgnoreCase(text)) {
                return chatLimitation;
            }
        }
        return null;
    }

    public void sortChatLimitations() {
        this.sortedChatLimitations =
                this.chatLimitations.stream()
                        .sorted(Comparator.comparingInt(ChatLimitation::getPriority))
                        .collect(Collectors.toList());
    }

    public List<ChatChannelType> getChatChannels() {
        return this.chatChannels;
    }

    public List<ChatLimitation> getChatLimitations() {
        return this.chatLimitations;
    }

    public List<ChatLimitation> getSortedChatLimitations() {
        return this.sortedChatLimitations;
    }

    public Phoenix getPlugin() {
        return this.plugin;
    }

    public Map<UUID, LimitedSizeList<Pair<UUID, Component>>> getLastChatMessages() {
        return this.lastChatMessages;
    }
}
