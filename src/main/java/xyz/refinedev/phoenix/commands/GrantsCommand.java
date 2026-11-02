package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.impl.GrantsMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.List;
import java.util.stream.Collectors;

@Register(name = "grants")
@Requires("core.command.grants")
public class GrantsCommand {

    private final Phoenix plugin;

    @Command(name = "", async = true)
    public void execute(
            @Sender CommandSender sender, @Option({"disguise", "multiple"}) IProfile profile) {
        if (sender instanceof Player) {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_VIEW_MESSAGE)
                                    .replace("<type>", "grants")
                                    .replace("<player>", profile.getNameWithColorNoDisguise())));
        }
        List<IGrant> values =
                profile.getGrants().stream().sorted(IGrant.COMPARATOR).collect(Collectors.toList());
        if (sender instanceof Player) {
            Player player = (Player) sender;
            this.plugin.getMenuHandler().openMenuAsync(new GrantsMenu(profile, values), player);
        } else {
            for (IGrant grant : values) {
                sender.sendMessage(Phoenix.MAIN_COLOR + "Grants");
                sender.sendMessage(
                        Color.translate(
                                grant.getRank().getDisplayName()
                                        + "&7 - &fActive: &a"
                                        + grant.isActive()
                                        + " &7(Expires: "
                                        + grant.getNiceExpire()
                                        + ")"));
            }
        }
    }

    public GrantsCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
