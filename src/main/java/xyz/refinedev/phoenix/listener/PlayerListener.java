package xyz.refinedev.phoenix.listener;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.staffmod.StaffModModule;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.handler.HookHandler;
import xyz.refinedev.phoenix.hook.impl.ApolloBukkitHook;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        HookHandler hookHandler;
        if ((hookHandler = Phoenix.getInstance().getHookHandler()).isHookEnabled("Apollo-Bukkit")) {
            ((ApolloBukkitHook) hookHandler.getHook("Apollo-Bukkit")).refreshPlayer(player);
        }
        if (SettingsConfig.SERVER.LUNAR.STAFF_MODULES) {
            if (player.hasPermission("core.lunar.staffmods")) {
                Apollo.getPlayerManager()
                        .getPlayer(player.getUniqueId())
                        .ifPresent(
                                result ->
                                        ((StaffModModule)
                                                        Apollo.getModuleManager()
                                                                .getModule(StaffModModule.class))
                                                .enableAllStaffMods(result));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        Player player = playerQuitEvent.getPlayer();
        if (SettingsConfig.SERVER.LUNAR.STAFF_MODULES) {
            if (player.hasPermission("core.lunar.staffmods")) {
                Apollo.getPlayerManager()
                        .getPlayer(player.getUniqueId())
                        .ifPresent(
                                result ->
                                        ((StaffModModule)
                                                        Apollo.getModuleManager()
                                                                .getModule(StaffModModule.class))
                                                .disableAllStaffMods(result));
            }
        }
    }
}
