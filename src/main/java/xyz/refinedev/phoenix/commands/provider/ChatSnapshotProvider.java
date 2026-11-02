package xyz.refinedev.phoenix.commands.provider;

import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.lib.commands.execution.argument.CommandArgument;
import xyz.refinedev.lib.commands.wrapper.binding.provider.Provider;
import xyz.refinedev.lib.commands.wrapper.binding.provider.ProviderType;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatSnapshot;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

public class ChatSnapshotProvider extends Provider<IChatSnapshot> {

    private final CommonPlatform platform;

    public ChatSnapshotProvider(CommonPlatform commonPlatform) {
        super(ProviderType.ARGUMENT, "chatSnapshot");
        this.platform = commonPlatform;
    }

    public IChatSnapshot provide(
            CommandExecution commandExecution, CommandArgument commandArgument) {
        String value = commandArgument.getValue();
        IChatSnapshot chatSnapshot;
        if ((chatSnapshot = this.platform.getChatSnapshotHandler().getByNiceId(value)) == null) {
            throw new ExitMessage(
                    LocaleUtil.tr(commandExecution, MessageConfig.ERROR.CHAT_SNAPSHOT_DOESNT_EXIST)
                            .replace("<chatSnapshot>", Color.escape(value)),
                    true);
        } else {
            return chatSnapshot;
        }
    }
}
