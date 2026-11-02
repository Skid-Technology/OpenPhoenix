package xyz.refinedev.phoenix.menu.impl.grant;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.menu.impl.GrantReasonMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.Duration;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.TimeConstants;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.chat.ConversationUtil;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrantDurationMenu extends PaginatedMenu {

    private final IProfile profile;

    private final IRank rank;

    private final List<IScope> scopes;

    int count = 0;

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.GRANT_MENU.DURATION_TITLE);
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries;
        (entries = new HashMap()).put(0, new GrantDurationMenu.EntryButton("5 Minutes", 300000L));
        entries.put(entries.size(), new GrantDurationMenu.EntryButton("30 Minutes", 1800000L));
        entries.put(entries.size(), new GrantDurationMenu.EntryButton("1 Hour", 3600000L));
        entries.put(entries.size(), new GrantDurationMenu.EntryButton("1 Day", 86400000L));
        entries.put(entries.size(), new GrantDurationMenu.EntryButton("3 Days", 259200000L));
        entries.put(entries.size(), new GrantDurationMenu.EntryButton("30 Days", 2592000000L));
        entries.put(entries.size(), new GrantDurationMenu.DurationInputButton());
        return entries;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries;
        (entries = new HashMap()).put(4, new GrantDurationMenu.CustomDurationButton());
        return entries;
    }

    public GrantDurationMenu(IProfile profile2, IRank rank2, List<IScope> scopes) {
        this.profile = profile2;
        this.rank = rank2;
        this.scopes = scopes;
    }

    class CustomDurationButton extends Button {

        private CustomDurationButton() {}

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.MAP.get()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.GRANT_MENU.DURATION_MENU.NAME)
                                    .replace("<time>", "Custom Duration"));
            List<String> values = LocaleUtil.tr(player, MenuConfig.GRANT_MENU.DURATION_MENU.LORE);
            ItemBuilder itemBuilder2 = itemBuilder;
            values.forEach(
                    result ->
                            itemBuilder2.addLoreLine(result.replace("<time>", "Custom Duration")));
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            this.applyPlayer(player);
        }

        private void applyPlayer(Player player) {
            Phoenix phoenix = GrantDurationMenu.super.plugin;
            String text = LocaleUtil.tr(player, MessageConfig.GRANT.GRANT_PROCEDURE_DURATION_ASK);
            ConversationUtil.ask(
                    phoenix,
                    player,
                    text,
                    result -> {
                        Duration duration;
                        if ((duration = Duration.of(result)) == null) {
                            player.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(
                                                    player,
                                                    MessageConfig.PUNISHMENT.INVALID_DURATION)));
                            this.applyPlayer(player);
                        } else {
                            player.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(
                                                            player,
                                                            MessageConfig.GRANT
                                                                    .GRANT_PROCEDURE_DURATION_SET)
                                                    .replace(
                                                            "<duration>",
                                                            duration.formatTimeMillis())));
                            GrantDurationMenu.super
                                    .plugin
                                    .getMenuHandler()
                                    .openMenuSync(
                                            new GrantReasonMenu(
                                                    GrantDurationMenu.this.profile,
                                                    GrantDurationMenu.this.rank,
                                                    GrantDurationMenu.this.scopes,
                                                    duration.getTimeTime()),
                                            player);
                        }
                    });
        }
    }

    class DurationInputButton extends Button {

        private static final long time = -5L;

        private DurationInputButton() {}

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.WHITE_WOOL.get()))
                    .setDurability(GrantDurationMenu.this.count);
            GrantDurationMenu.this.count++;
            itemBuilder.setName(
                    LocaleUtil.tr(player, MenuConfig.GRANT_MENU.DURATION_MENU.NAME)
                            .replace("<time>", TimeConstants.PERMANENT));
            List<String> values = LocaleUtil.tr(player, MenuConfig.GRANT_MENU.DURATION_MENU.LORE);
            ItemBuilder itemBuilder2 = itemBuilder;
            values.forEach(
                    result ->
                            itemBuilder2.addLoreLine(
                                    result.replace("<time>", TimeConstants.PERMANENT)));
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.GRANT.GRANT_PROCEDURE_DURATION_SET)
                                    .replace("<duration>", TimeConstants.PERMANENT)));
            GrantDurationMenu.super
                    .plugin
                    .getMenuHandler()
                    .openMenu(
                            new GrantReasonMenu(
                                    GrantDurationMenu.this.profile,
                                    GrantDurationMenu.this.rank,
                                    GrantDurationMenu.this.scopes,
                                    time),
                            player);
        }
    }

    class EntryButton extends Button {

        private final String text;

        private final long time;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.WHITE_WOOL.get()))
                    .setDurability(GrantDurationMenu.this.count);
            itemBuilder.setName(
                    LocaleUtil.tr(player, MenuConfig.GRANT_MENU.DURATION_MENU.NAME)
                            .replace("<time>", this.text));
            List<String> values = LocaleUtil.tr(player, MenuConfig.GRANT_MENU.DURATION_MENU.LORE);
            ItemBuilder itemBuilder2 = itemBuilder;
            values.forEach(result -> itemBuilder2.addLoreLine(result.replace("<time>", this.text)));
            GrantDurationMenu.this.count++;
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.GRANT.GRANT_PROCEDURE_DURATION_SET)
                                    .replace("<duration>", this.text)));
            GrantDurationMenu.super
                    .plugin
                    .getMenuHandler()
                    .openMenu(
                            new GrantReasonMenu(
                                    GrantDurationMenu.this.profile,
                                    GrantDurationMenu.this.rank,
                                    GrantDurationMenu.this.scopes,
                                    this.time),
                            player);
        }

        public EntryButton(String text, long time) {
            this.text = text;
            this.time = time;
        }
    }
}
