package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.profile.WatchlistUtils;

@Register(name = "watchlist")
@Requires("core.command.watchlist")
public class WatchlistCommand {

    private final Phoenix plugin;

    @Command(
            name = "",
            description =
                    "Players reported often enough to be worth a look (see /reports watchlist)",
            async = true)
    public void execute(@Sender Player player) {
        WatchlistUtils.applyPhoenix(this.plugin, this.plugin, player);
    }

    public WatchlistCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
