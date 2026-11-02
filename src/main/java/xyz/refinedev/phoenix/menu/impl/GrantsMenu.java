package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.filter.FilterablePaginatedMenu;
import xyz.refinedev.phoenix.menu.api.pagination.filter.PageFilter;
import xyz.refinedev.phoenix.menu.api.pagination.filter.PageFilterButton;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.StringUtils;
import xyz.refinedev.phoenix.utils.TimeConstants;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.chat.ConversationUtil;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;
import xyz.refinedev.phoenix.utils.reflection.InventoryTitleUpdate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GrantsMenu extends FilterablePaginatedMenu<IGrant> {

    private final IProfile profile;

    private final List<IGrant> grants;

    public static String formatString(IGrant grant, String text) {
        text =
                text.replace("<rank>", grant.getRank().getDisplayName())
                        .replace("<target>", grant.getNiceTarget())
                        .replace("<duration>", grant.getNiceDuration())
                        .replace("<expire>", grant.getNiceExpire())
                        .replace("<scopes>", StringUtils.join(grant.getNiceActiveScopes()))
                        .replace(
                                "<willExpire>",
                                DateUtils.getDate(grant.getIssuedAt() + grant.getDuration()))
                        .replace("<issuedOn>", grant.getIssuedOn())
                        .replace("<issuedBy>", grant.getNiceIssuedBy())
                        .replace("<issuedAt>", DateUtils.getDate(grant.getIssuedAt()))
                        .replace("<reason>", grant.getReason());
        if (grant.getRemovedBy() != null) {
            text =
                    text.replace("<removedOn>", grant.getRemovedOn())
                            .replace("<removedBy>", grant.getNiceRemovedBy())
                            .replace("<removedAt>", DateUtils.getDate(grant.getRemovedAt()))
                            .replace("<removedReason>", grant.getRemovedReason());
        } else {
            text =
                    text.replace("<removedOn>", "Internal")
                            .replace("<removedBy>", GlobalConfig.SERVER.CONSOLE_NAME)
                            .replace("<removedReason>", TimeConstants.EXPIRED)
                            .replace(
                                    "<removedAt>",
                                    DateUtils.getDate(grant.getIssuedAt() + grant.getDuration()));
        }
        return text;
    }

    @Override
    public String getTitle(Player player) {
        return Color.translate(
                LocaleUtil.tr(player, MenuConfig.GRANTS_MENU.TITLE)
                        .replace("<player>", this.profile.getName()));
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        if (this.willHaveBackButton()) {
            entries.put(3, new PageFilterButton<>(this));
            this.backButton(entries, 5);
        } else {
            entries.put(4, new PageFilterButton<>(this));
        }
        return entries;
    }

    @Override
    public Map<Integer, Button> getFilteredButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        Iterator<IGrant> iterator2 = this.grants.iterator();
        while (iterator2.hasNext()) {
            IGrant grant;
            if ((grant = (IGrant) iterator2.next()).getRank() != null
                    && this.passesFilter(player, grant)) {
                entries.put(entries.size(), new GrantsMenu.EntryButton(grant));
            }
        }
        return entries;
    }

    @Override
    public List<PageFilter<IGrant>> generateFilters(Player player) {
        ArrayList<PageFilter<IGrant>> values;
        (values = new ArrayList()).add(new PageFilter<>("Active", IGrant::isActiveOnOneScope));
        values.add(
                new PageFilter<>(
                        "Expired",
                        result -> !result.isActiveOnOneScope() && result.getRemovedBy() == null));
        values.add(new PageFilter<>("Inactive", result -> !result.isActiveOnOneScope()));
        return values;
    }

    @Override
    public void onOpen(Player player) {
        if (!this.isOpenedByMenu()) {
            InventoryTitleUpdate.sendInventoryTitle(
                    player,
                    LocaleUtil.tr(player, MenuConfig.GRANTS_MENU.TITLE)
                            .replace("<player>", this.profile.getNameWithColorNoDisguise()));
        }
    }

    public GrantsMenu(IProfile profile2, List<IGrant> grants) {
        this.setAutoUpdate(true);
        this.profile = profile2;
        this.grants = grants;
    }

    class EntryButton extends Button {

        private final IGrant grant;

        @Override
        public ItemStack getItem(Player player) {
            IRank rank = this.grant.getRank();
            IProfile cachedProfile =
                    Phoenix.getInstance()
                            .getProfileHandler()
                            .getCachedProfile(player.getUniqueId());
            ItemBuilder itemBuilder = new ItemBuilder(XMaterial.WHITE_WOOL.get());
            if (this.grant.isActiveOnOneScope() && !this.grant.isActive()) {
                itemBuilder.setName(
                        LocaleUtil.tr(player, MenuConfig.GRANTS_MENU.ACTIVE_GRANT.NOT_ON_SCOPE_NAME)
                                .replace("<issuedAt>", DateUtils.getDate(this.grant.getIssuedAt()))
                                .replace("<rank>", this.grant.getRank().getDisplayName()));
                itemBuilder.setDurability(13);
            } else if (this.grant.isActive()) {
                itemBuilder.setName(
                        LocaleUtil.tr(player, MenuConfig.GRANTS_MENU.ACTIVE_GRANT.NAME)
                                .replace("<issuedAt>", DateUtils.getDate(this.grant.getIssuedAt()))
                                .replace("<rank>", this.grant.getRank().getDisplayName()));
                itemBuilder.setDurability(5);
            } else if (!this.grant.isActiveOnOneScope() && this.grant.getRemovedBy() == null) {
                itemBuilder.setName(
                        LocaleUtil.tr(player, MenuConfig.GRANTS_MENU.INACTIVE_GRANT.EXPIRED_NAME)
                                .replace("<issuedAt>", DateUtils.getDate(this.grant.getIssuedAt()))
                                .replace("<rank>", this.grant.getRank().getDisplayName()));
                itemBuilder.setDurability(8);
            } else {
                itemBuilder.setName(
                        LocaleUtil.tr(player, MenuConfig.GRANTS_MENU.INACTIVE_GRANT.NAME)
                                .replace("<issuedAt>", DateUtils.getDate(this.grant.getIssuedAt()))
                                .replace("<rank>", this.grant.getRank().getDisplayName()));
                itemBuilder.setDurability(14);
            }
            List values;
            if (this.grant.isActiveOnOneScope()) {
                if (this.grant.isPermanent()) {
                    values = LocaleUtil.tr(player, MenuConfig.GRANTS_MENU.ACTIVE_GRANT.PERMANENT);
                } else {
                    values = LocaleUtil.tr(player, MenuConfig.GRANTS_MENU.ACTIVE_GRANT.TIMED);
                }
            } else if (this.grant.getRemovedBy() != null) {
                values = LocaleUtil.tr(player, MenuConfig.GRANTS_MENU.INACTIVE_GRANT.REMOVED);
            } else {
                values = LocaleUtil.tr(player, MenuConfig.GRANTS_MENU.INACTIVE_GRANT.EXPIRED);
            }
            Iterator iterator2 = values.iterator();
            while (iterator2.hasNext()) {
                String text;
                if ((text = (String) iterator2.next()).contains("<niceScopes>")) {
                    for (IScope scope : this.grant.getActiveScopes()) {
                        String text2 = "";
                        if (scope.isServer()) {
                            text2 =
                                    GrantsMenu.super
                                                    .api
                                                    .getNetworkHandler()
                                                    .isServerOnline(scope.getServerName())
                                            ? "&a"
                                            : "&c";
                        }
                        itemBuilder.addLoreLine(
                                LocaleUtil.tr(player, MenuConfig.GRANTS_MENU.NICE_SCOPE)
                                        .replace("<scope>", text2 + scope.getDisplayName()));
                    }
                } else {
                    itemBuilder.addLoreLine(GrantsMenu.formatString(this.grant, text));
                }
            }
            if (this.grant.isActiveOnOneScope()) {
                if (Phoenix.getInstance().getRankHandler().canGrant(cachedProfile, rank)) {
                    for (String text3 :
                            LocaleUtil.tr(player, MenuConfig.GRANTS_MENU.ACTIVE_GRANT.CAN_REMOVE)) {
                        itemBuilder.addLoreLine(text3);
                    }
                } else {
                    for (String text4 :
                            LocaleUtil.tr(
                                    player, MenuConfig.GRANTS_MENU.ACTIVE_GRANT.CANT_REMOVE)) {
                        itemBuilder.addLoreLine(text4);
                    }
                }
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            IProfile cachedProfile =
                    GrantsMenu.super.api.getProfileHandler().getCachedProfile(player.getUniqueId());
            IRank rank;
            if (!(rank = GrantsMenu.super.api.getRankHandler().getRank(this.grant.getRankId()))
                    .isDefaultRank()) {
                if (this.grant.isActiveOnOneScope()) {
                    if (GrantsMenu.super.api.getRankHandler().canGrant(cachedProfile, rank)) {
                        player.closeInventory();
                        Phoenix phoenix = GrantsMenu.super.plugin;
                        String text =
                                LocaleUtil.tr(
                                        player,
                                        MessageConfig.GRANT.GRANT_REMOVE_PROCEDURE_REASON_ASK);
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
                                                                            .GRANT_REMOVE_PROCEDURE_REASON_SET)
                                                            .replace("<reason>", result)));
                                    GrantsMenu.super
                                            .plugin
                                            .getMenuHandler()
                                            .openMenuSync(
                                                    new GrantMenu(
                                                            this.grant,
                                                            result,
                                                            GrantsMenu.this.profile),
                                                    player);
                                });
                    }
                }
            }
        }

        public EntryButton(IGrant grant2) {
            this.grant = grant2;
        }
    }
}
