package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.menu.impl.history.PunishmentTypeHistoryMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;

import java.util.Collections;

@Register(name = "lookup")
@Requires("core.command.lookup")
public class LookupCommand {

    private final Phoenix plugin;

    @Command(name = "", async = true)
    public void execute(@Sender Player player, @Named("id") IPunishment punishment) {
        IProfile profile = this.plugin.getProfileHandler().getProfile(punishment.getTarget());
        this.plugin
                .getMenuHandler()
                .openMenuAsync(
                        new PunishmentTypeHistoryMenu(
                                profile,
                                punishment.getPunishmentType(),
                                Collections.singletonList(punishment)),
                        player);
    }

    public LookupCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
