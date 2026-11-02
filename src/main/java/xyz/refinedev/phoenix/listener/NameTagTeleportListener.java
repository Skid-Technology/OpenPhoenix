package xyz.refinedev.phoenix.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.NameTagsConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.nametag.NameTagService;
import xyz.refinedev.phoenix.utils.general.Tasks;

public class NameTagTeleportListener implements Listener {

    private final Phoenix plugin;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent playerDeathEvent) {
        NameTagService nameTagService;
        if ((nameTagService = this.getNameTagService()) != null) {
            nameTagService.applyPlayer7(playerDeathEvent.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent playerRespawnEvent) {
        NameTagService nameTagService;
        if ((nameTagService = this.getNameTagService()) != null) {
            NameTagService nameTagService2 = nameTagService;
            Tasks.runLater(() -> nameTagService2.applyPlayer4(playerRespawnEvent.getPlayer()), 2L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent playerTeleportEvent) {
        NameTagService nameTagService;
        if ((nameTagService = this.getNameTagService()) != null) {
            if (NameTagsConfig.DISPLAY.DEBUG) {
                Player player = playerTeleportEvent.getPlayer();
                Bukkit.getLogger()
                        .info(
                                "[PhoenixNT] teleport "
                                        + player.getName()
                                        + " cause="
                                        + playerTeleportEvent.getCause()
                                        + " vehicle="
                                        + (player.getVehicle() == null
                                                ? "none"
                                                : player.getVehicle().getType())
                                        + " from="
                                        + playerTeleportEvent.getFrom().toVector()
                                        + " to="
                                        + (playerTeleportEvent.getTo() == null
                                                ? "null"
                                                : playerTeleportEvent.getTo().toVector()));
            }
            Location from = playerTeleportEvent.getFrom();
            Location location;
            if ((location = playerTeleportEvent.getTo()) != null
                    && from.getWorld() == location.getWorld()) {
                if (!(from.distanceSquared(location) < 256.0)) {
                    NameTagService nameTagService2 = nameTagService;
                    Tasks.runLater(
                            () -> nameTagService2.applyPlayer6(playerTeleportEvent.getPlayer()),
                            2L);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent playerGameModeChangeEvent) {
        NameTagService nameTagService;
        if ((nameTagService = this.getNameTagService()) != null) {
            Player player = playerGameModeChangeEvent.getPlayer();
            if (playerGameModeChangeEvent.getNewGameMode() == GameMode.SPECTATOR) {
                nameTagService.applyPlayer7(player);
            } else {
                if (player.getGameMode() == GameMode.SPECTATOR) {
                    NameTagService nameTagService2 = nameTagService;
                    Tasks.runLater(() -> nameTagService2.applyPlayer4(player), 2L);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent playerChangedWorldEvent) {
        NameTagService nameTagService;
        if ((nameTagService = this.getNameTagService()) != null) {
            NameTagService nameTagService2 = nameTagService;
            Tasks.runLater(
                    () -> nameTagService2.applyPlayerByVoid(playerChangedWorldEvent.getPlayer()),
                    2L);
        }
    }

    private NameTagService getNameTagService() {
        return !SettingsConfig.SERVER.NAMETAGS ? null : this.plugin.getNameTagService();
    }

    public NameTagTeleportListener(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
