package xyz.refinedev.phoenix.commands.provider;

import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.lib.commands.execution.argument.CommandArgument;
import xyz.refinedev.lib.commands.wrapper.binding.provider.Provider;
import xyz.refinedev.lib.commands.wrapper.binding.provider.ProviderType;
import xyz.refinedev.lib.commands.wrapper.suggestion.CommandSuggestion;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.List;

public class PunishmentLadderProvider extends Provider<IPunishmentLadder> {

    private final CommonPlatform platform;

    public PunishmentLadderProvider(CommonPlatform commonPlatform) {
        super(ProviderType.ARGUMENT, "punishmentLadder");
        this.platform = commonPlatform;
    }

    public IPunishmentLadder provide(
            CommandExecution commandExecution, CommandArgument commandArgument) {
        String value = commandArgument.getValue();
        IPunishmentLadder punishmentLadder;
        if ((punishmentLadder = this.platform.getPunishmentLadderHandler().getByName(value))
                == null) {
            throw new ExitMessage(
                    LocaleUtil.tr(commandExecution, MessageConfig.ERROR.LADDER_DOESNT_EXIST)
                            .replace("<ladder>", Color.escape(value)),
                    true);
        } else {
            return punishmentLadder;
        }
    }

    public List<String> suggest(
            CommandSuggestion commandSuggestion, CommandArgument commandArgument) {
        return FastStreams.mapArrayList(
                this.platform.getPunishmentLadderHandler().getRepository().getCache(),
                IPunishmentLadder::getId);
    }
}
