package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "speed")
@Requires("core.command.speed")
public class SpeedCommand {

    @Command(name = "")
    public void execute(
            @Sender Player player,
            @Named("fly|walk") String text,
            @Named("amount") int value,
            @Optional Player player2) {
        if (!text.equalsIgnoreCase("fly") && !text.equalsIgnoreCase("walk")) {
            throw new ExitMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.SPEED.WALK_OR_FLY)));
        }
        if (value >= 1 && value <= 10) {
            Player player3;
            if ((player3 = player2 == null ? null : player2.getPlayer()) == null) {
                if (text.equalsIgnoreCase("fly")) {
                    player.setFlySpeed(value * 0.1F);
                    player.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.SPEED.FLY)
                                            .replace("<value>", String.valueOf(value))));
                } else if (text.equalsIgnoreCase("walk")) {
                    player.setWalkSpeed(value * 0.1F);
                    player.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.SPEED.WALK)
                                            .replace("<value>", String.valueOf(value))));
                }
            } else if (text.equalsIgnoreCase("fly")) {
                player3.setFlySpeed(value * 0.1F);
                player3.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player3, MessageConfig.BASIC.ADMIN.SPEED.FLY)
                                        .replace("<value>", String.valueOf(value))));
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.SPEED.FLY_OTHER)
                                        .replace("<player>", player3.getName())
                                        .replace("<value>", String.valueOf(value))));
            } else if (text.equalsIgnoreCase("walk")) {
                player3.setWalkSpeed(value * 0.1F);
                player3.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player3, MessageConfig.BASIC.ADMIN.SPEED.WALK)
                                        .replace("<value>", String.valueOf(value))));
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.SPEED.WALK_OTHER)
                                        .replace("<player>", player3.getName())
                                        .replace("<value>", String.valueOf(value))));
            }
        } else {
            throw new ExitMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.SPEED.LIMIT)
                                    .replace("<min>", "1")
                                    .replace("<max>", "10")));
        }
    }
}
