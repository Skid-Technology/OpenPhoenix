package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.chat.ColorUtil;
import xyz.refinedev.phoenix.utils.general.WoolUtil;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DisguiseRankMenu extends PaginatedMenu {

    private final String text;

    private static final String PERMISSION = "core.disguise.";

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.DISGUISE_RANK_MENU.TITLE);
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        IProfile cachedProfile =
                Phoenix.getInstance().getProfileHandler().getCachedProfile(player.getUniqueId());
        Iterator<IRank> iterator2 = super.api.getRankHandler().getSortedRanksReversed().iterator();
        while (iterator2.hasNext()) {
            IRank rank;
            if ((rank = (IRank) iterator2.next()).isVisible()
                    && (rank.isDefaultRank()
                            || player.hasPermission(PERMISSION + rank.getName())
                                    && rank.getPriority() <= cachedProfile.getPriority())) {
                entries.put(entries.size(), new DisguiseRankMenu.EntryButton(rank));
            }
        }
        return entries;
    }

    public DisguiseRankMenu(String text) {
        this.text = text;
    }

    class EntryButton extends Button {

        private final IRank rank;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder = new ItemBuilder(XMaterial.WHITE_WOOL.get());
            int value =
                    WoolUtil.convertChatColorToWoolData(
                            ColorUtil.convertToColor(this.rank.getColor()));
            itemBuilder.setDurability(value);
            itemBuilder.setName(this.rank.getDisplayName());
            for (String text : LocaleUtil.tr(player, MenuConfig.DISGUISE_RANK_MENU.LORE)) {
                itemBuilder.addLoreLine(
                        text.replace("<rank>", this.rank.getDisplayName())
                                .replace(
                                        "<player>",
                                        this.rank.getPrefix()
                                                + this.rank.getColor()
                                                + player.getName()));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            DisguiseRankMenu.super
                    .plugin
                    .getMenuHandler()
                    .openMenuAsync(
                            new DisguiseSkinMenu(DisguiseRankMenu.this.text, this.rank), player);
        }

        public EntryButton(IRank rank2) {
            this.rank = rank2;
        }
    }
}
