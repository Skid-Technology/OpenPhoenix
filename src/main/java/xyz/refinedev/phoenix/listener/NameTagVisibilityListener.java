package xyz.refinedev.phoenix.listener;

import org.bukkit.block.Barrel;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import xyz.refinedev.lib.xseries.XMaterial;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.impl.staff.SilentContainerMenu;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

public class NameTagVisibilityListener implements Listener {

    private final Phoenix plugin;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
        Player player;
        if ((player = playerInteractEvent.getPlayer()).hasMetadata("vanished")) {
            if (playerInteractEvent.getClickedBlock() != null) {
                XMaterial xMaterial =
                        XMaterial.matchXMaterial(playerInteractEvent.getClickedBlock().getType());
                switch (xMaterial) {
                    case BARREL:
                    case BLUE_SHULKER_BOX:
                    case BLACK_SHULKER_BOX:
                    case RED_SHULKER_BOX:
                    case GREEN_SHULKER_BOX:
                    case YELLOW_SHULKER_BOX:
                    case BROWN_SHULKER_BOX:
                    case CYAN_SHULKER_BOX:
                    case GRAY_SHULKER_BOX:
                    case LIGHT_GRAY_SHULKER_BOX:
                    case LIGHT_BLUE_SHULKER_BOX:
                    case LIME_SHULKER_BOX:
                    case MAGENTA_SHULKER_BOX:
                    case PINK_SHULKER_BOX:
                    case ORANGE_SHULKER_BOX:
                    case PURPLE_SHULKER_BOX:
                    case WHITE_SHULKER_BOX:
                    case SHULKER_BOX:
                        Object result = null;
                        try {
                            result = (Barrel) playerInteractEvent.getClickedBlock().getState();
                        } catch (Exception exception) {
                        }
                        try {
                            result = (ShulkerBox) playerInteractEvent.getClickedBlock().getState();
                        } catch (Exception exception2) {
                        }
                        if (result != null) {
                            Phoenix.getInstance()
                                    .getMenuHandler()
                                    .openMenu(new SilentContainerMenu((Container) result), player);
                            player.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(
                                                    player,
                                                    MessageConfig.VANISH.SILENT_CONTAINER)));
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
        if (playerInteractEvent.getPlayer().hasMetadata("ModMode")) {
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
        }
    }

    public NameTagVisibilityListener(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
