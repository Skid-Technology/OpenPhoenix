package xyz.refinedev.phoenix.modmode;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;

public class GamemodeAction {

    private final String text;

    @Command(name = "")
    public void execute(@Sender Player player, @Optional Player player2) {
        if (player2 == null) {
            player.performCommand("gamemode " + this.text);
        } else {
            player.performCommand("gamemode " + this.text + " " + player2.getName());
        }
    }

    public GamemodeAction(String text) {
        this.text = text;
    }
}
