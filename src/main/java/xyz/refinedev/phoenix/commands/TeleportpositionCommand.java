package xyz.refinedev.phoenix.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.text.DecimalFormat;

@Register(name = "teleportposition", aliases = "tppos")
@Requires("core.command.teleportposition")
public class TeleportpositionCommand {

    private static final DecimalFormat decimalFormat = new DecimalFormat("0.0");

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(
            @Sender Player player,
            @Named("x") String text,
            @Named("y") String text2,
            @Named("z") String text3,
            @Named("yaw") @Optional String text4,
            @Named("pitch") @Optional String text5) {
        Location location = player.getLocation();
        double value = text.equals("~") ? location.getX() : Double.parseDouble(text);
        double value2 = text2.equals("~") ? location.getY() : Double.parseDouble(text2);
        double value3 = text3.equals("~") ? location.getZ() : Double.parseDouble(text3);
        if (text4 != null && text5 == null) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.TELEPORT.PITCH_REQUIRED), true);
        }
        if (text5 != null) {
            float value4 = text4.equals("~") ? location.getYaw() : Float.parseFloat(text4);
            float value5 = text5.equals("~") ? location.getPitch() : Float.parseFloat(text5);
            Location location2 =
                    new Location(player.getWorld(), value, value2, value3, value4, value5);
            this.plugin.getNmsHandler().getTeleportAPI().teleport(player, location2);
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(
                                            player,
                                            MessageConfig.BASIC
                                                    .ADMIN
                                                    .TELEPORT
                                                    .POSITION_WITH_YAW_PITCH)
                                    .replace("<x>", decimalFormat.format(value))
                                    .replace("<y>", decimalFormat.format(value2))
                                    .replace("<z>", decimalFormat.format(value3))
                                    .replace("<yaw>", decimalFormat.format(value4))
                                    .replace("<pitch>", decimalFormat.format(value5))));
        } else {
            Location location3 = new Location(player.getWorld(), value, value2, value3);
            this.plugin.getNmsHandler().getTeleportAPI().teleport(player, location3);
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.TELEPORT.POSITION)
                                    .replace("<x>", decimalFormat.format(value))
                                    .replace("<y>", decimalFormat.format(value2))
                                    .replace("<z>", decimalFormat.format(value3))));
        }
    }

    public TeleportpositionCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
