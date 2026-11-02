package xyz.refinedev.phoenix.menu.impl.rank;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.StringUtils;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.chat.ColorUtil;
import xyz.refinedev.phoenix.utils.general.WoolUtil;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.Map;

public class RankEditorMenu extends PaginatedMenu {

    private static final String title = "Rank Editor";

    @Override
    public String getTitle(Player player) {
        return title;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (IRank rank : super.api.getRankHandler().getSortedRanksReversed()) {
            entries.put(entries.size(), new RankEditorMenu.NoneButton(rank));
        }
        return entries;
    }

    class NoneButton extends Button {

        private final IRank rank;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.WHITE_WOOL.get()))
                    .setName(this.rank.getDisplayName());
            try {
                int value =
                        WoolUtil.convertChatColorToWoolData(
                                ColorUtil.convertToColor(this.rank.getColor()));
                itemBuilder.setDurability(value);
            } catch (Exception exception) {
                itemBuilder.setDurability(0);
                RankEditorMenu.super
                        .plugin
                        .getLogger()
                        .severe(this.rank.getName() + " doesn't have a properly setup color.");
            }
            for (String text : LocaleUtil.tr(player, MenuConfig.RANK_EDITOR_MENU.RANK_INFO)) {
                itemBuilder.addLoreLine(
                        text.replace("<rank>", this.rank.getDisplayName())
                                .replace("<name>", this.rank.getName())
                                .replace("<uuid>", this.rank.getUuid().toString())
                                .replace(
                                        "<prefix>",
                                        !this.rank.getPrefixLegacy().isEmpty()
                                                ? this.rank.getPrefix()
                                                : "None")
                                .replace(
                                        "<prefixModern>",
                                        !this.rank.getPrefixModern().isEmpty()
                                                ? this.rank.getPrefix()
                                                : "None")
                                .replace(
                                        "<suffix>",
                                        !this.rank.getSuffixLegacy().isEmpty()
                                                ? this.rank.getSuffix()
                                                : "None")
                                .replace(
                                        "<suffixModern>",
                                        !this.rank.getSuffixModern().isEmpty()
                                                ? this.rank.getSuffix()
                                                : "None")
                                .replace("<color>", this.rank.getColor() + "Color")
                                .replace("<colorModern>", this.rank.getColorModern() + "Color")
                                .replace("<playerList>", this.rank.getPlayerListPrefixLegacy())
                                .replace(
                                        "<playerListPrefixModern>",
                                        this.rank.getPlayerListPrefixModern())
                                .replace("<visible>", this.rank.isVisible() ? "&ayes" : "&cno")
                                .replace("<staff>", this.rank.isStaff() ? "&ayes" : "&cno")
                                .replace("<default>", this.rank.isDefaultRank() ? "&ayes" : "&cno")
                                .replace(
                                        "<subscription>",
                                        this.rank.isSubscription() ? "&ayes" : "&cno")
                                .replace("<priority>", String.valueOf(this.rank.getPriority()))
                                .replace("<price>", String.valueOf(this.rank.getPrice()))
                                .replace("<grantable>", this.rank.isGrantable() ? "&ayes" : "&cno")
                                .replace(
                                        "<purchasable>",
                                        this.rank.isPurchasable() ? "&ayes" : "&cno")
                                .replace(
                                        "<scopes>",
                                        StringUtils.getStringFromList(this.rank.getNiceScopes())));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            RankEditorMenu.super
                    .plugin
                    .getMenuHandler()
                    .openMenu(new RankPropertiesMenu(this.rank), player);
        }

        public NoneButton(IRank rank2) {
            this.rank = rank2;
        }
    }
}
