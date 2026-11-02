package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.utils.PunishmentUtil;

@Register(name = "removewarn")
@Requires("core.command.removewarn")
public class RemovewarnCommand {

    private final Phoenix plugin;

    private static final String REMOVED_TEXT = "Removed";

    @Command(name = "", async = true)
    public void execute(@Sender CommandSender sender, @Named("id") String text) {
        IPunishment punishment;
        if ((punishment = this.plugin.getPunishmentHandler().findByPunishmentID(text)) != null) {
            punishment.setRemovedSilent(true);
            punishment.remove(
                    sender instanceof Player
                            ? ((Player) sender).getUniqueId()
                            : IProfile.getConsoleUuid(),
                    REMOVED_TEXT);
            this.plugin
                    .getPunishmentHandler()
                    .getRepository()
                    .saveSync(punishment.getUuid(), punishment);
            PunishmentUtil.executeRevokePunishment(sender, punishment);
        }
    }

    public RemovewarnCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
