package xyz.refinedev.phoenix.dialog.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.CommonDialogData;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.Dialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.DialogAction;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.MultiActionDialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.action.DynamicCustomAction;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button.ActionButton;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button.CommonButtonData;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.nbt.NBTString;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.dialog.api.DialogResponse;
import xyz.refinedev.phoenix.dialog.api.PhoenixDialog;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RulesDialog extends PhoenixDialog {

    public static final String text = "rules-accepted";

    private static final String textString = "accepted";

    private final Phoenix plugin;

    public RulesDialog(Phoenix phoenix) {
        super("rules");
        this.plugin = phoenix;
    }

    public boolean isRulesAccepted(IProfile profile) {
        if (!SettingsConfig.MODERN.DIALOGS.RULES.ENABLED) {
            return false;
        } else {
            return !SettingsConfig.MODERN.DIALOGS.RULES.SHOW_ONCE
                    ? true
                    : !profile.isMetadata("rules-accepted", false);
        }
    }

    @Override
    public Dialog build(Player player) {
        List<xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.body.DialogBody>
                values;
        if ((values = this.body(SettingsConfig.MODERN.DIALOGS.RULES.BODY)).isEmpty()) {
            return null;
        }
        CommonDialogData commonDialogData =
                new CommonDialogData(
                        Color.translateComponent(SettingsConfig.MODERN.DIALOGS.RULES.TITLE),
                        null,
                        false,
                        false,
                        DialogAction.CLOSE,
                        values,
                        Collections.emptyList());
        ArrayList<ActionButton> values2;
        (values2 = new ArrayList())
                .add(this.create(SettingsConfig.MODERN.DIALOGS.RULES.ACCEPT_BUTTON, true));
        values2.add(this.create(SettingsConfig.MODERN.DIALOGS.RULES.DECLINE_BUTTON, false));
        return new MultiActionDialog(commonDialogData, values2, null, 2);
    }

    @Override
    public void handle(Player player, DialogResponse dialogResponse) {
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                != null) {
            if (dialogResponse.matchesEnabled("accepted", false)) {
                profile.updateMetadata("rules-accepted", true, true);
            } else {
                for (String text : SettingsConfig.MODERN.DIALOGS.RULES.DECLINE_COMMANDS) {
                    Bukkit.dispatchCommand(
                            Bukkit.getConsoleSender(), text.replace("<player>", player.getName()));
                }
            }
        }
    }

    private ActionButton create(String text, boolean matches) {
        NBTCompound nBTCompound;
        (nBTCompound = new NBTCompound())
                .setTag("accepted", new NBTString(String.valueOf(matches)));
        return new ActionButton(
                new CommonButtonData(Color.translateComponent(text), null, 150),
                new DynamicCustomAction(this.getActionId(), nBTCompound));
    }
}
