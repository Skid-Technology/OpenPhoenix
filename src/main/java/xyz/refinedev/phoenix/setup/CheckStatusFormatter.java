package xyz.refinedev.phoenix.setup;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public final class CheckStatusFormatter {

    private static final Pattern pattern =
            Pattern.compile("(?i)(&#[0-9a-f]{6}|&[0-9a-fk-or]|<[^<>]+>)+");

    public static String ordinal(CheckStatus checkStatus) {
        switch (checkStatus) {
            case FAIL:
                return MenuConfig.SETUP_MENU.STATUS.FAIL;
            case WARN:
                return MenuConfig.SETUP_MENU.STATUS.WARN;
            case TODO:
                return MenuConfig.SETUP_MENU.STATUS.TODO;
            default:
                return MenuConfig.SETUP_MENU.STATUS.OK;
        }
    }

    public static String formatString(CommandSender sender, CheckStatus checkStatus) {
        switch (checkStatus) {
            case FAIL:
                return LocaleUtil.tr(sender, MenuConfig.SETUP_MENU.STATUS.FAIL_LABEL);
            case WARN:
                return LocaleUtil.tr(sender, MenuConfig.SETUP_MENU.STATUS.WARN_LABEL);
            case TODO:
                return LocaleUtil.tr(sender, MenuConfig.SETUP_MENU.STATUS.TODO_LABEL);
            default:
                return LocaleUtil.tr(sender, MenuConfig.SETUP_MENU.STATUS.OK_LABEL);
        }
    }

    public static ItemStack create(CheckStatus checkStatus) {
        XMaterial xMaterial;
        switch (checkStatus) {
            case FAIL:
                xMaterial = XMaterial.RED_DYE;
                break;
            case WARN:
                xMaterial = XMaterial.ORANGE_DYE;
                break;
            case TODO:
                xMaterial = XMaterial.LIGHT_BLUE_DYE;
                break;
            default:
                xMaterial = XMaterial.LIME_DYE;
        }
        ItemStack itemStack;
        return (itemStack = xMaterial.parseItem()) == null
                ? new ItemStack(XMaterial.PAPER.get())
                : itemStack;
    }

    public static String formatString(String text) {
        return text != null && !text.isEmpty()
                ? "&f" + text.replace("&", "") + " " + text + "\u2588\u2588\u2588"
                : "&7(empty)";
    }

    public static boolean matchesEnabled(String text) {
        return text != null && !text.isEmpty() && pattern.matcher(text).matches();
    }

    public static String isEmpty(String text) {
        return text != null && !text.isEmpty() ? text : "&7(empty)";
    }

    public static List<String> findList(String text, String text2, String text3) {
        if (text != null && !text.isEmpty()) {
            ArrayList<String> values = new ArrayList<>();
            StringBuilder stringBuilder = new StringBuilder();
            String[] texts;
            int value = (texts = text.split(" ")).length;
            for (int index = 0; index < value; index++) {
                String text4 = texts[index];
                if (stringBuilder.length() > 0
                        && stringBuilder.length() + 1 + text4.length() > 42) {
                    values.add((values.isEmpty() ? text2 : text3) + stringBuilder);
                    stringBuilder.setLength(0);
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(' ');
                }
                stringBuilder.append(text4);
            }
            if (stringBuilder.length() > 0) {
                values.add((values.isEmpty() ? text2 : text3) + stringBuilder);
            }
            return values;
        } else {
            return Collections.emptyList();
        }
    }

    private CheckStatusFormatter() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
