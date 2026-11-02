package xyz.refinedev.phoenix.utils;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.handler.HookHandler;
import xyz.refinedev.phoenix.nametag.NameTagService;
import xyz.refinedev.phoenix.utils.nametags.util.ScoreboardUtil;

public final class NameTagUtils {

    public static void applyPlayer(Player player) {
        HookHandler hookHandler = Phoenix.getInstance().getHookHandler();
        NameTagService nameTagService;
        if (SettingsConfig.SERVER.NAMETAGS
                && (nameTagService = Phoenix.getInstance().getNameTagService()) != null) {
            nameTagService.applyPlayer2(player);
        }
        if (hookHandler.isHookEnabled("Apollo-Bukkit")) {
            hookHandler.getHook("Apollo-Bukkit").refreshPlayer(player);
        }
        if (hookHandler.isHookEnabled("TAB")) {
            hookHandler.getHook("TAB").refreshPlayer(player);
        }
    }

    public static void applyPlayerByNAMETAGS(Player player) {
        HookHandler hookHandler = Phoenix.getInstance().getHookHandler();
        NameTagService nameTagService;
        if (SettingsConfig.SERVER.NAMETAGS
                && (nameTagService = Phoenix.getInstance().getNameTagService()) != null) {
            nameTagService.applyPlayer3(player);
        }
        if (hookHandler.isHookEnabled("Apollo-Bukkit")) {
            hookHandler.getHook("Apollo-Bukkit").refreshPlayer(player);
        }
        if (hookHandler.isHookEnabled("TAB")) {
            hookHandler.getHook("TAB").refreshPlayer(player);
        }
    }

    public static void applyPlayer(Player player, String text) {
        if (Phoenix.getInstance().getNameTagHandler().isInitialized()) {
            ScoreboardUtil.separateTablistName(player, text);
        } else {
            applyPlayerByPlayer(player);
        }
    }

    public static void applyPlayerByVoid(Player player) {
        applyPlayer(player, player.getName());
    }

    public static void applyPlayerByPlayer(Player player) {
        if (SettingsConfig.SERVER.NAMETAGS) {
            NameTagService nameTagService;
            if ((nameTagService = Phoenix.getInstance().getNameTagService()) != null) {
                nameTagService.applyPlayer4(player);
            }
        }
    }

    private NameTagUtils() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
