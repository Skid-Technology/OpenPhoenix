package xyz.refinedev.phoenix.menu.impl.grant;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.StringUtils;
import xyz.refinedev.phoenix.utils.TimeConstants;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GrantConfirmationMenu extends Menu {

    private final IProfile profile;

    private final IRank rank;

    private final List<IScope> scopes;

    private final long time;

    private final String text;

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.GRANT_MENU.CONFIRM_TITLE);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (int index = 0; index < 45; index++) {
            if (Arrays.asList(10, 11, 12, 19, 20, 21, 28, 29, 30).contains(index)) {
                entries.put(index, new GrantConfirmationMenu.ConfirmButton());
            } else if (Arrays.asList(14, 15, 16, 23, 24, 25, 32, 33, 34).contains(index)) {
                entries.put(index, new GrantConfirmationMenu.EntryButton());
            } else {
                entries.put(index, Button.placeholder());
            }
        }
        return entries;
    }

    public GrantConfirmationMenu(
            IProfile profile2, IRank rank2, List<IScope> scopes, long time, String text) {
        this.profile = profile2;
        this.rank = rank2;
        this.scopes = scopes;
        this.time = time;
        this.text = text;
    }

    class ConfirmButton extends Button {

        private static final long timeTime = -5L;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.WHITE_TERRACOTTA.get())).setDurability(14);
            itemBuilder.setName(
                    LocaleUtil.tr(player, MenuConfig.GRANT_MENU.CONFIRM_MENU.CANCEL_NAME));
            Iterator<String> iterator2 =
                    LocaleUtil.tr(player, MenuConfig.GRANT_MENU.CONFIRM_MENU.CANCEL_LORE)
                            .iterator();
            while (iterator2.hasNext()) {
                String text2 =
                        ((String) iterator2.next())
                                .replace(
                                        "<player>",
                                        GrantConfirmationMenu.this.profile
                                                .getNameWithColorNoDisguise())
                                .replace("<rank>", GrantConfirmationMenu.this.rank.getDisplayName())
                                .replace("<reason>", GrantConfirmationMenu.this.text)
                                .replace(
                                        "<duration>",
                                        GrantConfirmationMenu.this.time == timeTime
                                                ? TimeConstants.PERMANENT
                                                : DateUtils.formatTimeMillis(
                                                        GrantConfirmationMenu.this.time))
                                .replace(
                                        "<scopes>",
                                        StringUtils.join(
                                                IScope.toNiceScopes(
                                                        GrantConfirmationMenu.this.scopes)));
                itemBuilder.addLoreLine(text2);
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
        }

        public ConfirmButton() {}
    }

    class EntryButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.WHITE_TERRACOTTA.get())).setDurability(5);
            itemBuilder.setName(
                    LocaleUtil.tr(player, MenuConfig.GRANT_MENU.CONFIRM_MENU.CONFRIM_NAME));
            Iterator<String> iterator2 =
                    LocaleUtil.tr(player, MenuConfig.GRANT_MENU.CONFIRM_MENU.CONFIRM_LORE)
                            .iterator();
            while (iterator2.hasNext()) {
                String text2 =
                        ((String) iterator2.next())
                                .replace(
                                        "<player>",
                                        GrantConfirmationMenu.this.profile
                                                .getNameWithColorNoDisguise())
                                .replace("<rank>", GrantConfirmationMenu.this.rank.getDisplayName())
                                .replace("<reason>", GrantConfirmationMenu.this.text)
                                .replace(
                                        "<duration>",
                                        GrantConfirmationMenu.this.time == -5L
                                                ? TimeConstants.PERMANENT
                                                : DateUtils.formatTimeMillis(
                                                        GrantConfirmationMenu.this.time))
                                .replace(
                                        "<scopes>",
                                        StringUtils.join(
                                                IScope.toNiceScopes(
                                                        GrantConfirmationMenu.this.scopes)));
                itemBuilder.addLoreLine(text2);
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            GrantConfirmationMenu.super
                    .api
                    .getGrantHandler()
                    .grant(
                            player.getUniqueId(),
                            GrantConfirmationMenu.this.profile,
                            GrantConfirmationMenu.this.time,
                            GrantConfirmationMenu.this.time == -5L,
                            GrantConfirmationMenu.this.text,
                            GrantConfirmationMenu.this.rank,
                            GrantConfirmationMenu.this.scopes);
        }

        public EntryButton() {}
    }
}
