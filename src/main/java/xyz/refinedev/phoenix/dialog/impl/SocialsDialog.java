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
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.input.BooleanInputControl;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.input.Input;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.input.TextInputControl;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.dialog.api.DialogResponse;
import xyz.refinedev.phoenix.dialog.api.PhoenixDialog;
import xyz.refinedev.phoenix.menu.impl.SocialMediaType;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;

import java.util.ArrayList;
import java.util.Collections;

public class SocialsDialog extends PhoenixDialog {

    private static final String text = "-privacy";

    private final Phoenix plugin;

    public SocialsDialog(Phoenix phoenix) {
        super("socials");
        this.plugin = phoenix;
    }

    @Override
    public Dialog build(Player player) {
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                == null) {
            return null;
        }
        ArrayList<Input> values2 = new ArrayList<>();
        ArrayList<String> values3;
        (values3 = new ArrayList()).add("&7Your socials. Empty a field to remove it.");
        SocialMediaType[] socialMediaTypes;
        int value = (socialMediaTypes = SocialMediaType.values()).length;
        for (int index = 0; index < value; index++) {
            SocialMediaType socialMediaType = socialMediaTypes[index];
            if (this.isPxDiscord(socialMediaType)) {
                values3.add(
                        socialMediaType.getColor()
                                + socialMediaType.getName()
                                + " &7is linked through &f/sync&7.");
            } else {
                String setting = profile.getSetting(socialMediaType.name(), "None");
                boolean matches =
                        profile.getSetting(socialMediaType.name() + "-privacy", "None")
                                .equalsIgnoreCase("Everyone");
                values2.add(
                        new Input(
                                socialMediaType.name(),
                                new TextInputControl(
                                        300,
                                        Color.translateComponent(
                                                socialMediaType.getColor()
                                                        + socialMediaType.getName()),
                                        true,
                                        setting.equalsIgnoreCase("None") ? "" : setting,
                                        256,
                                        null)));
                values2.add(
                        new Input(
                                inputKey(socialMediaType.name() + "-privacy"),
                                new BooleanInputControl(
                                        Color.translateComponent(
                                                "&7Show "
                                                        + socialMediaType.getName()
                                                        + " to everyone"),
                                        matches,
                                        "true",
                                        "false")));
            }
        }
        if (values2.isEmpty()) {
            return null;
        }
        CommonDialogData commonDialogData =
                new CommonDialogData(
                        Color.translateComponent("&eYour Socials"),
                        null,
                        true,
                        false,
                        DialogAction.CLOSE,
                        this.body(values3),
                        values2);
        ActionButton actionButton =
                new ActionButton(
                        new CommonButtonData(Color.translateComponent("&aSave"), null, 150),
                        new DynamicCustomAction(this.getActionId(), null));
        ActionButton actionButton2 =
                new ActionButton(
                        new CommonButtonData(Color.translateComponent("&cCancel"), null, 150),
                        null);
        return new MultiActionDialog(
                commonDialogData, Collections.singletonList(actionButton), actionButton2, 1);
    }

    @Override
    public void handle(Player player, DialogResponse dialogResponse) {
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                != null) {
            boolean matches2 = false;
            SocialMediaType[] socialMediaTypes;
            int value = (socialMediaTypes = SocialMediaType.values()).length;
            for (int index = 0; index < value; index++) {
                SocialMediaType socialMediaType = socialMediaTypes[index];
                if (!this.isPxDiscord(socialMediaType)) {
                    String setting = profile.getSetting(socialMediaType.name(), "None");
                    String text;
                    if ((text = dialogResponse.formatString(socialMediaType.name(), null, 256))
                            == null) {
                        if (!setting.equalsIgnoreCase("None")) {
                            profile.updateMetadata(
                                    socialMediaType.name() + "-privacy", "None", false);
                            profile.updateMetadata(socialMediaType.name(), "None", true);
                            matches2 = true;
                        }
                    } else if (!text.matches(socialMediaType.getRegex())) {
                        player.sendMessage(
                                Color.translate(
                                        this.plugin
                                                .getLocaleHandler()
                                                .get(
                                                        player,
                                                        "socials.invalid-url",
                                                        MessageConfig.SOCIALS.INVALID_URL)));
                    } else {
                        String text2 =
                                dialogResponse.matchesEnabled(
                                                inputKey(socialMediaType.name() + "-privacy"), true)
                                        ? "Everyone"
                                        : "None";
                        if (!text.equals(setting)
                                || !text2.equalsIgnoreCase(
                                        profile.getSetting(
                                                socialMediaType.name() + "-privacy", "None"))) {
                            profile.updateMetadata(
                                    socialMediaType.name() + "-privacy", text2, false);
                            profile.updateMetadata(socialMediaType.name(), text, true);
                            matches2 = true;
                            player.sendMessage(
                                    Color.translate(
                                            this.plugin
                                                    .getLocaleHandler()
                                                    .get(
                                                            player,
                                                            "socials.updated",
                                                            MessageConfig.SOCIALS.UPDATED)
                                                    .replace(
                                                            "<social>",
                                                            socialMediaType.getColor()
                                                                    + socialMediaType.getName())
                                                    .replace("<url>", text)));
                        }
                    }
                }
            }
            if (matches2) {
                profile.saveAndUpdate(false);
            }
        }
    }

    private boolean isPxDiscord(SocialMediaType socialMediaType) {
        return socialMediaType == SocialMediaType.DISCORD
                && Bukkit.getServer().getPluginManager().getPlugin("pxDiscord") != null;
    }
}
