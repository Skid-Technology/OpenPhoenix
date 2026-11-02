package xyz.refinedev.phoenix.menu.impl.rank;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.button.BooleanTraitButton;
import xyz.refinedev.phoenix.menu.api.button.IntegerTraitButton;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.scope.Scope;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.chat.ConversationUtil;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class RankPropertiesMenu extends PaginatedMenu {

    private final IRank rank;

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries;
        (entries = new HashMap()).put(0, new RankPropertiesMenu.NameButton());
        Integer size2 = entries.size();
        entries.put(
                size2,
                new RankPropertiesMenu.EntryButton(
                        "DisplayName",
                        false,
                        (result, result2) -> {
                            this.rank.setDisplayName(result2);
                            result.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(
                                                            result,
                                                            MessageConfig.RANK.DISPLAY_UPDATED)
                                                    .replace("<rank>", this.rank.getDisplayName())
                                                    .replace("<displayName>", result2)));
                        }));
        size2 = entries.size();
        entries.put(
                size2,
                new RankPropertiesMenu.EntryButton(
                        "DisplayName",
                        true,
                        (result, result2) -> {
                            this.rank.setDisplayNameModern(result2);
                            result.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(
                                                            result,
                                                            MessageConfig.RANK.DISPLAY_UPDATED)
                                                    .replace("<rank>", this.rank.getDisplayName())
                                                    .replace("<displayName>", result2)));
                        }));
        size2 = entries.size();
        entries.put(
                size2,
                new RankPropertiesMenu.EntryButton(
                        "Color",
                        false,
                        (result2, result3) -> {
                            this.rank.setColor(result3);
                            player.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(player, MessageConfig.RANK.COLOR_UPDATED)
                                                    .replace("<rank>", this.rank.getDisplayName())
                                                    .replace("<color>", result3)));
                        }));
        size2 = entries.size();
        entries.put(
                size2,
                new RankPropertiesMenu.EntryButton(
                        "Color",
                        true,
                        (result2, result3) -> {
                            this.rank.setColorModern(result3);
                            player.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(player, MessageConfig.RANK.COLOR_UPDATED)
                                                    .replace("<rank>", this.rank.getDisplayName())
                                                    .replace("<color>", result3)));
                        }));
        size2 = entries.size();
        entries.put(
                size2,
                new RankPropertiesMenu.EntryButton(
                        "Prefix",
                        false,
                        (result2, result3) -> {
                            this.rank.setPrefix(result3);
                            player.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(player, MessageConfig.RANK.PREFIX_UPDATED)
                                                    .replace("<rank>", this.rank.getDisplayName())
                                                    .replace(
                                                            "<prefix>",
                                                            this.rank.getPrefixLegacy())));
                        }));
        size2 = entries.size();
        entries.put(
                size2,
                new RankPropertiesMenu.EntryButton(
                        "Prefix",
                        true,
                        (result2, result3) -> {
                            this.rank.setPrefixModern(result3);
                            player.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(player, MessageConfig.RANK.PREFIX_UPDATED)
                                                    .replace("<rank>", this.rank.getDisplayName())
                                                    .replace(
                                                            "<prefix>",
                                                            this.rank.getPrefixModern())));
                        }));
        size2 = entries.size();
        entries.put(
                size2,
                new RankPropertiesMenu.EntryButton(
                        "PlayerList Prefix",
                        false,
                        (result2, result3) -> {
                            this.rank.setPlayerListPrefix(result3);
                            player.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(
                                                            player,
                                                            MessageConfig.RANK.PLAYERLIST_PREFIX)
                                                    .replace("<rank>", this.rank.getDisplayName())
                                                    .replace(
                                                            "<prefix>",
                                                            this.rank
                                                                    .getPlayerListPrefixLegacy())));
                        }));
        size2 = entries.size();
        entries.put(
                size2,
                new RankPropertiesMenu.EntryButton(
                        "PlayerList Prefix",
                        true,
                        (result2, result3) -> {
                            this.rank.setPlayerListPrefixModern(result3);
                            player.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(
                                                            player,
                                                            MessageConfig.RANK.PLAYERLIST_PREFIX)
                                                    .replace("<rank>", this.rank.getDisplayName())
                                                    .replace(
                                                            "<prefix>",
                                                            this.rank
                                                                    .getPlayerListPrefixModern())));
                        }));
        size2 = entries.size();
        entries.put(
                size2,
                new RankPropertiesMenu.EntryButton(
                        "Suffix",
                        false,
                        (result2, result3) -> {
                            this.rank.setSuffix(result3);
                            player.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(player, MessageConfig.RANK.SUFFIX_UPDATED)
                                                    .replace("<rank>", this.rank.getDisplayName())
                                                    .replace(
                                                            "<suffix>",
                                                            this.rank.getSuffixLegacy())));
                        }));
        size2 = entries.size();
        entries.put(
                size2,
                new RankPropertiesMenu.EntryButton(
                        "Suffix",
                        true,
                        (result2, result3) -> {
                            this.rank.setSuffixModern(result3);
                            player.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(player, MessageConfig.RANK.SUFFIX_UPDATED)
                                                    .replace("<rank>", this.rank.getDisplayName())
                                                    .replace(
                                                            "<suffix>",
                                                            this.rank.getSuffixModern())));
                        }));
        size2 = entries.size();
        IRank rank2 = this.rank;
        List values = Collections.singletonList("What should the priority be?");
        Material material = XMaterial.HOPPER.get();
        BiConsumer biConsumer = (BiConsumer<IRank, Integer>) IRank::setPriority;
        Function function = (Function<IRank, Integer>) IRank::getPriority;
        RankPropertiesMenu rankPropertiesMenu = this;
        entries.put(
                size2,
                new IntegerTraitButton<>(
                        rank2,
                        "Priority",
                        values,
                        material,
                        biConsumer,
                        function,
                        rankPropertiesMenu::initialize));
        size2 = entries.size();
        rank2 = this.rank;
        values = Arrays.asList("Should the rank be grantable", "by people who don't have it?");
        material = XMaterial.ANVIL.get();
        biConsumer = (BiConsumer<IRank, Boolean>) IRank::setGrantable;
        function = (Function<IRank, Boolean>) IRank::isGrantable;
        RankPropertiesMenu rankPropertiesMenu2 = this;
        entries.put(
                size2,
                new BooleanTraitButton<>(
                        rank2,
                        "Grantable",
                        values,
                        material,
                        biConsumer,
                        function,
                        rankPropertiesMenu2::initialize));
        size2 = entries.size();
        rank2 = this.rank;
        values = Collections.singletonList("Should the rank be staff?");
        material = XMaterial.LEVER.get();
        biConsumer = (BiConsumer<IRank, Boolean>) IRank::setStaff;
        function = (Function<IRank, Boolean>) IRank::isStaff;
        RankPropertiesMenu rankPropertiesMenu3 = this;
        entries.put(
                size2,
                new BooleanTraitButton<>(
                        rank2,
                        "Staff",
                        values,
                        material,
                        biConsumer,
                        function,
                        rankPropertiesMenu3::initialize));
        size2 = entries.size();
        rank2 = this.rank;
        values = Collections.singletonList("Should the rank be a subscription?");
        material = XMaterial.EMERALD.get();
        biConsumer = (BiConsumer<IRank, Boolean>) IRank::setSubscription;
        function = (Function<IRank, Boolean>) IRank::isSubscription;
        RankPropertiesMenu rankPropertiesMenu4 = this;
        entries.put(
                size2,
                new BooleanTraitButton<>(
                        rank2,
                        "Subscription",
                        values,
                        material,
                        biConsumer,
                        function,
                        rankPropertiesMenu4::initialize));
        size2 = entries.size();
        rank2 = this.rank;
        values = Collections.singletonList("Should the rank be visible?");
        material = XMaterial.SLIME_BALL.get();
        biConsumer = (BiConsumer<IRank, Boolean>) IRank::setVisible;
        function = (Function<IRank, Boolean>) IRank::isVisible;
        RankPropertiesMenu rankPropertiesMenu5 = this;
        entries.put(
                size2,
                new BooleanTraitButton<>(
                        rank2,
                        "Visible",
                        values,
                        material,
                        biConsumer,
                        function,
                        rankPropertiesMenu5::initialize));
        size2 = entries.size();
        rank2 = this.rank;
        values = Collections.singletonList("Should the rank be purchasable?");
        material = XMaterial.GOLD_INGOT.get();
        biConsumer = (BiConsumer<IRank, Boolean>) IRank::setPurchasable;
        function = (Function<IRank, Boolean>) IRank::isPurchasable;
        RankPropertiesMenu rankPropertiesMenu6 = this;
        entries.put(
                size2,
                new BooleanTraitButton<>(
                        rank2,
                        "Purchasable",
                        values,
                        material,
                        biConsumer,
                        function,
                        rankPropertiesMenu6::initialize));
        size2 = entries.size();
        rank2 = this.rank;
        values = Collections.singletonList("What should the price be?");
        material = XMaterial.GOLD_NUGGET.get();
        biConsumer = (BiConsumer<IRank, Integer>) IRank::setPrice;
        function = (Function<IRank, Integer>) IRank::getPrice;
        RankPropertiesMenu rankPropertiesMenu7 = this;
        entries.put(
                size2,
                new IntegerTraitButton<>(
                        rank2,
                        "Price",
                        values,
                        material,
                        biConsumer,
                        function,
                        rankPropertiesMenu7::initialize));
        entries.put(entries.size(), new RankPropertiesMenu.ScopesButton());
        entries.put(entries.size(), new RankPropertiesMenu.InheritanceButton());
        entries.put(entries.size(), new RankPropertiesMenu.PermissionsButton());
        return entries;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        this.backButton(entries, 4);
        return entries;
    }

    public void initialize() {
        super.api.getRankHandler().updateRank(this.rank);
    }

    @Override
    public String getTitle(Player player) {
        return "Rank Editor \u00bb " + this.rank.getName();
    }

    public RankPropertiesMenu(IRank rank2) {
        this.rank = rank2;
    }

    class EntryButton extends Button {

        private final String text;

        private final boolean enabled;

        private final BiConsumer<Player, String> biConsumer;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.WHITE_WOOL.get()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.RANK_EDITOR_MENU.NAME)
                                    .replace(
                                            "<type>",
                                            this.text + (this.enabled ? " (Modern)" : "")));
            List<String> values = LocaleUtil.tr(player, MenuConfig.RANK_EDITOR_MENU.LORE);
            ItemBuilder itemBuilder2 = itemBuilder;
            values.forEach(
                    result ->
                            itemBuilder2.addLoreLine(
                                    result.replace(
                                            "<type>",
                                            this.text + (this.enabled ? " (Modern)" : ""))));
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            String text2 =
                    "&aPlease input a " + this.text + (this.enabled ? " (Modern)" : "") + ".";
            ConversationUtil.ask(
                    RankPropertiesMenu.super.plugin,
                    player,
                    text2,
                    result -> {
                        this.biConsumer.accept(player, result);
                        RankPropertiesMenu.this.initialize();
                        RankPropertiesMenu.super
                                .plugin
                                .getMenuHandler()
                                .openMenu(
                                        new RankPropertiesMenu(RankPropertiesMenu.this.rank),
                                        player);
                    });
        }

        public EntryButton(String text, boolean enabled, BiConsumer<Player, String> biConsumer2) {
            this.text = text;
            this.enabled = enabled;
            this.biConsumer = biConsumer2;
        }
    }

    class InheritanceButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.PAPER.get()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.RANK_EDITOR_MENU.NAME)
                                    .replace("<type>", "Inheritance"));
            List<String> values = LocaleUtil.tr(player, MenuConfig.RANK_EDITOR_MENU.LORE);
            ItemBuilder itemBuilder2 = itemBuilder;
            values.forEach(
                    result -> itemBuilder2.addLoreLine(result.replace("<type>", "Inheritance")));
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType.isLeftClick()) {
                player.closeInventory();
                ConversationUtil.ask(
                        RankPropertiesMenu.super.plugin,
                        player,
                        "&aPlease input a rank.",
                        result -> {
                            player.chat(
                                    "/rank inheritance add "
                                            + RankPropertiesMenu.this.rank.getName()
                                            + " "
                                            + result);
                            RankPropertiesMenu.super
                                    .plugin
                                    .getMenuHandler()
                                    .openMenu(
                                            new RankPropertiesMenu(RankPropertiesMenu.this.rank),
                                            player);
                        });
            } else {
                RankPropertiesMenu.super
                        .plugin
                        .getMenuHandler()
                        .openMenu(new RankInheritanceMenu(RankPropertiesMenu.this.rank), player);
            }
        }

        public InheritanceButton() {}
    }

    class NameButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.OAK_SIGN.get()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.RANK_EDITOR_MENU.NAME)
                                    .replace("<type>", "Name"));
            List<String> values = LocaleUtil.tr(player, MenuConfig.RANK_EDITOR_MENU.LORE);
            ItemBuilder itemBuilder2 = itemBuilder;
            values.forEach(result -> itemBuilder2.addLoreLine(result.replace("<type>", "Name")));
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            ConversationUtil.ask(
                    RankPropertiesMenu.super.plugin,
                    player,
                    "&aPlease input a name.",
                    result -> {
                        RankPropertiesMenu.this.rank.setName(result);
                        player.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(player, MessageConfig.RANK.NAME_UPDATED)
                                                .replace(
                                                        "<rank>",
                                                        RankPropertiesMenu.this.rank
                                                                .getDisplayName())
                                                .replace("<name>", result)));
                        RankPropertiesMenu.this.initialize();
                        RankPropertiesMenu.super
                                .plugin
                                .getMenuHandler()
                                .openMenu(
                                        new RankPropertiesMenu(RankPropertiesMenu.this.rank),
                                        player);
                    });
        }

        public NameButton() {}
    }

    class PermissionsButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.CHEST.get()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.RANK_EDITOR_MENU.NAME)
                                    .replace("<type>", "Permissions"));
            List<String> values =
                    LocaleUtil.tr(player, MenuConfig.RANK_EDITOR_MENU.PERMISSIONS_LORE);
            ItemBuilder itemBuilder2 = itemBuilder;
            values.forEach(
                    result -> itemBuilder2.addLoreLine(result.replace("<type>", "Permissions")));
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType == ClickType.MIDDLE) {
                RankPropertiesMenu.super
                        .plugin
                        .getMenuHandler()
                        .openMenu(
                                new RankPermissionGroupsMenu(RankPropertiesMenu.this.rank), player);
            } else {
                if (clickType.isLeftClick()) {
                    player.closeInventory();
                    ConversationUtil.ask(
                            RankPropertiesMenu.super.plugin,
                            player,
                            "&aPlease input a permission.",
                            result -> {
                                player.chat(
                                        "/rank perm add "
                                                + RankPropertiesMenu.this.rank.getName()
                                                + " "
                                                + result
                                                + " global");
                                RankPropertiesMenu.super
                                        .plugin
                                        .getMenuHandler()
                                        .openMenu(
                                                new RankPropertiesMenu(
                                                        RankPropertiesMenu.this.rank),
                                                player);
                            });
                } else {
                    RankPropertiesMenu.super
                            .plugin
                            .getMenuHandler()
                            .openMenu(
                                    new RankPermissionsMenu(RankPropertiesMenu.this.rank), player);
                }
            }
        }

        public PermissionsButton() {}
    }

    class ScopesButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.COMPASS.get()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.RANK_EDITOR_MENU.NAME)
                                    .replace("<type>", "Scopes"));
            List<String> values = LocaleUtil.tr(player, MenuConfig.RANK_EDITOR_MENU.LORE);
            ItemBuilder itemBuilder2 = itemBuilder;
            values.forEach(result -> itemBuilder2.addLoreLine(result.replace("<type>", "Scopes")));
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            ConversationUtil.ask(
                    RankPropertiesMenu.super.plugin,
                    player,
                    "&aPlease input a scope.",
                    result -> {
                        if (!IScope.isValid(result)) {
                            player.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(player, MessageConfig.ERROR.INVALID_SCOPE)
                                                    .replace("<input>", result)));
                        } else {
                            Scope scope = new Scope(result);
                            if (clickType.isLeftClick()) {
                                if (RankPropertiesMenu.this.rank.hasScope(scope)) {
                                    player.sendMessage(
                                            Color.translate(
                                                    LocaleUtil.tr(
                                                                    player,
                                                                    MessageConfig.RANK
                                                                            .RANK_SCOPES
                                                                            .ALREADY_HAS)
                                                            .replace(
                                                                    "<scope>",
                                                                    scope.getDisplayName())
                                                            .replace(
                                                                    "<rank>",
                                                                    RankPropertiesMenu.this.rank
                                                                            .getDisplayName())));
                                    return;
                                }
                                player.sendMessage(
                                        Color.translate(
                                                LocaleUtil.tr(
                                                                player,
                                                                MessageConfig.RANK
                                                                        .RANK_SCOPES
                                                                        .ADDED)
                                                        .replace("<scope>", scope.getDisplayName())
                                                        .replace(
                                                                "<rank>",
                                                                RankPropertiesMenu.this.rank
                                                                        .getDisplayName())));
                                RankPropertiesMenu.this.rank.getScopes().add(scope);
                            } else {
                                if (!RankPropertiesMenu.this.rank.hasScope(scope)) {
                                    player.sendMessage(
                                            Color.translate(
                                                    LocaleUtil.tr(
                                                                    player,
                                                                    MessageConfig.RANK
                                                                            .RANK_SCOPES
                                                                            .DOESNT_HAVE)
                                                            .replace(
                                                                    "<scope>",
                                                                    scope.getDisplayName())
                                                            .replace(
                                                                    "<rank>",
                                                                    RankPropertiesMenu.this.rank
                                                                            .getDisplayName())));
                                    return;
                                }
                                player.sendMessage(
                                        Color.translate(
                                                LocaleUtil.tr(
                                                                player,
                                                                MessageConfig.RANK
                                                                        .RANK_SCOPES
                                                                        .REMOVED)
                                                        .replace("<scope>", scope.getDisplayName())
                                                        .replace(
                                                                "<rank>",
                                                                RankPropertiesMenu.this.rank
                                                                        .getDisplayName())));
                                RankPropertiesMenu.this.rank.getScopes().remove(scope);
                            }
                            RankPropertiesMenu.this.initialize();
                            RankPropertiesMenu.super
                                    .plugin
                                    .getMenuHandler()
                                    .openMenu(
                                            new RankPropertiesMenu(RankPropertiesMenu.this.rank),
                                            player);
                        }
                    });
        }

        public ScopesButton() {}
    }
}
