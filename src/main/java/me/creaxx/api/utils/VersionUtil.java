package me.creaxx.api.utils;

import org.bukkit.Bukkit;

public class VersionUtil {

    public static String VERSION;

    public static int MINOR_VERSION;

    public static boolean canHex() {
        return MINOR_VERSION >= 16;
    }

    static {
        try {
            String text;
            VERSION =
                    (text = Bukkit.getServer().getClass().getPackage().getName()).length() < 4
                            ? text.split("\\.")[2]
                            : text.split("\\.")[3];
            MINOR_VERSION = Integer.parseInt(VERSION.split("_")[1]);
        } catch (Exception exception) {
            VERSION =
                    "v"
                            + Bukkit.getServer()
                                    .getBukkitVersion()
                                    .replace("-SNAPSHOT", "")
                                    .replace("-R0.", "_R")
                                    .replace(".", "_");
            MINOR_VERSION = Integer.parseInt(VERSION.split("_")[1]);
        }
    }
}
