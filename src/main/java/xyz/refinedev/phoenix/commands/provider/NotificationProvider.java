package xyz.refinedev.phoenix.commands.provider;

import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.lib.commands.execution.argument.CommandArgument;
import xyz.refinedev.lib.commands.wrapper.binding.provider.Provider;
import xyz.refinedev.lib.commands.wrapper.binding.provider.ProviderType;
import xyz.refinedev.lib.commands.wrapper.suggestion.CommandSuggestion;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.notification.INotification;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.List;

public class NotificationProvider extends Provider<INotification> {

    private final CommonPlatform platform;

    public NotificationProvider(CommonPlatform commonPlatform) {
        super(ProviderType.ARGUMENT, "notification");
        this.platform = commonPlatform;
    }

    public INotification provide(
            CommandExecution commandExecution, CommandArgument commandArgument) {
        String value = commandArgument.getValue();
        INotification notification;
        if ((notification = this.platform.getNotificationHandler().getFromCache(value)) == null) {
            throw new ExitMessage(
                    LocaleUtil.tr(commandExecution, MessageConfig.ERROR.NOTIFICATION_DOESNT_EXIST)
                            .replace("<notification>", Color.escape(value)),
                    true);
        } else {
            return notification;
        }
    }

    public List<String> suggest(
            CommandSuggestion commandSuggestion, CommandArgument commandArgument) {
        return FastStreams.mapArrayList(
                this.platform.getNotificationHandler().getRepository().getCache(),
                INotification::getId);
    }
}
