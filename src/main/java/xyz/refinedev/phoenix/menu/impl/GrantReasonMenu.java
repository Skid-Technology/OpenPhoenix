package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.menu.impl.grant.GrantConfirmationMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.chat.ConversationUtil;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrantReasonMenu extends PaginatedMenu {

    private final IProfile profile;

    private final IRank rank;

    private final List<IScope> scopes;

    private final long time;

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.GRANT_MENU.REASON_TITLE);
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (String text : MenuConfig.GRANT_MENU.REASON_MENU.AVAILABLE_REASONS) {
            entries.put(entries.size(), new GrantReasonMenu.EntryButton(text));
        }
        return entries;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries;
        (entries = new HashMap()).put(4, new GrantReasonMenu.CustomReasonButton());
        return entries;
    }

    public GrantReasonMenu(IProfile profile2, IRank rank2, List<IScope> scopes, long time) {
        this.profile = profile2;
        this.rank = rank2;
        this.scopes = scopes;
        this.time = time;
    }

    class CustomReasonButton extends Button {

        private CustomReasonButton() {}

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.MAP.get()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.GRANT_MENU.REASON_MENU.NAME)
                                    .replace("<reason>", "Custom Reason"));
            List<String> values = LocaleUtil.tr(player, MenuConfig.GRANT_MENU.REASON_MENU.LORE);
            ItemBuilder itemBuilder2 = itemBuilder;
            values.forEach(
                    result ->
                            itemBuilder2.addLoreLine(result.replace("<reason>", "Custom Reason")));
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            Phoenix phoenix = GrantReasonMenu.super.plugin;
            String text = LocaleUtil.tr(player, MessageConfig.GRANT.GRANT_PROCEDURE_REASON_ASK);
            ConversationUtil.ask(
                    phoenix,
                    player,
                    text,
                    result -> {
                        player.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(
                                                        player,
                                                        MessageConfig.GRANT
                                                                .GRANT_PROCEDURE_REASON_SET)
                                                .replace("<reason>", result)));
                        GrantReasonMenu.super
                                .plugin
                                .getMenuHandler()
                                .openMenuSync(
                                        new GrantConfirmationMenu(
                                                GrantReasonMenu.this.profile,
                                                GrantReasonMenu.this.rank,
                                                GrantReasonMenu.this.scopes,
                                                GrantReasonMenu.this.time,
                                                result),
                                        player);
                    });
        }
    }

    class EntryButton extends Button {

        private final String text;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.WHITE_WOOL.get()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.GRANT_MENU.REASON_MENU.NAME)
                                    .replace("<reason>", this.text));
            List<String> values = LocaleUtil.tr(player, MenuConfig.GRANT_MENU.REASON_MENU.LORE);
            ItemBuilder itemBuilder2 = itemBuilder;
            values.forEach(
                    result -> itemBuilder2.addLoreLine(result.replace("<reason>", this.text)));
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.GRANT.GRANT_PROCEDURE_REASON_SET)
                                    .replace("<reason>", this.text)));
            GrantReasonMenu.super
                    .plugin
                    .getMenuHandler()
                    .openMenu(
                            new GrantConfirmationMenu(
                                    GrantReasonMenu.this.profile,
                                    GrantReasonMenu.this.rank,
                                    GrantReasonMenu.this.scopes,
                                    GrantReasonMenu.this.time,
                                    this.text),
                            player);
        }

        public EntryButton(String text) {
            this.text = text;
        }
    }
}
