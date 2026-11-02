package xyz.refinedev.phoenix.utils.auth.code.googleapi;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public final class QrCodeImage {

    public static String formatString(
            Player player, GoogleAuthenticatorKey googleAuthenticatorKey, String text) {
        return String.format(
                "https://api.qrserver.com/v1/create-qr-code/?data=%s&size=128x128&ecc=M&margin=0",
                encode(
                        GoogleAuthenticatorQRGenerator.formatString(
                                text, player.getName(), googleAuthenticatorKey)));
    }

    public static int calculateCount(Player player) {
        for (int index = 0; index < 9; index++) {
            ItemStack itemStack;
            if ((itemStack = player.getInventory().getItem(index)) != null
                    && itemStack.hasItemMeta()
                    && itemStack.getItemMeta().hasLore()
                    && ((String) itemStack.getItemMeta().getLore().get(0)).contains("QR")) {
                return index;
            }
        }
        return 0;
    }

    public static void applyPlayer(Player player) {
        for (int index = 0; index < 9; index++) {
            ItemStack itemStack;
            if ((itemStack = player.getInventory().getItem(index)) != null
                    && itemStack.hasItemMeta()
                    && itemStack.getItemMeta().hasLore()
                    && ((String) itemStack.getItemMeta().getLore().get(0)).contains("QR")) {
                player.getInventory().remove(itemStack);
            }
        }
    }

    public static boolean authorize(
            GoogleAuthenticator googleAuthenticator, String text, int value) {
        return googleAuthenticator.authorize(text, value);
    }

    private static String encode(String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new IllegalArgumentException(
                    "UTF-8 encoding is not supported by URLEncoder.", unsupportedEncodingException);
        }
    }

    private QrCodeImage() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
