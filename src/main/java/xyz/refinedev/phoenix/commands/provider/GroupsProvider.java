package xyz.refinedev.phoenix.commands.provider;

import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.lib.commands.execution.argument.CommandArgument;
import xyz.refinedev.lib.commands.wrapper.binding.provider.Provider;
import xyz.refinedev.lib.commands.wrapper.binding.provider.ProviderType;
import xyz.refinedev.lib.commands.wrapper.suggestion.CommandSuggestion;
import xyz.refinedev.phoenix.CommonPlatform;

import java.util.List;

public class GroupsProvider extends Provider<String> {

    private final CommonPlatform platform;

    private static final String GROUPS_TEXT = "groups";

    public GroupsProvider(CommonPlatform commonPlatform) {
        super(ProviderType.ARGUMENT, GROUPS_TEXT);
        this.platform = commonPlatform;
    }

    public String provide(CommandExecution commandExecution, CommandArgument commandArgument) {
        return commandArgument.getValue();
    }

    public List<String> suggest(
            CommandSuggestion commandSuggestion, CommandArgument commandArgument) {
        return this.platform.getNetworkHandler().getGroups();
    }
}
