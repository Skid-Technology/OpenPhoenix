package xyz.refinedev.phoenix.menu.api.pagination;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class PaginatedMenu extends Menu {

    private int page = 1;

    private int allButtonsSize = 0;

    public PaginatedMenu() {
        this.setUpdateAfterClick(false);
        this.setBordered(true);
    }

    public final void modPage(Player player, int page) {
        this.page += page;
        this.getButtons().clear();
        if (this.isFullyAsync()) {
            super.plugin.getMenuHandler().openMenuAsync(this, player);
        } else {
            super.plugin.getMenuHandler().openMenu(this, player);
        }
    }

    public final int getPages(Player player) {
        return this.allButtonsSize == 0
                ? 1
                : (int) Math.ceil((double) this.allButtonsSize / this.getMaxItemsPerPage(player));
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        int value = (int) ((double) (this.page - 1) * this.getMaxItemsPerPage(player));
        int value2 = (int) ((double) this.page * this.getMaxItemsPerPage(player));
        HashMap<Integer, Button> entries = new HashMap<>();
        Map<Integer, Button> entries2;
        if ((entries2 = this.getGlobalButtons(player)) != null) {
            entries.putAll(entries2);
        }
        int value3 = 1;
        byte value4 = 0;
        int value5 = 0;
        Map<Integer, Button> entries3;
        for (Entry<Integer, Button> entry :
                (entries3 = this.getAllPagesButtons(player)).entrySet()) {
            int value6 = value3++;
            switch (value5) {
                case 16:
                case 25:
                case 34:
                    value3 += 2;
                    value4 += 2;
                    value6 += 2;
            }
            if (value6 >= value && value6 <= value2 + value4) {
                int value7;
                value5 =
                        value7 =
                                value6
                                        - ((int)
                                                        ((double) this.getMaxItemsPerPage(player)
                                                                * (this.page - 1))
                                                - 9);
                entries.put(value7, (Button) entry.getValue());
            }
        }
        this.allButtonsSize = entries3.size();
        if (this.page > 1) {
            entries.put(0, new PreviousPageSlot(this));
        }
        if (this.page < this.getPages(player)) {
            entries.put(8, new NextPageSlot(this));
        }
        return entries;
    }

    public int getMaxItemsPerPage(Player player) {
        return 21;
    }

    public Map<Integer, Button> getGlobalButtons(Player player) {
        return null;
    }

    public abstract Map<Integer, Button> getAllPagesButtons(Player player);

    public int getPage() {
        return this.page;
    }

    public int getAllButtonsSize() {
        return this.allButtonsSize;
    }
}
