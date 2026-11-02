package xyz.refinedev.phoenix.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class Utilities {

    public static String formatNumber(long value) {
        if (value >= 1000000000L) {
            return String.format("%.1fB", value / 1.0E9);
        } else if (value >= 1000000L) {
            return String.format("%.1fM", value / 1000000.0);
        } else {
            return value >= 1000L ? String.format("%.1fK", value / 1000.0) : String.valueOf(value);
        }
    }

    public static String formatNumberNegative(long value) {
        if (value <= -1000000000L) {
            return String.format("%.1fB", value / 1.0E9);
        } else if (value <= -1000000L) {
            return String.format("%.1fM", value / 1000000.0);
        } else {
            return value <= -1000L ? String.format("%.1fK", value / 1000.0) : String.valueOf(value);
        }
    }

    public static boolean isNumberInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public static boolean isNumberLong(String text) {
        try {
            Long.parseLong(text);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public static List<Player> getOnlinePlayers() {
        return new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
    }

    public static void playSound(Player player, String text) {
        try {
            player.playSound(player.getLocation(), Sound.valueOf(text), 2.0F, 2.0F);
        } catch (Exception exception) {
        }
    }

    public static void sendPlayer(Player player, String text, JavaPlugin javaPlugin) {
        if (!Phoenix.getInstance()
                .getNetworkHandler()
                .getNicePresence(player.getUniqueId())
                .equalsIgnoreCase(text)) {
            ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
            try {
                byteArrayDataOutput.writeUTF("Connect");
                byteArrayDataOutput.writeUTF(text);
            } catch (Exception exception) {
                javaPlugin.getLogger().log(Level.SEVERE, ErrorReporter.formatString(exception));
            }
            player.sendPluginMessage(javaPlugin, "BungeeCord", byteArrayDataOutput.toByteArray());
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.GENERAL.SEND_SERVER)
                                    .replace(
                                            "<server>",
                                            Phoenix.getInstance()
                                                    .getNetworkHandler()
                                                    .getServerByName(text)
                                                    .getDisplayName())));
        }
    }

    public static String getRandomSaltedString() {
        StringBuilder stringBuilder = new StringBuilder();
        while (stringBuilder.length() < 6) {
            float value = new Random().nextFloat();
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890".length();
            int value2 = (int) (value * 62.0F);
            stringBuilder.append(
                    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890"
                            .charAt(value2));
        }
        return stringBuilder.toString();
    }

    public static String getRandomSaltedString(int value) {
        StringBuilder stringBuilder = new StringBuilder();
        while (stringBuilder.length() < value) {
            float value2 = new Random().nextFloat();
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890".length();
            int value3 = (int) (value2 * 62.0F);
            stringBuilder.append(
                    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890"
                            .charAt(value3));
        }
        return stringBuilder.toString();
    }
}
