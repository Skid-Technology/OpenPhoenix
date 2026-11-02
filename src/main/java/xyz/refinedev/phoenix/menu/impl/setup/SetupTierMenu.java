package xyz.refinedev.phoenix.menu.impl.setup;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;
import xyz.refinedev.phoenix.menu.impl.SetupMenu;
import xyz.refinedev.phoenix.setup.CheckStatus;
import xyz.refinedev.phoenix.setup.CheckStatusFormatter;
import xyz.refinedev.phoenix.setup.SetupAreas;
import xyz.refinedev.phoenix.setup.SetupCheck;
import xyz.refinedev.phoenix.setup.SetupChecks;
import xyz.refinedev.phoenix.setup.SetupMessages;
import xyz.refinedev.phoenix.setup.SetupSnapshot;
import xyz.refinedev.phoenix.setup.SetupSummary;
import xyz.refinedev.phoenix.setup.SetupTier;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.general.Tasks;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SetupTierMenu extends Menu {

    private static final int[] counts =
            new int[] {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};

    private final SetupTier setupTier;

    private List<SetupCheck> setupChecks;

    private List<SetupSummary> setupSummaries;

    public SetupTierMenu(SetupTier setupTier2, List<SetupCheck> values) {
        this.setupTier = setupTier2;
        this.setList(values);
        this.setUpdateAfterClick(false);
    }

    public static void applyPhoenix(Phoenix phoenix, Player player, SetupTier setupTier) {
        Tasks.runAsync(
                () ->
                        phoenix.getMenuHandler()
                                .openMenuAsync(
                                        new SetupTierMenu(
                                                setupTier,
                                                SetupSnapshot.findList(phoenix, setupTier)),
                                        player));
    }

    public void setList(List<SetupCheck> setupChecks) {
        this.setupChecks = setupChecks;
        this.setupSummaries = SetupChecks.findList(setupChecks);
    }

    @Override
    public int getSize() {
        return this.setupSummaries.size() > 7 ? 36 : 27;
    }

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(
                player,
                this.setupTier == SetupTier.FEATURES
                        ? MenuConfig.SETUP_MENU.FEATURE_TITLE
                        : MenuConfig.SETUP_MENU.TITLE);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        int size2 = this.getSize();
        for (int index = 0; index < size2; index++) {
            entries.put(index, Button.placeholder());
        }
        entries.put(4, new SetupTierMenu.SummaryButton());
        for (int index2 = 0;
                index2 < this.setupSummaries.size() && index2 < counts.length;
                index2++) {
            entries.put(
                    counts[index2], new SetupTierMenu.CheckButton(this.setupSummaries.get(index2)));
        }
        entries.put(size2 - 7, new SetupTierMenu.RecommendedChangesButton());
        entries.put(size2 - 5, new SetupTierMenu.EntryButton());
        entries.put(size2 - 3, new SetupTierMenu.BackButton());
        return entries;
    }

    class BackButton extends Button {

        private BackButton() {}

        @Override
        public ItemStack getItem(Player player) {
            boolean matches = SetupTierMenu.this.setupTier == SetupTier.FEATURES;
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    matches
                                            ? XMaterial.NETHER_STAR.get()
                                            : XMaterial.COMPARATOR.get()))
                    .setName(
                            LocaleUtil.tr(
                                    player,
                                    matches
                                            ? MenuConfig.SETUP_MENU.TIER.TO_ESSENTIALS_NAME
                                            : MenuConfig.SETUP_MENU.TIER.TO_FEATURES_NAME));
            itemBuilder.setLore(
                    LocaleUtil.tr(
                            player,
                            matches
                                    ? MenuConfig.SETUP_MENU.TIER.TO_ESSENTIALS_LORE
                                    : MenuConfig.SETUP_MENU.TIER.TO_FEATURES_LORE));
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Button.playNeutral(player);
            SetupTierMenu.applyPhoenix(
                    SetupTierMenu.super.plugin,
                    player,
                    SetupTierMenu.this.setupTier == SetupTier.FEATURES
                            ? SetupTier.ESSENTIALS
                            : SetupTier.FEATURES);
        }
    }

    class CheckButton extends Button {

        private final SetupSummary setupSummary;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    CheckStatusFormatter.create(
                                            this.setupSummary.getCheckStatus())))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.SETUP_MENU.AREA.NAME)
                                    .replace(
                                            "<status>",
                                            CheckStatusFormatter.ordinal(
                                                    this.setupSummary.getCheckStatus()))
                                    .replace("<area>", this.setupSummary.getText()));
            Iterator<String> iterator2 =
                    LocaleUtil.tr(player, MenuConfig.SETUP_MENU.AREA.LORE).iterator();
            while (iterator2.hasNext()) {
                String text;
                if ((text = (String) iterator2.next()).contains("<about>")) {
                    String text2 =
                            LocaleUtil.tr(player, MenuConfig.SETUP_MENU.AREA.ABOUT)
                                    .replace("<line>", "");
                    for (String text3 :
                            LocaleUtil.tr(
                                    player, SetupAreas.emptyList(this.setupSummary.getText()))) {
                        itemBuilder.addLoreLine(text2 + text3);
                    }
                } else if (text.contains("<checks>")) {
                    for (SetupCheck setupCheck : this.setupSummary.getSetupChecks()) {
                        this.applyPlayer(player, itemBuilder, setupCheck);
                    }
                } else {
                    itemBuilder.addLoreLine(text);
                }
            }
            return itemBuilder.toItemStack();
        }

        private void applyPlayer(Player player, ItemBuilder itemBuilder, SetupCheck setupCheck) {
            String text =
                    LocaleUtil.tr(player, MenuConfig.SETUP_MENU.AREA.CHECK)
                            .replace(
                                    "<status>",
                                    CheckStatusFormatter.ordinal(setupCheck.getCheckStatus()))
                            .replace("<message>", "");
            for (String text2 :
                    CheckStatusFormatter.findList(setupCheck.getTextString(), text, "   &7")) {
                itemBuilder.addLoreLine(text2);
            }
            String text3 =
                    LocaleUtil.tr(player, MenuConfig.SETUP_MENU.AREA.DETAIL)
                            .replace("<message>", "");
            Iterator<String> iterator2 = setupCheck.getStrings().iterator();
            while (iterator2.hasNext()) {
                for (String text4 :
                        CheckStatusFormatter.findList((String) iterator2.next(), text3, text3)) {
                    itemBuilder.addLoreLine(text4);
                }
            }
            if (setupCheck.getText2() != null) {
                String text5 =
                        LocaleUtil.tr(player, MenuConfig.SETUP_MENU.AREA.FIX).replace("<fix>", "");
                for (String text6 :
                        CheckStatusFormatter.findList(setupCheck.getText2(), text5, "        &f")) {
                    itemBuilder.addLoreLine(text6);
                }
            }
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Button.playNeutral(player);
            SetupTierMenu.super
                    .plugin
                    .getMenuHandler()
                    .openMenu(
                            new SetupMenu(
                                    SetupTierMenu.this.setupTier,
                                    SetupTierMenu.this.setupChecks,
                                    this.setupSummary.getText()),
                            player);
        }

        public CheckButton(SetupSummary setupSummary2) {
            this.setupSummary = setupSummary2;
        }
    }

    class EntryButton extends Button {

        private EntryButton() {}

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.CLOCK.get()))
                    .setName(LocaleUtil.tr(player, MenuConfig.SETUP_MENU.REFRESH.NAME));
            itemBuilder.setLore(LocaleUtil.tr(player, MenuConfig.SETUP_MENU.REFRESH.LORE));
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Button.playNeutral(player);
            Tasks.runAsync(
                    () -> {
                        SetupTierMenu.this.setList(
                                SetupSnapshot.findList(
                                        SetupTierMenu.super.plugin, SetupTierMenu.this.setupTier));
                        SetupTierMenu.super
                                .plugin
                                .getMenuHandler()
                                .openMenuAsync(SetupTierMenu.this, player);
                    });
        }
    }

    class RecommendedChangesButton extends Button {

        private RecommendedChangesButton() {}

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.PAPER.get()))
                    .setName(LocaleUtil.tr(player, MenuConfig.SETUP_MENU.REPORT.NAME));
            itemBuilder.setLore(LocaleUtil.tr(player, MenuConfig.SETUP_MENU.REPORT.LORE));
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Button.playNeutral(player);
            player.closeInventory();
            SetupMessages.sendMessage(
                    player,
                    SetupTierMenu.this.setupChecks,
                    LocaleUtil.tr(player, MessageConfig.PHOENIX.SETUP.HEADER));
        }
    }

    class SummaryButton extends Button {

        private SummaryButton() {}

        @Override
        public ItemStack getItem(Player player) {
            SetupSummary setupSummary =
                    SetupChecks.findSetupSummary(SetupTierMenu.this.setupSummaries);
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.BOOK.get()))
                    .setName(LocaleUtil.tr(player, MenuConfig.SETUP_MENU.SUMMARY.NAME));
            for (String text :
                    LocaleUtil.tr(
                            player,
                            setupSummary == null
                                    ? MenuConfig.SETUP_MENU.SUMMARY.DONE_LORE
                                    : MenuConfig.SETUP_MENU.SUMMARY.LORE)) {
                itemBuilder.addLoreLine(
                        text.replace(
                                        "<ok>",
                                        String.valueOf(
                                                SetupChecks.calculateCount(
                                                        SetupTierMenu.this.setupChecks,
                                                        CheckStatus.OK)))
                                .replace(
                                        "<todo>",
                                        String.valueOf(
                                                SetupChecks.calculateCount(
                                                        SetupTierMenu.this.setupChecks,
                                                        CheckStatus.TODO)))
                                .replace(
                                        "<warn>",
                                        String.valueOf(
                                                SetupChecks.calculateCount(
                                                        SetupTierMenu.this.setupChecks,
                                                        CheckStatus.WARN)))
                                .replace(
                                        "<fail>",
                                        String.valueOf(
                                                SetupChecks.calculateCount(
                                                        SetupTierMenu.this.setupChecks,
                                                        CheckStatus.FAIL)))
                                .replace(
                                        "<next>",
                                        setupSummary == null
                                                ? LocaleUtil.tr(
                                                        player,
                                                        MenuConfig.SETUP_MENU.SUMMARY.NEXT_NONE)
                                                : setupSummary.getText()));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            SetupSummary setupSummary;
            if ((setupSummary = SetupChecks.findSetupSummary(SetupTierMenu.this.setupSummaries))
                    != null) {
                Button.playNeutral(player);
                SetupTierMenu.super
                        .plugin
                        .getMenuHandler()
                        .openMenu(
                                new SetupMenu(
                                        SetupTierMenu.this.setupTier,
                                        SetupTierMenu.this.setupChecks,
                                        setupSummary.getText()),
                                player);
            }
        }
    }
}
