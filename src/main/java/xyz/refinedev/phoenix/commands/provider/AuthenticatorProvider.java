package xyz.refinedev.phoenix.commands.provider;

import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.lib.commands.execution.argument.CommandArgument;
import xyz.refinedev.lib.commands.wrapper.binding.provider.Provider;
import xyz.refinedev.lib.commands.wrapper.binding.provider.ProviderType;
import xyz.refinedev.lib.commands.wrapper.suggestion.CommandSuggestion;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.auth.api.Authenticator;

import java.util.List;

public class AuthenticatorProvider extends Provider<Authenticator> {

    private final Phoenix plugin;

    private static final String AUTHENTICATOR_TEXT = "authenticator";

    public AuthenticatorProvider(Phoenix phoenix) {
        super(ProviderType.ARGUMENT, AUTHENTICATOR_TEXT);
        this.plugin = phoenix;
    }

    public Authenticator provide(
            CommandExecution commandExecution, CommandArgument commandArgument) {
        String value = commandArgument.getValue();
        Authenticator authenticator;
        if ((authenticator = this.plugin.getAuthHandler().getAuthenticatorByType(value)) != null
                && authenticator.isEnabled()) {
            return authenticator;
        } else {
            throw new ExitMessage(
                    LocaleUtil.tr(commandExecution, MessageConfig.AUTH.AUTH_METHOD_NOT_ENABLED),
                    true);
        }
    }

    public List<String> suggest(
            CommandSuggestion commandSuggestion, CommandArgument commandArgument) {
        return FastStreams.mapArrayList(
                this.plugin.getAuthHandler().getAuthenticators(), Authenticator::getName);
    }
}
