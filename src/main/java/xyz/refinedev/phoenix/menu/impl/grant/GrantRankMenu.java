package xyz.refinedev.phoenix.menu.impl.grant;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.scope.Scope;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.chat.ColorUtil;
import xyz.refinedev.phoenix.utils.general.WoolUtil;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;
import xyz.refinedev.phoenix.utils.reflection.InventoryTitleUpdate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GrantRankMenu extends PaginatedMenu {

    private final IProfile profile;

    @Override
    public void onOpen(Player player) {
        InventoryTitleUpdate.sendInventoryTitle(
                player,
                LocaleUtil.tr(player, MenuConfig.GRANT_MENU.RANK_TITLE)
                        .replace("<player>", this.profile.getNameWithColorNoDisguise()));
    }

    @Override
    public String getTitle(Player player) {
        return Color.translate(
                LocaleUtil.tr(player, MenuConfig.GRANT_MENU.RANK_TITLE)
                        .replace("<player>", this.profile.getNameWithColorNoDisguise()));
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        IProfile cachedProfile =
                super.api.getProfileHandler().getCachedProfile(player.getUniqueId());
        Iterator<IRank> iterator2 = super.api.getRankHandler().getSortedRanksReversed().iterator();
        while (iterator2.hasNext()) {
            IRank rank;
            if (!(rank = (IRank) iterator2.next()).isDefaultRank()
                    && (MenuConfig.GRANT_MENU.SHOW_UNGRANTABLE_RANKS
                            || super.api.getRankHandler().canGrant(cachedProfile, rank))) {
                entries.put(entries.size(), new GrantRankMenu.EntryButton(rank, this.profile));
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

    public GrantRankMenu(IProfile profile2) {
        this.profile = profile2;
    }

    class EntryButton extends Button {

        private final IRank rank;

        private final IProfile profile;

        @Override
        public ItemStack getItem(Player player) {
            IProfile cachedProfile =
                    GrantRankMenu.super
                            .api
                            .getProfileHandler()
                            .getCachedProfile(player.getUniqueId());
            ItemBuilder itemBuilder;
            if (GrantRankMenu.super.api.getRankHandler().canGrant(cachedProfile, this.rank)) {
                itemBuilder = new ItemBuilder(XMaterial.WHITE_WOOL.get());
                int value =
                        WoolUtil.convertChatColorToWoolData(
                                ColorUtil.convertToColor(this.rank.getColor()));
                itemBuilder.setDurability(value);
            } else {
                itemBuilder = new ItemBuilder(XMaterial.OBSIDIAN.get());
            }
            itemBuilder.setName(Color.translate(this.rank.getDisplayName()));
            if (GrantRankMenu.super.api.getRankHandler().canGrant(cachedProfile, this.rank)) {
                List<String> values =
                        LocaleUtil.tr(player, MenuConfig.GRANT_MENU.RANK_MENU.ACCESS_LORE);
                ItemBuilder itemBuilder2 = itemBuilder;
                values.forEach(
                        result -> {
                            result =
                                    result.replace(
                                                    "<rank>",
                                                    Color.translate(this.rank.getDisplayName()))
                                            .replace(
                                                    "<priority>",
                                                    String.valueOf(this.rank.getPriority()))
                                            .replace("<prefix>", this.rank.getPrefix())
                                            .replace("<suffix>", this.rank.getSuffix())
                                            .replace(
                                                    "<visible>",
                                                    String.valueOf(this.rank.isVisible()))
                                            .replace(
                                                    "<grantable>",
                                                    String.valueOf(this.rank.isGrantable()))
                                            .replace("<color>", this.rank.getColor())
                                            .replace(
                                                    "<player>",
                                                    this.profile.getNameWithColorNoDisguise());
                            itemBuilder2.addLoreLine(result);
                        });
            } else {
                List<String> values2 =
                        LocaleUtil.tr(player, MenuConfig.GRANT_MENU.RANK_MENU.NO_ACCESS_LORE);
                ItemBuilder itemBuilder3 = itemBuilder;
                values2.forEach(
                        result -> {
                            result =
                                    result.replace(
                                                    "<rank>",
                                                    Color.translate(this.rank.getDisplayName()))
                                            .replace(
                                                    "<priority>",
                                                    String.valueOf(this.rank.getPriority()))
                                            .replace("<prefix>", this.rank.getPrefix())
                                            .replace("<suffix>", this.rank.getSuffix())
                                            .replace(
                                                    "<visible>",
                                                    String.valueOf(this.rank.isVisible()))
                                            .replace(
                                                    "<grantable>",
                                                    String.valueOf(this.rank.isGrantable()))
                                            .replace("<color>", this.rank.getColor())
                                            .replace(
                                                    "<player>",
                                                    this.profile.getNameWithColorNoDisguise());
                            itemBuilder3.addLoreLine(result);
                        });
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (this.profile.hasRankAndCantUpgrade(this.rank)) {
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.GRANT.ALREADY_HAS_RANK)
                                        .replace(
                                                "<player>",
                                                this.profile.getNameWithColorNoDisguise())));
            } else {
                IProfile cachedProfile =
                        GrantRankMenu.super
                                .api
                                .getProfileHandler()
                                .getCachedProfile(player.getUniqueId());
                if (!GrantRankMenu.super.api.getRankHandler().canGrant(cachedProfile, this.rank)) {
                    player.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(player, MessageConfig.GRANT.CANT_GRANT_RANK)
                                            .replace(
                                                    "<rank>",
                                                    Color.translate(this.rank.getDisplayName()))));
                } else {
                    player.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(
                                                    player,
                                                    MessageConfig.GRANT.GRANT_PROCEDURE_RANK_SET)
                                            .replace(
                                                    "<rank>",
                                                    Color.translate(this.rank.getDisplayName()))));
                    if (clickType.isRightClick()) {
                        GrantRankMenu.super
                                .plugin
                                .getMenuHandler()
                                .openMenu(new GrantScopeMenu(this.profile, this.rank), player);
                    } else {
                        GrantRankMenu.super
                                .plugin
                                .getMenuHandler()
                                .openMenu(
                                        new GrantDurationMenu(
                                                this.profile,
                                                this.rank,
                                                Collections.singletonList(Scope.scopeScope)),
                                        player);
                    }
                }
            }
        }

        public EntryButton(IRank rank2, IProfile profile2) {
            this.rank = rank2;
            this.profile = profile2;
        }
    }
}
