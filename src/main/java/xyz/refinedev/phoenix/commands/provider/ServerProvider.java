package xyz.refinedev.phoenix.commands.provider;

import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.lib.commands.execution.argument.CommandArgument;
import xyz.refinedev.lib.commands.wrapper.CommandParameter;
import xyz.refinedev.lib.commands.wrapper.binding.provider.Provider;
import xyz.refinedev.lib.commands.wrapper.binding.provider.ProviderType;
import xyz.refinedev.lib.commands.wrapper.suggestion.CommandSuggestion;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.List;

public class ServerProvider extends Provider<IServerData> {

    private final CommonPlatform platform;

    public ServerProvider(CommonPlatform commonPlatform) {
        super(ProviderType.ARGUMENT, "server");
        this.platform = commonPlatform;
    }

    public IServerData provide(CommandExecution commandExecution, CommandArgument commandArgument) {
        String value = commandArgument.getValue();
        CommandParameter commandParameter;
        if ((commandParameter = commandArgument.getParameter()).hasOption("proxy")) {
            IServerData serverData;
            if ((serverData = this.platform.getNetworkHandler().getProxyByName(value)) == null) {
                throw new ExitMessage(
                        LocaleUtil.tr(commandExecution, MessageConfig.ERROR.PROXY_DOESNT_EXIST)
                                .replace("<proxy>", Color.escape(value)),
                        true);
            } else {
                return serverData;
            }
        } else {
            IServerData serverData2;
            if ((serverData2 = this.platform.getNetworkHandler().getServerByName(value)) == null) {
                throw new ExitMessage(
                        LocaleUtil.tr(commandExecution, MessageConfig.ERROR.SERVER_DOESNT_EXIST)
                                .replace("<server>", Color.escape(value)),
                        true);
            } else if (!serverData2.isOnline() && commandParameter.hasOption("online")) {
                throw new ExitMessage(
                        LocaleUtil.tr(commandExecution, MessageConfig.ERROR.SERVER_NOT_ONLINE)
                                .replace("<server>", Color.escape(value)),
                        true);
            } else {
                return serverData2;
            }
        }
    }

    public List<String> suggest(
            CommandSuggestion commandSuggestion, CommandArgument commandArgument) {
        return commandArgument.getParameter().hasOption("proxy")
                ? FastStreams.mapArrayList(
                        this.platform.getNetworkHandler().getProxies(), IServerData::getServerName)
                : FastStreams.mapArrayList(
                        this.platform.getNetworkHandler().getServers(), IServerData::getServerName);
    }
}
