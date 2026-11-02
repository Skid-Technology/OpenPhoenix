package xyz.refinedev.phoenix.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.PluginManager;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.nametag.NameTagService;

public final class ClientWorldLoadListener {

    private static final String text = "io.papermc.paper.event.player.PlayerClientLoadedWorldEvent";

    private ClientWorldLoadListener() {}

    public static boolean matchesEnabled(Phoenix phoenix) {
        Class classValue;
        try {
            classValue =
                    Class.forName("io.papermc.paper.event.player.PlayerClientLoadedWorldEvent");
        } catch (Throwable throwable) {
            return false;
        }
        try {
            Listener listener = new Listener() {};
            PluginManager pluginManager = Bukkit.getPluginManager();
            pluginManager.registerEvent(
                    classValue,
                    listener,
                    EventPriority.MONITOR,
                    (result, result2) -> {
                        if (SettingsConfig.SERVER.NAMETAGS) {
                            if (result2 instanceof PlayerEvent) {
                                NameTagService nameTagService;
                                if ((nameTagService = phoenix.getNameTagService()) != null) {
                                    Player player = ((PlayerEvent) result2).getPlayer();
                                    nameTagService.applyPlayer(player);
                                }
                            }
                        }
                    },
                    phoenix);
            return true;
        } catch (Throwable throwable2) {
            phoenix.getLogger()
                    .warning(
                            "[PhoenixNT] could not bind io.papermc.paper.event.player.PlayerClientLoadedWorldEvent: "
                                    + throwable2);
            return false;
        }
    }
}
