package xyz.refinedev.phoenix.commands.provider;

import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.lib.commands.execution.argument.CommandArgument;
import xyz.refinedev.lib.commands.wrapper.binding.provider.Provider;
import xyz.refinedev.lib.commands.wrapper.binding.provider.ProviderType;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

public class PunishmentIdProvider extends Provider<IPunishment> {

    private final CommonPlatform platform;

    public PunishmentIdProvider(CommonPlatform commonPlatform) {
        super(ProviderType.ARGUMENT, "punishmentId");
        this.platform = commonPlatform;
    }

    public IPunishment provide(CommandExecution commandExecution, CommandArgument commandArgument) {
        String value = commandArgument.getValue();
        IPunishment punishment;
        if ((punishment =
                        this.platform
                                .getPunishmentHandler()
                                .findByPunishmentID(value.toUpperCase()))
                == null) {
            throw new ExitMessage(
                    LocaleUtil.tr(commandExecution, MessageConfig.PUNISHMENT.LOOKUP_INVALID_ID)
                            .replace("<id>", Color.escape(value.toUpperCase())),
                    true);
        } else {
            return punishment;
        }
    }
}
