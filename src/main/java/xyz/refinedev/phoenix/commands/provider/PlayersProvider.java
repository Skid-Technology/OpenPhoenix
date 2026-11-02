package xyz.refinedev.phoenix.commands.provider;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.actor.Actor;
import xyz.refinedev.lib.commands.bukkit.actor.BukkitActor;
import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.lib.commands.execution.argument.CommandArgument;
import xyz.refinedev.lib.commands.wrapper.binding.provider.Provider;
import xyz.refinedev.lib.commands.wrapper.binding.provider.ProviderType;
import xyz.refinedev.lib.commands.wrapper.suggestion.CommandSuggestion;
import xyz.refinedev.phoenix.Phoenix;

import java.util.ArrayList;
import java.util.List;

public class PlayersProvider extends Provider<String> {

    private final Phoenix plugin;

    private static final String PLAYERS_TEXT = "players";

    public PlayersProvider(Phoenix phoenix) {
        super(ProviderType.ARGUMENT, PLAYERS_TEXT);
        this.plugin = phoenix;
    }

    public String provide(CommandExecution commandExecution, CommandArgument commandArgument) {
        return commandArgument.getValue();
    }

    public List<String> suggest(
            CommandSuggestion commandSuggestion, CommandArgument commandArgument) {
        Actor actor;
        Player player =
                (actor = commandSuggestion.getActor()).isPlayer()
                        ? ((BukkitActor) actor).getPlayer()
                        : null;
        return new ArrayList<>(
                this.plugin.getVisibilityHandler().getAllVisibleToNamesOnNetwork(player));
    }
}
