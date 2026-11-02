package xyz.refinedev.phoenix.utils.nametags.util;

import com.google.common.collect.ImmutableMap;

import lombok.Generated;

import org.bukkit.ChatColor;

import java.awt.Color;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ColorUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#[A-Fa-f0-9]{6}");

    private static final Map<ChatColor, Color> COLOR_MAPPINGS =
            ImmutableMap.<ChatColor, Color>builder()
                    .put(ChatColor.BLACK, new Color(0, 0, 0))
                    .put(ChatColor.DARK_BLUE, new Color(0, 0, 170))
                    .put(ChatColor.DARK_GREEN, new Color(0, 170, 0))
                    .put(ChatColor.DARK_AQUA, new Color(0, 170, 170))
                    .put(ChatColor.DARK_RED, new Color(170, 0, 0))
                    .put(ChatColor.DARK_PURPLE, new Color(170, 0, 170))
                    .put(ChatColor.GOLD, new Color(255, 170, 0))
                    .put(ChatColor.GRAY, new Color(170, 170, 170))
                    .put(ChatColor.DARK_GRAY, new Color(85, 85, 85))
                    .put(ChatColor.BLUE, new Color(85, 85, 255))
                    .put(ChatColor.GREEN, new Color(85, 255, 85))
                    .put(ChatColor.AQUA, new Color(85, 255, 255))
                    .put(ChatColor.RED, new Color(255, 85, 85))
                    .put(ChatColor.LIGHT_PURPLE, new Color(255, 85, 255))
                    .put(ChatColor.YELLOW, new Color(255, 255, 85))
                    .put(ChatColor.WHITE, new Color(255, 255, 255))
                    .build();

    public static ChatColor getLastColors(String text) {
        String text2;
        if ((text2 = ChatColor.getLastColors(color(text))).isEmpty()) {
            return ChatColor.WHITE;
        }
        if (VersionUtil.canUseHexColors()) {
            try {
                return getClosestChatColor(
                        net.md_5.bungee.api.ChatColor.of(
                                        text2.replace("\u00a7", "").replace("x", "#"))
                                .getColor());
            } catch (Exception exception) {
                return parseLastChatColor(text2);
            }
        } else {
            return parseLastChatColor(text2);
        }
    }

    public static String color(String text) {
        if (text == null) {
            return "";
        }
        if (VersionUtil.canUseHexColors()) {
            Matcher matcher2 = HEX_PATTERN.matcher(text);
            while (matcher2.find()) {
                try {
                    String text2;
                    String text3 = (text2 = matcher2.group()).replace("&", "").replace("x", "#");
                    text = text.replace(text2, net.md_5.bungee.api.ChatColor.of(text3).toString());
                } catch (Exception exception) {
                }
            }
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String getRaw(String text) {
        return text.replace("\u00a7", "&");
    }

    public static ChatColor getClosestChatColor(Color color) {
        ChatColor chatColor = null;
        int value = Integer.MAX_VALUE;
        Iterator<Entry<ChatColor, Color>> iterator2 = COLOR_MAPPINGS.entrySet().iterator();
        while (iterator2.hasNext()) {
            Entry entry;
            int value2;
            if ((value2 =
                            colorDifference(
                                    (Color) (entry = (Entry) iterator2.next()).getValue(), color))
                    < value) {
                value = value2;
                chatColor = (ChatColor) entry.getKey();
            }
        }
        return chatColor;
    }

    private static int colorDifference(Color color, Color color2) {
        int value = color.getAlpha() - color2.getAlpha();
        int value2 = color.getRed() - color2.getRed();
        int value3 = color.getGreen() - color2.getGreen();
        int value4 = color.getBlue() - color2.getBlue();
        return value * value + value2 * value2 + value3 * value3 + value4 * value4;
    }

    private static ChatColor parseLastChatColor(String text) {
        return ChatColor.getByChar(text.charAt(text.length() - 1));
    }

    @Generated
    private ColorUtil() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
