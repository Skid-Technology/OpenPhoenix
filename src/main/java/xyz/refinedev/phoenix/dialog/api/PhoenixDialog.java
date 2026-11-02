package xyz.refinedev.phoenix.dialog.api;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.Dialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.action.DynamicCustomAction;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.body.DialogBody;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.body.PlainMessage;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.body.PlainMessageDialogBody;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button.ActionButton;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button.CommonButtonData;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.nbt.NBTString;
import xyz.refinedev.lib.com.github.retrooper.packetevents.resources.ResourceLocation;
import xyz.refinedev.lib.kyori.adventure.text.Component;
import xyz.refinedev.lib.kyori.adventure.text.TextComponent.Builder;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class PhoenixDialog {

    public static final String NAMESPACE = "phoenix";

    private static final int DEFAULT_BODY_WIDTH = 300;

    protected static final String KEY_CLOSE = "close";

    private final String key;

    public void forget(UUID uUID) {}

    public ResourceLocation getActionId() {
        return new ResourceLocation("phoenix", this.key);
    }

    public abstract Dialog build(Player player);

    public abstract void handle(Player player, DialogResponse dialogResponse);

    protected static String inputKey(String text) {
        StringBuilder stringBuilder = new StringBuilder(text.length());
        for (int index = 0; index < text.length(); index++) {
            char value = text.charAt(index);
            stringBuilder.append(
                    (value < 'a' || value > 'z')
                                    && (value < 'A' || value > 'Z')
                                    && (value < '0' || value > '9')
                            ? '_'
                            : value);
        }
        return stringBuilder.toString();
    }

    protected ActionButton button(String text, String text2, String text3, String text4) {
        NBTCompound nBTCompound;
        (nBTCompound = new NBTCompound()).setTag(text3, new NBTString(text4));
        return new ActionButton(
                new CommonButtonData(
                        Color.translateComponent(text),
                        text2 == null ? null : Color.translateComponent(text2),
                        200),
                new DynamicCustomAction(this.getActionId(), nBTCompound));
    }

    protected ActionButton close(Player player) {
        return this.close(player, LocaleUtil.tr(player, MenuConfig.GENERAL.FORM.CLOSE));
    }

    protected ActionButton close(Player player, String text) {
        return this.button(text, null, "close", "1");
    }

    protected boolean closed(Player player, DialogResponse dialogResponse) {
        if (dialogResponse.formatString("close", null, 1) == null) {
            return false;
        }
        this.dismiss(player);
        return true;
    }

    protected void dismiss(Player player) {
        Phoenix.getInstance().getDialogHandler().applyPlayer(player);
    }

    protected List<DialogBody> body(List<String> values) {
        return this.body(values, 300);
    }

    protected List<DialogBody> body(List<String> values, int value) {
        if (values.isEmpty()) {
            return Collections.emptyList();
        }
        Builder builder = Component.text();
        for (int index = 0; index < values.size(); index++) {
            if (index > 0) {
                builder.append(Component.newline());
            }
            builder.append(Color.translateComponent((String) values.get(index)));
        }
        return Collections.singletonList(
                new PlainMessageDialogBody(new PlainMessage(builder.build(), value)));
    }

    public String getKey() {
        return this.key;
    }

    public PhoenixDialog(String key) {
        this.key = key;
    }
}
