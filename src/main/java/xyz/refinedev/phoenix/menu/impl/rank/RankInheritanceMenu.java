package xyz.refinedev.phoenix.menu.impl.rank;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.chat.ColorUtil;
import xyz.refinedev.phoenix.utils.general.WoolUtil;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RankInheritanceMenu extends PaginatedMenu {

    private final IRank rank;

    private static final String text = "Inheritance \u00bb ";

    @Override
    public String getTitle(Player player) {
        return text + this.rank.getName();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (UUID uUID : this.rank.getInheritance()) {
            entries.put(entries.size(), new RankInheritanceMenu.ClickToRemoveButton(uUID));
        }
        return entries;
    }

    public RankInheritanceMenu(IRank rank2) {
        this.rank = rank2;
    }

    class ClickToRemoveButton extends Button {

        private final UUID id;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder = new ItemBuilder(XMaterial.WHITE_WOOL.get());
            IRank rank;
            int value =
                    WoolUtil.convertChatColorToWoolData(
                            ColorUtil.convertToColor(
                                    (rank =
                                                    RankInheritanceMenu.super
                                                            .api
                                                            .getRankHandler()
                                                            .getRank(this.id))
                                            .getColor()));
            itemBuilder.setDurability(value);
            itemBuilder.setName(rank.getDisplayName());
            itemBuilder.addLoreLine("&7&m------------------------");
            itemBuilder.addLoreLine("&c&lClick to remove");
            itemBuilder.addLoreLine("&c&lthis inheritance");
            itemBuilder.addLoreLine("&7&m------------------------");
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.chat(
                    "/rank inheritance delete "
                            + RankInheritanceMenu.this.rank.getName()
                            + " "
                            + RankInheritanceMenu.this.rank.getName());
        }

        public ClickToRemoveButton(UUID id) {
            this.id = id;
        }
    }
}
