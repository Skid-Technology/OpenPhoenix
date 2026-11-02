package xyz.refinedev.phoenix.listener;

import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import xyz.refinedev.lib.xseries.XMaterial;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.dialog.impl.StaffOnlineDialog;
import xyz.refinedev.phoenix.menu.impl.staff.SilentChestMenu;
import xyz.refinedev.phoenix.menu.impl.staff.StaffOnlineMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.EventHandUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.ModModeItem;
import xyz.refinedev.phoenix.utils.Utilities;
import xyz.refinedev.phoenix.utils.events.misc.ProfileSettingUpdateEvent;
import xyz.refinedev.phoenix.utils.events.player.PlayerVanishUpdateEvent;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProfileSettingUpdateListener implements Listener {

    private final Phoenix plugin;

    @EventHandler
    public void onProfileSettingUpdate(ProfileSettingUpdateEvent profileSettingUpdateEvent) {
        if (profileSettingUpdateEvent.getSetting().equalsIgnoreCase("show-staff")) {
            IProfile profile =
                    this.plugin.getProfileHandler().getProfile(profileSettingUpdateEvent.getUuid());
            Player player = Bukkit.getPlayer(profileSettingUpdateEvent.getUuid());
            if (profile.isSetting("show-staff")) {
                for (Player player2 : Bukkit.getOnlinePlayers()) {
                    if (this.plugin.getVanishHandler().isVanished(player2)) {
                        player.showPlayer(player2.getPlayer());
                    }
                }
            } else {
                for (Player player3 : Bukkit.getOnlinePlayers()) {
                    if (this.plugin.getVanishHandler().isVanished(player3)) {
                        player.hidePlayer(player3.getPlayer());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        if (this.plugin.getVanishHandler().isVanished(player)) {
            if (player.hasPermission("core.command.vanish")) {
                this.plugin.getVanishHandler().vanishPlayer(player);
            } else {
                this.plugin.getVanishHandler().unvanishPlayer(player);
            }
        }
        for (Player player2 : Utilities.getOnlinePlayers()) {
            if (this.plugin.getVanishHandler().isVanished(player2)) {
                this.plugin.getVanishHandler().vanishPlayerFor(player2, player);
            }
        }
        if (SettingsConfig.MODMODE.ON_JOIN && player.hasPermission("core.command.modmode")) {
            this.plugin.getModModeHandler().sendMessage(player);
        }
        if (SettingsConfig.VANISH.ON_JOIN
                && player.hasPermission("core.command.vanish")
                && !this.plugin.getVanishHandler().isVanished(player)) {
            this.plugin.getVanishHandler().vanishPlayer(player);
            player.sendMessage(
                    Color.translate(LocaleUtil.tr(player, MessageConfig.VANISH.AUTOMATIC)));
        }
        if (SettingsConfig.VANISH.FORCE_MODMODE
                && this.plugin.getVanishHandler().isVanished(player)
                && !player.hasMetadata("ModMode")
                && player.hasPermission("core.command.modmode")) {
            this.plugin.getModModeHandler().sendMessage(player);
        }
        this.plugin.getReportHandler().applyPlayer(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        Player player;
        if ((player = playerQuitEvent.getPlayer()).hasMetadata("ModMode")) {
            this.plugin.getModModeHandler().sendMessageByFORCE_MODMODE(player);
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent playerKickEvent) {
        Player player = playerKickEvent.getPlayer();
        if (!SettingsConfig.SERVER.DISABLE_SPAM_KICK
                || !playerKickEvent.getReason().equalsIgnoreCase("disconnect.spam")
                        && !playerKickEvent
                                .getReason()
                                .equalsIgnoreCase("You have been kicked for spamming!")) {
            if (player.hasMetadata("ModMode")) {
                this.plugin.getModModeHandler().sendMessageByFORCE_MODMODE(player);
            }
        } else {
            playerKickEvent.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
        Player player;
        if ((player = playerInteractEvent.getPlayer()).hasMetadata("vanished")) {
            if (playerInteractEvent.getClickedBlock() != null) {
                XMaterial xMaterial =
                        XMaterial.matchXMaterial(playerInteractEvent.getClickedBlock().getType());
                switch (xMaterial) {
                    case ITEM_FRAME:
                    case GLOW_ITEM_FRAME:
                    case LOOM:
                    case CRAFTING_TABLE:
                    case SMOKER:
                    case CARTOGRAPHY_TABLE:
                    case GRINDSTONE:
                    case STONECUTTER:
                    case FURNACE:
                    case ACACIA_FENCE_GATE:
                    case BAMBOO_FENCE_GATE:
                    case CHERRY_FENCE_GATE:
                    case CRIMSON_FENCE_GATE:
                    case BIRCH_FENCE_GATE:
                    case DARK_OAK_FENCE_GATE:
                    case MANGROVE_FENCE_GATE:
                    case SPRUCE_FENCE_GATE:
                    case WARPED_FENCE_GATE:
                    case JUNGLE_FENCE_GATE:
                    case OAK_FENCE_GATE:
                        playerInteractEvent.setCancelled(true);
                        playerInteractEvent.setUseInteractedBlock(Result.DENY);
                        playerInteractEvent.setUseItemInHand(Result.DENY);
                        return;
                    case ENDER_CHEST:
                        player.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(
                                                player, MessageConfig.VANISH.SILENT_ENDER_CHEST)));
                        playerInteractEvent.setCancelled(true);
                        playerInteractEvent.setUseInteractedBlock(Result.DENY);
                        playerInteractEvent.setUseItemInHand(Result.DENY);
                        return;
                    case CHEST:
                    case TRAPPED_CHEST:
                        try {
                            Chest chest = (Chest) playerInteractEvent.getClickedBlock().getState();
                            Phoenix.getInstance()
                                    .getMenuHandler()
                                    .openMenu(new SilentChestMenu(chest), player);
                            player.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(
                                                    player, MessageConfig.VANISH.SILENT_CHEST)));
                        } catch (Exception exception) {
                        }
                        playerInteractEvent.setCancelled(true);
                        playerInteractEvent.setUseInteractedBlock(Result.DENY);
                        playerInteractEvent.setUseItemInHand(Result.DENY);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractByCounts(PlayerInteractEvent playerInteractEvent) {
        Player player = playerInteractEvent.getPlayer();
        ItemStack item = playerInteractEvent.getItem();
        Action action = playerInteractEvent.getAction();
        if (player.hasMetadata("ModMode")) {
            if (playerInteractEvent.getClickedBlock() != null) {
                XMaterial xMaterial =
                        XMaterial.matchXMaterial(playerInteractEvent.getClickedBlock().getType());
                switch (xMaterial) {
                    case ITEM_FRAME:
                    case GLOW_ITEM_FRAME:
                        playerInteractEvent.setCancelled(true);
                        playerInteractEvent.setUseInteractedBlock(Result.DENY);
                        playerInteractEvent.setUseItemInHand(Result.DENY);
                }
            }
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                if (!EventHandUtils.matchesEnabled(playerInteractEvent)) {
                    if (item != null) {
                        ModModeItem modModeItem;
                        if ((modModeItem = this.plugin.getModModeHandler().findModModeItem(item))
                                != null) {
                            if (modModeItem.getCommand().isEnabled()) {
                                player.performCommand(modModeItem.getCommand().getCommand());
                            }
                            if (modModeItem.getAction().isEnabled()) {
                                switch (modModeItem.getAction().getAction()) {
                                    case "compass":
                                        Vector vector =
                                                player.getLocation().getDirection().multiply(2.5);
                                        player.setVelocity(vector);
                                        break;
                                    case "random-teleport":
                                        Stream<Player> stream2 =
                                                Utilities.getOnlinePlayers().stream();
                                        List values;
                                        if (!(values =
                                                        stream2.filter(
                                                                        result ->
                                                                                !this.plugin
                                                                                        .getProfileHandler()
                                                                                        .getProfile(
                                                                                                result
                                                                                                        .getUniqueId())
                                                                                        .isStaff())
                                                                .collect(Collectors.toList()))
                                                .isEmpty()) {
                                            Random random = new Random();
                                            Player player2;
                                            if ((player2 =
                                                            (Player)
                                                                    values.get(
                                                                            random.nextInt(
                                                                                    values.size())))
                                                    == null) {
                                                return;
                                            }
                                            if (!this.plugin
                                                            .getNetworkHandler()
                                                            .getServerName()
                                                            .equalsIgnoreCase("Practice")
                                                    && !this.plugin
                                                            .getNetworkHandler()
                                                            .getServerName()
                                                            .endsWith("-practice")) {
                                                Phoenix.getInstance()
                                                        .getNmsHandler()
                                                        .getTeleportAPI()
                                                        .teleport(player, player2.getLocation());
                                            } else {
                                                player.chat("/spec " + player2.getName());
                                            }
                                        }
                                        break;
                                    case "online-staff":
                                        if (SettingsConfig.MODERN.DIALOGS.ONLINE_STAFF
                                                && this.plugin
                                                        .getDialogHandler()
                                                        .findPhoenixDialog(StaffOnlineDialog.class)
                                                        .getDialogHandler(player)) {
                                            return;
                                        }
                                        Phoenix.getInstance()
                                                .getMenuHandler()
                                                .openMenuAsync(new StaffOnlineMenu(), player);
                                        break;
                                    case "enable-vanish":
                                        this.plugin.getVanishHandler().vanishPlayer(player);
                                        player.sendMessage(
                                                Color.translate(
                                                        LocaleUtil.tr(
                                                                player,
                                                                MessageConfig.VANISH.ENABLED)));
                                        BukkitAPI.sendAdminAlert(
                                                player,
                                                MessageConfig.STAFF.ADMIN_ALERTS.SELF_VANISH_ON);
                                        ModModeItem modModeItem2;
                                        if ((modModeItem2 =
                                                        this.plugin
                                                                .getModModeHandler()
                                                                .getModModeItem())
                                                != null) {
                                            player.setItemInHand(modModeItem2.build());
                                            player.updateInventory();
                                        }
                                        break;
                                    case "disable-vanish":
                                        this.plugin.getVanishHandler().unvanishPlayer(player);
                                        player.sendMessage(
                                                Color.translate(
                                                        LocaleUtil.tr(
                                                                player,
                                                                MessageConfig.VANISH.DISABLED)));
                                        BukkitAPI.sendAdminAlert(
                                                player,
                                                MessageConfig.STAFF.ADMIN_ALERTS.SELF_VANISH_OFF);
                                        ModModeItem modModeItem3;
                                        if ((modModeItem3 =
                                                        this.plugin
                                                                .getModModeHandler()
                                                                .getModModeItemByModModeItems())
                                                != null) {
                                            player.setItemInHand(modModeItem3.build());
                                            player.updateInventory();
                                        }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent playerInteractEntityEvent) {
        Player player;
        if (((player = playerInteractEntityEvent.getPlayer()).hasMetadata("vanished")
                        || player.hasMetadata("ModMode"))
                && !player.hasMetadata("BuildMode")) {
            if (playerInteractEntityEvent.getRightClicked() instanceof ItemFrame) {
                playerInteractEntityEvent.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractEntityByPlugin(
            PlayerInteractEntityEvent playerInteractEntityEvent) {
        Player player;
        ItemStack itemInHand = (player = playerInteractEntityEvent.getPlayer()).getItemInHand();
        if (player.hasMetadata("ModMode")) {
            if (playerInteractEntityEvent.getRightClicked() instanceof Player) {
                if (!EventHandUtils.matchesEnabled(playerInteractEntityEvent)) {
                    ModModeItem modModeItem;
                    if ((modModeItem = this.plugin.getModModeHandler().findModModeItem(itemInHand))
                            != null) {
                        if (modModeItem.getAction().isEnabled()) {
                            Player player2 = (Player) playerInteractEntityEvent.getRightClicked();
                            switch (modModeItem.getAction().getAction()) {
                                case "freeze":
                                    playerInteractEntityEvent.setCancelled(true);
                                    player.performCommand("freeze " + player2.getName());
                                    break;
                                case "view-inventory":
                                    playerInteractEntityEvent.setCancelled(true);
                                    player.performCommand("invsee " + player2.getName());
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerVanishUpdate(PlayerVanishUpdateEvent playerVanishUpdateEvent) {
        Player player;
        if ((player = playerVanishUpdateEvent.getPlayer()).hasMetadata("ModMode")) {
            ModModeItem modModeItemByModModeItems =
                    this.plugin.getModModeHandler().getModModeItemByModModeItems();
            ModModeItem modModeItem = this.plugin.getModModeHandler().getModModeItem();
            boolean matches;
            if ((matches = this.plugin.getVanishHandler().isVanished(player))
                    && modModeItem != null) {
                player.getInventory().setItem(modModeItem.getSlot(), modModeItem.build());
            } else if (!matches && modModeItemByModModeItems != null) {
                player.getInventory()
                        .setItem(
                                modModeItemByModModeItems.getSlot(),
                                modModeItemByModModeItems.build());
            }
            player.updateInventory();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        Player player;
        if ((player = blockBreakEvent.getPlayer()).hasMetadata("ModMode")) {
            if (!player.hasMetadata("BuildMode")) {
                blockBreakEvent.setCancelled(true);
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.MODMODE.CANT_BREAK_BLOCKS)));
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent blockPlaceEvent) {
        Player player;
        if ((player = blockPlaceEvent.getPlayer()).hasMetadata("ModMode")) {
            if (!player.hasMetadata("BuildMode")) {
                blockPlaceEvent.setCancelled(true);
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.MODMODE.CANT_PLACE_BLOCKS)));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractByVoid(PlayerInteractEvent playerInteractEvent) {
        Player player;
        if (!(player = playerInteractEvent.getPlayer()).hasMetadata("npc")) {
            if (player.hasMetadata("ModMode")) {
                if (!player.hasMetadata("BuildMode")) {
                    if (playerInteractEvent.getAction() == Action.PHYSICAL
                            && playerInteractEvent.getClickedBlock().getType()
                                    == XMaterial.FARMLAND.parseMaterial()) {
                        playerInteractEvent.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent playerPickupItemEvent) {
        Player player;
        if ((player = playerPickupItemEvent.getPlayer()).hasMetadata("ModMode")) {
            if (!player.hasMetadata("BuildMode")) {
                playerPickupItemEvent.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if (entityDamageByEntityEvent.getDamager() instanceof Player) {
            Player player;
            if ((player = (Player) entityDamageByEntityEvent.getDamager()).hasMetadata("ModMode")
                    || this.plugin.getVanishHandler().isVanished(player)) {
                entityDamageByEntityEvent.setCancelled(true);
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.MODMODE.CANT_HIT_ENTITIES)));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent playerDropItemEvent) {
        Player player;
        if (!(player = playerDropItemEvent.getPlayer()).hasMetadata("BuildMode")
                && player.hasMetadata("ModMode")) {
            playerDropItemEvent.setCancelled(true);
        }
        if (this.plugin.getVanishHandler().isVanished(player)) {
            playerDropItemEvent.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent entityDamageEvent) {
        if (entityDamageEvent.getEntity() instanceof Player) {
            Player player;
            if ((player = (Player) entityDamageEvent.getEntity()).hasMetadata("ModMode")
                    || this.plugin.getVanishHandler().isVanished(player)) {
                entityDamageEvent.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleEnter(VehicleEnterEvent vehicleEnterEvent) {
        if (vehicleEnterEvent.getEntered() instanceof Player) {
            Player player;
            if ((player = (Player) vehicleEnterEvent.getEntered()).hasMetadata("ModMode")) {
                vehicleEnterEvent.setCancelled(true);
            }
            if (this.plugin.getFreezeHandler().isFrozen(player.getUniqueId())) {
                vehicleEnterEvent.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        Player player;
        if ((player = (Player) inventoryClickEvent.getWhoClicked()).hasMetadata("ModMode")) {
            ItemStack itemStack;
            if ((itemStack = inventoryClickEvent.getCurrentItem()) != null) {
                if (inventoryClickEvent.getClick() == ClickType.MIDDLE
                        || this.plugin.getModModeHandler().findModModeItem(itemStack) != null) {
                    inventoryClickEvent.setCancelled(!player.hasMetadata("BuildMode"));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent inventoryDragEvent) {
        Player player;
        if ((player = (Player) inventoryDragEvent.getWhoClicked()).hasMetadata("ModMode")) {
            if (!player.hasMetadata("BuildMode")) {
                inventoryDragEvent.setCancelled(true);
                player.updateInventory();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryCreative(InventoryCreativeEvent inventoryCreativeEvent) {
        Player player;
        if ((player = (Player) inventoryCreativeEvent.getWhoClicked()).hasMetadata("ModMode")) {
            if (!player.hasMetadata("BuildMode")) {
                inventoryCreativeEvent.setCancelled(true);
                player.updateInventory();
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocess(
            PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
        Player player;
        if ((player = playerCommandPreprocessEvent.getPlayer()).hasMetadata("ModMode")) {
            if (!player.hasMetadata("BuildMode")) {
                if (SettingsConfig.VANISH.COMMANDS.ENABLED) {
                    playerCommandPreprocessEvent.setCancelled(
                            !this.matchesEnabled(playerCommandPreprocessEvent.getMessage()));
                }
            }
        }
    }

    private boolean matchesEnabled(String text) {
        for (String text2 : SettingsConfig.VANISH.COMMANDS.ALLOWED_COMMANDS) {
            if (text.toLowerCase().startsWith(text2.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public ProfileSettingUpdateListener(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
