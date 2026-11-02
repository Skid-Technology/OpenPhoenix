package xyz.refinedev.phoenix.menu.impl.rank;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.permissible.database.PermissionInfo;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankPermissionGroupsMenu extends PaginatedMenu {

    private final IRank rank;

    private static final String text = "Permissions \u00bb ";

    @Override
    public String getTitle(Player player) {
        return text + this.rank.getName();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (String text : super.plugin.getPermissionDatabase().getSetByPermissionInfosByName()) {
            List<PermissionInfo> values;
            if (!(values = super.plugin.getPermissionDatabase().findList(text)).isEmpty()) {
                entries.put(entries.size(), new RankPermissionGroupsMenu.EntryButton(text, values));
            }
        }
        return entries;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        this.backButton(entries, 4);
        return entries;
    }

    public RankPermissionGroupsMenu(IRank rank2) {
        this.rank = rank2;
    }

    class EntryButton extends Button {

        private final String text;

        private final List<PermissionInfo> permissionInfos;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.PAPER.get())).setName("&a" + this.text);
            itemBuilder.addLoreLine("&7");
            itemBuilder.addLoreLine("&eClick to view permissions.");
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            RankPermissionGroupsMenu.super
                    .plugin
                    .getMenuHandler()
                    .openMenu(
                            new RankPermissionGroupMenu(
                                    this.text,
                                    this.permissionInfos,
                                    RankPermissionGroupsMenu.this.rank),
                            player);
        }

        public EntryButton(String text, List<PermissionInfo> permissionInfos) {
            this.text = text;
            this.permissionInfos = permissionInfos;
        }
    }
}
