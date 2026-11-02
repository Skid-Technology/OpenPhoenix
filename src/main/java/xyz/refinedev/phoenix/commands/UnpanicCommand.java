package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.packet.api.IPacketHandler;
import xyz.refinedev.phoenix.packet.impl.server.SendToStaffPacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "unpanic")
@Requires("core.command.unpanic")
public class UnpanicCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(@Sender CommandSender sender, Player player) {
        IProfile profile =
                this.plugin
                        .getProfileHandler()
                        .getProfile(
                                sender instanceof Player
                                        ? ((Player) sender).getUniqueId()
                                        : IProfile.getConsoleUuid());
        IProfile profile2 = this.plugin.getProfileHandler().getProfile(player.getUniqueId());
        if (!this.plugin.getPanicHandler().isPanic(player.getUniqueId())) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.PANIC.NOT_IN_PANIC)
                            .replace("<target>", profile2.getNameWithColorNoDisguise()));
        }
        this.plugin.getPanicHandler().setPanic(profile2.getUniqueId(), false);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.PANIC.UNPANIC_OTHER)
                                .replace("<target>", profile2.getNameWithColorNoDisguise())));
        player.sendMessage(Color.translate(LocaleUtil.tr(player, MessageConfig.PANIC.UNPANICKED)));
        if (profile2.isStaff()) {
            IPacketHandler packetHandler = this.plugin.getPacketHandler();
            String text = MessageConfig.STAFF.UNPANIC_ALERT;
            String[] texts = new String[6];
            texts[0] = "<server>";
            texts[1] = this.plugin.getNetworkHandler().getDisplayName();
            texts[2] = "<player>";
            texts[3] = profile2.getNameWithColorNoDisguise();
            texts[4] = "<sender>";
            texts[5] = profile.getNameWithColorNoDisguise();
            packetHandler.send(
                    new SendToStaffPacket(text, LocaleUtil.replacements(texts), false, true));
        }
    }

    public UnpanicCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
