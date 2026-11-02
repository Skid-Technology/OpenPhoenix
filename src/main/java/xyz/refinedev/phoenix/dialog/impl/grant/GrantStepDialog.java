package xyz.refinedev.phoenix.dialog.impl.grant;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.CommonDialogData;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.DialogAction;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button.ActionButton;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.dialog.api.DialogResponse;
import xyz.refinedev.phoenix.dialog.impl.InputDialog;
import xyz.refinedev.phoenix.dialog.impl.StaffDialog;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.StringUtils;
import xyz.refinedev.phoenix.utils.TimeConstants;
import xyz.refinedev.phoenix.utils.chat.ConversationUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public abstract class GrantStepDialog extends StaffDialog {

    protected static final String KEY_BACK = "back";

    protected static final int MAX_VALUE_LENGTH = 128;

    protected final Phoenix plugin;

    private static final long time = -5L;

    protected GrantStepDialog(Phoenix phoenix, String text) {
        super(text);
        this.plugin = phoenix;
    }

    protected List<String> summary(
            IProfile profile, IRank rank, List<IScope> values, Long value, String text) {
        ArrayList<String> values2;
        (values2 = new ArrayList()).add("&7Granting &f" + profile.getName());
        if (rank != null) {
            values2.add("&7Rank: &f" + Color.translate(rank.getDisplayName()));
        }
        if (values != null) {
            values2.add("&7Scopes: &f" + StringUtils.join(IScope.toNiceScopes(values)));
        }
        if (value != null) {
            values2.add("&7Duration: &f" + this.niceDuration(value));
        }
        if (text != null) {
            values2.add("&7Reason: &f" + text);
        }
        return values2;
    }

    protected CommonDialogData common(String text, List<String> values) {
        return new CommonDialogData(
                Color.translateComponent(text),
                null,
                true,
                false,
                DialogAction.NONE,
                this.body(values),
                Collections.emptyList());
    }

    protected void ask(Player player, String text, Consumer<String> consumer) {
        InputDialog inputDialog =
                this.plugin.getDialogHandler().findPhoenixDialog(InputDialog.class);
        if (!SettingsConfig.MODERN.DIALOGS.INPUT
                || inputDialog == null
                || !inputDialog.matchesEnabled(player, text, consumer)) {
            this.dismiss(player);
            ConversationUtil.ask(this.plugin, player, text, consumer);
        }
    }

    protected ActionButton backButton(Player player) {
        return this.button(LocaleUtil.tr(player, MenuConfig.GENERAL.FORM.BACK), null, "back", "1");
    }

    protected boolean stepped(DialogResponse dialogResponse) {
        return dialogResponse.formatString("back", null, 1) != null;
    }

    protected boolean allowed(Player player, IProfile profile, IRank rank) {
        IProfile profile2;
        if ((profile2 = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                        == null
                || !this.plugin.getRankHandler().canGrant(profile2, rank)) {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.GRANT.CANT_GRANT_RANK)
                                    .replace("<rank>", Color.translate(rank.getDisplayName()))));
            return false;
        } else if (profile.hasRankAndCantUpgrade(rank)) {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.GRANT.ALREADY_HAS_RANK)
                                    .replace("<player>", profile.getNameWithColorNoDisguise())));
            return false;
        } else {
            return true;
        }
    }

    protected String niceDuration(long value) {
        return value == time ? TimeConstants.PERMANENT : DateUtils.formatTimeMillis(value);
    }

    protected String tooltip(List<String> values) {
        if (values != null && !values.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String text : values) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append('\n');
                }
                stringBuilder.append(text);
            }
            return stringBuilder.toString();
        } else {
            return null;
        }
    }
}
