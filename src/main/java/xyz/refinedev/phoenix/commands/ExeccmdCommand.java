package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.commands.api.annotations.RequireSecurity;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.packet.impl.server.ServerCommandPacket;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Help
@RequireSecurity
@Register(name = "execcmd")
@Requires("core.command.execcmd")
public class ExeccmdCommand {

    private final CommonPlatform platform;

    @Command(name = "server")
    public void server(
            @Sender CommandSender sender,
            @Option("online") IServerData serverData,
            @Named("command") @Text String text) {
        this.platform
                .getPacketHandler()
                .send(new ServerCommandPacket(serverData.getServerName(), text, sender.getName()));
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.EXEC_CMD.PERFORM_COMMAND)
                                .replace("<server>", serverData.getServerName())
                                .replace("<cmd>", text)));
    }

    @Command(name = "group")
    public void group(
            @Sender CommandSender sender,
            @Named("group") String text,
            @Named("command") @Text String text2) {
        if (!this.platform.getNetworkHandler().doesGroupExist(text)) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.EXEC_CMD.GROUP_DOESNT_EXIST)
                            .replace("<group>", text));
        }
        for (String text3 : this.platform.getNetworkHandler().getServersByGroup(text)) {
            this.platform
                    .getPacketHandler()
                    .send(new ServerCommandPacket(text3, text2, sender.getName()));
        }
        sender.sendMessage(
                Color.translate(LocaleUtil.tr(sender, MessageConfig.EXEC_CMD.PERFORM_GROUP_CMD))
                        .replace("<group>", text)
                        .replace("<cmd>", text2));
    }

    @Command(name = "global")
    public void global(@Sender CommandSender sender, @Named("command") @Text String text) {
        String text2 =
                sender instanceof Player
                        ? this.platform
                                .getProfileHandler()
                                .getCachedProfile(((Player) sender).getUniqueId())
                                .getNameWithColorNoDisguise()
                        : GlobalConfig.SERVER.CONSOLE_NAME;
        this.platform.getPacketHandler().send(new ServerCommandPacket("all", text, text2));
        sender.sendMessage(
                Color.translate(LocaleUtil.tr(sender, MessageConfig.EXEC_CMD.PERFORM_GLOBAL_CMD))
                        .replace("<cmd>", text));
    }

    public ExeccmdCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
