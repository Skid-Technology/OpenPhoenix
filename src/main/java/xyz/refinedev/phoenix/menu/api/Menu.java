package xyz.refinedev.phoenix.menu.api;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.menu.api.button.BackButton;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class Menu {

    public final Phoenix plugin = Phoenix.getInstance();

    public final CommonPlatform api = Phoenix.getInstance();

    private Map<Integer, Button> buttons = new HashMap<>();

    private boolean autoUpdate = false;

    private boolean updateAfterClick = true;

    private boolean closedByMenu = false;

    private boolean openedByMenu = false;

    private boolean bordered = false;

    private boolean inventoryChanged = false;

    private boolean fullyAsync = false;

    private Menu previousMenu = null;

    private boolean isFirst = false;

    public int size(Map<Integer, Button> entries) {
        int value = 0;
        Iterator<Integer> iterator2 = entries.keySet().iterator();
        while (iterator2.hasNext()) {
            int value2;
            if ((value2 = (Integer) iterator2.next()) > value) {
                value = value2;
            }
        }
        return (int) (Math.ceil((value + 1) / 9.0) * 9.0);
    }

    public int getSize() {
        return -1;
    }

    public int getSlot(int value, int value2) {
        return 9 * value2 + value;
    }

    public abstract String getTitle(Player player);

    public abstract Map<Integer, Button> getButtons(Player player);

    public void onOpen(Player player) {}

    public void onRefresh(Player player) {}

    public void onClose(Player player) {}

    public void back(Player player) {
        Menu menu;
        if ((menu = this.getPreviousMenu()) != null && !this.isFirst()) {
            Button.playNeutral(player);
            if (menu.isFullyAsync()) {
                this.plugin.getMenuHandler().openMenuAsync(menu, player);
            } else {
                this.plugin.getMenuHandler().openMenu(menu, player);
            }
        }
    }

    public void backButton(Map<Integer, Button> entries, int value) {
        Menu menu;
        if ((menu = this.getPreviousMenu()) != null && !this.isFirst()) {
            entries.put(value, new BackButton(menu));
        }
    }

    public boolean willHaveBackButton() {
        return this.getPreviousMenu() != null && !this.isFirst();
    }

    public Phoenix getPlugin() {
        return this.plugin;
    }

    public CommonPlatform getApi() {
        return this.api;
    }

    public Map<Integer, Button> getButtons() {
        return this.buttons;
    }

    public boolean isAutoUpdate() {
        return this.autoUpdate;
    }

    public boolean isUpdateAfterClick() {
        return this.updateAfterClick;
    }

    public boolean isClosedByMenu() {
        return this.closedByMenu;
    }

    public boolean isOpenedByMenu() {
        return this.openedByMenu;
    }

    public boolean isBordered() {
        return this.bordered;
    }

    public boolean isInventoryChanged() {
        return this.inventoryChanged;
    }

    public boolean isFullyAsync() {
        return this.fullyAsync;
    }

    public Menu getPreviousMenu() {
        return this.previousMenu;
    }

    public boolean isFirst() {
        return this.isFirst;
    }

    public void setButtons(Map<Integer, Button> entries) {
        this.buttons = entries;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public void setUpdateAfterClick(boolean updateAfterClick) {
        this.updateAfterClick = updateAfterClick;
    }

    public void setClosedByMenu(boolean closedByMenu) {
        this.closedByMenu = closedByMenu;
    }

    public void setOpenedByMenu(boolean openedByMenu) {
        this.openedByMenu = openedByMenu;
    }

    public void setBordered(boolean bordered) {
        this.bordered = bordered;
    }

    public void setInventoryChanged(boolean inventoryChanged) {
        this.inventoryChanged = inventoryChanged;
    }

    public void setFullyAsync(boolean fullyAsync) {
        this.fullyAsync = fullyAsync;
    }

    public void setPreviousMenu(Menu menu) {
        this.previousMenu = menu;
    }

    public void setFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }
}
