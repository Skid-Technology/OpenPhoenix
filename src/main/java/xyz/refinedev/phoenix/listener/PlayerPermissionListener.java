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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.packet.impl.staff.LeftFrozenPacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

public class PlayerPermissionListener implements Listener {

    private final Phoenix plugin;

    private boolean matchesEnabled(Player player) {
        return this.plugin.getFreezeHandler().isFrozen(player.getUniqueId());
    }

    private boolean matchesEnabledByPlugin(Player player) {
        return this.plugin.getPanicHandler().isPanic(player.getUniqueId());
    }

    private boolean matchesEnabled(String text) {
        for (String text2 : SettingsConfig.FREEZE.ALLOWED_COMMANDS) {
            if (text.toLowerCase().startsWith(text2.toLowerCase())) {
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
        if ((this.matchesEnabled(player) || this.matchesEnabledByPlugin(player))
                && (from.getX() != to.getX() || from.getZ() != to.getZ())) {
            Phoenix.getInstance().getNmsHandler().getTeleportAPI().teleport(player, from);
        }
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
                if (this.matchesEnabled(player)) {
                    entityDamageByEntityEvent.setCancelled(true);
                } else {
                    if (this.matchesEnabled(player)) {
                        player2.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(
                                                player2, MessageConfig.FREEZE.CANT_HIT_ENTITIES)));
                        entityDamageByEntityEvent.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        Player player = playerQuitEvent.getPlayer();
        this.plugin.getDialogHandler().applyUUID(player.getUniqueId());
        if (this.matchesEnabled(player)) {
            IProfile profile = this.plugin.getProfileHandler().getProfile(player.getUniqueId());
            this.plugin
                    .getPacketHandler()
                    .send(
                            new LeftFrozenPacket(
                                    profile.getPlayerName(),
                                    profile.getNameWithColorNoDisguise(),
                                    this.plugin.getNetworkHandler().getDisplayName()));
        }
    }

    public PlayerPermissionListener(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
