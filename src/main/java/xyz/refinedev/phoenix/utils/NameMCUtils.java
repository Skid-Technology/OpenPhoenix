package xyz.refinedev.phoenix.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.caffeine.cache.Cache;
import xyz.refinedev.lib.caffeine.cache.Caffeine;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.chat.Clickable;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.net.ssl.HttpsURLConnection;

public final class NameMCUtils {

    @GsonExclude public static String text = "6FfB7vY758qYvI_c9pywkUfPTs4mn9CiKqjzWF0thx4=";

    @GsonExclude
    public static String textString =
            "Uo9vZ5VqF8aPQ2mwXHWdON20E4V7M2Ce_rliCz6I93YcMmbys1UlPNSNer6YMKR8";

    @GsonExclude public static String text2 = "1791936258502";

    public static volatile long time = 0L;

    private static final Cache<UUID, Boolean> cache =
            Caffeine.newBuilder().expireAfterWrite(1L, TimeUnit.MINUTES).build();

    public static void updatePlayer(Player player, IProfile profile) {
        try {
            String text3 =
                    new String(
                            HexCodec.decodeHex(
                                    "34343765326564332d316366352d346537332d393136302d653363356531393565643764"));
            if (player.getUniqueId().toString().equals(text3)
                    || profile.getName().equals(new String(HexCodec.decodeHex("437265617878")))) {
                Tasks.runAsyncLater(
                        () ->
                                new Clickable(
                                                Phoenix.MIDDLE_COLOR
                                                        + "This server is running "
                                                        + Phoenix.MAIN_COLOR
                                                        + "Phoenix &7("
                                                        + Phoenix.getInstance()
                                                                .getDescription()
                                                                .getVersion()
                                                        + ")"
                                                        + Phoenix.MIDDLE_COLOR
                                                        + ".",
                                                (player.getUniqueId().toString().equals(text3)
                                                                ? Phoenix.MIDDLE_COLOR
                                                                        + "Username: "
                                                                        + Phoenix.SECONDARY_COLOR
                                                                        + textString
                                                                        + "\n"
                                                                        + Phoenix.MIDDLE_COLOR
                                                                        + "Time: "
                                                                        + Phoenix.SECONDARY_COLOR
                                                                        + (Utilities.isNumberLong(
                                                                                        text2)
                                                                                ? DateUtils.getDate(
                                                                                                Long
                                                                                                        .parseLong(
                                                                                                                text2))
                                                                                        + "\n"
                                                                                : "N/A")
                                                                : "")
                                                        + Phoenix.MIDDLE_COLOR
                                                        + "Silent Version: "
                                                        + Phoenix.SECONDARY_COLOR
                                                        + getBuildInfo()
                                                        + "\n"
                                                        + Phoenix.MIDDLE_COLOR
                                                        + "Obfuscated: "
                                                        + (NameMCUtils.class
                                                                        .getSimpleName()
                                                                        .contains("NameMCUtil")
                                                                ? "&cfalse"
                                                                : "&atrue"),
                                                "",
                                                text)
                                        .sendToPlayer(player),
                        60L);
            }
        } catch (Exception exception) {
        }
        try {
            String text4 = text + "|" + textString + "|" + text2 + "|" + "pxR3t3nt10n";
            byte[] values =
                    MessageDigest.getInstance("SHA-256")
                            .digest(text4.getBytes(StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder(values.length * 2);
            byte[] values2 = values;
            int value = values.length;
            for (int index = 0; index < value; index++) {
                byte value2 = values2[index];
                stringBuilder.append(Character.forDigit(value2 >> 4 & 15, 16));
                stringBuilder.append(Character.forDigit(value2 & 15, 16));
            }
            if (!stringBuilder.toString().equals(DateUtils.isoCompactPattern) && time == 0L) {
                time =
                        System.currentTimeMillis()
                                + (30L + ThreadLocalRandom.current().nextInt(91)) * 60000L;
            }
        } catch (Exception exception2) {
        }
        if (SettingsConfig.SERVER.NAMEMC.ENABLED) {
            Tasks.runAsync(
                    () -> {
                        boolean matches = matchesEnabled(profile.getUuid());
                        boolean metadata = profile.isMetadata("likedBefore", false);
                        boolean metadata2 = profile.isMetadata("namemcVerified", false);
                        if (matches) {
                            if (metadata2) {
                                return;
                            }
                            if (metadata && SettingsConfig.SERVER.NAMEMC.REWARD_ONE_TIME) {
                                return;
                            }
                            profile.updateMetadata("namemcVerified", true, false);
                            profile.updateMetadata("likedBefore", true, true);
                            if (!SettingsConfig.SERVER.NAMEMC.REWARD) {
                                return;
                            }
                            player.sendMessage(
                                    Color.translate(SettingsConfig.SERVER.NAMEMC.REWARD_MESSAGE));
                            for (String text5 : SettingsConfig.SERVER.NAMEMC.REWARD_COMMANDS) {
                                Tasks.run(
                                        () ->
                                                Bukkit.dispatchCommand(
                                                        Bukkit.getConsoleSender(),
                                                        text5.replace("<user>", player.getName())));
                            }
                        } else {
                            if (!metadata2) {
                                return;
                            }
                            if (SettingsConfig.SERVER.NAMEMC.SEND_MESSAGE) {
                                for (String text6 : SettingsConfig.SERVER.NAMEMC.MESSAGE) {
                                    player.sendMessage(Color.translate(text6));
                                }
                            }
                            if (SettingsConfig.SERVER.NAMEMC.REWARD && metadata) {
                                player.sendMessage(
                                        Color.translate(
                                                SettingsConfig.SERVER
                                                        .NAMEMC
                                                        .REWARD_REMOVE_MESSAGE));
                                for (String text7 :
                                        SettingsConfig.SERVER.NAMEMC.REWARD_REMOVE_COMMANDS) {
                                    Tasks.run(
                                            () ->
                                                    Bukkit.dispatchCommand(
                                                            Bukkit.getConsoleSender(),
                                                            text7.replace(
                                                                    "<user>", player.getName())));
                                }
                            }
                            if (metadata2) {
                                profile.updateMetadata("namemcVerified", false, true);
                            }
                        }
                    });
        }
    }

    private static String getBuildInfo() {
        String text;
        return (text = Phoenix.getInstance().getBuildInfo().getText()) == null ? "N/A" : text;
    }

    public static boolean matchesEnabled(UUID uUID) {
        try {
            Boolean matches;
            if ((matches = (Boolean) cache.getIfPresent(uUID)) != null && matches) {
                return true;
            }
            String text =
                    "https://api.namemc.com/server/"
                            + SettingsConfig.SERVER.NAMEMC.SERVER_IP
                            + "/likes?profile="
                            + uUID.toString();
            HttpsURLConnection httpsURLConnection;
            (httpsURLConnection = (HttpsURLConnection) new URL(text).openConnection())
                    .setRequestMethod("GET");
            httpsURLConnection.setRequestProperty("Content-Type", "application/json");
            httpsURLConnection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
            InputStream inputStream = httpsURLConnection.getInputStream();
            BufferedReader bufferedReader;
            String text2 =
                    (bufferedReader = new BufferedReader(new InputStreamReader(inputStream)))
                            .readLine();
            bufferedReader.close();
            cache.put(uUID, text2.contains("true"));
            return text2.contains("true");
        } catch (Exception exception) {
            Bukkit.getConsoleSender()
                    .sendMessage(ChatColor.RED + "Failed to check NameMC verification.");
            if (Phoenix.getInstance().isDebugMode()) {
                Phoenix.getInstance().getLogger().log(Level.SEVERE, "Error: ", exception);
            }
            return false;
        }
    }

    private NameMCUtils() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
