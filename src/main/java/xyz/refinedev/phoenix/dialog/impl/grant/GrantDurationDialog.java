package xyz.refinedev.phoenix.dialog.impl.grant;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.Dialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.MultiActionDialog;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.dialog.api.DialogResponse;
import xyz.refinedev.phoenix.menu.impl.GrantReasonMenu;
import xyz.refinedev.phoenix.menu.impl.grant.GrantScopeMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.Duration;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.TimeConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GrantDurationDialog extends GrantStepDialog {

    private static final String DURATION_TEXT = "duration";

    private static final String CUSTOM_TEXT = "custom";

    private static final long time = Long.MIN_VALUE;

    private static final GrantDurationDialog.Entry[] entries =
            new GrantDurationDialog.Entry[] {
                new GrantDurationDialog.Entry("5 Minutes", 300000L),
                new GrantDurationDialog.Entry("30 Minutes", 1800000L),
                new GrantDurationDialog.Entry("1 Hour", 3600000L),
                new GrantDurationDialog.Entry("1 Day", 86400000L),
                new GrantDurationDialog.Entry("3 Days", 259200000L),
                new GrantDurationDialog.Entry("30 Days", 2592000000L),
                new GrantDurationDialog.Entry(TimeConstants.PERMANENT, -5L)
            };

    private final Map<UUID, GrantDurationDialog.GrantContext> grantContextsById =
            new ConcurrentHashMap<>();

    public GrantDurationDialog(Phoenix phoenix) {
        super(phoenix, "grant-duration");
    }

    public boolean matchesEnabled(
            Player player, IProfile profile, IRank rank, List<IScope> values) {
        this.grantContextsById.put(
                player.getUniqueId(), new GrantDurationDialog.GrantContext(profile, rank, values));
        if (super.plugin.getDialogHandler().matchesEnabled(player, this, profile.getUniqueId())) {
            return true;
        }
        this.grantContextsById.remove(player.getUniqueId());
        return false;
    }

    @Override
    public void forget(UUID uUID) {
        this.grantContextsById.remove(uUID);
    }

    @Override
    public Dialog build(Player player) {
        GrantDurationDialog.GrantContext grantContext;
        if ((grantContext = this.grantContextsById.get(player.getUniqueId())) == null) {
            return null;
        }
        ArrayList<
                        xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button
                                .ActionButton>
                values = new ArrayList<>();
        GrantDurationDialog.Entry[] entrys = entries;
        int value = entries.length;
        for (int index = 0; index < value; index++) {
            GrantDurationDialog.Entry entry = entrys[index];
            values.add(
                    this.button(
                            this.formatString(player, GrantDurationDialog.Entry.getText(entry)),
                            this.formatStringByLORE(
                                    player, GrantDurationDialog.Entry.getText(entry)),
                            "duration",
                            String.valueOf(GrantDurationDialog.Entry.getTime(entry))));
        }
        values.add(
                this.button(
                        this.formatString(player, "Custom Duration"),
                        this.formatStringByLORE(player, "Custom Duration"),
                        "custom",
                        "1"));
        values.add(this.backButton(player));
        List<String> values2 =
                this.summary(
                        GrantDurationDialog.GrantContext.getProfile(grantContext),
                        GrantDurationDialog.GrantContext.getRank(grantContext),
                        GrantDurationDialog.GrantContext.getScopes(grantContext),
                        null,
                        null);
        String text = LocaleUtil.tr(player, MenuConfig.GRANT_MENU.DURATION_TITLE);
        return new MultiActionDialog(this.common(text, values2), values, this.close(player), 2);
    }

    @Override
    public void handle(Player player, DialogResponse dialogResponse) {
        GrantDurationDialog.GrantContext grantContext;
        if ((grantContext = this.grantContextsById.get(player.getUniqueId())) == null) {
            this.dismiss(player);
        } else if (!this.closed(player, dialogResponse)) {
            if (!this.stepped(dialogResponse)) {
                if (!this.allowed(
                        player,
                        GrantDurationDialog.GrantContext.getProfile(grantContext),
                        GrantDurationDialog.GrantContext.getRank(grantContext))) {
                    this.dismiss(player);
                } else if (dialogResponse.formatString("custom", null, 1) != null) {
                    this.applyPlayer(player, grantContext);
                } else {
                    long value;
                    if ((value =
                                    dialogResponse.calculateTime(
                                            "duration", Long.MIN_VALUE, -5L, Long.MAX_VALUE))
                            == Long.MIN_VALUE) {
                        this.dismiss(player);
                    } else {
                        this.sendMessage(
                                player, grantContext, value, this.formatSelectionDuration(value));
                    }
                }
            } else {
                GrantScopeDialog grantScopeDialog;
                if ((grantScopeDialog =
                                        super.plugin
                                                .getDialogHandler()
                                                .findPhoenixDialog(GrantScopeDialog.class))
                                == null
                        || !grantScopeDialog.matchesEnabled(
                                player,
                                GrantDurationDialog.GrantContext.getProfile(grantContext),
                                GrantDurationDialog.GrantContext.getRank(grantContext))) {
                    super.plugin
                            .getMenuHandler()
                            .openMenu(
                                    new GrantScopeMenu(
                                            GrantDurationDialog.GrantContext.getProfile(
                                                    grantContext),
                                            GrantDurationDialog.GrantContext.getRank(grantContext)),
                                    player);
                }
            }
        }
    }

    private void applyPlayer(Player player, GrantDurationDialog.GrantContext grantContext) {
        String text = LocaleUtil.tr(player, MessageConfig.GRANT.GRANT_PROCEDURE_DURATION_ASK);
        this.ask(
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
                        this.applyPlayer(player, grantContext);
                    } else {
                        this.sendMessage(
                                player,
                                grantContext,
                                duration.getTimeTime(),
                                this.formatSelectionDuration(duration.getTimeTime()));
                    }
                });
    }

    private void sendMessage(
            Player player, GrantDurationDialog.GrantContext grantContext, long value, String text) {
        player.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player, MessageConfig.GRANT.GRANT_PROCEDURE_DURATION_SET)
                                .replace("<duration>", text)));
        GrantReasonDialog grantReasonDialog;
        if ((grantReasonDialog =
                                super.plugin
                                        .getDialogHandler()
                                        .findPhoenixDialog(GrantReasonDialog.class))
                        == null
                || !grantReasonDialog.matchesEnabled(
                        player,
                        GrantDurationDialog.GrantContext.getProfile(grantContext),
                        GrantDurationDialog.GrantContext.getRank(grantContext),
                        GrantDurationDialog.GrantContext.getScopes(grantContext),
                        value)) {
            super.plugin
                    .getMenuHandler()
                    .openMenu(
                            new GrantReasonMenu(
                                    GrantDurationDialog.GrantContext.getProfile(grantContext),
                                    GrantDurationDialog.GrantContext.getRank(grantContext),
                                    GrantDurationDialog.GrantContext.getScopes(grantContext),
                                    value),
                            player);
        }
    }

    private String formatSelectionDuration(long value) {
        GrantDurationDialog.Entry[] entrys = entries;
        int value2 = entries.length;
        for (int index = 0; index < value2; index++) {
            GrantDurationDialog.Entry entry;
            if (GrantDurationDialog.Entry.getTime(entry = entrys[index]) == value) {
                return GrantDurationDialog.Entry.getText(entry);
            }
        }
        return super.niceDuration(value);
    }

    private String formatString(Player player, String text) {
        return LocaleUtil.tr(player, MenuConfig.GRANT_MENU.DURATION_MENU.NAME)
                .replace("<time>", text);
    }

    private String formatStringByLORE(Player player, String text) {
        ArrayList<String> values = new ArrayList<>();
        Iterator<String> iterator2 =
                LocaleUtil.tr(player, MenuConfig.GRANT_MENU.DURATION_MENU.LORE).iterator();
        while (iterator2.hasNext()) {
            String text2;
            if (!(text2 = ((String) iterator2.next()).replace("<time>", text)).trim().isEmpty()) {
                values.add(text2);
            }
        }
        return this.tooltip(values);
    }

    static class Entry {

        private final String text;

        private final long time;

        public Entry(String text, long time) {
            this.text = text;
            this.time = time;
        }

        static String getText(GrantDurationDialog.Entry entry) {
            return entry.text;
        }

        static long getTime(GrantDurationDialog.Entry entry) {
            return entry.time;
        }
    }

    static class GrantContext {

        private final IProfile profile;

        private final IRank rank;

        private final List<IScope> scopes;

        public GrantContext(IProfile profile2, IRank rank2, List<IScope> scopes) {
            this.profile = profile2;
            this.rank = rank2;
            this.scopes = scopes;
        }

        static IProfile getProfile(GrantDurationDialog.GrantContext grantContext) {
            return grantContext.profile;
        }

        static IRank getRank(GrantDurationDialog.GrantContext grantContext) {
            return grantContext.rank;
        }

        static List<IScope> getScopes(GrantDurationDialog.GrantContext grantContext) {
            return grantContext.scopes;
        }
    }
}
