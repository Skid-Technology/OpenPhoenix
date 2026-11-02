package xyz.refinedev.phoenix.commands.provider;

import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.lib.commands.execution.argument.CommandArgument;
import xyz.refinedev.lib.commands.wrapper.binding.provider.Provider;
import xyz.refinedev.lib.commands.wrapper.binding.provider.ProviderType;
import xyz.refinedev.lib.commands.wrapper.suggestion.CommandSuggestion;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.handler.IReportCategoryHandler;
import xyz.refinedev.phoenix.report.IReportCategory;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CategoryProvider extends Provider<IReportCategory> {

    private final IReportCategoryHandler reportCategoryHandler;

    public CategoryProvider(IReportCategoryHandler reportCategoryHandler2) {
        super(ProviderType.ARGUMENT, "category");
        this.reportCategoryHandler = reportCategoryHandler2;
    }

    public IReportCategory provide(
            CommandExecution commandExecution, CommandArgument commandArgument) {
        String value = commandArgument.getValue();
        Optional<IReportCategory> optional;
        if (!(optional = this.reportCategoryHandler.getRepository().getFromCache(value))
                .isPresent()) {
            throw new ExitMessage(
                    LocaleUtil.tr(commandExecution, MessageConfig.REPORT.CATEGORY.DOESNT_EXIST)
                            .replace("<category>", Color.escape(value)),
                    true);
        } else {
            return (IReportCategory) optional.get();
        }
    }

    public List<String> suggest(
            CommandSuggestion commandSuggestion, CommandArgument commandArgument) {
        return this.reportCategoryHandler.getRepository().getCache().stream()
                .map(IReportCategory::getId)
                .collect(Collectors.toList());
    }
}
