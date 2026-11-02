package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;
import xyz.refinedev.phoenix.menu.impl.setup.SetupTierMenu;
import xyz.refinedev.phoenix.setup.CheckStatus;
import xyz.refinedev.phoenix.setup.CheckStatusFormatter;
import xyz.refinedev.phoenix.setup.SetupAreas;
import xyz.refinedev.phoenix.setup.SetupCatalog;
import xyz.refinedev.phoenix.setup.SetupCheck;
import xyz.refinedev.phoenix.setup.SetupChecks;
import xyz.refinedev.phoenix.setup.SetupMenuFactory;
import xyz.refinedev.phoenix.setup.SetupMessages;
import xyz.refinedev.phoenix.setup.SetupSnapshot;
import xyz.refinedev.phoenix.setup.SetupSummary;
import xyz.refinedev.phoenix.setup.SetupTier;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.general.Tasks;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SetupMenu extends Menu {

    private static final int[] counts = new int[] {10, 11, 12, 13, 14, 15, 16};

    private static final int[] countsCountArray = new int[] {19, 20, 21, 22, 23, 24, 25, 26};

    private final SetupTier setupTier;

    private final List<SetupCheck> setupChecks;

    private final SetupSummary setupSummary;

    private List<Button> buttons;

    public SetupMenu(SetupTier setupTier2, List<SetupCheck> setupChecks, String text) {
        this.setupTier = setupTier2;
        this.setupChecks = setupChecks;
        this.setupSummary = findSetupSummary(setupChecks, text);
        this.setUpdateAfterClick(false);
    }

    private static SetupSummary findSetupSummary(List<SetupCheck> values, String text) {
        Iterator<SetupSummary> iterator2 = SetupChecks.findList(values).iterator();
        while (iterator2.hasNext()) {
            SetupSummary setupSummary;
            if ((setupSummary = (SetupSummary) iterator2.next()).getText().equals(text)) {
                return setupSummary;
            }
        }
        return SetupChecks.findList(
                        Collections.singletonList(
                                SetupCheck.of(text, "This area reported nothing.")))
                .get(0);
    }

    public static void applyPhoenix(
            Phoenix phoenix, Player player, SetupTier setupTier, String text) {
        Tasks.runAsync(
                () -> {
                    List<SetupCheck> values = SetupSnapshot.findList(phoenix, setupTier);
                    phoenix.getMenuHandler()
                            .openMenuAsync(new SetupMenu(setupTier, values, text), player);
                });
    }

    @Override
    public int getSize() {
        return this.getList().size() > countsCountArray.length ? 36 : 27;
    }

    private List<Button> getList() {
        if (this.buttons == null) {
            this.buttons =
                    SetupMenuFactory.findList(super.plugin, this.setupTier, this.setupSummary);
        }
        return this.buttons;
    }

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.SETUP_MENU.AREA_TITLE)
                .replace("<area>", this.setupSummary.getText());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (int index = 0; index < this.getSize(); index++) {
            entries.put(index, Button.placeholder());
        }
        entries.put(4, new SetupMenu.StatusButton());
        entries.put(8, new SetupMenu.AreaButton());
        List<SetupCheck> setupChecks = this.setupSummary.getSetupChecks();
        for (int index2 = 0; index2 < setupChecks.size() && index2 < counts.length; index2++) {
            entries.put(
                    counts[index2],
                    new SetupMenu.EntryButton((SetupCheck) setupChecks.get(index2)));
        }
        List<Button> list = this.getList();
        for (int index3 = 0;
                index3 < list.size() && index3 < countsCountArray.length * 2;
                index3++) {
            entries.put(
                    index3 < countsCountArray.length
                            ? countsCountArray[index3]
                            : countsCountArray[index3 - countsCountArray.length] + 9,
                    (Button) list.get(index3));
        }
        entries.put(18, new SetupMenu.BackButton());
        return entries;
    }

    private SetupSummary getSetupSummary() {
        ArrayList<SetupSummary> values = new ArrayList<>();
        Iterator<SetupSummary> iterator2 = SetupChecks.findList(this.setupChecks).iterator();
        while (iterator2.hasNext()) {
            SetupSummary setupSummary2;
            if (!(setupSummary2 = (SetupSummary) iterator2.next())
                    .getText()
                    .equals(this.setupSummary.getText())) {
                values.add(setupSummary2);
            }
        }
        return SetupChecks.findSetupSummary(values);
    }

    class AreaButton extends Button {

        private AreaButton() {}

        @Override
        public ItemStack getItem(Player player) {
            SetupSummary setupSummary;
            if ((setupSummary = SetupMenu.this.getSetupSummary()) == null) {
                ItemBuilder itemBuilder;
                (itemBuilder = new ItemBuilder(CheckStatusFormatter.create(CheckStatus.OK)))
                        .setName(LocaleUtil.tr(player, MenuConfig.SETUP_MENU.NEXT.NONE_NAME));
                itemBuilder.setLore(LocaleUtil.tr(player, MenuConfig.SETUP_MENU.NEXT.NONE_LORE));
                return itemBuilder.toItemStack();
            }
            ItemBuilder itemBuilder2;
            (itemBuilder2 =
                            new ItemBuilder(
                                    CheckStatusFormatter.create(setupSummary.getCheckStatus())))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.SETUP_MENU.NEXT.NAME)
                                    .replace("<area>", setupSummary.getText()));
            for (String text : LocaleUtil.tr(player, MenuConfig.SETUP_MENU.NEXT.LORE)) {
                itemBuilder2.addLoreLine(
                        text.replace(
                                        "<status>",
                                        CheckStatusFormatter.ordinal(setupSummary.getCheckStatus()))
                                .replace(
                                        "<label>",
                                        CheckStatusFormatter.formatString(
                                                player, setupSummary.getCheckStatus())));
            }
            return itemBuilder2.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            SetupSummary setupSummary;
            if ((setupSummary = SetupMenu.this.getSetupSummary()) == null) {
                Button.playFail(player);
            } else {
                Button.playNeutral(player);
                SetupMenu.super
                        .plugin
                        .getMenuHandler()
                        .openMenu(
                                new SetupMenu(
                                        SetupMenu.this.setupTier,
                                        SetupMenu.this.setupChecks,
                                        setupSummary.getText()),
                                player);
            }
        }
    }

    class BackButton extends Button {

        private BackButton() {}

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(MenuConfig.GENERAL.BACK_SLOT.MATERIAL))
                    .setDurability(MenuConfig.GENERAL.BACK_SLOT.DURABILITY);
            itemBuilder.setName(LocaleUtil.tr(player, MenuConfig.SETUP_MENU.BOARD.NAME));
            itemBuilder.setLore(LocaleUtil.tr(player, MenuConfig.SETUP_MENU.BOARD.LORE));
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Button.playNeutral(player);
            SetupMenu.super
                    .plugin
                    .getMenuHandler()
                    .openMenu(
                            new SetupTierMenu(SetupMenu.this.setupTier, SetupMenu.this.setupChecks),
                            player);
        }
    }

    class EntryButton extends Button {

        private final SetupCheck setupCheck;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    CheckStatusFormatter.create(this.setupCheck.getCheckStatus())))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.SETUP_MENU.CHECK.NAME)
                                    .replace(
                                            "<status>",
                                            CheckStatusFormatter.ordinal(
                                                    this.setupCheck.getCheckStatus()))
                                    .replace(
                                            "<label>",
                                            CheckStatusFormatter.formatString(
                                                    player, this.setupCheck.getCheckStatus())));
            Iterator<String> iterator2 =
                    LocaleUtil.tr(player, MenuConfig.SETUP_MENU.CHECK.LORE).iterator();
            while (iterator2.hasNext()) {
                String text;
                if ((text = (String) iterator2.next()).contains("<message>")) {
                    this.applyItemBuilder(
                            itemBuilder,
                            this.setupCheck.getTextString(),
                            text.replace("<message>", "&7"),
                            " &7");
                } else if (text.contains("<detail>")) {
                    for (String text2 : this.setupCheck.getStrings()) {
                        this.applyItemBuilder(
                                itemBuilder, text2, text.replace("<detail>", " &8"), " &8");
                    }
                } else if (text.contains("<fix>")) {
                    if (this.setupCheck.getText2() == null) {
                        itemBuilder.addLoreLine(
                                LocaleUtil.tr(player, MenuConfig.SETUP_MENU.CHECK.NO_FIX));
                    } else {
                        this.applyItemBuilder(
                                itemBuilder,
                                this.setupCheck.getText2(),
                                text.replace("<fix>", ""),
                                "      &f");
                    }
                } else {
                    itemBuilder.addLoreLine(text);
                }
            }
            return itemBuilder.toItemStack();
        }

        private void applyItemBuilder(
                ItemBuilder itemBuilder, String text, String text2, String text3) {
            for (String text4 : CheckStatusFormatter.findList(text, text2, text3)) {
                itemBuilder.addLoreLine(text4);
            }
        }

        public EntryButton(SetupCheck setupCheck2) {
            this.setupCheck = setupCheck2;
        }
    }

    class StatusButton extends Button {

        private StatusButton() {}

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    CheckStatusFormatter.create(
                                            SetupMenu.this.setupSummary.getCheckStatus())))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.SETUP_MENU.AREA.NAME)
                                    .replace(
                                            "<status>",
                                            CheckStatusFormatter.ordinal(
                                                    SetupMenu.this.setupSummary.getCheckStatus()))
                                    .replace("<area>", SetupMenu.this.setupSummary.getText()));
            itemBuilder.addLoreLine("");
            String text =
                    LocaleUtil.tr(player, MenuConfig.SETUP_MENU.AREA.ABOUT).replace("<line>", "");
            for (String text2 :
                    LocaleUtil.tr(
                            player, SetupAreas.emptyList(SetupMenu.this.setupSummary.getText()))) {
                itemBuilder.addLoreLine(text + text2);
            }
            itemBuilder.addLoreLine("");
            itemBuilder.addLoreLine(
                    LocaleUtil.tr(player, MenuConfig.SETUP_MENU.AREA.CHECK)
                            .replace(
                                    "<status>",
                                    CheckStatusFormatter.ordinal(
                                            SetupMenu.this.setupSummary.getCheckStatus()))
                            .replace(
                                    "<message>",
                                    CheckStatusFormatter.formatString(
                                            player, SetupMenu.this.setupSummary.getCheckStatus())));
            if (SetupCatalog.formatString(SetupMenu.this.setupSummary.getText()) != null) {
                itemBuilder.addLoreLine("");
                itemBuilder.addLoreLine(LocaleUtil.tr(player, MenuConfig.SETUP_MENU.AREA.DOCS));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (SetupCatalog.formatString(SetupMenu.this.setupSummary.getText()) != null) {
                player.closeInventory();
                SetupMessages.sendMessage(player, SetupMenu.this.setupSummary.getText());
            }
        }
    }
}
