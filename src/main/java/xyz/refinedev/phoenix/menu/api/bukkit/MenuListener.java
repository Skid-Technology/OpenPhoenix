package xyz.refinedev.phoenix.menu.api.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;
import xyz.refinedev.phoenix.menu.api.MenuHandler;
import xyz.refinedev.phoenix.utils.general.Tasks;

public class MenuListener implements Listener {

    private final MenuHandler instance;

    public MenuListener(MenuHandler menuHandler) {
        this.instance = menuHandler;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onButtonPress(InventoryClickEvent inventoryClickEvent) {
        Player player = (Player) inventoryClickEvent.getWhoClicked();
        Menu menu;
        if ((menu = this.instance.getOpenedMenus().get(player.getUniqueId())) != null) {
            if (inventoryClickEvent.getSlot() != inventoryClickEvent.getRawSlot()) {
                if (inventoryClickEvent.getClick() == ClickType.SHIFT_LEFT
                        || inventoryClickEvent.getClick() == ClickType.SHIFT_RIGHT) {
                    inventoryClickEvent.setCancelled(true);
                }
            } else {
                if (menu.getButtons().containsKey(inventoryClickEvent.getSlot())) {
                    Button button;
                    boolean matches;
                    if (!(matches =
                                    (button = menu.getButtons().get(inventoryClickEvent.getSlot()))
                                            .shouldCancel(player, inventoryClickEvent.getClick()))
                            && (inventoryClickEvent.getClick() == ClickType.SHIFT_LEFT
                                    || inventoryClickEvent.getClick() == ClickType.SHIFT_RIGHT)) {
                        inventoryClickEvent.setCancelled(true);
                        if (inventoryClickEvent.getCurrentItem() != null) {
                            player.getInventory()
                                    .addItem(
                                            new ItemStack[] {inventoryClickEvent.getCurrentItem()});
                        }
                    } else {
                        inventoryClickEvent.setCancelled(matches);
                    }
                    menu.setInventoryChanged(true);
                    button.clicked(player, inventoryClickEvent.getClick());
                    button.clicked(
                            player,
                            inventoryClickEvent.getSlot(),
                            inventoryClickEvent.getClick(),
                            inventoryClickEvent.getHotbarButton());
                    if (this.instance.getOpenedMenus().containsKey(player.getUniqueId())) {
                        Menu menu2;
                        if ((menu2 = this.instance.getOpenedMenus().get(player.getUniqueId()))
                                        == menu
                                && (button.shouldUpdate(player, inventoryClickEvent.getClick())
                                        || menu2.isUpdateAfterClick())) {
                            menu.setClosedByMenu(true);
                            menu.setOpenedByMenu(true);
                            this.instance.openMenu(menu, player);
                        }
                    } else if (button.shouldUpdate(player, inventoryClickEvent.getClick())) {
                        menu.setClosedByMenu(true);
                        menu.setOpenedByMenu(true);
                        this.instance.openMenu(menu, player);
                    }
                    if (inventoryClickEvent.isCancelled()) {
                        Phoenix plugin = this.instance.getPlugin();
                        Tasks.runLater(plugin, player::updateInventory, 1L);
                    }
                } else {
                    if (inventoryClickEvent.getCurrentItem() != null) {
                        inventoryClickEvent.setCancelled(true);
                    }
                    if (inventoryClickEvent.getClick() == ClickType.SHIFT_LEFT
                            || inventoryClickEvent.getClick() == ClickType.SHIFT_RIGHT) {
                        inventoryClickEvent.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClose(InventoryCloseEvent inventoryCloseEvent) {
        Player player = (Player) inventoryCloseEvent.getPlayer();
        Menu menu;
        if ((menu = this.instance.getOpenedMenus().get(player.getUniqueId())) != null) {
            menu.setClosedByMenu(false);
            menu.onClose(player);
            menu.setPreviousMenu(null);
            this.instance.getOpenedMenus().remove(player.getUniqueId());
        }
    }
}
