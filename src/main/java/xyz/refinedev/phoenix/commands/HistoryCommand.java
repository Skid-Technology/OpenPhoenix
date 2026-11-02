package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.menu.impl.HistoryMenu;
import xyz.refinedev.phoenix.profile.IProfile;

import java.util.List;

@Register(
        name = "history",
        aliases = {"hist", "check", "c"})
@Requires("core.command.history")
public class HistoryCommand {

    private final Phoenix plugin;

    @Command(name = "", async = true)
    public void execute(@Sender Player player, @Option({"disguise", "multiple"}) IProfile profile) {
        List<xyz.refinedev.phoenix.profile.punishment.IPunishment> punishments =
                profile.getPunishments();
        this.plugin.getMenuHandler().openMenuAsync(new HistoryMenu(profile, punishments), player);
    }

    public HistoryCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
