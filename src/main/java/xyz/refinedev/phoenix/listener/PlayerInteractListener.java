package xyz.refinedev.phoenix.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

public class PlayerInteractListener implements Listener {

    private final Phoenix plugin;

    private boolean matchesEnabled(Player player) {
        return this.plugin.getPanicHandler().isPanic(player.getUniqueId());
    }

    private boolean matchesEnabled(String text) {
        for (String text2 : SettingsConfig.PANIC.ALLOWED_COMMANDS) {
            if (text.toLowerCase().startsWith(text2.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
        if (this.matchesEnabled(playerInteractEvent.getPlayer())) {
            playerInteractEvent.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        if (this.matchesEnabled(blockBreakEvent.getPlayer())) {
            blockBreakEvent.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent blockPlaceEvent) {
        if (this.matchesEnabled(blockPlaceEvent.getPlayer())) {
            blockPlaceEvent.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        if (this.matchesEnabled((Player) inventoryClickEvent.getWhoClicked())) {
            inventoryClickEvent.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryCreative(InventoryCreativeEvent inventoryCreativeEvent) {
        inventoryCreativeEvent.setCancelled(
                this.matchesEnabled((Player) inventoryCreativeEvent.getWhoClicked()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onVehicleEnter(VehicleEnterEvent vehicleEnterEvent) {
        if (vehicleEnterEvent.getEntered() instanceof Player) {
            vehicleEnterEvent.setCancelled(
                    this.matchesEnabled((Player) vehicleEnterEvent.getEntered()));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocess(
            PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
        playerCommandPreprocessEvent.setCancelled(
                this.matchesEnabled(playerCommandPreprocessEvent.getPlayer())
                        && !this.matchesEnabled(playerCommandPreprocessEvent.getMessage()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent entityDamageEvent) {
        if (entityDamageEvent.getEntity() instanceof Player) {
            Player player = (Player) entityDamageEvent.getEntity();
            if (this.matchesEnabled(player)) {
                entityDamageEvent.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if (entityDamageByEntityEvent.getEntity() instanceof Player) {
            if (entityDamageByEntityEvent.getDamager() instanceof Player) {
                Player player = (Player) entityDamageByEntityEvent.getEntity();
                Player player2 = (Player) entityDamageByEntityEvent.getDamager();
                if (this.matchesEnabled(player2)) {
                    entityDamageByEntityEvent.setCancelled(true);
                } else {
                    if (this.matchesEnabled(player)) {
                        player2.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(
                                                player2, MessageConfig.PANIC.CANT_HIT_ENTITIES)));
                        entityDamageByEntityEvent.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        if (this.matchesEnabled(playerQuitEvent.getPlayer())) {
            this.plugin
                    .getPanicHandler()
                    .setPanic(playerQuitEvent.getPlayer().getUniqueId(), false);
        }
    }

    public PlayerInteractListener(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
