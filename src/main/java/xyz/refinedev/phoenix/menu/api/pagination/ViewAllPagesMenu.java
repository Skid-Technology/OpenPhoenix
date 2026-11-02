package xyz.refinedev.phoenix.menu.api.pagination;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.button.BackButton;

import java.util.HashMap;
import java.util.Map;

public class ViewAllPagesMenu extends PaginatedMenu {

    public PaginatedMenu menu;

    private static final String title = "Open a page...";

    public ViewAllPagesMenu(PaginatedMenu paginatedMenu) {
        this.menu = paginatedMenu;
    }

    @Override
    public String getTitle(Player player) {
        return title;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (int index = 1; index <= this.menu.getPages(player); index++) {
            entries.put(
                    entries.size(),
                    new JumpToPageButton(index, this.menu, this.menu.getPage() == index));
        }
        return entries;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries;
        (entries = new HashMap()).put(4, new BackButton(this.menu));
        return entries;
    }

    @Override
    public boolean isAutoUpdate() {
        return false;
    }

    public PaginatedMenu getMenu() {
        return this.menu;
    }
}
