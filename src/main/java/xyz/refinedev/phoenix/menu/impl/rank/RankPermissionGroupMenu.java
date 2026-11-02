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

public class RankPermissionGroupMenu extends PaginatedMenu {

    private final String text;

    private final List<PermissionInfo> permissionInfos;

    private final IRank rank;

    private static final String textString = "Permissions \u00bb ";

    @Override
    public String getTitle(Player player) {
        return textString + this.text;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        this.backButton(entries, 4);
        return entries;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (PermissionInfo permissionInfo : this.permissionInfos) {
            entries.put(entries.size(), new RankPermissionGroupMenu.CommandsButton(permissionInfo));
        }
        return entries;
    }

    public RankPermissionGroupMenu(String text, List<PermissionInfo> permissionInfos, IRank rank2) {
        this.text = text;
        this.permissionInfos = permissionInfos;
        this.rank = rank2;
    }

    class CommandsButton extends Button {

        private final PermissionInfo permissionInfo;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.PAPER.get()))
                    .setName("&a" + this.permissionInfo.getPermission());
            if (!this.permissionInfo.getDescription().isEmpty()) {
                itemBuilder.addLoreLine("&7Description: &f" + this.permissionInfo.getDescription());
            }
            itemBuilder.addLoreLine("&7Source: &f" + this.permissionInfo.getSource());
            itemBuilder.addLoreLine("");
            if (this.permissionInfo.getCommands().length != 0) {
                itemBuilder.addLoreLine("&7Commands");
                String[] texts;
                int value = (texts = this.permissionInfo.getCommands()).length;
                for (int index = 0; index < value; index++) {
                    String text = texts[index];
                    itemBuilder.addLoreLine("&7- &f" + text);
                }
                itemBuilder.addLoreLine("&7");
            }
            if (this.permissionInfo.hasPlaceholders()) {
                itemBuilder.addLoreLine(
                        "&cThis permission has placeholders and must be added manually.");
            } else {
                itemBuilder.addLoreLine(
                        "&aClick to add to "
                                + RankPermissionGroupMenu.this.rank.getDisplayName()
                                + "&a.");
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (!this.permissionInfo.hasPlaceholders()) {
                player.chat(
                        "/rank perm add "
                                + RankPermissionGroupMenu.this.rank.getName()
                                + " "
                                + this.permissionInfo.getPermission());
            }
        }

        public CommandsButton(PermissionInfo permissionInfo2) {
            this.permissionInfo = permissionInfo2;
        }
    }
}
