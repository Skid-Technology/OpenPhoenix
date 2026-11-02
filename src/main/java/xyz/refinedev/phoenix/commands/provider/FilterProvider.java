package xyz.refinedev.phoenix.commands.provider;

import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.lib.commands.execution.argument.CommandArgument;
import xyz.refinedev.lib.commands.wrapper.binding.provider.Provider;
import xyz.refinedev.lib.commands.wrapper.binding.provider.ProviderType;
import xyz.refinedev.lib.commands.wrapper.suggestion.CommandSuggestion;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.filter.IFilter;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.List;
import java.util.Optional;

public class FilterProvider extends Provider<IFilter> {

    private final CommonPlatform platform;

    public FilterProvider(CommonPlatform commonPlatform) {
        super(ProviderType.ARGUMENT, "filter");
        this.platform = commonPlatform;
    }

    public IFilter provide(CommandExecution commandExecution, CommandArgument commandArgument) {
        String value = commandArgument.getValue();
        Optional<IFilter> optional;
        if (!(optional = this.platform.getFilterHandler().getRepository().getFromCache(value))
                .isPresent()) {
            throw new ExitMessage(
                    LocaleUtil.tr(commandExecution, MessageConfig.ERROR.FILTER_DOESNT_EXIST)
                            .replace("<filter>", Color.escape(value)),
                    true);
        } else {
            return (IFilter) optional.get();
        }
    }

    public List<String> suggest(
            CommandSuggestion commandSuggestion, CommandArgument commandArgument) {
        return FastStreams.mapArrayList(
                this.platform.getFilterHandler().getRepository().getCache(), IFilter::getName);
    }
}
