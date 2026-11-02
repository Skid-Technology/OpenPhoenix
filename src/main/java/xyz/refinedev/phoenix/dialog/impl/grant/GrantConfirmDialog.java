package xyz.refinedev.phoenix.dialog.impl.grant;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.Dialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.MultiActionDialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button.ActionButton;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.dialog.api.DialogResponse;
import xyz.refinedev.phoenix.menu.impl.GrantReasonMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GrantConfirmDialog extends GrantStepDialog {

    private static final String text = "confirm";

    private final Map<UUID, GrantConfirmDialog.Entry> entriesById = new ConcurrentHashMap<>();

    private static final long time = -5L;

    public GrantConfirmDialog(Phoenix phoenix) {
        super(phoenix, "grant-confirm");
    }

    public boolean matchesEnabled(
            Player player,
            IProfile profile,
            IRank rank,
            List<IScope> values,
            long value,
            String text) {
        this.entriesById.put(
                player.getUniqueId(),
                new GrantConfirmDialog.Entry(profile, rank, values, value, text));
        if (super.plugin.getDialogHandler().matchesEnabled(player, this, profile.getUniqueId())) {
            return true;
        }
        this.entriesById.remove(player.getUniqueId());
        return false;
    }

    @Override
    public void forget(UUID uUID) {
        this.entriesById.remove(uUID);
    }

    @Override
    public Dialog build(Player player) {
        GrantConfirmDialog.Entry entry;
        if ((entry = this.entriesById.get(player.getUniqueId())) == null) {
            return null;
        }
        List<ActionButton> values =
                Arrays.asList(
                        this.button(
                                LocaleUtil.tr(
                                        player, MenuConfig.GRANT_MENU.CONFIRM_MENU.CONFRIM_NAME),
                                this.formatString(
                                        player,
                                        entry,
                                        MenuConfig.GRANT_MENU.CONFIRM_MENU.CONFIRM_LORE),
                                "confirm",
                                "1"),
                        this.backButton(player));
        ActionButton actionButton =
                this.close(
                        player,
                        LocaleUtil.tr(player, MenuConfig.GRANT_MENU.CONFIRM_MENU.CANCEL_NAME));
        List<String> values2 =
                this.summary(
                        GrantConfirmDialog.Entry.getProfile(entry),
                        GrantConfirmDialog.Entry.getRank(entry),
                        GrantConfirmDialog.Entry.getScopes(entry),
                        GrantConfirmDialog.Entry.getTime(entry),
                        GrantConfirmDialog.Entry.getText(entry));
        String text = LocaleUtil.tr(player, MenuConfig.GRANT_MENU.CONFIRM_TITLE);
        return new MultiActionDialog(this.common(text, values2), values, actionButton, 1);
    }

    @Override
    public void handle(Player player, DialogResponse dialogResponse) {
        GrantConfirmDialog.Entry entry;
        if ((entry = this.entriesById.get(player.getUniqueId())) == null) {
            this.dismiss(player);
        } else if (!this.closed(player, dialogResponse)) {
            if (!this.stepped(dialogResponse)) {
                if (dialogResponse.formatString("confirm", null, 1) != null
                        && this.allowed(
                                player,
                                GrantConfirmDialog.Entry.getProfile(entry),
                                GrantConfirmDialog.Entry.getRank(entry))) {
                    this.entriesById.remove(player.getUniqueId());
                    this.dismiss(player);
                    super.plugin
                            .getGrantHandler()
                            .grant(
                                    player.getUniqueId(),
                                    GrantConfirmDialog.Entry.getProfile(entry),
                                    GrantConfirmDialog.Entry.getTime(entry),
                                    GrantConfirmDialog.Entry.getTime(entry) == time,
                                    GrantConfirmDialog.Entry.getText(entry),
                                    GrantConfirmDialog.Entry.getRank(entry),
                                    GrantConfirmDialog.Entry.getScopes(entry));
                } else {
                    this.dismiss(player);
                }
            } else {
                GrantReasonDialog grantReasonDialog;
                if ((grantReasonDialog =
                                        super.plugin
                                                .getDialogHandler()
                                                .findPhoenixDialog(GrantReasonDialog.class))
                                == null
                        || !grantReasonDialog.matchesEnabled(
                                player,
                                GrantConfirmDialog.Entry.getProfile(entry),
                                GrantConfirmDialog.Entry.getRank(entry),
                                GrantConfirmDialog.Entry.getScopes(entry),
                                GrantConfirmDialog.Entry.getTime(entry))) {
                    super.plugin
                            .getMenuHandler()
                            .openMenu(
                                    new GrantReasonMenu(
                                            GrantConfirmDialog.Entry.getProfile(entry),
                                            GrantConfirmDialog.Entry.getRank(entry),
                                            GrantConfirmDialog.Entry.getScopes(entry),
                                            GrantConfirmDialog.Entry.getTime(entry)),
                                    player);
                }
            }
        }
    }

    private String formatString(
            Player player, GrantConfirmDialog.Entry entry, List<String> values) {
        ArrayList<String> values2 = new ArrayList<>();
        Iterator<String> iterator2 = LocaleUtil.tr(player, values).iterator();
        while (iterator2.hasNext()) {
            String text;
            if (!(text =
                            ((String) iterator2.next())
                                    .replace(
                                            "<player>",
                                            GrantConfirmDialog.Entry.getProfile(entry)
                                                    .getNameWithColorNoDisguise())
                                    .replace(
                                            "<rank>",
                                            GrantConfirmDialog.Entry.getRank(entry)
                                                    .getDisplayName())
                                    .replace("<reason>", GrantConfirmDialog.Entry.getText(entry))
                                    .replace(
                                            "<duration>",
                                            this.niceDuration(
                                                    GrantConfirmDialog.Entry.getTime(entry)))
                                    .replace(
                                            "<scopes>",
                                            StringUtils.join(
                                                    IScope.toNiceScopes(
                                                            GrantConfirmDialog.Entry.getScopes(
                                                                    entry)))))
                    .trim()
                    .isEmpty()) {
                values2.add(text);
            }
        }
        return this.tooltip(values2);
    }

    static class Entry {

        private final IProfile profile;

        private final IRank rank;

        private final List<IScope> scopes;

        private final long time;

        private final String text;

        public Entry(IProfile profile2, IRank rank2, List<IScope> scopes, long time, String text) {
            this.profile = profile2;
            this.rank = rank2;
            this.scopes = scopes;
            this.time = time;
            this.text = text;
        }

        static IProfile getProfile(GrantConfirmDialog.Entry entry) {
            return entry.profile;
        }

        static IRank getRank(GrantConfirmDialog.Entry entry) {
            return entry.rank;
        }

        static List<IScope> getScopes(GrantConfirmDialog.Entry entry) {
            return entry.scopes;
        }

        static long getTime(GrantConfirmDialog.Entry entry) {
            return entry.time;
        }

        static String getText(GrantConfirmDialog.Entry entry) {
            return entry.text;
        }
    }
}
