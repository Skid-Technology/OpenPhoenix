package xyz.refinedev.phoenix.profile;

import org.bukkit.command.CommandSender;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

public class SocialMediaAccount {

    private final String text;

    @Command(name = "")
    public void execute(@Sender CommandSender sender) {
        sender.sendMessage(Color.translate(LocaleUtil.tr(sender, this.text)));
    }

    public SocialMediaAccount(String text) {
        this.text = text;
    }
}
