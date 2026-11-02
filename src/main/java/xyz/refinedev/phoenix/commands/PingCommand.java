package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Register(name = "ping")
public class PingCommand {

    private static final String[] PING_FIELD_NAMES = new String[] {"ping", "e", "f"};

    private static Method getPingMethod;

    public static int getPing(Player player) {
        try {
            if (getPingMethod != null) {
                return (Integer) getPingMethod.invoke(player);
            }
            Object result = player.getClass().getMethod("getHandle").invoke(player);
            String[] texts = PING_FIELD_NAMES;
            int value = PING_FIELD_NAMES.length;
            for (int index = 0; index < value; index++) {
                String text = texts[index];
                try {
                    Field field;
                    (field = result.getClass().getDeclaredField(text)).setAccessible(true);
                    return field.getInt(result);
                } catch (NoSuchFieldException noSuchFieldException) {
                }
            }
        } catch (Exception exception) {
        }
        return -1;
    }

    @Command(name = "")
    public void execute(@Sender Player player, @Optional Player player2) {
        if (player2 != null) {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.GENERAL.PING_OTHER)
                                    .replace(
                                            "<player>",
                                            Phoenix.getInstance()
                                                    .getProfileHandler()
                                                    .getProfile(player2.getUniqueId())
                                                    .getNameWithColor())
                                    .replace("<ping>", this.formatPing(getPing(player2)))));
        } else {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.GENERAL.PING_SELF)
                                    .replace("<ping>", this.formatPing(getPing(player)))));
        }
    }

    public String formatPing(int value) {
        if (value < 100) {
            return Color.translate("&a" + value);
        } else {
            return value < 250 ? Color.translate("&6" + value) : Color.translate("&c" + value);
        }
    }

    static {
        try {
            Class[] classs = new Class[0];
            getPingMethod = Player.class.getMethod("getPing", classs);
        } catch (Exception exception) {
        }
    }
}
