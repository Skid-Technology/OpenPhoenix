package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.commands.api.annotations.Groups;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.StringUtils;

@Help
@Register(name = "groupinfo")
@Requires("core.command.groupinfo")
public class GroupinfoCommand {

    private final CommonPlatform platform;

    @Command(name = "list", description = "Fetch all servers with a specific group")
    public void list(@Sender CommandSender sender) {
        for (String text : LocaleUtil.tr(sender, MessageConfig.GROUP_INFO.LIST)) {
            sender.sendMessage(Color.translate(text.replace("<groups>", this.getString())));
        }
    }

    @Command(name = "info", description = "Fetch information about a specific group")
    public void info(@Sender CommandSender sender, @Named("group") @Groups String text) {
        for (String text2 : LocaleUtil.tr(sender, MessageConfig.GROUP_INFO.SERVERS)) {
            sender.sendMessage(
                    Color.translate(text2.replace("<servers>", this.formatString(text))));
        }
    }

    private String getString() {
        return StringUtils.getStringFromList(this.platform.getNetworkHandler().getGroups());
    }

    private String formatString(String text) {
        return StringUtils.getStringFromList(
                this.platform.getNetworkHandler().getServersByGroup(text));
    }

    public GroupinfoCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
