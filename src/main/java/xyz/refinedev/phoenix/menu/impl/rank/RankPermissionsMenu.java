package xyz.refinedev.phoenix.menu.impl.rank;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.rank.permission.IPermission;
import xyz.refinedev.phoenix.utils.StringUtils;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.general.WoolUtil;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.Map;

public class RankPermissionsMenu extends PaginatedMenu {

    private final IRank rank;

    private static final String text = "Permissions \u00bb ";

    @Override
    public String getTitle(Player player) {
        return text + this.rank.getName();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (IPermission permission : this.rank.getPermissions()) {
            entries.put(entries.size(), new RankPermissionsMenu.ClickToRemoveButton(permission));
        }
        return entries;
    }

    public RankPermissionsMenu(IRank rank2) {
        this.rank = rank2;
    }

    class ClickToRemoveButton extends Button {

        private final IPermission permission;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.WHITE_WOOL.get()))
                    .setDurability(WoolUtil.convertChatColorToWoolData(ChatColor.GREEN));
            itemBuilder.setName(
                    "&a"
                            + this.permission.getPermission()
                            + "&7["
                            + StringUtils.join(this.permission.getNiceScopes()));
            itemBuilder.addLoreLine("&7&m------------------------");
            itemBuilder.addLoreLine("&c&lClick to remove");
            itemBuilder.addLoreLine("&c&lthis permission");
            itemBuilder.addLoreLine("&7&m------------------------");
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.chat(
                    "/rank perm delete "
                            + RankPermissionsMenu.this.rank.getName()
                            + " "
                            + this.permission.getPermission());
        }

        public ClickToRemoveButton(IPermission permission2) {
            this.permission = permission2;
        }
    }
}
