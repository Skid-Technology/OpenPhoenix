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
import xyz.refinedev.phoenix.dialog.api.DialogResponse;
import xyz.refinedev.phoenix.dialog.api.PhoenixDialog;
import xyz.refinedev.phoenix.utils.Color;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class InputDialog extends PhoenixDialog {

    private static final String VALUE_TEXT = "value";

    private final Phoenix plugin;

    private final Map<UUID, InputDialog.Entry> entriesById = new ConcurrentHashMap<>();

    public InputDialog(Phoenix phoenix) {
        super("input");
        this.plugin = phoenix;
    }

    public boolean matchesEnabled(Player player, String text, Consumer<String> consumer) {
        return this.matchesEnabled(player, text, consumer, null);
    }

    public boolean matchesEnabled(
            Player player, String text, Consumer<String> consumer, Runnable runnable) {
        this.entriesById.put(player.getUniqueId(), new InputDialog.Entry(text, consumer, runnable));
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
        InputDialog.Entry entry;
        if ((entry = this.entriesById.get(player.getUniqueId())) == null) {
            return null;
        }
        List<Input> values =
                Collections.singletonList(
                        new Input(
                                "value",
                                new TextInputControl(
                                        200,
                                        Color.translateComponent("&7Value"),
                                        false,
                                        "",
                                        256,
                                        null)));
        List<xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.body.DialogBody>
                values2 =
                        Collections.singletonList(
                                new PlainMessageDialogBody(
                                        new PlainMessage(
                                                Color.translateComponent(
                                                        InputDialog.Entry.getText(entry)),
                                                200)));
        CommonDialogData commonDialogData =
                new CommonDialogData(
                        Color.translateComponent(InputDialog.Entry.getText(entry)),
                        null,
                        true,
                        false,
                        DialogAction.CLOSE,
                        values2,
                        values);
        ActionButton actionButton =
                new ActionButton(
                        new CommonButtonData(Color.translateComponent("&aConfirm"), null, 150),
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
        InputDialog.Entry entry;
        if ((entry = this.entriesById.remove(player.getUniqueId())) != null) {
            String text;
            if ((text = dialogResponse.formatString("value", null, 256)) != null) {
                if (!text.equalsIgnoreCase("cancel") && !text.equalsIgnoreCase("/no")) {
                    InputDialog.Entry.getConsumer(entry).accept(text);
                } else {
                    if (InputDialog.Entry.getRunnable(entry) != null) {
                        InputDialog.Entry.getRunnable(entry).run();
                    }
                }
            }
        }
    }

    static class Entry {

        private final String text;

        private final Consumer<String> consumer;

        private final Runnable runnable;

        private Entry(String text, Consumer<String> consumer2, Runnable runnable2) {
            this.text = text;
            this.consumer = consumer2;
            this.runnable = runnable2;
        }

        static String getText(InputDialog.Entry entry) {
            return entry.text;
        }

        static Runnable getRunnable(InputDialog.Entry entry) {
            return entry.runnable;
        }

        static Consumer<String> getConsumer(InputDialog.Entry entry) {
            return entry.consumer;
        }
    }
}
