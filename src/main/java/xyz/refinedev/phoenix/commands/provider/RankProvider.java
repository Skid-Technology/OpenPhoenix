package xyz.refinedev.phoenix.commands.provider;

import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.lib.commands.execution.argument.CommandArgument;
import xyz.refinedev.lib.commands.wrapper.binding.provider.Provider;
import xyz.refinedev.lib.commands.wrapper.binding.provider.ProviderType;
import xyz.refinedev.lib.commands.wrapper.suggestion.CommandSuggestion;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.List;

public class RankProvider extends Provider<IRank> {

    private final CommonPlatform platform;

    public RankProvider(CommonPlatform commonPlatform) {
        super(ProviderType.ARGUMENT, "rank");
        this.platform = commonPlatform;
    }

    public IRank provide(CommandExecution commandExecution, CommandArgument commandArgument) {
        String value = commandArgument.getValue();
        IRank rank;
        if ((rank = this.platform.getRankHandler().getRank(value)) == null) {
            throw new ExitMessage(
                    LocaleUtil.tr(commandExecution, MessageConfig.ERROR.RANK_DOESNT_EXIST)
                            .replace("<rank>", Color.escape(value)),
                    true);
        } else {
            return rank;
        }
    }

    public List<String> suggest(
            CommandSuggestion commandSuggestion, CommandArgument commandArgument) {
        return FastStreams.mapArrayList(
                this.platform.getRankHandler().getAvailableRanks(), IRank::getName);
    }
}
