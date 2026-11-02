package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.disguise.IDisguiseHistory;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;
import xyz.refinedev.phoenix.utils.reflection.Reflection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisguiseHistoryMenu extends PaginatedMenu {

    private final IProfile profile;

    private final List<IDisguiseHistory> disguiseHistories;

    private static final String text = "<player>";

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.DISGUISE_HISTORY_MENU.TITLE)
                .replace(text, this.profile.getPlayerName());
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (IDisguiseHistory disguiseHistory : this.disguiseHistories) {
            entries.put(entries.size(), new DisguiseHistoryMenu.EntryButton(disguiseHistory));
        }
        return entries;
    }

    public DisguiseHistoryMenu(IProfile profile2, List<IDisguiseHistory> disguiseHistories) {
        this.profile = profile2;
        this.disguiseHistories = disguiseHistories;
    }

    public class EntryButton extends Button {

        private final IDisguiseHistory disguiseHistory;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.DISGUISE_HISTORY_MENU.NAME)
                                    .replace(
                                            "<date>",
                                            DateUtils.getDate(this.disguiseHistory.getDate()))
                                    .replace("<name>", this.disguiseHistory.getName())
                                    .replace("<skin>", this.disguiseHistory.getSkin().getName())
                                    .replace(
                                            "<rank>",
                                            DisguiseHistoryMenu.super
                                                                    .api
                                                                    .getRankHandler()
                                                                    .getRank(
                                                                            this.disguiseHistory
                                                                                    .getRankId())
                                                            != null
                                                    ? DisguiseHistoryMenu.super
                                                            .api
                                                            .getRankHandler()
                                                            .getRank(
                                                                    this.disguiseHistory
                                                                            .getRankId())
                                                            .getDisplayName()
                                                    : "N/A"));
            for (String text : LocaleUtil.tr(player, MenuConfig.DISGUISE_HISTORY_MENU.LORE)) {
                itemBuilder.addLoreLine(
                        text.replace("<date>", DateUtils.getDate(this.disguiseHistory.getDate()))
                                .replace("<name>", this.disguiseHistory.getName())
                                .replace("<skin>", this.disguiseHistory.getSkin().getName())
                                .replace(
                                        "<rank>",
                                        DisguiseHistoryMenu.super
                                                                .api
                                                                .getRankHandler()
                                                                .getRank(
                                                                        this.disguiseHistory
                                                                                .getRankId())
                                                        != null
                                                ? DisguiseHistoryMenu.super
                                                        .api
                                                        .getRankHandler()
                                                        .getRank(this.disguiseHistory.getRankId())
                                                        .getDisplayName()
                                                : "N/A"));
            }
            return Reflection.itemWithBase64(
                    itemBuilder.toItemStack(), this.disguiseHistory.getSkin().getValue());
        }

        public EntryButton(IDisguiseHistory disguiseHistory2) {
            this.disguiseHistory = disguiseHistory2;
        }
    }
}
