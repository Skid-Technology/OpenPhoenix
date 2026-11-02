package xyz.refinedev.phoenix.menu.impl.staff;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.lib.xseries.XMaterial;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.Map;

public class PlayerInventoryMenu extends Menu {

    private final Player player;

    private static final String text = "'s Inventory";

    @Override
    public String getTitle(Player player2) {
        return this.player.getName() + text;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player2) {
        HashMap<Integer, Button> entries = new HashMap<>();
        ItemStack[] itemStacks;
        int value = (itemStacks = this.player.getInventory().getContents()).length;
        for (int index = 0; index < value; index++) {
            final ItemStack itemStack = itemStacks[index];
            entries.put(
                    entries.size(),
                    new Button() {

                        @Override
                        public ItemStack getItem(Player player2) {
                            return itemStack;
                        }
                    });
        }
        entries.put(
                36,
                new Button() {

                    private static final String text = "&cNo Helmet";

                    @Override
                    public ItemStack getItem(Player player2) {
                        return PlayerInventoryMenu.this.player.getInventory().getHelmet() == null
                                ? new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE.parseItem())
                                        .setName(text)
                                        .toItemStack()
                                : PlayerInventoryMenu.this.player.getInventory().getHelmet();
                    }
                });
        entries.put(
                37,
                new Button() {

                    private static final String text = "&cNo Chestplate";

                    @Override
                    public ItemStack getItem(Player player2) {
                        return PlayerInventoryMenu.this.player.getInventory().getChestplate()
                                        == null
                                ? new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE.parseItem())
                                        .setName(text)
                                        .toItemStack()
                                : PlayerInventoryMenu.this.player.getInventory().getChestplate();
                    }
                });
        entries.put(
                38,
                new Button() {

                    private static final String text = "&cNo Leggings";

                    @Override
                    public ItemStack getItem(Player player2) {
                        return PlayerInventoryMenu.this.player.getInventory().getLeggings() == null
                                ? new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE.parseItem())
                                        .setName(text)
                                        .toItemStack()
                                : PlayerInventoryMenu.this.player.getInventory().getLeggings();
                    }
                });
        entries.put(
                39,
                new Button() {

                    private static final String text = "&cNo Boots";

                    @Override
                    public ItemStack getItem(Player player2) {
                        return PlayerInventoryMenu.this.player.getInventory().getBoots() == null
                                ? new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE.parseItem())
                                        .setName(text)
                                        .toItemStack()
                                : PlayerInventoryMenu.this.player.getInventory().getBoots();
                    }
                });
        return entries;
    }

    public PlayerInventoryMenu(Player player2) {
        this.setAutoUpdate(true);
        this.player = player2;
    }
}
