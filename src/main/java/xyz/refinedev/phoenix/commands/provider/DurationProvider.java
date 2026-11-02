package xyz.refinedev.phoenix.commands.provider;

import org.bukkit.command.CommandSender;

import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.lib.commands.execution.argument.CommandArgument;
import xyz.refinedev.lib.commands.wrapper.binding.provider.Provider;
import xyz.refinedev.lib.commands.wrapper.binding.provider.ProviderType;
import xyz.refinedev.lib.commands.wrapper.suggestion.CommandSuggestion;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Duration;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DurationProvider extends Provider<Duration> {

    public static final String text = "temporary";

    private static final List<String> strings =
            Collections.unmodifiableList(Arrays.asList("1h", "1d", "7d", "30d"));

    public DurationProvider() {
        super(ProviderType.ARGUMENT, "duration");
    }

    public Duration provide(CommandExecution commandExecution, CommandArgument commandArgument) {
        Duration duration = Duration.of(commandArgument.getValue());
        boolean matches = commandArgument.getParameter().hasOption("temporary");
        if (duration != null && (!matches || !duration.isEnabled())) {
            return duration;
        } else {
            throw new ExitMessage(
                    LocaleUtil.tr(commandExecution, MessageConfig.PUNISHMENT.INVALID_DURATION));
        }
    }

    public List<String> suggest(
            CommandSuggestion commandSuggestion, CommandArgument commandArgument) {
        return getStrings(
                commandArgument.getValue(), !commandArgument.getParameter().hasOption("temporary"));
    }

    public static List<String> getStrings(String text, boolean matches) {
        return Duration.findList(text, strings, matches);
    }

    public static Duration tr(CommandSender sender, String text, boolean matches) {
        Duration duration;
        if ((duration = Duration.of(text)) != null && (matches || !duration.isEnabled())) {
            return duration;
        } else {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.PUNISHMENT.INVALID_DURATION));
        }
    }
}
