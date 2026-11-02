package xyz.refinedev.phoenix.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "masssay", aliases = "massay")
@Requires("core.command.masssay")
public class MasssayCommand {

    private final Phoenix plugin;

    private static final String text = "<chat>";

    @Command(name = "")
    public void execute(@Sender Player player, @Named("chat|command") @Text String text2) {
        IProfile profile = this.plugin.getProfileHandler().getProfile(player.getUniqueId());
        if (text2.trim().startsWith("/")) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.SUDO.NO_COMMANDS));
        }
        for (Player player2 : Bukkit.getOnlinePlayers()) {
            if (this.plugin
                            .getProfileHandler()
                            .getProfile(player2.getUniqueId())
                            .getGeneralRank()
                            .getPriority()
                    <= profile.getGeneralRank().getPriority()) {
                player2.chat(text2);
            }
        }
        player.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.SUDO.ALL)
                                .replace(text, text2)));
    }

    public MasssayCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
