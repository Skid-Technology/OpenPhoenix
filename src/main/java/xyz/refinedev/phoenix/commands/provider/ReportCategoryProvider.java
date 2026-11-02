package xyz.refinedev.phoenix.commands.provider;

import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.lib.commands.execution.argument.CommandArgument;
import xyz.refinedev.lib.commands.wrapper.binding.provider.Provider;
import xyz.refinedev.lib.commands.wrapper.binding.provider.ProviderType;
import xyz.refinedev.lib.commands.wrapper.suggestion.CommandSuggestion;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.tag.ITagCategory;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.List;

public class ReportCategoryProvider extends Provider<ITagCategory> {

    private final CommonPlatform platform;

    public ReportCategoryProvider(CommonPlatform commonPlatform) {
        super(ProviderType.ARGUMENT, "category");
        this.platform = commonPlatform;
    }

    public ITagCategory provide(
            CommandExecution commandExecution, CommandArgument commandArgument) {
        String value = commandArgument.getValue();
        ITagCategory tagCategory;
        if ((tagCategory = this.platform.getTagHandler().getTagCategory(value)) == null) {
            throw new ExitMessage(
                    LocaleUtil.tr(commandExecution, MessageConfig.ERROR.TAG_CATEGORY_DOESNT_EXIST)
                            .replace("<category>", Color.escape(value)),
                    true);
        } else {
            return tagCategory;
        }
    }

    public List<String> suggest(
            CommandSuggestion commandSuggestion, CommandArgument commandArgument) {
        return FastStreams.mapArrayList(
                this.platform.getTagHandler().getCategoryRepository().getCache(),
                ITagCategory::getName);
    }
}
