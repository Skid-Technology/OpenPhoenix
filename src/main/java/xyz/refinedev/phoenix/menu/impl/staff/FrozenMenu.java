package xyz.refinedev.phoenix.menu.impl.staff;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.lib.xseries.XMaterial;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrozenMenu extends Menu {

    private static final String title = "&cYou've been frozen";

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (int index = 0; index < 27; index++) {
            entries.put(index, new FrozenMenu.EntryButton());
        }
        return entries;
    }

    @Override
    public String getTitle(Player player) {
        return Color.translate(title);
    }

    class EntryButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.OBSIDIAN.parseMaterial()))
                    .setName(LocaleUtil.tr(player, MenuConfig.FREEZE_MENU.NAME));
            List<String> values = LocaleUtil.tr(player, MenuConfig.FREEZE_MENU.LORE);
            ItemBuilder itemBuilder2 = itemBuilder;
            values.forEach(itemBuilder2::addLoreLine);
            return itemBuilder.toItemStack();
        }

        public EntryButton() {}
    }
}
