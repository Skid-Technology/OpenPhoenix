package xyz.refinedev.phoenix.chat.limitation;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.chat.api.ChatLimitation;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AntiSpamChatLimitation extends ChatLimitation {

    private static final Map<UUID, String> stringsById = new ConcurrentHashMap<>();

    public AntiSpamChatLimitation(CommonPlatform commonPlatform) {
        super(
                "AntiSpam",
                4,
                (result, result2) -> {
                    if (!SettingsConfig.SERVER.ANTISPAM.ENABLED) {
                        return true;
                    } else if (result.hasPermission("core.antispam.bypass")) {
                        return true;
                    } else {
                        String text =
                                commonPlatform
                                        .getFilterHandler()
                                        .sanitize(result2)
                                        .replace(" ", "");
                        String text2;
                        if ((text2 = stringsById.put(result.getUniqueId(), text)) == null) {
                            return true;
                        } else if (calculateValue(text2, text)
                                >= SettingsConfig.SERVER.ANTISPAM.SIMILARITY) {
                            result.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(
                                                    result,
                                                    MessageConfig.ANTISPAM.YOU_CANT_SEND_TWICE)));
                            return false;
                        } else {
                            return true;
                        }
                    }
                });
    }

    public static void applyUUID(UUID uUID) {
        stringsById.remove(uUID);
    }

    private static double calculateValue(String text, String text2) {
        if (text.equalsIgnoreCase(text2)) {
            return 1.0;
        } else {
            int value;
            if ((value = Math.max(text.length(), text2.length())) == 0) {
                return 1.0;
            } else if (!text.contains(text2) && !text2.contains(text)) {
                return (double) calculateCount(text, text2) / value;
            } else {
                return text.length() > text2.length()
                        ? (double) text2.length() / text.length()
                        : (double) text.length() / text2.length();
            }
        }
    }

    private static int calculateCount(String text, String text2) {
        int value = Math.min(text.length(), text2.length());
        int value2 = 0;
        for (int index = 0; index < value; index++) {
            if (text.charAt(index) == text2.charAt(index)) {
                value2++;
            }
        }
        return value2;
    }
}
