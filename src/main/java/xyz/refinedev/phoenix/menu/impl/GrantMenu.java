package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GrantMenu extends Menu {

    private final IGrant grant;

    private final String text;

    private final IProfile profile;

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (int index = 0; index < 45; index++) {
            if (Arrays.asList(10, 11, 12, 19, 20, 21, 28, 29, 30).contains(index)) {
                entries.put(index, new GrantMenu.RankButton());
            } else if (Arrays.asList(14, 15, 16, 23, 24, 25, 32, 33, 34).contains(index)) {
                entries.put(index, new GrantMenu.EntryButton());
            } else {
                entries.put(index, Button.placeholder());
            }
        }
        return entries;
    }

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.GRANT_MENU.REMOVAL_TITLE);
    }

    public GrantMenu(IGrant grant2, String text, IProfile profile2) {
        this.grant = grant2;
        this.text = text;
        this.profile = profile2;
    }

    class EntryButton extends Button {

        private static final String text = "<rank>";

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.WHITE_TERRACOTTA.get())).setDurability(5);
            itemBuilder.setName(LocaleUtil.tr(player, MenuConfig.GRANTS_REMOVE_MENU.CONFIRM_NAME));
            for (String text2 : LocaleUtil.tr(player, MenuConfig.GRANTS_REMOVE_MENU.CONFIRM_LORE)) {
                itemBuilder.addLoreLine(
                        text2.replace(text, GrantMenu.this.grant.getRank().getDisplayName()));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            GrantMenu.super
                    .api
                    .getGrantHandler()
                    .ungrant(
                            player.getUniqueId(),
                            GrantMenu.this.profile,
                            GrantMenu.this.grant,
                            GrantMenu.this.text);
            player.closeInventory();
        }

        public EntryButton() {}
    }

    class RankButton extends Button {

        private static final String text = "<rank>";

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.WHITE_TERRACOTTA.get())).setDurability(14);
            itemBuilder.setName(LocaleUtil.tr(player, MenuConfig.GRANTS_REMOVE_MENU.CANCEL_NAME));
            for (String text2 : LocaleUtil.tr(player, MenuConfig.GRANTS_REMOVE_MENU.CANCEL_LORE)) {
                itemBuilder.addLoreLine(
                        text2.replace(text, GrantMenu.this.grant.getRank().getDisplayName()));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MenuConfig.GRANT_MENU.GRANT_REMOVAL_CANCELLED)));
        }

        public RankButton() {}
    }
}
