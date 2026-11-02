package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.ClientVersion;
import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.version.ClientVersionUtil;
import xyz.refinedev.phoenix.utils.version.ServerVersion;

@Register(name = "transfer")
@Requires("core.command.transfer")
public class TransferCommand {

    private static final ServerVersion serverVersion = ServerVersion.V_1_20_5;

    private static final ClientVersion clientVersion = ClientVersion.V_1_20_5;

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(
            @Sender CommandSender sender,
            @Named("player") Player player,
            @Named("host") String text,
            @Optional @Named("port") Integer value) {
        if (!SettingsConfig.MODERN.TRANSFER.ENABLED) {
            sender.sendMessage(
                    Color.translate(LocaleUtil.tr(sender, MessageConfig.TRANSFER.DISABLED)));
        } else if (!ClientVersionUtil.serverSupports(serverVersion)) {
            sender.sendMessage(
                    Color.translate(LocaleUtil.tr(sender, MessageConfig.TRANSFER.SERVER_TOO_OLD)));
        } else if (!ClientVersionUtil.supports(player, clientVersion)) {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.TRANSFER.CLIENT_TOO_OLD)
                                    .replace("<player>", player.getName())));
        } else {
            byte value2;
            if ((value2 = (byte) (value == null ? 25565 : value)) >= 1 && value2 <= '\uffff') {
                if (!SettingsConfig.MODERN.TRANSFER.ALLOWED_HOSTS.isEmpty()
                        && !SettingsConfig.MODERN.TRANSFER.ALLOWED_HOSTS.contains(
                                text.toLowerCase())) {
                    sender.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(sender, MessageConfig.TRANSFER.NOT_ALLOWED)
                                            .replace("<host>", text)));
                } else {
                    if (this.plugin.getTransferHandler().matchesEnabled(player, text, value2)) {
                        sender.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(sender, MessageConfig.TRANSFER.SENDING)
                                                .replace("<player>", player.getName())
                                                .replace("<host>", text + ":" + value2)));
                    } else {
                        sender.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(sender, MessageConfig.TRANSFER.FAILED)));
                    }
                }
            } else {
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.TRANSFER.INVALID_PORT)));
            }
        }
    }

    public TransferCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
