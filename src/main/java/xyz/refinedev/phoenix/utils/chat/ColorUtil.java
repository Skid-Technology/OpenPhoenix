package xyz.refinedev.phoenix.utils.chat;

import com.google.common.collect.ImmutableMap;

import org.bukkit.ChatColor;

import xyz.refinedev.phoenix.utils.StringUtils;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class ColorUtil {

    public static Map<ChatColor, String> colorMap = new HashMap<>();

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

    public static ChatColor getClosestChatColor(Color color) {
        ChatColor chatColor = null;
        int value = 0;
        Iterator<Entry<ChatColor, Color>> iterator2 = COLOR_MAPPINGS.entrySet().iterator();
        while (iterator2.hasNext()) {
            Entry entry;
            ChatColor chatColor2 = (ChatColor) (entry = (Entry) iterator2.next()).getKey();
            int diff = getDiff((Color) entry.getValue(), color);
            if (chatColor == null || diff < value) {
                chatColor = chatColor2;
                value = diff;
            }
        }
        return chatColor;
    }

    private static int getDiff(Color color, Color color2) {
        int value = color.getAlpha() - color2.getAlpha();
        int value2 = color.getRed() - color2.getRed();
        int value3 = color.getGreen() - color2.getGreen();
        int value4 = color.getBlue() - color2.getBlue();
        return value * value + value2 * value2 + value3 * value3 + value4 * value4;
    }

    public static ChatColor convertToColor(String text) {
        return xyz.refinedev.phoenix.utils.Color.isHexColor(text)
                ? getClosestChatColor(
                        Color.decode(
                                text.replace("&#", "#")
                                        .replace("\u00a7x", "#")
                                        .replace("&l", "")
                                        .replace("&n", "")
                                        .replace("&m", "")
                                        .replace("&o", "")
                                        .replace("&k", "")
                                        .replace("\u00a7l", "")
                                        .replace("\u00a7n", "")
                                        .replace("\u00a7m", "")
                                        .replace("\u00a7o", "")
                                        .replace("\u00a7k", "")))
                : ChatColor.getByChar(
                        text.replace(String.valueOf('\u00a7'), "")
                                .replace("&", "")
                                .replace("l", "")
                                .replace("n", "")
                                .replace("m", "")
                                .replace("o", "")
                                .replace("k", ""));
    }

    public static String convertChatColor(ChatColor chatColor) {
        return colorMap.get(chatColor);
    }

    public static String convertChatColor(ChatColor chatColor, boolean matches) {
        if (!matches) {
            return convertChatColor(chatColor);
        }
        String text;
        if (!(text = convertChatColor(chatColor).toLowerCase()).contains("_")) {
            return StringUtils.convertFirstUpperCase(text);
        }
        StringBuilder stringBuilder = new StringBuilder();
        String[] texts;
        int value = (texts = text.split("_")).length;
        for (int index = 0; index < value; index++) {
            String text2 = texts[index];
            stringBuilder.append(StringUtils.convertFirstUpperCase(text2));
            stringBuilder.append(" ");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public static ChatColor getOr(String text, ChatColor chatColor) {
        try {
            return ChatColor.valueOf(text);
        } catch (Exception exception) {
            return chatColor;
        }
    }

    static {
        colorMap.put(ChatColor.BLACK, "BLACK");
        colorMap.put(ChatColor.DARK_BLUE, "DARK_BLUE");
        colorMap.put(ChatColor.DARK_GREEN, "DARK_GREEN");
        colorMap.put(ChatColor.DARK_AQUA, "DARK_AQUA");
        colorMap.put(ChatColor.DARK_RED, "DARK_RED");
        colorMap.put(ChatColor.DARK_PURPLE, "DARK_PURPLE");
        colorMap.put(ChatColor.GOLD, "GOLD");
        colorMap.put(ChatColor.GRAY, "GRAY");
        colorMap.put(ChatColor.DARK_GRAY, "DARK_GRAY");
        colorMap.put(ChatColor.BLUE, "BLUE");
        colorMap.put(ChatColor.GREEN, "GREEN");
        colorMap.put(ChatColor.AQUA, "AQUA");
        colorMap.put(ChatColor.RED, "RED");
        colorMap.put(ChatColor.LIGHT_PURPLE, "LIGHT_PURPLE");
        colorMap.put(ChatColor.YELLOW, "YELLOW");
        colorMap.put(ChatColor.WHITE, "WHITE");
        colorMap.put(ChatColor.RESET, "RESET");
        colorMap.put(ChatColor.ITALIC, "ITALIC");
        colorMap.put(ChatColor.UNDERLINE, "UNDERLINE");
        colorMap.put(ChatColor.STRIKETHROUGH, "STRIKETHROUGH");
        colorMap.put(ChatColor.MAGIC, "MAGIC");
        colorMap.put(ChatColor.BOLD, "BOLD");
    }
}
