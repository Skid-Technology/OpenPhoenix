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
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.input.BooleanInputControl;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.input.Input;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.nbt.NBTString;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.dialog.api.DialogResponse;
import xyz.refinedev.phoenix.dialog.api.PhoenixDialog;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatSnapshot;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.Proof;
import xyz.refinedev.phoenix.profile.punishment.ProofType;
import xyz.refinedev.phoenix.profile.punishment.Punishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadderType;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PunishmentUtil;
import xyz.refinedev.phoenix.utils.TimeConstants;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PunishConfirmDialog extends PhoenixDialog {

    private static final String LADDER_TEXT = "ladder";

    private static final String SILENT_TEXT = "silent";

    private final Phoenix plugin;

    private final boolean enabled;

    public PunishConfirmDialog(Phoenix phoenix, boolean enabled) {
        super(enabled ? "punish-confirm" : "punish");
        this.plugin = phoenix;
        this.enabled = enabled;
    }

    @Override
    public Dialog build(Player player) {
        IProfile profile;
        if ((profile = this.findProfile(player)) == null) {
            return null;
        }
        List<IPunishment> punishments = profile.getPunishments();
        ArrayList<ActionButton> values = new ArrayList<>();
        Iterator<IPunishmentLadder> iterator2 =
                this.plugin.getPunishmentLadderHandler().getSortedLadders().iterator();
        while (iterator2.hasNext()) {
            IPunishmentLadder punishmentLadder;
            IPunishmentLadderType punishmentLadderType;
            if (!(punishmentLadder = (IPunishmentLadder) iterator2.next()).isHidden()
                    && player.hasPermission("core.punish." + punishmentLadder.getId())
                    && (punishmentLadderType = punishmentLadder.getNextLadder(punishments))
                            != null) {
                values.add(
                        new ActionButton(
                                new CommonButtonData(
                                        Color.translateComponent(
                                                "&c" + punishmentLadder.getReason()),
                                        Color.translateComponent(
                                                this.formatString(
                                                        punishmentLadder,
                                                        punishmentLadderType,
                                                        punishments)),
                                        150),
                                new DynamicCustomAction(
                                        this.getActionId(),
                                        this.create(punishmentLadder.getId(), null))));
            }
        }
        if (values.isEmpty()) {
            return null;
        }
        boolean matches = SettingsConfig.SERVER.PUNISHMENTS.AUTO_SILENT_PUNISHMENT_LADDERS;
        List<Input> values2 =
                Collections.singletonList(
                        new Input(
                                "silent",
                                new BooleanInputControl(
                                        Color.translateComponent("&7Silent"),
                                        matches,
                                        "true",
                                        "false")));
        List<xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.body.DialogBody>
                values3 =
                        Collections.singletonList(
                                new PlainMessageDialogBody(
                                        new PlainMessage(
                                                Color.translateComponent(
                                                        "&7Punishing &f" + profile.getName()),
                                                200)));
        CommonDialogData commonDialogData =
                new CommonDialogData(
                        Color.translateComponent("&cPunish " + profile.getName()),
                        null,
                        true,
                        false,
                        DialogAction.CLOSE,
                        values3,
                        values2);
        int value = values.size() > 15 ? 3 : 2;
        return new MultiActionDialog(commonDialogData, values, null, value);
    }

    @Override
    public void handle(Player player, DialogResponse dialogResponse) {
        IProfile profile;
        if ((profile = this.findProfile(player)) != null) {
            String text;
            if ((text = dialogResponse.formatString("ladder", null, 64)) != null) {
                IPunishmentLadder punishmentLadder;
                if ((punishmentLadder = this.findPunishmentLadder(text)) != null) {
                    if (!punishmentLadder.isHidden()
                            && player.hasPermission("core.punish." + punishmentLadder.getId())) {
                        boolean matches =
                                dialogResponse.matchesEnabled(
                                        "silent",
                                        SettingsConfig.SERVER
                                                .PUNISHMENTS
                                                .AUTO_SILENT_PUNISHMENT_LADDERS);
                        if (this.enabled) {
                            this.sendMessage(player, profile, punishmentLadder, matches);
                        } else {
                            PhoenixDialog phoenixDialog;
                            if ((phoenixDialog =
                                            this.plugin
                                                    .getDialogHandler()
                                                    .findPhoenixDialog("punish-confirm"))
                                    != null) {
                                this.plugin
                                        .getDialogHandler()
                                        .matchesEnabled(
                                                player,
                                                phoenixDialog,
                                                profile.getUniqueId(),
                                                this.findDialog(
                                                        player,
                                                        profile,
                                                        punishmentLadder,
                                                        matches,
                                                        phoenixDialog));
                            }
                        }
                    }
                }
            }
        }
    }

    private void sendMessage(
            Player player, IProfile profile, IPunishmentLadder punishmentLadder, boolean matches) {
        List<IPunishment> punishments = profile.getPunishments();
        IPunishmentLadderType punishmentLadderType;
        if ((punishmentLadderType = punishmentLadder.getNextLadder(punishments)) != null) {
            if (profile.hasActivePunishment(punishmentLadderType.getType())
                    && punishmentLadderType.getType() != PunishmentType.KICK
                    && punishmentLadderType.getType() != PunishmentType.WARN) {
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.PUNISHMENT.ALREADY_PUNISHED)
                                        .replace("<player>", profile.getNameWithColorNoDisguise())
                                        .replace(
                                                "<type>",
                                                punishmentLadderType.getType().getPunished())));
            } else {
                this.plugin.getModUtilsHook().setFrozen(profile.getUniqueId(), false);
                Punishment punishment;
                (punishment = new Punishment()).setPunishmentType(punishmentLadderType.getType());
                punishment.setTarget(profile.getUniqueId());
                punishment.setAddress(profile.getCurrentAddress());
                punishment.setSilent(matches);
                punishment.setPermanent(punishmentLadderType.getDuration() == -5L);
                punishment.setDuration(punishmentLadderType.getDuration());
                punishment.setIp(
                        punishmentLadderType.isIp()
                                || punishmentLadderType.getType() == PunishmentType.BLACKLIST);
                punishment.setShadow(punishmentLadderType.isShadow());
                punishment.setIssuedOn(GlobalConfig.SERVER.NAME);
                punishment.setIssuedAt(System.currentTimeMillis());
                punishment.setReason(punishmentLadder.getReason());
                punishment.setIssuedBy(player.getUniqueId());
                IChatSnapshot chatSnapshot;
                if (punishmentLadder.isRequireChatSnapshot()
                        && (chatSnapshot =
                                        this.plugin
                                                .getChatSnapshotHandler()
                                                .createSnapshot(
                                                        player.getUniqueId(),
                                                        profile.getUniqueId()))
                                != null) {
                    punishment
                            .getProof()
                            .add(
                                    new Proof(
                                            ProofType.CHAT_SNAPSHOT,
                                            chatSnapshot.getNiceId(),
                                            player.getUniqueId()));
                }
                Punishment punishment2 = punishment;
                Tasks.runAsync(this.plugin, () -> PunishmentUtil.issue(player, punishment2));
            }
        }
    }

    private Dialog findDialog(
            Player player,
            IProfile profile,
            IPunishmentLadder punishmentLadder,
            boolean matches,
            PhoenixDialog phoenixDialog) {
        List<IPunishment> punishments = profile.getPunishments();
        IPunishmentLadderType punishmentLadderType;
        if ((punishmentLadderType = punishmentLadder.getNextLadder(punishments)) == null) {
            return null;
        }
        List<xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.body.DialogBody>
                values =
                        Collections.singletonList(
                                new PlainMessageDialogBody(
                                        new PlainMessage(
                                                Color.translateComponent(
                                                        "&f"
                                                                + profile.getName()
                                                                + "&7 will be &f"
                                                                + this.formatString(
                                                                        punishmentLadderType)
                                                                + "&7 for &f"
                                                                + punishmentLadder.getReason()
                                                                + "&7, "
                                                                + (matches
                                                                        ? "&8silently"
                                                                        : "&fpublicly")
                                                                + "&7."),
                                                200)));
        CommonDialogData commonDialogData =
                new CommonDialogData(
                        Color.translateComponent("&cAre you sure?"),
                        null,
                        true,
                        false,
                        DialogAction.CLOSE,
                        values,
                        Collections.emptyList());
        ActionButton actionButton =
                new ActionButton(
                        new CommonButtonData(Color.translateComponent("&aConfirm"), null, 150),
                        new DynamicCustomAction(
                                phoenixDialog.getActionId(),
                                this.create(punishmentLadder.getId(), matches)));
        ActionButton actionButton2 =
                new ActionButton(
                        new CommonButtonData(Color.translateComponent("&cCancel"), null, 150),
                        null);
        return new MultiActionDialog(
                commonDialogData, Collections.singletonList(actionButton), actionButton2, 1);
    }

    private String formatString(
            IPunishmentLadder punishmentLadder,
            IPunishmentLadderType punishmentLadderType,
            List<IPunishment> values) {
        return "&7Offence &f#"
                + (punishmentLadder.getPunishedTimes(values) + 1)
                + "\n&7Punishment: &f"
                + this.formatString(punishmentLadderType)
                + (punishmentLadder.isRequireChatSnapshot() ? "\n&8Attaches a chat snapshot" : "");
    }

    private String formatString(IPunishmentLadderType punishmentLadderType) {
        String text =
                punishmentLadderType.getDuration() == -5L
                        ? TimeConstants.PERMANENT
                        : DateUtils.formatTimeMillis(punishmentLadderType.getDuration());
        return (punishmentLadderType.isIp() ? "IP " : "")
                + (punishmentLadderType.isShadow() ? "Shadow " : "")
                + punishmentLadderType.getType().getNiceName()
                + " &7(&f"
                + text
                + "&7)";
    }

    private NBTCompound create(String text, Boolean matches) {
        NBTCompound nBTCompound;
        (nBTCompound = new NBTCompound()).setTag("ladder", new NBTString(text));
        if (matches != null) {
            nBTCompound.setTag("silent", new NBTString(String.valueOf(matches)));
        }
        return nBTCompound;
    }

    private IPunishmentLadder findPunishmentLadder(String text) {
        Iterator<IPunishmentLadder> iterator2 =
                this.plugin.getPunishmentLadderHandler().getSortedLadders().iterator();
        while (iterator2.hasNext()) {
            IPunishmentLadder punishmentLadder;
            if ((punishmentLadder = (IPunishmentLadder) iterator2.next())
                    .getId()
                    .equalsIgnoreCase(text)) {
                return punishmentLadder;
            }
        }
        return null;
    }

    private IProfile findProfile(Player player) {
        UUID uUID;
        return (uUID = this.plugin.getDialogHandler().getUniqueId(player)) == null
                ? null
                : this.plugin.getProfileHandler().getCachedProfile(uUID);
    }
}
