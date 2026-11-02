package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.impl.GroupInfoMenu;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.StringUtils;

import java.util.Iterator;

@Help
@Register(
        name = "serverinfo",
        aliases = {"si", "servermanagement", "servers"})
@Requires("core.command.serverinfo")
public class ServerinfoCommand {

    private final Phoenix plugin;

    @Command(name = "", description = "The servers on the network, grouped")
    public void execute(@Sender CommandSender sender) {
        if (!(sender instanceof Player)) {
            this.list(sender);
        } else {
            this.plugin.getMenuHandler().openMenu(new GroupInfoMenu(), (Player) sender);
        }
    }

    @Command(name = "list", description = "Fetch all servers running Phoenix")
    public void list(@Sender CommandSender sender) {
        for (String text : LocaleUtil.tr(sender, MessageConfig.SERVER_INFO.SERVERS)) {
            sender.sendMessage(Color.translate(text.replace("<servers>", this.getString())));
        }
    }

    @Command(name = "delete", description = "Delete an offline server")
    @Requires("core.command.serverinfo.delete")
    public void delete(@Sender CommandSender sender, IServerData serverData) {
        this.plugin.getNetworkHandler().deleteServer(serverData);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.SERVER_INFO.DELETE)
                                .replace("<server>", serverData.getDisplayName())));
    }

    @Command(name = "info", description = "Fetch information about a specific server")
    public void info(@Sender CommandSender sender, IServerData serverData) {
        Iterator<String> iterator2 =
                LocaleUtil.tr(sender, MessageConfig.SERVER_INFO.INFO).iterator();
        while (iterator2.hasNext()) {
            String text =
                    ((String) iterator2.next())
                            .replace("<server>", serverData.getServerName())
                            .replace(
                                    "<count>",
                                    String.valueOf(
                                            this.plugin
                                                    .getNetworkHandler()
                                                    .getOnlinePlayers(serverData)
                                                    .size()))
                            .replace("<max>", String.valueOf(serverData.getMaxPlayers()))
                            .replace(
                                    "<whitelisted>",
                                    serverData.isWhitelisted() ? "&atrue" : "&cfalse")
                            .replace("<display>", serverData.getDisplayName())
                            .replace("<tps>", String.valueOf(serverData.getTps()))
                            .replace(
                                    "<group>",
                                    StringUtils.joinCustomSpacer(
                                            serverData.getServerGroups(), ", "))
                            .replace("<region>", serverData.getRegion());
            sender.sendMessage(Color.translate(text));
        }
    }

    private String getString() {
        return StringUtils.getStringFromList(
                FastStreams.mapArrayList(
                        this.plugin.getNetworkHandler().getServers(), IServerData::getServerName));
    }

    public ServerinfoCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
