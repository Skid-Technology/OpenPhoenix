package xyz.refinedev.phoenix.menu.api.pagination.filter;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class FilterablePaginatedMenu<T> extends PaginatedMenu {

    private List<PageFilter<T>> filters;

    private int scrollIndex = 0;

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries;
        (entries = new HashMap())
                .put(MenuConfig.GENERAL.FILTERS_SLOT.SLOT, new PageFilterButton<>(this));
        return entries;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        return this.getFilteredButtons(player);
    }

    public boolean passesFilter(Player player, T t) {
        Iterator<xyz.refinedev.phoenix.menu.api.pagination.filter.PageFilter<T>> iterator2 =
                this.getFilters(player).iterator();
        while (iterator2.hasNext()) {
            if (!((PageFilter) iterator2.next()).test(t)) {
                return false;
            }
        }
        return true;
    }

    public List<PageFilter<T>> getFilters(Player player) {
        if (this.filters == null) {
            this.filters = this.generateFilters(player);
        }
        return this.filters;
    }

    public abstract Map<Integer, Button> getFilteredButtons(Player player);

    public abstract List<PageFilter<T>> generateFilters(Player player);

    public int getScrollIndex() {
        return this.scrollIndex;
    }

    public void setScrollIndex(int scrollIndex) {
        this.scrollIndex = scrollIndex;
    }
}
