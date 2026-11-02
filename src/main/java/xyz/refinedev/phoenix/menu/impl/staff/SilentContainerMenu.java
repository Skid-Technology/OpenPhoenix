package xyz.refinedev.phoenix.menu.impl.staff;

import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;

import java.util.HashMap;
import java.util.Map;

public class SilentContainerMenu extends Menu {

    private final Container container;

    @Override
    public String getTitle(Player player) {
        return "Silent " + (this.container instanceof ShulkerBox ? "Shulker Box" : "Barrel");
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        ItemStack[] itemStacks;
        int value = (itemStacks = this.container.getInventory().getContents()).length;
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

    public SilentContainerMenu(Container container2) {
        this.setAutoUpdate(true);
        this.container = container2;
    }
}
