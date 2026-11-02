package xyz.refinedev.phoenix.dialog.impl.grant;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.Dialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.MultiActionDialog;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.dialog.api.DialogResponse;
import xyz.refinedev.phoenix.menu.impl.grant.GrantConfirmationMenu;
import xyz.refinedev.phoenix.menu.impl.grant.GrantDurationMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GrantReasonDialog extends GrantStepDialog {

    private static final String text = "reason";

    private static final String textString = "custom";

    private final Map<UUID, GrantReasonDialog.Entry> entriesById = new ConcurrentHashMap<>();

    public GrantReasonDialog(Phoenix phoenix) {
        super(phoenix, "grant-reason");
    }

    public boolean matchesEnabled(
            Player player, IProfile profile, IRank rank, List<IScope> values, long value) {
        this.entriesById.put(
                player.getUniqueId(), new GrantReasonDialog.Entry(profile, rank, values, value));
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
        GrantReasonDialog.Entry entry;
        if ((entry = this.entriesById.get(player.getUniqueId())) == null) {
            return null;
        }
        List<String> values = MenuConfig.GRANT_MENU.REASON_MENU.AVAILABLE_REASONS;
        ArrayList<
                        xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button
                                .ActionButton>
                values2 = new ArrayList<>();
        for (String text : this.page(player, values)) {
            values2.add(
                    this.button(
                            this.formatString(player, text),
                            this.formatStringByLORE(player, text),
                            "reason",
                            text));
        }
        this.navigation(values2, player, values.size());
        values2.add(
                this.button(
                        this.formatString(player, "Custom Reason"),
                        this.formatStringByLORE(player, "Custom Reason"),
                        "custom",
                        "1"));
        values2.add(this.backButton(player));
        List<String> values3;
        (values3 =
                        this.summary(
                                GrantReasonDialog.Entry.getProfile(entry),
                                GrantReasonDialog.Entry.getRank(entry),
                                GrantReasonDialog.Entry.getScopes(entry),
                                GrantReasonDialog.Entry.getTime(entry),
                                null))
                .addAll(this.showing(player, values.size()));
        String text2 = LocaleUtil.tr(player, MenuConfig.GRANT_MENU.REASON_TITLE);
        return new MultiActionDialog(this.common(text2, values3), values2, this.close(player), 2);
    }

    @Override
    public void handle(Player player, DialogResponse dialogResponse) {
        GrantReasonDialog.Entry entry;
        if ((entry = this.entriesById.get(player.getUniqueId())) == null) {
            this.dismiss(player);
        } else if (!this.closed(player, dialogResponse)) {
            if (this.turned(player, dialogResponse)) {
                super.plugin
                        .getDialogHandler()
                        .matchesEnabled(
                                player,
                                this,
                                GrantReasonDialog.Entry.getProfile(entry).getUniqueId());
            } else if (!this.stepped(dialogResponse)) {
                if (!this.allowed(
                        player,
                        GrantReasonDialog.Entry.getProfile(entry),
                        GrantReasonDialog.Entry.getRank(entry))) {
                    this.dismiss(player);
                } else if (dialogResponse.formatString("custom", null, 1) != null) {
                    String text =
                            LocaleUtil.tr(player, MessageConfig.GRANT.GRANT_PROCEDURE_REASON_ASK);
                    GrantReasonDialog.Entry entry2 = entry;
                    this.ask(player, text, result -> this.sendMessage(player, entry2, result));
                } else {
                    String text2;
                    if ((text2 = dialogResponse.formatString("reason", null, 128)) == null) {
                        this.dismiss(player);
                    } else {
                        this.sendMessage(player, entry, text2);
                    }
                }
            } else {
                GrantDurationDialog grantDurationDialog;
                if ((grantDurationDialog =
                                        super.plugin
                                                .getDialogHandler()
                                                .findPhoenixDialog(GrantDurationDialog.class))
                                == null
                        || !grantDurationDialog.matchesEnabled(
                                player,
                                GrantReasonDialog.Entry.getProfile(entry),
                                GrantReasonDialog.Entry.getRank(entry),
                                GrantReasonDialog.Entry.getScopes(entry))) {
                    super.plugin
                            .getMenuHandler()
                            .openMenu(
                                    new GrantDurationMenu(
                                            GrantReasonDialog.Entry.getProfile(entry),
                                            GrantReasonDialog.Entry.getRank(entry),
                                            GrantReasonDialog.Entry.getScopes(entry)),
                                    player);
                }
            }
        }
    }

    private void sendMessage(Player player, GrantReasonDialog.Entry entry, String text) {
        player.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player, MessageConfig.GRANT.GRANT_PROCEDURE_REASON_SET)
                                .replace("<reason>", text)));
        GrantConfirmDialog grantConfirmDialog;
        if ((grantConfirmDialog =
                                super.plugin
                                        .getDialogHandler()
                                        .findPhoenixDialog(GrantConfirmDialog.class))
                        == null
                || !grantConfirmDialog.matchesEnabled(
                        player,
                        GrantReasonDialog.Entry.getProfile(entry),
                        GrantReasonDialog.Entry.getRank(entry),
                        GrantReasonDialog.Entry.getScopes(entry),
                        GrantReasonDialog.Entry.getTime(entry),
                        text)) {
            super.plugin
                    .getMenuHandler()
                    .openMenu(
                            new GrantConfirmationMenu(
                                    GrantReasonDialog.Entry.getProfile(entry),
                                    GrantReasonDialog.Entry.getRank(entry),
                                    GrantReasonDialog.Entry.getScopes(entry),
                                    GrantReasonDialog.Entry.getTime(entry),
                                    text),
                            player);
        }
    }

    private String formatString(Player player, String text) {
        return LocaleUtil.tr(player, MenuConfig.GRANT_MENU.REASON_MENU.NAME)
                .replace("<reason>", text);
    }

    private String formatStringByLORE(Player player, String text) {
        ArrayList<String> values = new ArrayList<>();
        Iterator<String> iterator2 =
                LocaleUtil.tr(player, MenuConfig.GRANT_MENU.REASON_MENU.LORE).iterator();
        while (iterator2.hasNext()) {
            String text2;
            if (!(text2 = ((String) iterator2.next()).replace("<reason>", text)).trim().isEmpty()) {
                values.add(text2);
            }
        }
        return this.tooltip(values);
    }

    static class Entry {

        private final IProfile profile;

        private final IRank rank;

        private final List<IScope> scopes;

        private final long time;

        public Entry(IProfile profile2, IRank rank2, List<IScope> scopes, long time) {
            this.profile = profile2;
            this.rank = rank2;
            this.scopes = scopes;
            this.time = time;
        }

        static IProfile getProfile(GrantReasonDialog.Entry entry) {
            return entry.profile;
        }

        static IRank getRank(GrantReasonDialog.Entry entry) {
            return entry.rank;
        }

        static List<IScope> getScopes(GrantReasonDialog.Entry entry) {
            return entry.scopes;
        }

        static long getTime(GrantReasonDialog.Entry entry) {
            return entry.time;
        }
    }
}
