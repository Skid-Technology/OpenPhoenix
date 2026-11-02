package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.menu.impl.AdminProfileSectionsMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.GsonExclude;

@Register(name = "adminprofile", aliases = "aprofile")
@Requires("core.command.adminprofile")
public class AdminprofileCommand {

    @GsonExclude
    public static String text = "Uo9vZ5VqF8aPQ2mwXHWdON20E4V7M2Ce_rliCz6I93YcMmbys1UlPNSNer6YMKR8";

    private final Phoenix plugin;

    @Command(name = "", async = true)
    public void execute(
            @Sender Player player,
            @Option({"disguise", "multiple"}) @Named("player") IProfile profile) {
        if (GlobalConfig.SERVER.ALTS.ENABLED) {
            profile.loadAlts();
        }
        this.plugin.getMenuHandler().openMenuAsync(new AdminProfileSectionsMenu(profile), player);
    }

    public AdminprofileCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
