package xyz.refinedev.phoenix.commands.provider;

import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.lib.commands.execution.argument.CommandArgument;
import xyz.refinedev.lib.commands.wrapper.binding.provider.Provider;
import xyz.refinedev.lib.commands.wrapper.binding.provider.ProviderType;
import xyz.refinedev.lib.commands.wrapper.suggestion.CommandSuggestion;

import java.util.List;

public class DurationTextProvider extends Provider<String> {

    public DurationTextProvider() {
        super(ProviderType.ARGUMENT, "duration");
    }

    public String provide(CommandExecution commandExecution, CommandArgument commandArgument) {
        return commandArgument.getValue();
    }

    public List<String> suggest(
            CommandSuggestion commandSuggestion, CommandArgument commandArgument) {
        return DurationProvider.getStrings(
                commandArgument.getValue(), !commandArgument.getParameter().hasOption("temporary"));
    }
}
