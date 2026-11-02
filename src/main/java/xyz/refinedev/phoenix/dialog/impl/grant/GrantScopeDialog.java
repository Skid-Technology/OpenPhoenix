package xyz.refinedev.phoenix.dialog.impl.grant;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.Dialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.MultiActionDialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button.ActionButton;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.dialog.api.DialogResponse;
import xyz.refinedev.phoenix.menu.impl.grant.GrantDurationMenu;
import xyz.refinedev.phoenix.menu.impl.grant.GrantRankMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.scope.Scope;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GrantScopeDialog extends GrantStepDialog {

    private static final String SCOPE_TEXT = "scope";

    private static final String NEXT_TEXT = "next";

    private final Map<UUID, GrantScopeDialog.Entry> entriesById = new ConcurrentHashMap<>();

    public GrantScopeDialog(Phoenix phoenix) {
        super(phoenix, "grant-scope");
    }

    public boolean matchesEnabled(Player player, IProfile profile, IRank rank) {
        GrantScopeDialog.Entry entry;
        if ((entry = this.entriesById.get(player.getUniqueId())) == null
                || !GrantScopeDialog.Entry.matchesEnabled(entry, profile, rank)) {
            entry = new GrantScopeDialog.Entry(profile, rank, new ArrayList<>());
            this.entriesById.put(player.getUniqueId(), entry);
        }
        if (super.plugin.getDialogHandler().matchesEnabled(player, this, profile.getUniqueId())) {
            return true;
        }
        this.entriesById.remove(player.getUniqueId());
        return false;
    }

    @Override
    public void forget(UUID uUID) {
        this.entriesById.remove(uUID);
        this.forgetPage(uUID);
    }

    @Override
    public Dialog build(Player player) {
        GrantScopeDialog.Entry entry;
        if ((entry = this.entriesById.get(player.getUniqueId())) == null) {
            return null;
        }
        List<xyz.refinedev.phoenix.dialog.impl.grant.GrantScopeDialog.ScopeOption> list =
                this.getList();
        ArrayList<ActionButton> values;
        (values = new ArrayList())
                .add(
                        this.button(
                                LocaleUtil.tr(
                                        player, MenuConfig.GRANT_MENU.SCOPE_MENU.CONFIRM_NAME),
                                this.tooltip(
                                        this.findList(
                                                LocaleUtil.tr(
                                                        player,
                                                        MenuConfig.GRANT_MENU
                                                                .SCOPE_MENU
                                                                .CONFIRM_LORE),
                                                "<scopes>",
                                                this.formatString(player, entry))),
                                "next",
                                "1"));
        values.add(
                this.findActionButton(
                        player,
                        entry,
                        new GrantScopeDialog.ScopeOption(
                                Scope.scopeScope.getScope(),
                                LocaleUtil.tr(
                                        player, MenuConfig.GRANT_MENU.SCOPE_MENU.GLOBAL_NAME))));
        for (GrantScopeDialog.ScopeOption scopeOption : this.page(player, list)) {
            values.add(this.findActionButton(player, entry, scopeOption));
        }
        this.navigation(values, player, list.size());
        values.add(this.backButton(player));
        List<String> values2;
        (values2 =
                        this.summary(
                                GrantScopeDialog.Entry.getProfile(entry),
                                GrantScopeDialog.Entry.getRank(entry),
                                null,
                                null,
                                null))
                .add("&7Scopes: &f" + this.formatString(player, entry));
        values2.addAll(this.showing(player, list.size()));
        String text = LocaleUtil.tr(player, MenuConfig.GRANT_MENU.SCOPE_TITLE);
        return new MultiActionDialog(
                this.common(text, values2), values, this.close(player), values.size() > 15 ? 3 : 2);
    }

    @Override
    public void handle(Player player, DialogResponse dialogResponse) {
        GrantScopeDialog.Entry entry;
        if ((entry = this.entriesById.get(player.getUniqueId())) != null) {
            if (!this.closed(player, dialogResponse)) {
                if (this.turned(player, dialogResponse)) {
                    this.applyPlayer(player, entry);
                } else if (this.stepped(dialogResponse)) {
                    GrantRankDialog grantRankDialog;
                    if ((grantRankDialog =
                                            super.plugin
                                                    .getDialogHandler()
                                                    .findPhoenixDialog(GrantRankDialog.class))
                                    == null
                            || !grantRankDialog.matchesEnabled(
                                    player, GrantScopeDialog.Entry.getProfile(entry))) {
                        super.plugin
                                .getMenuHandler()
                                .openMenu(
                                        new GrantRankMenu(GrantScopeDialog.Entry.getProfile(entry)),
                                        player);
                    }
                } else {
                    String text;
                    if ((text = dialogResponse.formatString("scope", null, 128)) != null) {
                        this.applyEntry(entry, text);
                        this.applyPlayer(player, entry);
                    } else if (dialogResponse.formatString("next", null, 1) != null) {
                        if (!this.allowed(
                                player,
                                GrantScopeDialog.Entry.getProfile(entry),
                                GrantScopeDialog.Entry.getRank(entry))) {
                            this.dismiss(player);
                        } else {
                            if (GrantScopeDialog.Entry.getStrings(entry).isEmpty()) {
                                GrantScopeDialog.Entry.getStrings(entry)
                                        .add(Scope.scopeScope.getScope());
                            }
                            List<IScope> values =
                                    Scope.findListByScopeScope(
                                            GrantScopeDialog.Entry.getStrings(entry));
                            player.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(
                                                            player,
                                                            MessageConfig.GRANT
                                                                    .GRANT_PROCEDURE_SCOPES_SET)
                                                    .replace(
                                                            "<scopes>",
                                                            StringUtils.join(
                                                                    IScope.toNiceScopes(values)))));
                            GrantDurationDialog grantDurationDialog;
                            if ((grantDurationDialog =
                                                    super.plugin
                                                            .getDialogHandler()
                                                            .findPhoenixDialog(
                                                                    GrantDurationDialog.class))
                                            == null
                                    || !grantDurationDialog.matchesEnabled(
                                            player,
                                            GrantScopeDialog.Entry.getProfile(entry),
                                            GrantScopeDialog.Entry.getRank(entry),
                                            values)) {
                                super.plugin
                                        .getMenuHandler()
                                        .openMenu(
                                                new GrantDurationMenu(
                                                        GrantScopeDialog.Entry.getProfile(entry),
                                                        GrantScopeDialog.Entry.getRank(entry),
                                                        values),
                                                player);
                            }
                        }
                    }
                }
            }
        }
    }

    private void applyPlayer(Player player, GrantScopeDialog.Entry entry) {
        super.plugin
                .getDialogHandler()
                .matchesEnabled(
                        player, this, GrantScopeDialog.Entry.getProfile(entry).getUniqueId());
    }

    private void applyEntry(GrantScopeDialog.Entry entry, String text) {
        if (!GrantScopeDialog.Entry.getStrings(entry).remove(text)) {
            if (Scope.scopeScope.getScope().equals(text)
                    || GrantScopeDialog.Entry.getStrings(entry)
                            .contains(Scope.scopeScope.getScope())) {
                GrantScopeDialog.Entry.getStrings(entry).clear();
            }
            GrantScopeDialog.Entry.getStrings(entry).add(text);
        }
    }

    private ActionButton findActionButton(
            Player player, GrantScopeDialog.Entry entry, GrantScopeDialog.ScopeOption scopeOption) {
        boolean matches =
                Scope.scopeScope
                        .getScope()
                        .equals(GrantScopeDialog.ScopeOption.getText(scopeOption));
        boolean matches2 =
                GrantScopeDialog.Entry.getStrings(entry)
                        .contains(GrantScopeDialog.ScopeOption.getText(scopeOption));
        String text =
                matches
                        ? (matches2 ? "&c" : "&a")
                                + GrantScopeDialog.ScopeOption.getTextString(scopeOption)
                        : LocaleUtil.tr(player, MenuConfig.GRANT_MENU.SCOPE_MENU.SCOPE_NAME)
                                .replace("<color>", matches2 ? "&c" : "&a")
                                .replace(
                                        "<scope>",
                                        GrantScopeDialog.ScopeOption.getTextString(scopeOption));
        List<String> values =
                LocaleUtil.tr(
                        player,
                        matches
                                ? (matches2
                                        ? MenuConfig.GRANT_MENU.SCOPE_MENU.GLOBAL_REMOVE_LORE
                                        : MenuConfig.GRANT_MENU.SCOPE_MENU.GLOBAL_ADD_LORE)
                                : (matches2
                                        ? MenuConfig.GRANT_MENU.SCOPE_MENU.SCOPE_REMOVE_LORE
                                        : MenuConfig.GRANT_MENU.SCOPE_MENU.SCOPE_ADD_LORE));
        return this.button(
                text,
                this.tooltip(
                        this.findList(
                                values,
                                "<scope>",
                                GrantScopeDialog.ScopeOption.getTextString(scopeOption))),
                "scope",
                GrantScopeDialog.ScopeOption.getText(scopeOption));
    }

    private List<GrantScopeDialog.ScopeOption> getList() {
        ArrayList<GrantScopeDialog.ScopeOption> values = new ArrayList<>();
        for (String text : super.plugin.getNetworkHandler().getGroups()) {
            values.add(new GrantScopeDialog.ScopeOption("g:" + text, text + " Group"));
        }
        for (IServerData serverData : super.plugin.getNetworkHandler().getServers()) {
            values.add(
                    new GrantScopeDialog.ScopeOption(
                            "s:" + serverData.getServerName(), serverData.getDisplayName()));
        }
        return values;
    }

    private String formatString(Player player, GrantScopeDialog.Entry entry) {
        return GrantScopeDialog.Entry.getStrings(entry).isEmpty()
                ? LocaleUtil.tr(player, MenuConfig.GRANT_MENU.SCOPE_MENU.GLOBAL_NAME)
                : StringUtils.join(
                        IScope.toNiceScopes(
                                Scope.findListByScopeScope(
                                        GrantScopeDialog.Entry.getStrings(entry))));
    }

    private List<String> findList(List<String> values, String text, String text2) {
        ArrayList<String> values2 = new ArrayList<>();
        for (String text3 : values) {
            values2.add(text3.replace(text, text2));
        }
        return values2;
    }

    static class Entry {

        private final IProfile profile;

        private final IRank rank;

        private final List<String> strings;

        private boolean matchesEnabled(IProfile profile2, IRank rank2) {
            return this.profile.getUniqueId().equals(profile2.getUniqueId())
                    && this.rank.getName().equalsIgnoreCase(rank2.getName());
        }

        public Entry(IProfile profile2, IRank rank2, List<String> strings) {
            this.profile = profile2;
            this.rank = rank2;
            this.strings = strings;
        }

        static boolean matchesEnabled(GrantScopeDialog.Entry entry, IProfile profile, IRank rank) {
            return entry.matchesEnabled(profile, rank);
        }

        static IProfile getProfile(GrantScopeDialog.Entry entry) {
            return entry.profile;
        }

        static IRank getRank(GrantScopeDialog.Entry entry) {
            return entry.rank;
        }

        static List<String> getStrings(GrantScopeDialog.Entry entry) {
            return entry.strings;
        }
    }

    static class ScopeOption {

        private final String text;

        private final String textString;

        public ScopeOption(String text, String textString) {
            this.text = text;
            this.textString = textString;
        }

        static String getText(GrantScopeDialog.ScopeOption scopeOption) {
            return scopeOption.text;
        }

        static String getTextString(GrantScopeDialog.ScopeOption scopeOption) {
            return scopeOption.textString;
        }
    }
}
