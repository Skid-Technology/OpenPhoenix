package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "reset2fa")
@Requires("core.command.reset2fa")
public class Reset2faCommand {

    private static final String text = "<player>";

    @Command(name = "")
    public void execute(
            @Sender CommandSender sender, @Option("disguise") @Named("player") IProfile profile) {
        profile.setTwoFactor(true);
        profile.setSecretKey("");
        profile.setAuthType(null);
        profile.saveAndUpdate();
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.AUTH.RESET)
                                .replace(text, profile.getNameWithColorNoDisguise())));
    }
}
