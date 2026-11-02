package xyz.refinedev.phoenix.menu.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class MenuHandler {

    private final Map<UUID, Menu> openedMenus = new ConcurrentHashMap<>();

    private final Phoenix plugin;

    public MenuHandler(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    public Menu getByPlayer(Player player) {
        return this.openedMenus.get(player.getUniqueId());
    }

    public Menu getByPlayer(UUID uUID) {
        return this.openedMenus.get(uUID);
    }

    public void setPreviousMenu(Menu menu, Menu menu2) {
        if (menu2 != null && menu2.getPreviousMenu() == null) {
            menu2.setFirst(true);
        }
        if (menu2 != null
                && menu2.getClass() != menu.getClass()
                && menu.getPreviousMenu() == null) {
            menu.setPreviousMenu(menu2);
        }
    }

    public void openMenuAsync(Menu menu, Player player) {
        long now = System.currentTimeMillis();
        CompletableFuture<HashMap> completableFuture =
                CompletableFuture.supplyAsync(
                        () -> {
                            Menu menu2 = this.openedMenus.get(player.getUniqueId());
                            this.setPreviousMenu(menu, menu2);
                            menu.setButtons(menu.getButtons(player));
                            int result =
                                    menu.getSize() == -1
                                            ? menu.size(menu.getButtons())
                                            : menu.getSize();
                            if (menu.isBordered()) {
                                this.addBorderButtons(menu, result);
                            }
                            HashMap result2 = new HashMap();
                            for (Entry<Integer, xyz.refinedev.phoenix.menu.api.Button> result3 :
                                    (Iterable<
                                                    Entry<
                                                            Integer,
                                                            xyz.refinedev.phoenix.menu.api.Button>>)
                                            ((Iterable) menu.getButtons().entrySet())) {
                                result2.put(
                                        (Integer) result3.getKey(),
                                        ((Button) result3.getValue()).getItem(player));
                            }
                            return result2;
                        });
        completableFuture.whenCompleteAsync(
                (result, result2) -> {
                    if (result2 != null) {
                        if (!(result2 instanceof ArrayIndexOutOfBoundsException)
                                && !(result2 instanceof ConcurrentModificationException)
                                && !(result2.getCause()
                                        instanceof ConcurrentModificationException)) {
                            Bukkit.getLogger()
                                    .log(
                                            Level.SEVERE,
                                            "There was an error while opening "
                                                    + menu.getClass().getName()
                                                    + " menu: ",
                                            result2);
                        }
                    } else {
                        Tasks.runAtEntity(
                                player,
                                () -> {
                                    menu.setFullyAsync(true);
                                    String text = Color.translate(menu.getTitle(player));
                                    Inventory result3 = null;
                                    int value =
                                            menu.getSize() == -1
                                                    ? menu.size(menu.getButtons())
                                                    : menu.getSize();
                                    boolean matches = false;
                                    if (text.length() > 32) {
                                        text = text.substring(0, 32);
                                    }
                                    Menu menu3;
                                    if ((menu3 = this.openedMenus.get(player.getUniqueId())) != null
                                            && player.getOpenInventory() != null) {
                                        if (player.getOpenInventory().getTopInventory().getSize()
                                                        == value
                                                && text.equals(
                                                        Color.translate(
                                                                player.getOpenInventory()
                                                                        .getTitle()))) {
                                            result3 = player.getOpenInventory().getTopInventory();
                                            matches = true;
                                        } else {
                                            menu3.setClosedByMenu(true);
                                            menu3.onClose(player);
                                            this.openedMenus.remove(player.getUniqueId());
                                        }
                                    }
                                    if (result3 == null) {
                                        result3 = Bukkit.createInventory(player, value, text);
                                    }
                                    if (matches) {
                                        try {
                                            Inventory inventory =
                                                    Bukkit.createInventory(
                                                            player, result3.getSize(), text);
                                            for (Entry entry :
                                                    (Iterable<Entry>)
                                                            ((Iterable) result.entrySet())) {
                                                inventory.setItem(
                                                        (Integer) entry.getKey(),
                                                        (ItemStack) entry.getValue());
                                            }
                                            result3.setContents(inventory.getContents());
                                            player.updateInventory();
                                            this.openedMenus.put(player.getUniqueId(), menu);
                                            menu.onOpen(player);
                                        } catch (Exception exception) {
                                            if (exception
                                                    instanceof ArrayIndexOutOfBoundsException) {
                                                return;
                                            }
                                            Bukkit.getLogger()
                                                    .log(
                                                            Level.SEVERE,
                                                            "There was an error while opening this menu: ",
                                                            exception);
                                        }
                                    } else {
                                        try {
                                            for (Entry entry2 :
                                                    (Iterable<Entry>)
                                                            ((Iterable) result.entrySet())) {
                                                result3.setItem(
                                                        (Integer) entry2.getKey(),
                                                        (ItemStack) entry2.getValue());
                                            }
                                            this.plugin
                                                    .getMenuTitleHandler()
                                                    .expect(player, menu.getTitle(player));
                                            player.openInventory(result3);
                                            this.openedMenus.put(player.getUniqueId(), menu);
                                            menu.onOpen(player);
                                        } catch (Exception exception2) {
                                            if (exception2
                                                    instanceof ArrayIndexOutOfBoundsException) {
                                                return;
                                            }
                                            Bukkit.getLogger()
                                                    .log(
                                                            Level.SEVERE,
                                                            "There was an error while opening "
                                                                    + menu.getClass().getName()
                                                                    + " menu: ",
                                                            exception2);
                                        }
                                    }
                                    if (this.plugin.isDebugMode()) {
                                        this.plugin
                                                .getLogger()
                                                .info(
                                                        (matches ? "Updated " : "Opened ")
                                                                + menu.getClass().getSimpleName()
                                                                + " in "
                                                                + (System.currentTimeMillis() - now)
                                                                + "ms.");
                                    }
                                });
                    }
                });
    }

    public void openMenuSync(Menu menu, Player player) {
        Tasks.runAtEntity(
                player,
                () -> {
                    long now = System.currentTimeMillis();
                    Menu menu2 = this.openedMenus.get(player.getUniqueId());
                    this.setPreviousMenu(menu, menu2);
                    menu.setButtons(menu.getButtons(player));
                    String text = Color.translate(menu.getTitle(player));
                    int value =
                            menu.getSize() == -1 ? menu.size(menu.getButtons()) : menu.getSize();
                    if (menu.isBordered()) {
                        this.addBorderButtons(menu, value);
                    }
                    Inventory inventory = null;
                    int value2 =
                            menu.getSize() == -1 ? menu.size(menu.getButtons()) : menu.getSize();
                    boolean matches = false;
                    if (text.length() > 32) {
                        text = text.substring(0, 32);
                    }
                    if (menu2 != null && player.getOpenInventory() != null) {
                        if (player.getOpenInventory().getTopInventory().getSize() == value2
                                && text.equals(
                                        Color.translate(player.getOpenInventory().getTitle()))) {
                            inventory = player.getOpenInventory().getTopInventory();
                            matches = true;
                        } else {
                            menu2.setClosedByMenu(true);
                            menu2.onClose(player);
                            this.openedMenus.remove(player.getUniqueId());
                        }
                    }
                    if (inventory == null) {
                        inventory = Bukkit.createInventory(player, value2, text);
                    }
                    Inventory inventory2 = inventory;
                    if (matches) {
                        Inventory inventory3 =
                                Bukkit.createInventory(player, inventory.getSize(), text);
                        for (Entry<Integer, xyz.refinedev.phoenix.menu.api.Button> entry :
                                (Iterable<Entry<Integer, xyz.refinedev.phoenix.menu.api.Button>>)
                                        ((Iterable) menu.getButtons().entrySet())) {
                            inventory3.setItem(
                                    (Integer) entry.getKey(),
                                    ((Button) entry.getValue()).getItem(player));
                        }
                        try {
                            inventory2.setContents(inventory3.getContents());
                            player.updateInventory();
                            this.openedMenus.put(player.getUniqueId(), menu);
                            menu.onOpen(player);
                        } catch (Exception exception) {
                            Bukkit.getLogger()
                                    .log(
                                            Level.SEVERE,
                                            "There was an error while opening "
                                                    + menu.getClass().getName()
                                                    + " menu: ",
                                            exception);
                        }
                    } else {
                        for (Entry<Integer, xyz.refinedev.phoenix.menu.api.Button> entry2 :
                                (Iterable<Entry<Integer, xyz.refinedev.phoenix.menu.api.Button>>)
                                        ((Iterable) menu.getButtons().entrySet())) {
                            inventory.setItem(
                                    (Integer) entry2.getKey(),
                                    ((Button) entry2.getValue()).getItem(player));
                        }
                        try {
                            this.plugin.getMenuTitleHandler().expect(player, menu.getTitle(player));
                            player.openInventory(inventory2);
                            this.openedMenus.put(player.getUniqueId(), menu);
                            menu.onOpen(player);
                        } catch (Exception exception2) {
                            Bukkit.getLogger()
                                    .log(
                                            Level.SEVERE,
                                            "There was an error while opening "
                                                    + menu.getClass().getName()
                                                    + " menu: ",
                                            exception2);
                        }
                    }
                    if (this.plugin.isDebugMode()) {
                        this.plugin
                                .getLogger()
                                .info(
                                        (matches ? "Updated " : "Opened ")
                                                + menu.getClass().getSimpleName()
                                                + " in "
                                                + (System.currentTimeMillis() - now)
                                                + "ms.");
                    }
                });
    }

    public void openMenu(Menu menu, Player player) {
        long now = System.currentTimeMillis();
        Menu menu2 = this.openedMenus.get(player.getUniqueId());
        this.setPreviousMenu(menu, menu2);
        menu.setButtons(menu.getButtons(player));
        String text = Color.translate(menu.getTitle(player));
        int value = menu.getSize() == -1 ? menu.size(menu.getButtons()) : menu.getSize();
        if (menu.isBordered()) {
            this.addBorderButtons(menu, value);
        }
        Inventory inventory = null;
        int value2 = menu.getSize() == -1 ? menu.size(menu.getButtons()) : menu.getSize();
        boolean matches = false;
        if (text.length() > 32) {
            text = text.substring(0, 32);
        }
        if (menu2 != null && player.getOpenInventory() != null) {
            if (player.getOpenInventory().getTopInventory().getSize() == value2
                    && text.equals(Color.translate(player.getOpenInventory().getTitle()))) {
                inventory = player.getOpenInventory().getTopInventory();
                matches = true;
            } else {
                menu2.setClosedByMenu(true);
                menu2.onClose(player);
                this.openedMenus.remove(player.getUniqueId());
            }
        }
        if (inventory == null) {
            inventory = Bukkit.createInventory(player, value2, text);
        }
        if (matches) {
            Inventory inventory2 = Bukkit.createInventory(player, inventory.getSize(), text);
            for (Entry<Integer, xyz.refinedev.phoenix.menu.api.Button> entry :
                    (Iterable<Entry<Integer, xyz.refinedev.phoenix.menu.api.Button>>)
                            ((Iterable) menu.getButtons().entrySet())) {
                inventory2.setItem(
                        (Integer) entry.getKey(), ((Button) entry.getValue()).getItem(player));
            }
            try {
                inventory.setContents(inventory2.getContents());
                player.updateInventory();
            } catch (Exception exception) {
                Bukkit.getLogger()
                        .log(
                                Level.SEVERE,
                                "There was an error while opening "
                                        + menu.getClass().getName()
                                        + " menu: ",
                                exception);
            }
        } else {
            for (Entry<Integer, xyz.refinedev.phoenix.menu.api.Button> entry2 :
                    (Iterable<Entry<Integer, xyz.refinedev.phoenix.menu.api.Button>>)
                            ((Iterable) menu.getButtons().entrySet())) {
                inventory.setItem(
                        (Integer) entry2.getKey(), ((Button) entry2.getValue()).getItem(player));
            }
            try {
                this.plugin.getMenuTitleHandler().expect(player, menu.getTitle(player));
                player.openInventory(inventory);
            } catch (Exception exception2) {
                Bukkit.getLogger()
                        .log(
                                Level.SEVERE,
                                "There was an error while opening "
                                        + menu.getClass().getName()
                                        + " menu: ",
                                exception2);
            }
        }
        this.openedMenus.put(player.getUniqueId(), menu);
        menu.onOpen(player);
        if (this.plugin.isDebugMode()) {
            this.plugin
                    .getLogger()
                    .info(
                            (matches ? "Updated " : "Opened ")
                                    + menu.getClass().getSimpleName()
                                    + " in "
                                    + (System.currentTimeMillis() - now)
                                    + "ms.");
        }
    }

    public void updateMenuPartiallySync(Menu menu, Player player) {
        long now = System.currentTimeMillis();
        Menu menu2;
        if ((menu2 = this.openedMenus.get(player.getUniqueId())) != null) {
            this.setPreviousMenu(menu, menu2);
            menu.setButtons(menu.getButtons(player));
            int value = menu.getSize() == -1 ? menu.size(menu.getButtons()) : menu.getSize();
            if (menu.isBordered()) {
                this.addBorderButtons(menu, value);
            }
            HashMap entries = new HashMap();
            for (Entry<Integer, xyz.refinedev.phoenix.menu.api.Button> entry :
                    (Iterable<Entry<Integer, xyz.refinedev.phoenix.menu.api.Button>>)
                            ((Iterable) menu.getButtons().entrySet())) {
                entries.put((Integer) entry.getKey(), ((Button) entry.getValue()).getItem(player));
            }
            if (this.plugin.isDebugMode()) {
                this.plugin
                        .getLogger()
                        .info(
                                "Updated (Buttons) "
                                        + menu.getClass().getSimpleName()
                                        + " in "
                                        + (System.currentTimeMillis() - now)
                                        + "ms on thread "
                                        + Thread.currentThread().getName()
                                        + ".");
            }
            Tasks.runAtEntity(
                    player,
                    () -> {
                        if (player.getOpenInventory() != null) {
                            menu.setFullyAsync(true);
                            String text = Color.translate(menu.getTitle(player));
                            int value2 =
                                    menu.getSize() == -1
                                            ? menu.size(menu.getButtons())
                                            : menu.getSize();
                            if (text.length() > 32) {
                                text = text.substring(0, 32);
                            }
                            if (player.getOpenInventory().getTopInventory().getSize() == value2
                                    && text.equals(
                                            Color.translate(
                                                    player.getOpenInventory().getTitle()))) {
                                Inventory inventory;
                                if ((inventory = player.getOpenInventory().getTopInventory())
                                        == null) {
                                    inventory = Bukkit.createInventory(player, value2, text);
                                }
                                try {
                                    Inventory inventory2 =
                                            Bukkit.createInventory(
                                                    player, inventory.getSize(), text);
                                    for (Entry entry2 :
                                            (Iterable<Entry>) ((Iterable) entries.entrySet())) {
                                        inventory2.setItem(
                                                (Integer) entry2.getKey(),
                                                (ItemStack) entry2.getValue());
                                    }
                                    inventory.setContents(inventory2.getContents());
                                    player.updateInventory();
                                    this.openedMenus.put(player.getUniqueId(), menu);
                                    menu.onRefresh(player);
                                } catch (Exception exception) {
                                    if (exception instanceof ArrayIndexOutOfBoundsException) {
                                        return;
                                    }
                                    Bukkit.getLogger()
                                            .log(
                                                    Level.SEVERE,
                                                    "There was an error while opening this menu: ",
                                                    exception);
                                }
                                if (this.plugin.isDebugMode()) {
                                    this.plugin
                                            .getLogger()
                                            .info(
                                                    "Updated "
                                                            + menu.getClass().getSimpleName()
                                                            + " in "
                                                            + (System.currentTimeMillis() - now)
                                                            + "ms on thread "
                                                            + Thread.currentThread().getName()
                                                            + ".");
                                }
                            }
                        }
                    });
        }
    }

    public void addBorderButtons(Menu menu, int value) {
        int value2 = value >= 36 ? 36 : (value >= 27 ? 27 : 18);
        for (int index = 0; index < 9; index++) {
            menu.getButtons().computeIfAbsent(index, result -> Button.placeholder());
        }
        menu.getButtons().put(9, Button.placeholder());
        menu.getButtons().put(17, Button.placeholder());
        if (value2 >= 27) {
            menu.getButtons().put(18, Button.placeholder());
            menu.getButtons().put(26, Button.placeholder());
        }
        if (value2 == 36) {
            menu.getButtons().put(27, Button.placeholder());
            menu.getButtons().put(35, Button.placeholder());
        }
        for (int value3 = value2; value3 <= value2 + 8; value3++) {
            menu.getButtons().computeIfAbsent(value3, result -> Button.placeholder());
        }
    }

    public Map<UUID, Menu> getOpenedMenus() {
        return this.openedMenus;
    }

    public Phoenix getPlugin() {
        return this.plugin;
    }
}
