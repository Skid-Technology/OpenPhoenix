package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "force2fa")
@Requires("core.command.force2fa")
public class Force2faCommand {

    private static final String text = "<player>";

    @Command(name = "")
    public void execute(@Sender CommandSender sender, @Option("disguise") IProfile profile) {
        profile.setTwoFactor(true);
        profile.saveAndUpdate();
        sender.sendMessage(
                LocaleUtil.tr(sender, MessageConfig.AUTH.FORCE)
                        .replace(text, profile.getNameWithColorNoDisguise()));
    }
}
