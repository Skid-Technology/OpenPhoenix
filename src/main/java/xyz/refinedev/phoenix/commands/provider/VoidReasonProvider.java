package xyz.refinedev.phoenix.commands.provider;

import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.lib.commands.execution.argument.CommandArgument;
import xyz.refinedev.lib.commands.wrapper.binding.provider.Provider;
import xyz.refinedev.lib.commands.wrapper.binding.provider.ProviderType;
import xyz.refinedev.lib.commands.wrapper.suggestion.CommandSuggestion;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.punishment.VoidReason;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.List;

public class VoidReasonProvider extends Provider<VoidReason> {

    public VoidReasonProvider() {
        super(ProviderType.ARGUMENT, "voidReason");
    }

    public VoidReason provide(CommandExecution commandExecution, CommandArgument commandArgument) {
        String text;
        VoidReason voidReason;
        if ((voidReason = VoidReason.parse(text = commandArgument.getValue())) == null) {
            throw new ExitMessage(
                    LocaleUtil.tr(commandExecution, MessageConfig.ERROR.VOIDREASON_DOESNT_EXIST)
                            .replace("<reason>", Color.escape(text)),
                    true);
        } else {
            return voidReason;
        }
    }

    public List<String> suggest(
            CommandSuggestion commandSuggestion, CommandArgument commandArgument) {
        return FastStreams.mapArray(
                VoidReason.values(), result -> result.getNiceName().replace(" ", "_"));
    }
}
