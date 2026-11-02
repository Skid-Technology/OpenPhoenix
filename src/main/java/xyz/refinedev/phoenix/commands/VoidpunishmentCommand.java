package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.packet.impl.punishments.BukkitUnPunishPacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.VoidReason;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "voidpunishment")
@Requires("core.command.voidpunishment")
public class VoidpunishmentCommand {

    private final CommonPlatform platform;

    @Command(name = "", async = true)
    public void execute(
            @Sender CommandSender sender,
            IPunishment punishment,
            @Named("void reason") VoidReason voidReason,
            @Optional @Named("note") @Text String text) {
        punishment.voidAs(
                sender instanceof Player
                        ? ((Player) sender).getUniqueId()
                        : IProfile.getConsoleUuid(),
                voidReason,
                text);
        this.platform
                .getPunishmentHandler()
                .getRepository()
                .saveSync(punishment.getUuid(), punishment);
        this.platform.getPacketHandler().sendSync(new BukkitUnPunishPacket(punishment));
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.PUNISHMENT.VOIDED)
                                .replace("<id>", punishment.getPunishmentID())
                                .replace("<void_reason>", voidReason.getNiceName())));
    }

    public VoidpunishmentCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
