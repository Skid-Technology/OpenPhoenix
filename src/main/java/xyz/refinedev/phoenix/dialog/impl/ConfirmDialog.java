package xyz.refinedev.phoenix.dialog.impl;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.CommonDialogData;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.ConfirmationDialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.Dialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.DialogAction;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.action.DynamicCustomAction;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.body.PlainMessage;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.body.PlainMessageDialogBody;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button.ActionButton;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button.CommonButtonData;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.nbt.NBTString;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.dialog.api.DialogResponse;
import xyz.refinedev.phoenix.dialog.api.PhoenixDialog;
import xyz.refinedev.phoenix.menu.api.TypeCallback;
import xyz.refinedev.phoenix.utils.Color;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConfirmDialog extends PhoenixDialog {

    private static final String VALUE_TEXT = "value";

    private final Phoenix plugin;

    private final Map<UUID, ConfirmDialog.Entry> entriesById = new ConcurrentHashMap<>();

    public ConfirmDialog(Phoenix phoenix) {
        super("confirm");
        this.plugin = phoenix;
    }

    public boolean matchesEnabled(Player player, String text, TypeCallback<Boolean> typeCallback) {
        this.entriesById.put(player.getUniqueId(), new ConfirmDialog.Entry(text, typeCallback));
        if (this.plugin.getDialogHandler().matchesEnabled(player, this, null)) {
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
        ConfirmDialog.Entry entry;
        if ((entry = this.entriesById.get(player.getUniqueId())) == null) {
            return null;
        }
        List<xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.body.DialogBody>
                values =
                        Collections.singletonList(
                                new PlainMessageDialogBody(
                                        new PlainMessage(
                                                Color.translateComponent(
                                                        ConfirmDialog.Entry.getText(entry)),
                                                200)));
        CommonDialogData commonDialogData =
                new CommonDialogData(
                        Color.translateComponent(ConfirmDialog.Entry.getText(entry)),
                        null,
                        true,
                        false,
                        DialogAction.CLOSE,
                        values,
                        Collections.emptyList());
        return new ConfirmationDialog(
                commonDialogData, this.create("&aConfirm", true), this.create("&cCancel", false));
    }

    @Override
    public void handle(Player player, DialogResponse dialogResponse) {
        ConfirmDialog.Entry entry;
        if ((entry = this.entriesById.remove(player.getUniqueId())) != null) {
            ConfirmDialog.Entry.getTypeCallback(entry)
                    .callback(dialogResponse.matchesEnabled("value", false));
        }
    }

    private ActionButton create(String text, boolean matches) {
        NBTCompound nBTCompound;
        (nBTCompound = new NBTCompound()).setTag("value", new NBTString(String.valueOf(matches)));
        return new ActionButton(
                new CommonButtonData(Color.translateComponent(text), null, 150),
                new DynamicCustomAction(this.getActionId(), nBTCompound));
    }

    static class Entry {

        private final String text;

        private final TypeCallback<Boolean> typeCallback;

        private Entry(String text, TypeCallback<Boolean> typeCallback2) {
            this.text = text;
            this.typeCallback = typeCallback2;
        }

        static String getText(ConfirmDialog.Entry entry) {
            return entry.text;
        }

        static TypeCallback<Boolean> getTypeCallback(ConfirmDialog.Entry entry) {
            return entry.typeCallback;
        }
    }
}
