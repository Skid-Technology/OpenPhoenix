package xyz.refinedev.phoenix.utils.reflection;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.Preconditions;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class InventoryTitleUpdate {

    private static Method m_InventoryView_setTitle;

    private static boolean resolvedViewSetTitle;

    private static Method m_Player_GetHandle;

    private static Method m_PlayerConnection_sendPacket;

    private static Method m_CraftChatMessage_fromString;

    private static Method m_EntityPlayer_updateInventory;

    private static Field f_EntityPlayer_playerConnection;

    private static Field f_EntityPlayer_activeContainer;

    private static Field f_Container_windowId;

    private static Constructor<?> c_PacketOpenWindow;

    private static String nms_package;

    private static String crb_package;

    public static void sendInventoryTitle(Player player, String text) {
        Preconditions.checkNotNull(player, "player");
        String text2 = Color.translate(text);
        if (!setViewTitle(player, text2)) {
            try {
                sendInventoryTitle0(player, text2);
            } catch (Exception exception) {
            }
        }
    }

    private static boolean setViewTitle(Player player, String title) {
        if (m_InventoryView_setTitle == null) {
            if (resolvedViewSetTitle) {
                return false;
            }
            resolvedViewSetTitle = true;
            try {
                m_InventoryView_setTitle =
                        player.getOpenInventory().getClass().getMethod("setTitle", String.class);
            } catch (Throwable exception) {
                return false;
            }
        }
        try {
            m_InventoryView_setTitle.invoke(player.getOpenInventory(), title);
            return true;
        } catch (Throwable exception) {
            return false;
        }
    }

    private static void sendInventoryTitle0(Player player, String text) throws Exception {
        Inventory inventory;
        if ((inventory = player.getOpenInventory().getTopInventory()) != null) {
            if (m_Player_GetHandle == null) {
                m_Player_GetHandle = player.getClass().getMethod("getHandle");
            }
            Object result = m_Player_GetHandle.invoke(player);
            if (Phoenix.is1point16()) {
                if (f_EntityPlayer_playerConnection == null) {
                    f_EntityPlayer_playerConnection = result.getClass().getField("b");
                }
            } else if (f_EntityPlayer_playerConnection == null) {
                f_EntityPlayer_playerConnection = result.getClass().getField("playerConnection");
            }
            Object result2 = f_EntityPlayer_playerConnection.get(result);
            if (f_EntityPlayer_activeContainer == null) {
                f_EntityPlayer_activeContainer = result.getClass().getField("activeContainer");
            }
            Object result3 = f_EntityPlayer_activeContainer.get(result);
            if (f_Container_windowId == null) {
                f_Container_windowId = result3.getClass().getField("windowId");
            }
            int intValue = f_Container_windowId.getInt(result3);
            String text2;
            if ((text2 = getNmsVersion()).startsWith("v1_7_")) {
                sendPacket15a16a17(result2, result, result3, intValue, inventory, text);
            } else if (text2.startsWith("v1_8_")) {
                sendPacket18(result2, result, result3, intValue, inventory, text);
            } else {
                sendPacket18(result2, result, result3, intValue, inventory, text);
            }
        }
    }

    private static void sendPacket15a16a17(
            Object result,
            Object result2,
            Object result3,
            int value,
            Inventory inventory,
            String text)
            throws Exception {
        if (c_PacketOpenWindow == null) {
            c_PacketOpenWindow =
                    findNmsClass("PacketPlayOutOpenWindow")
                            .getConstructor(
                                    int.class, int.class, String.class, int.class, boolean.class);
        }
        byte value2;
        int value3;
        switch (inventory.getType()) {
            case ANVIL:
                value2 = 8;
                value3 = 9;
                break;
            case BEACON:
                value2 = 7;
                value3 = 1;
                break;
            case BREWING:
                value2 = 5;
                value3 = 4;
                break;
            case CRAFTING:
                return;
            case CREATIVE:
                return;
            case DISPENSER:
                value2 = 3;
                value3 = 9;
                break;
            case DROPPER:
                value2 = 10;
                value3 = 9;
                break;
            case ENCHANTING:
                value2 = 4;
                value3 = 9;
                break;
            case CHEST:
                value2 = 0;
                value3 = inventory.getSize();
                break;
            case FURNACE:
                value2 = 2;
                value3 = 2;
                break;
            case HOPPER:
                value2 = 9;
                value3 = 5;
                break;
            case MERCHANT:
                value2 = 6;
                value3 = 3;
                break;
            case PLAYER:
                return;
            case WORKBENCH:
                value2 = 1;
                value3 = 9;
                break;
            default:
                return;
        }
        if (text != null && text.length() > 32) {
            text = text.substring(0, 32);
        }
        if (m_EntityPlayer_updateInventory == null) {
            m_EntityPlayer_updateInventory =
                    result2.getClass().getMethod("updateInventory", findNmsClass("Container"));
        }
        Object result4 =
                c_PacketOpenWindow.newInstance(
                        value, Integer.valueOf(value2), text != null ? text : "", value3, true);
        sendPacket(result, result4);
        m_EntityPlayer_updateInventory.invoke(result2, result3);
    }

    private static void sendPacket18(
            Object result,
            Object result2,
            Object result3,
            int value,
            Inventory inventory,
            String text)
            throws Exception {
        if (c_PacketOpenWindow == null) {
            c_PacketOpenWindow =
                    findNmsClass("PacketPlayOutOpenWindow")
                            .getConstructor(
                                    int.class,
                                    String.class,
                                    findNmsClass("IChatBaseComponent"),
                                    int.class);
        }
        int value2 = 0;
        String text2;
        switch (inventory.getType()) {
            case ANVIL:
                text2 = "minecraft:anvil";
                break;
            case BEACON:
                text2 = "minecraft:beacon";
                break;
            case BREWING:
                text2 = "minecraft:brewing_stand";
                break;
            case CRAFTING:
                return;
            case CREATIVE:
                return;
            case DISPENSER:
                text2 = "minecraft:dispenser";
                break;
            case DROPPER:
                text2 = "minecraft:dropper";
                break;
            case ENCHANTING:
                text2 = "minecraft:enchanting_table";
                break;
            case CHEST:
                text2 = "minecraft:chest";
                value2 = inventory.getSize();
                break;
            case FURNACE:
                text2 = "minecraft:furnace";
                break;
            case HOPPER:
                text2 = "minecraft:hopper";
                break;
            case MERCHANT:
                text2 = "minecraft:villager";
                value2 = 3;
                break;
            case PLAYER:
                return;
            case WORKBENCH:
                text2 = "minecraft:crafting_table";
                break;
            default:
                return;
        }
        if (m_CraftChatMessage_fromString == null) {
            m_CraftChatMessage_fromString =
                    findCrbClass("util.CraftChatMessage").getMethod("fromString", String.class);
        }
        if (m_EntityPlayer_updateInventory == null) {
            m_EntityPlayer_updateInventory =
                    result2.getClass().getMethod("updateInventory", findNmsClass("Container"));
        }
        Object result4 =
                c_PacketOpenWindow.newInstance(
                        value,
                        text2,
                        findNmsClass("ChatMessage")
                                .getConstructor(String.class, Object[].class)
                                .newInstance(text, new Object[0]),
                        value2);
        sendPacket(result, result4);
        m_EntityPlayer_updateInventory.invoke(result2, result3);
    }

    private static void sendPacket(Object result, Object result2) throws Exception {
        if (Phoenix.is1point16()) {
            if (m_PlayerConnection_sendPacket == null) {
                m_PlayerConnection_sendPacket =
                        result.getClass().getMethod("a", findNmsClass("Packet"));
            }
            m_PlayerConnection_sendPacket.invoke(result, result2);
        } else {
            if (m_PlayerConnection_sendPacket == null) {
                m_PlayerConnection_sendPacket =
                        result.getClass().getMethod("sendPacket", findNmsClass("Packet"));
            }
            m_PlayerConnection_sendPacket.invoke(result, result2);
        }
    }

    private static String getNmsVersion() {
        return Phoenix.VERSION;
    }

    private static String getNmsPackage() {
        if (nms_package == null) {
            nms_package = "net.minecraft.server." + getNmsVersion();
        }
        return nms_package;
    }

    private static String getCrbPackage() {
        if (crb_package == null) {
            crb_package = "org.bukkit.craftbukkit." + getNmsVersion();
        }
        return crb_package;
    }

    private static Class<?> findNmsClass(String text) throws Exception {
        return Class.forName(getNmsPackage() + "." + text);
    }

    private static Class<?> findCrbClass(String text) throws Exception {
        return Class.forName(getCrbPackage() + "." + text);
    }

    private InventoryTitleUpdate() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
