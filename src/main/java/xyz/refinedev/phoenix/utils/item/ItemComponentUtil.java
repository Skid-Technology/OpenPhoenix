package xyz.refinedev.phoenix.utils.item;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;

public final class ItemComponentUtil {

    private static Method setCustomModelData;

    private static Method setItemModel;

    private static Method setTooltipStyle;

    private static boolean resolvedCustomModelData;

    private static boolean resolvedItemModel;

    private static boolean resolvedTooltipStyle;

    public static boolean setCustomModelData(ItemMeta itemMeta, int value) {
        if (itemMeta == null) {
            return false;
        }
        if (!resolvedCustomModelData) {
            resolvedCustomModelData = true;
            try {
                Class[] classs = new Class[] {Integer.class};
                setCustomModelData = ItemMeta.class.getMethod("setCustomModelData", classs);
            } catch (Throwable throwable) {
                setCustomModelData = null;
            }
        }
        if (setCustomModelData == null) {
            return false;
        }
        try {
            setCustomModelData.invoke(itemMeta, value);
            return true;
        } catch (Throwable throwable2) {
            return false;
        }
    }

    public static boolean setItemModel(ItemMeta itemMeta, String text) {
        if (itemMeta != null && text != null && !text.isEmpty()) {
            if (!resolvedItemModel) {
                resolvedItemModel = true;
                try {
                    Class[] classs = new Class[] {NamespacedKey.class};
                    setItemModel = ItemMeta.class.getMethod("setItemModel", classs);
                } catch (Throwable throwable) {
                    setItemModel = null;
                }
            }
            if (setItemModel == null) {
                return false;
            }
            NamespacedKey namespacedKey;
            if ((namespacedKey = parseKey(text)) == null) {
                return false;
            }
            try {
                setItemModel.invoke(itemMeta, namespacedKey);
                return true;
            } catch (Throwable throwable2) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean setTooltipStyle(ItemMeta itemMeta, String text) {
        if (itemMeta != null && text != null && !text.isEmpty()) {
            if (!resolvedTooltipStyle) {
                resolvedTooltipStyle = true;
                try {
                    Class[] classs = new Class[] {NamespacedKey.class};
                    setTooltipStyle = ItemMeta.class.getMethod("setTooltipStyle", classs);
                } catch (Throwable throwable) {
                    setTooltipStyle = null;
                }
            }
            if (setTooltipStyle == null) {
                return false;
            }
            NamespacedKey namespacedKey;
            if ((namespacedKey = parseKey(text)) == null) {
                return false;
            }
            try {
                setTooltipStyle.invoke(itemMeta, namespacedKey);
                return true;
            } catch (Throwable throwable2) {
                return false;
            }
        } else {
            return false;
        }
    }

    private static NamespacedKey parseKey(String text) {
        try {
            int value;
            if ((value = text.indexOf(58)) < 0) {
                return NamespacedKey.minecraft(text.toLowerCase());
            }
            String text2 = text.substring(0, value).toLowerCase();
            String text3 = text.substring(value + 1).toLowerCase();
            return new NamespacedKey(text2, text3);
        } catch (Throwable throwable) {
            return null;
        }
    }

    private ItemComponentUtil() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
