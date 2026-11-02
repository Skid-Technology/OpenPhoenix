package xyz.refinedev.phoenix.setup;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.chat.Clickable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SetupMessages {

    public static void sendMessage(CommandSender sender, List<SetupCheck> values) {
        HashSet values2 = new HashSet();
        for (SetupCheck setupCheck : values) {
            sender.sendMessage(formatString(sender, setupCheck));
            for (String text : setupCheck.getStrings()) {
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.PHOENIX.DOCTOR.DETAIL)
                                        .replace("<message>", text)));
            }
            applyCommandSender(sender, setupCheck, values2);
        }
    }

    public static String formatString(CommandSender sender, SetupCheck setupCheck) {
        return Color.translate(
                LocaleUtil.tr(sender, ordinal(setupCheck.getCheckStatus()))
                        .replace("<message>", setupCheck.getTextString()));
    }

    public static void sendMessage(CommandSender sender, List<SetupCheck> values, String text) {
        sender.sendMessage(Color.translate(text));
        HashSet values2 = new HashSet();
        for (SetupCheck setupCheck : values) {
            sender.sendMessage(formatString(sender, setupCheck));
            for (String text2 : setupCheck.getStrings()) {
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.PHOENIX.DOCTOR.DETAIL)
                                        .replace("<message>", text2)));
            }
            if (setupCheck.getText2() != null) {
                sendMessageByFIX(sender, setupCheck.getText2());
            }
            applyCommandSender(sender, setupCheck, values2);
        }
    }

    public static void sendMessage(CommandSender sender, String text) {
        String text2;
        if ((text2 = SetupCatalog.formatString(text)) != null) {
            String text3 =
                    LocaleUtil.tr(sender, MessageConfig.PHOENIX.SETUP.DOCS).replace("<url>", text2);
            if (!(sender instanceof Player)) {
                sender.sendMessage(Color.translate(text3));
            } else {
                Clickable clickable;
                (clickable = new Clickable(""))
                        .addUrl(
                                text3,
                                LocaleUtil.tr(sender, MessageConfig.PHOENIX.SETUP.DOCS_HOVER),
                                text2);
                clickable.sendToPlayer(sender);
            }
        }
    }

    private static void applyCommandSender(
            CommandSender sender, SetupCheck setupCheck, Set<String> values) {
        if (setupCheck.getCheckStatus() != CheckStatus.OK && values.add(setupCheck.getText())) {
            sendMessage(sender, setupCheck.getText());
        }
    }

    private static void sendMessageByFIX(CommandSender sender, String text) {
        String text2 =
                LocaleUtil.tr(sender, MessageConfig.PHOENIX.SETUP.FIX).replace("<fix>", text);
        String text3;
        if ((text3 = formatString(text)) != null && sender instanceof Player) {
            new Clickable(
                            text2,
                            LocaleUtil.tr(sender, MessageConfig.PHOENIX.SETUP.FIX_HOVER)
                                    .replace("<command>", text3),
                            null,
                            text3)
                    .sendToPlayer(sender);
        } else {
            sender.sendMessage(Color.translate(text2));
        }
    }

    private static String formatString(String text) {
        int value;
        return text != null && text.startsWith("/")
                ? ((value = text.indexOf(" -- ")) < 0 ? text : text.substring(0, value)).trim()
                : null;
    }

    private static String ordinal(CheckStatus checkStatus) {
        switch (checkStatus) {
            case FAIL:
                return MessageConfig.PHOENIX.DOCTOR.FAIL;
            case WARN:
                return MessageConfig.PHOENIX.DOCTOR.WARN;
            case TODO:
                return MessageConfig.PHOENIX.DOCTOR.TODO;
            default:
                return MessageConfig.PHOENIX.DOCTOR.OK;
        }
    }

    private SetupMessages() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
