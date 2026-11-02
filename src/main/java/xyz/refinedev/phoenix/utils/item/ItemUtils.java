package xyz.refinedev.phoenix.utils.item;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.utils.XMaterial;

import java.util.Optional;
import java.util.logging.Level;

public final class ItemUtils {

    private static final String SEPARATOR = ";";

    private static final String MODEL_DATA_KEY = "model-data";

    private static final String MODEL_KEY = "model";

    private static final String TOOLTIP_KEY = "tooltip";

    public static XMaterial parseFromX(String text) {
        Optional<XMaterial> optional;
        if (!(optional = XMaterial.matchXMaterial(materialOf(text))).isPresent()) {
            Phoenix.getInstance()
                    .getLogger()
                    .log(
                            Level.SEVERE,
                            "Invalid Material specified '{}', please fix this in menus.yml!",
                            text);
            return XMaterial.REDSTONE_BLOCK;
        } else {
            return (XMaterial) optional.get();
        }
    }

    public static String materialOf(String text) {
        int value;
        return text == null
                ? null
                : ((value = text.indexOf(";")) < 0 ? text : text.substring(0, value)).trim();
    }

    public static int modelDataOf(String text) {
        String text2;
        if ((text2 = valueOf(text, "model-data")) == null) {
            return -1;
        }
        try {
            return Integer.parseInt(text2);
        } catch (NumberFormatException numberFormatException) {
            Phoenix.getInstance()
                    .getLogger()
                    .warning("Invalid model-data '" + text2 + "' in '" + text + "', ignoring it.");
            return -1;
        }
    }

    public static String itemModelOf(String text) {
        return valueOf(text, "model");
    }

    public static String tooltipStyleOf(String text) {
        return valueOf(text, "tooltip");
    }

    private static String valueOf(String text, String text2) {
        if (text == null) {
            return null;
        }
        int value;
        if ((value = text.indexOf(";")) < 0) {
            return null;
        }
        String[] texts;
        int value2 = (texts = text.substring(value + 1).split(";")).length;
        for (int index = 0; index < value2; index++) {
            String text3;
            int value3;
            if ((value3 = (text3 = texts[index]).indexOf(61)) >= 0
                    && text3.substring(0, value3).trim().equalsIgnoreCase(text2)) {
                String text4;
                return (text4 = text3.substring(value3 + 1).trim()).isEmpty() ? null : text4;
            }
        }
        return null;
    }

    private ItemUtils() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
