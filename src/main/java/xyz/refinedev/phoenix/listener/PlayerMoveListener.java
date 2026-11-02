package xyz.refinedev.phoenix.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

public class PlayerMoveListener implements Listener {

    private final Phoenix plugin;

    private boolean matchesEnabled(Player player) {
        IProfile profile;
        return (profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                        != null
                && profile.isTwoFactor();
    }

    private boolean matchesEnabledByPlugin(Player player) {
        IProfile profile;
        return (profile = this.plugin.getProfileHandler().getProfile(player.getUniqueId())) != null
                && profile.isTwoFactor();
    }

    private boolean matchesEnabled(String text) {
        for (String text2 : SettingsConfig.AUTH.ALLOWED_COMMANDS) {
            if (text.startsWith(text2)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        Player player = playerMoveEvent.getPlayer();
        Location from = playerMoveEvent.getFrom();
        Location to = playerMoveEvent.getTo();
        if ((from.getX() != to.getX() || from.getZ() != to.getZ()) && this.matchesEnabled(player)) {
            this.plugin.getNmsHandler().getTeleportAPI().teleport(player, from);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
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
    public void onInventoryCreative(InventoryCreativeEvent inventoryCreativeEvent) {
        if (inventoryCreativeEvent.getWhoClicked() instanceof Player) {
            if (this.matchesEnabled((Player) inventoryCreativeEvent.getWhoClicked())) {
                inventoryCreativeEvent.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryInteract(InventoryInteractEvent inventoryInteractEvent) {
        if (inventoryInteractEvent.getWhoClicked() instanceof Player) {
            if (this.matchesEnabled((Player) inventoryInteractEvent.getWhoClicked())) {
                inventoryInteractEvent.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onVehicleEnter(VehicleEnterEvent vehicleEnterEvent) {
        if (vehicleEnterEvent.getEntered() instanceof Player) {
            if (this.matchesEnabled((Player) vehicleEnterEvent.getEntered())) {
                vehicleEnterEvent.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocess(
            PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
        if (this.matchesEnabledByPlugin(playerCommandPreprocessEvent.getPlayer())
                && !this.matchesEnabled(
                        playerCommandPreprocessEvent.getMessage().replace("/", ""))) {
            playerCommandPreprocessEvent
                    .getPlayer()
                    .sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(
                                            playerCommandPreprocessEvent.getPlayer(),
                                            MessageConfig.AUTH.CANNOT_COMMAND)));
            playerCommandPreprocessEvent.setCancelled(true);
        }
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
        if (entityDamageByEntityEvent.getDamager() instanceof Player) {
            Player player = (Player) entityDamageByEntityEvent.getDamager();
            if (this.matchesEnabled(player)) {
                entityDamageByEntityEvent.setCancelled(true);
            }
        }
    }

    public PlayerMoveListener(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
