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
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.scope.Scope;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.StringUtils;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GrantScopeMenu extends PaginatedMenu {

    private final IProfile profile;

    private final IRank rank;

    private final List<String> strings = new ArrayList<>();

    @Override
    public void onClose(Player player) {
        if (!this.isClosedByMenu()) {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MenuConfig.GRANT_MENU.GRANTING_CANCELLED)));
        }
    }

    @Override
    public String getTitle(Player player) {
        return Color.translate(LocaleUtil.tr(player, MenuConfig.GRANT_MENU.SCOPE_TITLE));
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        List<String> groups = super.api.getNetworkHandler().getGroups();
        groups.forEach(
                result -> entries.put(entries.size(), new GrantScopeMenu.GroupButton(result)));
        Set<IServerData> servers = super.api.getNetworkHandler().getServers();
        servers.forEach(
                result ->
                        entries.put(entries.size(), new GrantScopeMenu.ServerScopeButton(result)));
        return entries;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries;
        (entries = new HashMap()).put(3, new GrantScopeMenu.EntryButton());
        entries.put(5, new GrantScopeMenu.SelectedScopesButton());
        return entries;
    }

    public GrantScopeMenu(IProfile profile2, IRank rank2) {
        this.profile = profile2;
        this.rank = rank2;
    }

    class EntryButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            boolean matches = GrantScopeMenu.this.strings.contains(Scope.scopeScope.getScope());
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.WHITE_WOOL.get()))
                    .setName(
                            (matches ? "&c" : "&a")
                                    + LocaleUtil.tr(
                                            player, MenuConfig.GRANT_MENU.SCOPE_MENU.GLOBAL_NAME));
            if (matches) {
                itemBuilder.setDurability(4);
                itemBuilder.setLore(
                        LocaleUtil.tr(player, MenuConfig.GRANT_MENU.SCOPE_MENU.GLOBAL_REMOVE_LORE));
            } else {
                itemBuilder.setDurability(14);
                itemBuilder.setLore(
                        LocaleUtil.tr(player, MenuConfig.GRANT_MENU.SCOPE_MENU.GLOBAL_ADD_LORE));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (!GrantScopeMenu.this.strings.contains(Scope.scopeScope.getScope())) {
                GrantScopeMenu.this.strings.clear();
                GrantScopeMenu.this.strings.add(Scope.scopeScope.getScope());
            } else {
                GrantScopeMenu.this.strings.clear();
            }
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        public EntryButton() {}
    }

    class GroupButton extends Button {

        private final String text;

        @Override
        public ItemStack getItem(Player player) {
            boolean matches = GrantScopeMenu.this.strings.contains("g:" + this.text);
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.WHITE_WOOL.get()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.GRANT_MENU.SCOPE_MENU.SCOPE_NAME)
                                    .replace("<color>", matches ? "&c" : "&a")
                                    .replace("<scope>", this.text + " Group"));
            if (matches) {
                itemBuilder.setDurability(4);
                for (String text2 :
                        LocaleUtil.tr(player, MenuConfig.GRANT_MENU.SCOPE_MENU.SCOPE_REMOVE_LORE)) {
                    itemBuilder.addLoreLine(text2.replace("<scope>", this.text + " Group"));
                }
            } else {
                itemBuilder.setDurability(14);
                for (String text3 :
                        LocaleUtil.tr(player, MenuConfig.GRANT_MENU.SCOPE_MENU.SCOPE_ADD_LORE)) {
                    itemBuilder.addLoreLine(text3.replace("<scope>", this.text + " Group"));
                }
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            boolean matches = GrantScopeMenu.this.strings.contains("g:" + this.text);
            if (GrantScopeMenu.this.strings.contains(Scope.scopeScope.getScope())) {
                GrantScopeMenu.this.strings.clear();
                GrantScopeMenu.this.strings.add("g:" + this.text);
            } else if (!matches) {
                GrantScopeMenu.this.strings.add("g:" + this.text);
            } else {
                GrantScopeMenu.this.strings.remove("g:" + this.text);
            }
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        public GroupButton(String text) {
            this.text = text;
        }
    }

    class SelectedScopesButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.WHITE_WOOL.get()))
                    .setName(LocaleUtil.tr(player, MenuConfig.GRANT_MENU.SCOPE_MENU.CONFIRM_NAME));
            itemBuilder.setDurability(5);
            for (String text :
                    LocaleUtil.tr(player, MenuConfig.GRANT_MENU.SCOPE_MENU.CONFIRM_LORE)) {
                itemBuilder.addLoreLine(
                        text.replace("<scopes>", StringUtils.join(GrantScopeMenu.this.strings)));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (GrantScopeMenu.this.strings.isEmpty()) {
                GrantScopeMenu.this.strings.add(Scope.scopeScope.getScope());
            }
            List<IScope> values = Scope.findListByScopeScope(GrantScopeMenu.this.strings);
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.GRANT.GRANT_PROCEDURE_SCOPES_SET)
                                    .replace(
                                            "<scopes>",
                                            StringUtils.join(IScope.toNiceScopes(values)))));
            GrantScopeMenu.super
                    .plugin
                    .getMenuHandler()
                    .openMenu(
                            new GrantDurationMenu(
                                    GrantScopeMenu.this.profile, GrantScopeMenu.this.rank, values),
                            player);
        }

        public SelectedScopesButton() {}
    }

    class ServerScopeButton extends Button {

        private final IServerData serverData;

        @Override
        public ItemStack getItem(Player player) {
            boolean matches =
                    GrantScopeMenu.this.strings.contains("s:" + this.serverData.getServerName());
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.WHITE_WOOL.get()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.GRANT_MENU.SCOPE_MENU.SCOPE_NAME)
                                    .replace(
                                            "<color>",
                                            GrantScopeMenu.this.strings.contains(
                                                            this.serverData.getServerName())
                                                    ? "&c"
                                                    : "&a")
                                    .replace("<scope>", this.serverData.getDisplayName()));
            if (matches) {
                itemBuilder.setDurability(4);
                for (String text :
                        LocaleUtil.tr(player, MenuConfig.GRANT_MENU.SCOPE_MENU.SCOPE_REMOVE_LORE)) {
                    itemBuilder.addLoreLine(
                            text.replace("<scope>", this.serverData.getDisplayName()));
                }
            } else {
                itemBuilder.setDurability(14);
                for (String text2 :
                        LocaleUtil.tr(player, MenuConfig.GRANT_MENU.SCOPE_MENU.SCOPE_ADD_LORE)) {
                    itemBuilder.addLoreLine(
                            text2.replace("<scope>", this.serverData.getDisplayName()));
                }
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            boolean matches =
                    GrantScopeMenu.this.strings.contains("s:" + this.serverData.getServerName());
            if (GrantScopeMenu.this.strings.contains(Scope.scopeScope.getScope())) {
                GrantScopeMenu.this.strings.clear();
                GrantScopeMenu.this.strings.add("s:" + this.serverData.getServerName());
            } else if (!matches) {
                GrantScopeMenu.this.strings.add("s:" + this.serverData.getServerName());
            } else {
                GrantScopeMenu.this.strings.remove("s:" + this.serverData.getServerName());
            }
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        public ServerScopeButton(IServerData serverData2) {
            this.serverData = serverData2;
        }
    }
}
