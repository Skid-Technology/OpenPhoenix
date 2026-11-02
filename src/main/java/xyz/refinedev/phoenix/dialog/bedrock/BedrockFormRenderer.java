package xyz.refinedev.phoenix.dialog.bedrock;

import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.form.ModalForm.Builder;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.form.util.FormBuilder;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.CommonDialogData;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.ConfirmationDialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.Dialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.MultiActionDialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.action.DynamicCustomAction;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.body.DialogBody;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.body.PlainMessageDialogBody;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button.ActionButton;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.input.BooleanInputControl;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.input.Input;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.input.InputControl;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.input.NumberRangeInputControl;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.input.NumberRangeInputControl.RangeInfo;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.input.SingleOptionInputControl;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.input.SingleOptionInputControl.Entry;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.input.TextInputControl;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.nbt.NBT;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.nbt.NBTFloat;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.nbt.NBTString;
import xyz.refinedev.lib.kyori.adventure.text.Component;
import xyz.refinedev.lib.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class BedrockFormRenderer implements IBedrockDialogRenderer {

    private static final LegacyComponentSerializer legacyComponentSerializer =
            LegacyComponentSerializer.legacySection();

    private final Phoenix plugin;

    public BedrockFormRenderer(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    @Override
    public boolean isBedrock(Player player) {
        try {
            return FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
        } catch (Throwable throwable) {
            return false;
        }
    }

    @Override
    public boolean show(Player player, Dialog dialog) {
        try {
            if (dialog instanceof ConfirmationDialog) {
                return this.matchesEnabled(player, (ConfirmationDialog) dialog);
            } else if (dialog instanceof MultiActionDialog) {
                MultiActionDialog multiActionDialog;
                return (multiActionDialog = (MultiActionDialog) dialog)
                                .getCommon()
                                .getInputs()
                                .isEmpty()
                        ? this.matchesEnabled(player, multiActionDialog)
                        : this.matchesEnabledByEnabled(player, multiActionDialog);
            } else {
                return false;
            }
        } catch (Throwable throwable) {
            this.plugin
                    .getLogger()
                    .log(Level.WARNING, "Failed to render a dialog as a Bedrock form.", throwable);
            return false;
        }
    }

    private boolean matchesEnabled(Player player, ConfirmationDialog confirmationDialog) {
        CommonDialogData common = confirmationDialog.getCommon();
        Builder builder2 =
                ((Builder) ModalForm.builder().title(this.serialize(common.getTitle())))
                        .content(this.formatString(common))
                        .button1(this.formatString(confirmationDialog.getYesButton()))
                        .button2(this.formatString(confirmationDialog.getNoButton()));
        Builder builder3 =
                (Builder)
                        builder2.validResultHandler(
                                result ->
                                        this.applyPlayer(
                                                player,
                                                result.clickedFirst()
                                                        ? confirmationDialog.getYesButton()
                                                        : confirmationDialog.getNoButton(),
                                                new NBTCompound()));
        this.applyPlayer(player, confirmationDialog, common, builder3);
        return this.matchesEnabled(player, builder3.build());
    }

    private boolean matchesEnabled(Player player, MultiActionDialog multiActionDialog) {
        CommonDialogData common = multiActionDialog.getCommon();
        List<ActionButton> values;
        if ((values = this.findList(multiActionDialog)).isEmpty()) {
            return false;
        }
        SimpleForm.Builder builder2 =
                ((SimpleForm.Builder) SimpleForm.builder().title(this.serialize(common.getTitle())))
                        .content(this.formatString(common));
        for (ActionButton actionButton : values) {
            builder2.button(this.formatString(actionButton));
        }
        List values2 = values;
        builder2.validResultHandler(
                result -> {
                    int value;
                    if ((value = result.clickedButtonId()) >= 0 && value < values2.size()) {
                        this.applyPlayer(
                                player, (ActionButton) values2.get(value), new NBTCompound());
                    }
                });
        this.applyPlayer(player, multiActionDialog, common, builder2);
        return this.matchesEnabled(player, builder2.build());
    }

    private boolean matchesEnabledByEnabled(Player player, MultiActionDialog multiActionDialog) {
        CommonDialogData common = multiActionDialog.getCommon();
        List<ActionButton> values;
        if ((values = this.findList(multiActionDialog)).isEmpty()) {
            return false;
        }
        CustomForm.Builder builder2 =
                (CustomForm.Builder) CustomForm.builder().title(this.serialize(common.getTitle()));
        ArrayList values2 = new ArrayList();
        int value = 0;
        Iterator<DialogBody> iterator2 = common.getBody().iterator();
        while (iterator2.hasNext()) {
            DialogBody dialogBody;
            if ((dialogBody = (DialogBody) iterator2.next()) instanceof PlainMessageDialogBody) {
                builder2.label(
                        this.serialize(
                                ((PlainMessageDialogBody) dialogBody).getMessage().getContents()));
                value++;
            }
        }
        for (Input input : common.getInputs()) {
            if (this.matchesEnabled(builder2, input, values2, value)) {
                value++;
            }
        }
        int value2 = -1;
        if (values.size() > 1) {
            ArrayList<String> values3 = new ArrayList<>();
            for (ActionButton actionButton : values) {
                values3.add(this.formatString(actionButton));
            }
            builder2.dropdown(this.serialize(common.getTitle()), values3, 0);
            value2 = value;
        }
        int value3 = value2;
        List values4 = values;
        builder2.validResultHandler(
                result -> {
                    NBTCompound nBTCompound = new NBTCompound();
                    Iterator iterator3 = values2.iterator();
                    while (iterator3.hasNext()) {
                        ((BedrockFormRenderer.Reader) iterator3.next()).read(result, nBTCompound);
                    }
                    ActionButton actionButton2 = (ActionButton) values4.get(0);
                    int value4;
                    if (value3 >= 0
                            && (value4 = result.asDropdown(value3)) >= 0
                            && value4 < values4.size()) {
                        actionButton2 = (ActionButton) values4.get(value4);
                    }
                    this.applyPlayer(player, actionButton2, nBTCompound);
                });
        this.applyPlayer(player, multiActionDialog, common, builder2);
        return this.matchesEnabled(player, builder2.build());
    }

    private boolean matchesEnabled(
            CustomForm.Builder builder,
            Input input2,
            List<BedrockFormRenderer.Reader> values,
            int value) {
        InputControl control = input2.getControl();
        String key = input2.getKey();
        if (control instanceof TextInputControl) {
            TextInputControl textInputControl = (TextInputControl) control;
            builder.input(
                    this.serialize(textInputControl.getLabel()),
                    "",
                    textInputControl.getInitial() == null ? "" : textInputControl.getInitial());
            values.add(
                    (result, result2) -> {
                        String result3;
                        if ((result3 = result.asInput(value)) != null) {
                            result2.setTag(key, new NBTString(result3));
                        }
                    });
            return true;
        }
        if (control instanceof BooleanInputControl) {
            BooleanInputControl booleanInputControl = (BooleanInputControl) control;
            builder.toggle(
                    this.serialize(booleanInputControl.getLabel()),
                    booleanInputControl.isInitial());
            values.add(
                    (result2, result3) ->
                            result3.setTag(
                                    key,
                                    new NBTString(
                                            result2.asToggle(value)
                                                    ? booleanInputControl.getOnTrue()
                                                    : booleanInputControl.getOnFalse())));
            return true;
        }
        if (control instanceof SingleOptionInputControl) {
            SingleOptionInputControl singleOptionInputControl;
            List<Entry> options =
                    (singleOptionInputControl = (SingleOptionInputControl) control).getOptions();
            ArrayList<String> values2 = new ArrayList<>();
            int value2 = 0;
            for (int index = 0; index < options.size(); index++) {
                values2.add(this.serialize(((Entry) options.get(index)).getDisplay()));
                if (((Entry) options.get(index)).isInitial()) {
                    value2 = index;
                }
            }
            builder.dropdown(this.serialize(singleOptionInputControl.getLabel()), values2, value2);
            values.add(
                    (result2, result3) -> {
                        int value3;
                        if ((value3 = result2.asDropdown(value)) >= 0 && value3 < options.size()) {
                            result3.setTag(
                                    key, new NBTString(((Entry) options.get(value3)).getId()));
                        }
                    });
            return true;
        } else if (control instanceof NumberRangeInputControl) {
            RangeInfo rangeInfo;
            float value4 =
                    (rangeInfo = ((NumberRangeInputControl) control).getRangeInfo()).getStep()
                                    == null
                            ? 1.0F
                            : rangeInfo.getStep();
            float value5 =
                    rangeInfo.getInitial() == null ? rangeInfo.getStart() : rangeInfo.getInitial();
            builder.slider(
                    this.serialize(((NumberRangeInputControl) control).getLabel()),
                    rangeInfo.getStart(),
                    rangeInfo.getEnd(),
                    value4,
                    value5);
            values.add(
                    (result, result2) -> result2.setTag(key, new NBTFloat(result.asSlider(value))));
            return true;
        } else {
            return false;
        }
    }

    private void applyPlayer(Player player, ActionButton actionButton, NBTCompound nBTCompound) {
        if (actionButton.getAction() instanceof DynamicCustomAction) {
            DynamicCustomAction dynamicCustomAction;
            if ((dynamicCustomAction = (DynamicCustomAction) actionButton.getAction())
                            .getAdditions()
                    != null) {
                for (java.util.Map.Entry entry :
                        dynamicCustomAction.getAdditions().getTags().entrySet()) {
                    nBTCompound.setTag((String) entry.getKey(), (NBT) entry.getValue());
                }
            }
            DynamicCustomAction dynamicCustomAction2 = dynamicCustomAction;
            Tasks.run(
                    () -> {
                        if (player.isOnline()) {
                            this.plugin
                                    .getDialogHandler()
                                    .applyPlayer(player, dynamicCustomAction2.getId(), nBTCompound);
                        }
                    });
        }
    }

    private void applyPlayer(
            Player player,
            Dialog dialog,
            CommonDialogData commonDialogData,
            FormBuilder<?, ?, ?> formBuilder) {
        if (!commonDialogData.isCanCloseWithEscape()) {
            formBuilder.closedOrInvalidResultHandler(
                    () -> {
                        Tasks.runLater(
                                () -> {
                                    if (player.isOnline()) {
                                        this.show(player, dialog);
                                    }
                                },
                                10L);
                    });
        }
    }

    private List<ActionButton> findList(MultiActionDialog multiActionDialog) {
        ArrayList<ActionButton> values = new ArrayList<>();
        Iterator<ActionButton> iterator2 = multiActionDialog.getActions().iterator();
        while (iterator2.hasNext()) {
            ActionButton actionButton;
            if ((actionButton = (ActionButton) iterator2.next()).getAction()
                    instanceof DynamicCustomAction) {
                values.add(actionButton);
            }
        }
        return values;
    }

    private String formatString(CommonDialogData commonDialogData) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<DialogBody> iterator2 = commonDialogData.getBody().iterator();
        while (iterator2.hasNext()) {
            DialogBody dialogBody;
            if ((dialogBody = (DialogBody) iterator2.next()) instanceof PlainMessageDialogBody) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append((char) (10));
                }
                stringBuilder.append(
                        this.serialize(
                                ((PlainMessageDialogBody) dialogBody).getMessage().getContents()));
            }
        }
        return stringBuilder.toString();
    }

    private String formatString(ActionButton actionButton) {
        return actionButton == null ? "" : this.serialize(actionButton.getButton().getLabel());
    }

    private String serialize(Component component) {
        return component == null ? "" : legacyComponentSerializer.serialize(component);
    }

    private boolean matchesEnabled(Player player, Form form) {
        try {
            FloodgateApi.getInstance().sendForm(player.getUniqueId(), form);
            return true;
        } catch (Throwable throwable) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to send a Bedrock form.", throwable);
            return false;
        }
    }

    interface Reader {

        void read(CustomFormResponse customFormResponse, NBTCompound nBTCompound);
    }
}
