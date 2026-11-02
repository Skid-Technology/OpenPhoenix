package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.impl.history.StaffHistoryOverviewMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.List;

@Register(
        name = "staffhistory",
        aliases = {"staffhist", "cstaff"})
@Requires("core.command.staffhistory")
public class StaffhistoryCommand {

    private final Phoenix plugin;

    @Command(name = "", async = true)
    public void execute(@Sender Player player, @Option({"disguise", "multiple"}) IProfile profile) {
        player.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player, MessageConfig.GENERAL.GENERIC_VIEW_MESSAGE)
                                .replace("<type>", "staff history")
                                .replace("<player>", profile.getNameWithColorNoDisguise())));
        List<IPunishment> values = profile.getPunishmentsIssued().join();
        List<IPunishment> values2 = profile.getPunishmentsRemoved().join();
        values.sort(IPunishment.COMPARATOR);
        values2.sort(IPunishment.COMPARATOR);
        this.plugin
                .getMenuHandler()
                .openMenuAsync(new StaffHistoryOverviewMenu(profile, values, values2), player);
    }

    public StaffhistoryCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
