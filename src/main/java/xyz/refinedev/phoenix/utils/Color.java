package xyz.refinedev.phoenix.utils;

import xyz.refinedev.lib.caffeine.cache.Cache;
import xyz.refinedev.lib.caffeine.cache.Caffeine;
import xyz.refinedev.lib.kyori.adventure.text.Component;
import xyz.refinedev.lib.kyori.adventure.text.TextComponent;
import xyz.refinedev.lib.kyori.adventure.text.format.Style;
import xyz.refinedev.lib.kyori.adventure.text.format.Style.Merge.Strategy;
import xyz.refinedev.lib.kyori.adventure.text.minimessage.MiniMessage;
import xyz.refinedev.lib.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.handler.INetworkHandler;
import xyz.refinedev.phoenix.utils.version.VersionUtil;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Color {

    private static final Pattern STRIP_COLOR_PATTERN =
            Pattern.compile("(?i)[\u00a7&][0-9A-FK-ORX]");

    private static final Pattern STRIP_COLOR_HEX_PATTERN = Pattern.compile("(?i)&#[a-fA-F0-9]{6}");

    private static final Pattern MINIMESSAGE_HEX_TAG_PATTERN =
            Pattern.compile("(?i)<#[a-f0-9]{6}>");

    public static final String BLANK_MESSAGE =
            String.join(
                    "",
                    Collections.nCopies(
                            1000,
                            "\u00a78 \u00a78 \u00a71 \u00a73 \u00a73 \u00a77 \u00a78 \u00a7r\n"));

    private static final String DECORATION_CODES = "klmno";

    private static final String[] DECORATION_NEGATIONS =
            new String[] {
                "<!obfuscated>", "<!bold>", "<!strikethrough>", "<!underlined>", "<!italic>"
            };

    private static final int MAX_NESTING = 128;

    private static final MiniMessage MINI_MESSAGE =
            MiniMessage.builder().preProcessor(Color::preProcessForMiniMessage).build();

    private static final LegacyComponentSerializer SECTION_SERIALIZER =
            LegacyComponentSerializer.builder()
                    .character('\u00a7')
                    .hexColors()
                    .useUnusualXRepeatedCharacterHexFormat()
                    .build();

    private static final Cache<String, Component> COMPONENT_CACHE;

    public static void clearComponentCache() {
        COMPONENT_CACHE.invalidateAll();
    }

    public static String translate(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        if (SettingsConfig.SERVER.MINI_MESSAGE) {
            return SECTION_SERIALIZER.serialize(translateComponent(text));
        }
        try {
            return color(replace(text));
        } catch (Exception exception) {
            PlatformGetter.getInstance()
                    .getLogger()
                    .log(Level.SEVERE, "Failed to translate string color: " + text, exception);
            return text;
        }
    }

    public static Component translateComponent(String text, String text2) {
        return translateComponent(carryTrailingColor(text, text2));
    }

    public static Component translateComponent(String text) {
        if (text != null && !text.isEmpty()) {
            try {
                text = replace(text);
                Component component;
                if ((component = (Component) COMPONENT_CACHE.getIfPresent(text)) != null) {
                    return component;
                }
                Component component2 =
                        flattenNesting(
                                (Component)
                                        (SettingsConfig.SERVER.MINI_MESSAGE
                                                ? MINI_MESSAGE.deserialize(text)
                                                : SECTION_SERIALIZER.deserialize(color(text))));
                COMPONENT_CACHE.put(text, component2);
                return component2;
            } catch (Exception exception) {
                PlatformGetter.getInstance()
                        .getLogger()
                        .log(
                                Level.SEVERE,
                                "Failed to build component from string: " + text,
                                exception);
                return SECTION_SERIALIZER.deserialize(color(text));
            }
        } else {
            return Component.empty();
        }
    }

    public static List<String> translate(List<String> values) {
        return values == null
                ? Collections.emptyList()
                : values.stream().map(Color::translate).collect(Collectors.toList());
    }

    public static String replace(String text) {
        if (text == null) {
            return null;
        }
        if (text.indexOf(60) == -1 && text.indexOf(123) == -1) {
            return text;
        }
        text = text.replace("{0}", "\n");
        Phoenix phoenix;
        INetworkHandler networkHandler;
        if ((networkHandler =
                        (phoenix = Phoenix.getInstance()) == null
                                ? null
                                : phoenix.getNetworkHandler())
                != null) {
            text =
                    text.replace("<main>", orEmpty(Phoenix.MAIN_COLOR))
                            .replace("<middle>", orEmpty(Phoenix.MIDDLE_COLOR))
                            .replace("<secondary>", orEmpty(Phoenix.SECONDARY_COLOR))
                            .replace("<bright>", orEmpty(Phoenix.BRIGHT_COLOR))
                            .replace("<muted>", orEmpty(Phoenix.MUTED_COLOR))
                            .replace("<rail>", orEmpty(Phoenix.RAIL_COLOR))
                            .replace("<website>", orEmpty(networkHandler.getWebsite()))
                            .replace("<store>", orEmpty(networkHandler.getStore()))
                            .replace("<discord>", orEmpty(networkHandler.getDiscord()))
                            .replace("<teamspeak>", orEmpty(networkHandler.getTeamspeak()))
                            .replace("<twitter>", orEmpty(networkHandler.getTwitter()));
        }
        if (text.contains("{centered}")) {
            text = CenteredTextUtils.formatString(text.replace("{centered}", ""));
        }
        return text;
    }

    private static String orEmpty(String text) {
        return text != null ? text : "";
    }

    public static String escape(String text) {
        if (text != null && !text.isEmpty() && SettingsConfig.SERVER.MINI_MESSAGE) {
            return text.indexOf(92) == -1 && text.indexOf(60) == -1
                    ? text
                    : text.replace("\\", "\\\\").replace("<", "\\<");
        } else {
            return text;
        }
    }

    public static boolean isHexColor(String text) {
        return VersionUtil.canHex() && text != null
                ? STRIP_COLOR_HEX_PATTERN.matcher(text).find()
                        || MINIMESSAGE_HEX_TAG_PATTERN.matcher(text).find()
                : false;
    }

    private static Component flattenNesting(Component component) {
        if (!nestsDeeperThan(component, 128)) {
            return component;
        }
        ArrayList values = new ArrayList();
        Iterator<Component> iterator2 = component.children().iterator();
        while (iterator2.hasNext()) {
            flattenInto((Component) iterator2.next(), component.style(), values);
        }
        return component.children(values);
    }

    private static boolean nestsDeeperThan(Component component, int value) {
        List<Component> values = component.children();
        for (int index = 0; !values.isEmpty(); index++) {
            if (index >= value) {
                return true;
            }
            ArrayList values2 = new ArrayList();
            for (Component component2 : values) {
                values2.addAll(component2.children());
            }
            values = values2;
        }
        return false;
    }

    private static void flattenInto(Component component, Style style2, List<Component> values) {
        ArrayDeque values2 = new ArrayDeque();
        ArrayDeque values3 = new ArrayDeque();
        values2.push(component);
        values3.push(style2);
        while (!values2.isEmpty()) {
            Component component2;
            Style style3 =
                    (component2 = (Component) values2.pop())
                            .style()
                            .merge((Style) values3.pop(), Strategy.IF_ABSENT_ON_TARGET);
            if (!(component2 instanceof TextComponent)
                    || !((TextComponent) component2).content().isEmpty()) {
                values.add(component2.children(Collections.emptyList()).style(style3));
            }
            List<Component> values4;
            for (int value = (values4 = component2.children()).size() - 1; value >= 0; value--) {
                values2.push((Component) values4.get(value));
                values3.push(style3);
            }
        }
    }

    public static String trailingLegacyColor(String text) {
        if (text != null && !text.isEmpty()) {
            Serializable serializable = "";
            StringBuilder stringBuilder = new StringBuilder();
            char[] values = text.toCharArray();
            for (int index = 0; index < values.length; index++) {
                int value;
                if ((value = legacyCodeLength(values, index)) != 0) {
                    char value2 = Character.toLowerCase(values[index + 1]);
                    int value3 = index;
                    index += value - 1;
                    if (value == 2 && "klmno".indexOf(value2) != -1) {
                        stringBuilder.append(values, value3, 2);
                    } else {
                        serializable = value2 == 'r' ? "" : new String(values, value3, value);
                        stringBuilder.setLength(0);
                    }
                }
            }
            return String.valueOf(serializable) + stringBuilder;
        } else {
            return "";
        }
    }

    public static String carryTrailingColor(String text, String text2) {
        if (text2 == null) {
            return null;
        }
        String text3;
        return (text3 = trailingLegacyColor(text)).isEmpty() ? text2 : text3 + text2;
    }

    public static String stripColor(String text) {
        if (text != null && !text.isEmpty()) {
            if (SettingsConfig.SERVER.MINI_MESSAGE) {
                text = MINI_MESSAGE.stripTags(text);
            }
            text = STRIP_COLOR_HEX_PATTERN.matcher(text).replaceAll("");
            return STRIP_COLOR_PATTERN.matcher(text).replaceAll("");
        } else {
            return text;
        }
    }

    private static int legacyCodeLength(char[] values, int value) {
        char value2;
        if (((value2 = values[value]) == '&' || value2 == 167) && value + 1 < values.length) {
            char value3;
            if ((value3 = Character.toLowerCase(values[value + 1])) == '#') {
                return isHexSequence(values, value + 2, 1) ? 8 : 0;
            } else if (value3 == 'x') {
                return isHexSequence(values, value + 3, 2) ? 14 : 0;
            } else {
                return getMiniMessageTag(value3) != null ? 2 : 0;
            }
        } else {
            return 0;
        }
    }

    private static boolean isHexSequence(char[] values, int value, int value2) {
        if (value + 5 * value2 >= values.length) {
            return false;
        }
        int value3 = 0;
        for (int value4 = value; value3 < 6; value4 += value2) {
            if (!isHexDigit(values[value4])) {
                return false;
            }
            if (value2 == 2 && values[value4 - 1] != '&' && values[value4 - 1] != 167) {
                return false;
            }
            value3++;
        }
        return true;
    }

    private static String preProcessForMiniMessage(String text) {
        if (text.indexOf(38) == -1 && text.indexOf(167) == -1) {
            return text;
        }
        char[] values = text.toCharArray();
        StringBuilder stringBuilder = new StringBuilder(text.length() + 32);
        int value = 0;
        for (int index = 0; index < values.length; index++) {
            int value2;
            if ((value2 = legacyCodeLength(values, index)) == 0) {
                stringBuilder.append(values[index]);
            } else {
                char value3 = Character.toLowerCase(values[index + 1]);
                int value4;
                if ((value4 = value2 == 2 ? "klmno".indexOf(value3) : -1) != -1) {
                    value |= 1 << value4;
                } else if (value3 == 'r') {
                    value = 0;
                } else {
                    value = closeDecorations(stringBuilder, value);
                }
                if (value2 == 2) {
                    stringBuilder.append(getMiniMessageTag(value3));
                } else {
                    int value5 = value2 == 8 ? 1 : 2;
                    stringBuilder.append("<#");
                    int value6 = 0;
                    for (int value7 = index + 1 + value5; value6 < 6; value7 += value5) {
                        stringBuilder.append(values[value7]);
                        value6++;
                    }
                    stringBuilder.append('>');
                }
                index += value2 - 1;
            }
        }
        return stringBuilder.toString();
    }

    private static int closeDecorations(StringBuilder stringBuilder, int value) {
        if (value == 0) {
            return 0;
        }
        for (int index = 0; index < DECORATION_NEGATIONS.length; index++) {
            if ((value & 1 << index) != 0) {
                stringBuilder.append(DECORATION_NEGATIONS[index]);
            }
        }
        return 0;
    }

    private static String getMiniMessageTag(char value) {
        switch (value) {
            case '0':
                return "<black>";
            case '1':
                return "<dark_blue>";
            case '2':
                return "<dark_green>";
            case '3':
                return "<dark_aqua>";
            case '4':
                return "<dark_red>";
            case '5':
                return "<dark_purple>";
            case '6':
                return "<gold>";
            case '7':
                return "<gray>";
            case '8':
                return "<dark_gray>";
            case '9':
                return "<blue>";
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'p':
            case 'q':
            default:
                return null;
            case 'a':
                return "<green>";
            case 'b':
                return "<aqua>";
            case 'c':
                return "<red>";
            case 'd':
                return "<light_purple>";
            case 'e':
                return "<yellow>";
            case 'f':
                return "<white>";
            case 'k':
                return "<obfuscated>";
            case 'l':
                return "<bold>";
            case 'm':
                return "<strikethrough>";
            case 'n':
                return "<underlined>";
            case 'o':
                return "<italic>";
            case 'r':
                return "<reset>";
        }
    }

    private static String color(String text) {
        if (text != null && !text.isEmpty()) {
            char[] values = text.toCharArray();
            StringBuilder stringBuilder = new StringBuilder(values.length + 16);
            for (int index = 0; index < values.length; index++) {
                char value;
                if ((value = values[index]) == '&' && index + 1 < values.length) {
                    char value2;
                    if ((value2 = values[index + 1]) == '#'
                            && VersionUtil.canHex()
                            && index + 7 < values.length) {
                        boolean matches = true;
                        for (int index2 = 2; index2 <= 7; index2++) {
                            if (!isHexDigit(values[index + index2])) {
                                matches = false;
                                break;
                            }
                        }
                        if (matches) {
                            stringBuilder.append('\u00a7').append('x');
                            for (int index3 = 2; index3 <= 7; index3++) {
                                stringBuilder
                                        .append('\u00a7')
                                        .append(Character.toLowerCase(values[index + index3]));
                            }
                            index += 7;
                            continue;
                        }
                    }
                    if (isColorCode(value2)) {
                        stringBuilder.append('\u00a7').append(Character.toLowerCase(value2));
                        index++;
                        continue;
                    }
                }
                stringBuilder.append(value);
            }
            return stringBuilder.toString();
        } else {
            return text;
        }
    }

    private static boolean isHexDigit(char value) {
        return value >= '0' && value <= '9'
                || value >= 'a' && value <= 'f'
                || value >= 'A' && value <= 'F';
    }

    private static boolean isColorCode(char value) {
        return value >= '0' && value <= '9'
                || value >= 'a' && value <= 'f'
                || value >= 'A' && value <= 'F'
                || value >= 'k' && value <= 'o'
                || value >= 'K' && value <= 'O'
                || value == 'r'
                || value == 'R'
                || VersionUtil.canHex() && (value == 'x' || value == 'X');
    }

    private Color() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }

    static {
        COMPONENT_CACHE = Caffeine.newBuilder().maximumSize(4096L).build();
    }
}
