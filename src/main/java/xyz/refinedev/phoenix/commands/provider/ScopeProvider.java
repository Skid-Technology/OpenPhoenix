package xyz.refinedev.phoenix.commands.provider;

import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.lib.commands.execution.argument.CommandArgument;
import xyz.refinedev.lib.commands.wrapper.binding.provider.Provider;
import xyz.refinedev.lib.commands.wrapper.binding.provider.ProviderType;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.scope.Scope;
import xyz.refinedev.phoenix.scope.ScopeSet;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

public class ScopeProvider extends Provider<ScopeSet> {

    public ScopeProvider() {
        super(ProviderType.ARGUMENT, "scope");
    }

    public ScopeSet provide(CommandExecution commandExecution, CommandArgument commandArgument) {
        String text;
        if (!IScope.isValidMultiple(text = commandArgument.getValue())) {
            throw new ExitMessage(
                    LocaleUtil.tr(commandExecution, MessageConfig.ERROR.INVALID_SCOPE)
                            .replace("<input>", Color.escape(text)),
                    true);
        } else {
            return new ScopeSet(Scope.findList(text));
        }
    }
}
