package xyz.refinedev.phoenix.commands.provider;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.actor.Actor;
import xyz.refinedev.lib.commands.actor.PlayerActor;
import xyz.refinedev.lib.commands.bukkit.actor.BukkitActor;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.lib.commands.execution.argument.CommandArgument;
import xyz.refinedev.lib.commands.wrapper.CommandParameter;
import xyz.refinedev.lib.commands.wrapper.binding.provider.Provider;
import xyz.refinedev.lib.commands.wrapper.binding.provider.ProviderType;
import xyz.refinedev.lib.commands.wrapper.suggestion.CommandSuggestion;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlayerProvider extends Provider<Player> {

    private final Phoenix plugin;

    public PlayerProvider(Phoenix phoenix) {
        super(ProviderType.ARGUMENT, "player");
        this.plugin = phoenix;
    }

    public Player provide(CommandExecution commandExecution, CommandArgument commandArgument) {
        String value = commandArgument.getValue();
        Actor actor = commandExecution.getActor();
        CommandParameter parameter = commandArgument.getParameter();
        boolean player = actor.isPlayer();
        if (BukkitAPI.isUuid(value)) {
            Player player2;
            if ((player2 = Bukkit.getPlayer(UUID.fromString(value))) == null
                    || !this.matchesEnabled(actor, player2, player)) {
                throw new ExitMessage(
                        LocaleUtil.tr(commandExecution, MessageConfig.ERROR.PLAYER_NOT_ONLINE)
                                .replace("<player>", Color.escape(value)),
                        true);
            } else if (!this.isOnline(actor, parameter, player2, value, player)) {
                throw new ExitMessage(
                        LocaleUtil.tr(commandExecution, MessageConfig.ERROR.PLAYER_NOT_ONLINE)
                                .replace("<player>", Color.escape(value)),
                        true);
            } else {
                return player2;
            }
        } else {
            Collection onlinePlayers = Bukkit.getOnlinePlayers();
            Function<Player, String> function = OfflinePlayer::getName;
            List<Player> values;
            if ((values =
                                    SuggestionUtils.findList(
                                            onlinePlayers,
                                            function,
                                            result -> this.matchesEnabled(actor, result, player),
                                            value))
                            .size()
                    > 1) {
                String text =
                        values.stream()
                                .<CharSequence>map(OfflinePlayer::getName)
                                .collect(Collectors.joining(", "));
                throw new ExitMessage(
                        LocaleUtil.tr(commandExecution, MessageConfig.ERROR.MULTIPLE_PLAYERS_MATCH)
                                .replace("<search>", Color.escape(value))
                                .replace("<all>", text));
            }
            boolean validName = BukkitAPI.isValidName(value.replace("*", "").replace(".", ""));
            if (values.isEmpty()) {
                if (!validName) {
                    throw new ExitMessage(
                            LocaleUtil.tr(commandExecution, MessageConfig.ERROR.INVALID_USERNAME)
                                    .replace("<name>", Color.escape(value)));
                } else {
                    throw new ExitMessage(
                            LocaleUtil.tr(commandExecution, MessageConfig.ERROR.PLAYER_NOT_ONLINE)
                                    .replace("<player>", Color.escape(value)));
                }
            } else {
                Player player3 = (Player) values.get(0);
                if (!this.isOnline(actor, parameter, player3, value, player)) {
                    throw new ExitMessage(
                            LocaleUtil.tr(commandExecution, MessageConfig.ERROR.PLAYER_NOT_ONLINE)
                                    .replace("<player>", Color.escape(value)),
                            true);
                } else {
                    return player3;
                }
            }
        }
    }

    public List<String> suggest(
            CommandSuggestion commandSuggestion, CommandArgument commandArgument) {
        Actor actor;
        if ((actor = commandSuggestion.getActor()).isPlayer()) {
            Player player = ((BukkitActor) actor).getPlayer();
            return new ArrayList<>(this.plugin.getVisibilityHandler().getAllVisibleToNames(player));
        } else {
            return FastStreams.mapArrayList(Bukkit.getOnlinePlayers(), OfflinePlayer::getName);
        }
    }

    public boolean isOnline(
            PlayerActor playerActor,
            CommandParameter commandParameter,
            Player player,
            String text,
            boolean matches) {
        if (commandParameter.hasOption("online")) {
            return !this.plugin.getNetworkHandler().isOnline(player.getUniqueId())
                    ? false
                    : this.matchesEnabled(playerActor, player, matches);
        } else {
            return true;
        }
    }

    private boolean matchesEnabled(PlayerActor playerActor, Player player, boolean matches) {
        return !matches
                || this.plugin
                        .getVisibilityHandler()
                        .canSee(playerActor.getUniqueId(), player.getUniqueId());
    }
}
