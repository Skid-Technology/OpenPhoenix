package xyz.refinedev.phoenix.utils.general;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import xyz.refinedev.phoenix.utils.XMaterial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WoolUtil {

    public static final ArrayList<ChatColor> woolColors;

    public static final List<XMaterial> materials;

    public static final List<XMaterial> terracota;

    public static final List<XMaterial> panes;

    public static final List<XMaterial> dyes;

    public static final List<XMaterial> beds;

    public static int convertChatColorToWoolData(ChatColor chatColor) {
        if (chatColor == ChatColor.DARK_RED) {
            chatColor = ChatColor.RED;
        }
        if (chatColor == ChatColor.DARK_BLUE) {
            chatColor = ChatColor.BLUE;
        }
        return woolColors.indexOf(chatColor);
    }

    public static Material intToWool(int value) {
        return at(materials, value);
    }

    public static Material intToTerracota(int value) {
        return at(terracota, value);
    }

    public static Material intToPanes(int value) {
        return at(panes, value);
    }

    public static Material intToDye(int value) {
        return at(dyes, value);
    }

    public static Material intToBed(int value) {
        return at(beds, value);
    }

    private static Material at(List<XMaterial> values, int value) {
        return value >= 0 && value < values.size()
                ? ((XMaterial) values.get(value)).get()
                : ((XMaterial) values.get(0)).get();
    }

    static {
        ChatColor[] chatColors = new ChatColor[16];
        chatColors[0] = ChatColor.WHITE;
        chatColors[1] = ChatColor.GOLD;
        chatColors[2] = ChatColor.LIGHT_PURPLE;
        chatColors[3] = ChatColor.AQUA;
        chatColors[4] = ChatColor.YELLOW;
        chatColors[5] = ChatColor.GREEN;
        chatColors[6] = ChatColor.LIGHT_PURPLE;
        chatColors[7] = ChatColor.DARK_GRAY;
        chatColors[8] = ChatColor.GRAY;
        chatColors[9] = ChatColor.DARK_AQUA;
        chatColors[10] = ChatColor.DARK_PURPLE;
        chatColors[11] = ChatColor.BLUE;
        chatColors[12] = ChatColor.RESET;
        chatColors[13] = ChatColor.DARK_GREEN;
        chatColors[14] = ChatColor.RED;
        chatColors[15] = ChatColor.BLACK;
        woolColors = new ArrayList<>(Arrays.asList(chatColors));
        XMaterial[] xMaterials = new XMaterial[16];
        xMaterials[0] = XMaterial.WHITE_WOOL;
        xMaterials[1] = XMaterial.ORANGE_WOOL;
        xMaterials[2] = XMaterial.MAGENTA_WOOL;
        xMaterials[3] = XMaterial.LIGHT_BLUE_WOOL;
        xMaterials[4] = XMaterial.YELLOW_WOOL;
        xMaterials[5] = XMaterial.LIME_WOOL;
        xMaterials[6] = XMaterial.PINK_WOOL;
        xMaterials[7] = XMaterial.GRAY_WOOL;
        xMaterials[8] = XMaterial.LIGHT_GRAY_WOOL;
        xMaterials[9] = XMaterial.CYAN_WOOL;
        xMaterials[10] = XMaterial.PURPLE_WOOL;
        xMaterials[11] = XMaterial.BLUE_WOOL;
        xMaterials[12] = XMaterial.BROWN_WOOL;
        xMaterials[13] = XMaterial.GREEN_WOOL;
        xMaterials[14] = XMaterial.RED_WOOL;
        xMaterials[15] = XMaterial.BLACK_WOOL;
        materials = new ArrayList<>(Arrays.asList(xMaterials));
        XMaterial[] xMaterials2 = new XMaterial[16];
        xMaterials2[0] = XMaterial.WHITE_TERRACOTTA;
        xMaterials2[1] = XMaterial.ORANGE_TERRACOTTA;
        xMaterials2[2] = XMaterial.MAGENTA_TERRACOTTA;
        xMaterials2[3] = XMaterial.LIGHT_BLUE_TERRACOTTA;
        xMaterials2[4] = XMaterial.YELLOW_TERRACOTTA;
        xMaterials2[5] = XMaterial.LIME_TERRACOTTA;
        xMaterials2[6] = XMaterial.PINK_TERRACOTTA;
        xMaterials2[7] = XMaterial.GRAY_TERRACOTTA;
        xMaterials2[8] = XMaterial.LIGHT_GRAY_TERRACOTTA;
        xMaterials2[9] = XMaterial.CYAN_TERRACOTTA;
        xMaterials2[10] = XMaterial.PURPLE_TERRACOTTA;
        xMaterials2[11] = XMaterial.BLUE_TERRACOTTA;
        xMaterials2[12] = XMaterial.BROWN_TERRACOTTA;
        xMaterials2[13] = XMaterial.GREEN_TERRACOTTA;
        xMaterials2[14] = XMaterial.RED_TERRACOTTA;
        xMaterials2[15] = XMaterial.BLACK_TERRACOTTA;
        terracota = new ArrayList<>(Arrays.asList(xMaterials2));
        XMaterial[] xMaterials3 = new XMaterial[16];
        xMaterials3[0] = XMaterial.WHITE_STAINED_GLASS_PANE;
        xMaterials3[1] = XMaterial.ORANGE_STAINED_GLASS_PANE;
        xMaterials3[2] = XMaterial.MAGENTA_STAINED_GLASS_PANE;
        xMaterials3[3] = XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE;
        xMaterials3[4] = XMaterial.YELLOW_STAINED_GLASS_PANE;
        xMaterials3[5] = XMaterial.LIME_STAINED_GLASS_PANE;
        xMaterials3[6] = XMaterial.PINK_STAINED_GLASS_PANE;
        xMaterials3[7] = XMaterial.GRAY_STAINED_GLASS_PANE;
        xMaterials3[8] = XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE;
        xMaterials3[9] = XMaterial.CYAN_STAINED_GLASS_PANE;
        xMaterials3[10] = XMaterial.PURPLE_STAINED_GLASS_PANE;
        xMaterials3[11] = XMaterial.BLUE_STAINED_GLASS_PANE;
        xMaterials3[12] = XMaterial.BROWN_STAINED_GLASS_PANE;
        xMaterials3[13] = XMaterial.GREEN_STAINED_GLASS_PANE;
        xMaterials3[14] = XMaterial.RED_STAINED_GLASS_PANE;
        xMaterials3[15] = XMaterial.BLACK_STAINED_GLASS_PANE;
        panes = new ArrayList<>(Arrays.asList(xMaterials3));
        XMaterial[] xMaterials4 = new XMaterial[16];
        xMaterials4[0] = XMaterial.INK_SAC;
        xMaterials4[1] = XMaterial.RED_DYE;
        xMaterials4[2] = XMaterial.GREEN_DYE;
        xMaterials4[3] = XMaterial.COCOA_BEANS;
        xMaterials4[4] = XMaterial.LAPIS_LAZULI;
        xMaterials4[5] = XMaterial.PURPLE_DYE;
        xMaterials4[6] = XMaterial.CYAN_DYE;
        xMaterials4[7] = XMaterial.LIGHT_GRAY_DYE;
        xMaterials4[8] = XMaterial.GRAY_DYE;
        xMaterials4[9] = XMaterial.PINK_DYE;
        xMaterials4[10] = XMaterial.LIME_DYE;
        xMaterials4[11] = XMaterial.YELLOW_DYE;
        xMaterials4[12] = XMaterial.LIGHT_BLUE_DYE;
        xMaterials4[13] = XMaterial.MAGENTA_DYE;
        xMaterials4[14] = XMaterial.ORANGE_DYE;
        xMaterials4[15] = XMaterial.WHITE_DYE;
        dyes = new ArrayList<>(Arrays.asList(xMaterials4));
        XMaterial[] xMaterials5 = new XMaterial[16];
        xMaterials5[0] = XMaterial.WHITE_BED;
        xMaterials5[1] = XMaterial.ORANGE_BED;
        xMaterials5[2] = XMaterial.MAGENTA_BED;
        xMaterials5[3] = XMaterial.LIGHT_BLUE_BED;
        xMaterials5[4] = XMaterial.YELLOW_BED;
        xMaterials5[5] = XMaterial.LIME_BED;
        xMaterials5[6] = XMaterial.PINK_BED;
        xMaterials5[7] = XMaterial.GRAY_BED;
        xMaterials5[8] = XMaterial.LIGHT_GRAY_BED;
        xMaterials5[9] = XMaterial.CYAN_BED;
        xMaterials5[10] = XMaterial.PURPLE_BED;
        xMaterials5[11] = XMaterial.BLUE_BED;
        xMaterials5[12] = XMaterial.BROWN_BED;
        xMaterials5[13] = XMaterial.GREEN_BED;
        xMaterials5[14] = XMaterial.RED_BED;
        xMaterials5[15] = XMaterial.BLACK_BED;
        beds = new ArrayList<>(Arrays.asList(xMaterials5));
    }
}
