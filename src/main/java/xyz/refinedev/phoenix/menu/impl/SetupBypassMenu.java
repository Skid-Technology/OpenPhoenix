package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.rank.permission.Permission;
import xyz.refinedev.phoenix.scope.Scope;
import xyz.refinedev.phoenix.setup.CheckStatus;
import xyz.refinedev.phoenix.setup.CheckStatusFormatter;
import xyz.refinedev.phoenix.setup.SetupSummary;
import xyz.refinedev.phoenix.setup.SetupTier;
import xyz.refinedev.phoenix.setup.SetupWriter;
import xyz.refinedev.phoenix.setup.check.BypassPermissionCatalog;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SetupBypassMenu extends PaginatedMenu {

    private final SetupSummary setupSummary;

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.SETUP_MENU.BYPASS.TITLE);
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (IRank rank : super.api.getRankHandler().getSortedRanksReversed()) {
            entries.put(entries.size(), new SetupBypassMenu.EntryButton(rank));
        }
        return entries;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        this.backButton(entries, 4);
        return entries;
    }

    private List<String> findList(IRank rank) {
        ArrayList<String> values = new ArrayList<>();
        for (String text : BypassPermissionCatalog.strings) {
            if (!rank.hasPermission(text)) {
                values.add(text);
            }
        }
        return values;
    }

    public SetupBypassMenu(SetupSummary setupSummary2) {
        this.setupSummary = setupSummary2;
    }

    class EntryButton extends Button {

        private final IRank rank;

        @Override
        public ItemStack getItem(Player player) {
            List<String> values = SetupBypassMenu.this.findList(this.rank);
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    values.isEmpty()
                                            ? CheckStatusFormatter.create(CheckStatus.OK)
                                            : new ItemStack(XMaterial.IRON_CHESTPLATE.get())))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.SETUP_MENU.BYPASS.RANK_NAME)
                                    .replace("<rank>", this.rank.getDisplayName()));
            Iterator<String> iterator2 =
                    LocaleUtil.tr(
                                    player,
                                    values.isEmpty()
                                            ? MenuConfig.SETUP_MENU.BYPASS.RANK_HOLDS_ALL_LORE
                                            : MenuConfig.SETUP_MENU.BYPASS.RANK_LORE)
                            .iterator();
            while (iterator2.hasNext()) {
                String text;
                if ((text = (String) iterator2.next()).contains("<nodes>")) {
                    for (String text2 : values) {
                        itemBuilder.addLoreLine(
                                LocaleUtil.tr(player, MenuConfig.SETUP_MENU.BYPASS.NODE)
                                        .replace("<node>", text2));
                    }
                } else {
                    itemBuilder.addLoreLine(
                            text.replace("<missing>", String.valueOf(values.size()))
                                    .replace(
                                            "<total>",
                                            String.valueOf(
                                                    BypassPermissionCatalog.strings.size())));
                }
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            List<String> values;
            if ((values = SetupBypassMenu.this.findList(this.rank)).isEmpty()) {
                Button.playFail(player);
            } else {
                ArrayList<String> values2;
                (values2 = new ArrayList())
                        .add(
                                " &7Grants &f"
                                        + values.size()
                                        + " &7permissions to &f"
                                        + this.rank.getName()
                                        + "&7:");
                for (String text : values) {
                    values2.add(" &8" + text);
                }
                values2.add(" &7Nothing this rank holds is removed.");
                Phoenix phoenix = SetupBypassMenu.super.plugin;
                SetupTier setupTier = SetupTier.ESSENTIALS;
                String text2 = SetupBypassMenu.this.setupSummary.getText();
                String name = this.rank.getName();
                List values3 = values;
                SetupWriter.applyPhoenix(
                        phoenix,
                        player,
                        setupTier,
                        text2,
                        name,
                        values2,
                        () -> this.sendGlobal(player, values3));
            }
        }

        private void sendGlobal(Player player, List<String> values) {
            int value = 0;
            for (String text : values) {
                if (!this.rank.hasPermission(text)) {
                    this.rank.getPermissions().add(new Permission(text, Scope.findList("global")));
                    value++;
                }
            }
            if (value != 0) {
                SetupBypassMenu.super.plugin.getRankHandler().updateRank(this.rank, true);
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.PHOENIX.SETUP.BYPASSES_GRANTED)
                                        .replace("<count>", String.valueOf(value))
                                        .replace("<rank>", this.rank.getDisplayName())));
            }
        }

        public EntryButton(IRank rank2) {
            this.rank = rank2;
        }
    }
}
