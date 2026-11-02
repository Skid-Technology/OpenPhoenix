package xyz.refinedev.phoenix.dialog.impl;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.CommonDialogData;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.Dialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.DialogAction;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.MultiActionDialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.action.DynamicCustomAction;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.body.PlainMessage;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.body.PlainMessageDialogBody;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button.ActionButton;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button.CommonButtonData;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.input.Input;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.input.TextInputControl;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.dialog.api.DialogResponse;
import xyz.refinedev.phoenix.dialog.api.PhoenixDialog;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.util.Collections;
import java.util.List;

public class AuthDialog extends PhoenixDialog {

    private static final String CODE_TEXT = "code";

    private final Phoenix plugin;

    public AuthDialog(Phoenix phoenix) {
        super("auth");
        this.plugin = phoenix;
    }

    @Override
    public Dialog build(Player player) {
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                        != null
                && profile.isTwoFactor()) {
            List<Input> values =
                    Collections.singletonList(
                            new Input(
                                    "code",
                                    new TextInputControl(
                                            150,
                                            Color.translateComponent("&7Code"),
                                            true,
                                            "",
                                            32,
                                            null)));
            List<
                            xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.body
                                    .DialogBody>
                    values2 =
                            Collections.singletonList(
                                    new PlainMessageDialogBody(
                                            new PlainMessage(
                                                    Color.translateComponent(
                                                            MessageConfig.AUTH.CODE_REQUIRED
                                                                    .replace("<type>", "")),
                                                    200)));
            CommonDialogData commonDialogData =
                    new CommonDialogData(
                            Color.translateComponent("&eTwo Factor Authentication"),
                            null,
                            false,
                            true,
                            DialogAction.CLOSE,
                            values2,
                            values);
            ActionButton actionButton =
                    new ActionButton(
                            new CommonButtonData(
                                    Color.translateComponent("&aAuthenticate"), null, 150),
                            new DynamicCustomAction(this.getActionId(), null));
            return new MultiActionDialog(
                    commonDialogData, Collections.singletonList(actionButton), null, 1);
        } else {
            return null;
        }
    }

    @Override
    public void handle(Player player, DialogResponse dialogResponse) {
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                        != null
                && profile.isTwoFactor()) {
            String text;
            if ((text = dialogResponse.formatString("code", null, 32)) == null) {
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.AUTH.CODE_REQUIRED)
                                        .replace("<type>", "")));
                this.plugin.getDialogHandler().matchesEnabled(player, this, null);
            } else {
                player.performCommand("auth " + text.replace(" ", ""));
                IProfile profile2 = profile;
                Tasks.runLater(
                        () -> {
                            if (player.isOnline() && profile2.isTwoFactor()) {
                                this.plugin.getDialogHandler().matchesEnabled(player, this, null);
                            }
                        },
                        20L);
            }
        }
    }
}
