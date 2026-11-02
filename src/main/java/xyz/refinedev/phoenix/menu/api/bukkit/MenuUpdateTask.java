package xyz.refinedev.phoenix.menu.api.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.menu.api.Menu;
import xyz.refinedev.phoenix.menu.api.MenuHandler;

public class MenuUpdateTask implements Runnable {

    private final MenuHandler handler;

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Menu menu;
            if ((menu = this.handler.getByPlayer(player)) != null && menu.isAutoUpdate()) {
                menu.setOpenedByMenu(true);
                this.handler.updateMenuPartiallySync(menu, player);
            }
        }
    }

    public MenuUpdateTask(MenuHandler menuHandler) {
        this.handler = menuHandler;
    }
}
