package xyz.refinedev.phoenix.commands;

import org.bukkit.GameMode;
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

@Register(name = "gamemode", aliases = "gm")
@Requires("core.command.gamemode")
public class GamemodeCommand {

    @Command(name = "")
    public void execute(
            @Sender Player player,
            @Named("gamemode") String text,
            @Requires("core.command.gamemode.others") @Optional Player player2) {
        GameMode gameMode = null;
        switch (text) {
            case "survival":
            case "s":
            case "0":
                gameMode = GameMode.SURVIVAL;
                break;
            case "creative":
            case "c":
            case "1":
                gameMode = GameMode.CREATIVE;
                break;
            case "adventure":
            case "a":
            case "2":
                gameMode = GameMode.ADVENTURE;
                break;
            case "spectator":
            case "sp":
            case "3":
                if (!Phoenix.getInstance().is1point7()) {
                    gameMode = GameMode.SPECTATOR;
                }
        }
        if (gameMode == null) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.GAMEMODE.INVALID));
        }
        if (!player.hasPermission("core.command.gamemode." + gameMode.name().toLowerCase())) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.GAMEMODE.NO_PERMISSION));
        }
        if (player2 == null) {
            player.setGameMode(gameMode);
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.GAMEMODE.UPDATED)
                                    .replace("<gamemode>", gameMode.name())));
        } else {
            player2.setGameMode(gameMode);
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.GAMEMODE.OTHER)
                                    .replace("<player>", player2.getName())
                                    .replace("<gamemode>", gameMode.name())));
            player2.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player2, MessageConfig.BASIC.ADMIN.GAMEMODE.R_OTHER)
                                    .replace("<player>", player.getName())
                                    .replace("<gamemode>", gameMode.name())));
        }
    }
}
