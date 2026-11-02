package xyz.refinedev.phoenix.menu.impl.staff;

import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;

import java.util.HashMap;
import java.util.Map;

public class SilentChestMenu extends Menu {

    private final Chest chest;

    private static final String title = "Silent Chest";

    @Override
    public String getTitle(Player player) {
        return title;
    }

    @Override
    public int getSize() {
        return this.chest.getInventory() instanceof DoubleChestInventory ? 54 : 27;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        ItemStack[] itemStacks;
        int value = (itemStacks = this.chest.getInventory().getContents()).length;
        for (int index = 0; index < value; index++) {
            final ItemStack itemStack = itemStacks[index];
            entries.put(
                    entries.size(),
                    new Button() {

                        @Override
                        public ItemStack getItem(Player player) {
                            return itemStack;
                        }
                    });
        }
        return entries;
    }

    public SilentChestMenu(Chest chest2) {
        this.setAutoUpdate(true);
        this.chest = chest2;
    }
}
