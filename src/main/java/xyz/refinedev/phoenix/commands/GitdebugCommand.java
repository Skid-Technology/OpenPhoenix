package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.GsonExclude;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.chat.Clickable;

@Register(name = "gitdebug")
public class GitdebugCommand {

    private final Phoenix plugin;

    @GsonExclude
    private String text =
            "[54, 70, 102, 66, 55, 118, 89, 55, 53, 56, 113, 89, 118, 73, 95, 99, 57, 112, 121, 119, 107, 101, 103, 103, 102, 95, 56, 98, 72, 53, 84, 75, 109, 95, 89, 101, 121, 101, 85, 84, 65, 57, 105, 106, 67, 101, 84, 102, 76, 107, 104, 78, 110, 121, 68, 120, 90, 52, 119, 52, 53, 117, 45, 99, 71, 117, 73, 119, 75, 107, 104, 107, 51, 105, 89, 71, 106, 112, 97, 100, 90, 111, 79, 48, 110, 108, 50, 109, 86, 48, 82, 45, 117, 53, 118, 82, 95, 51, 82, 120, 101, 45, 108, 102, 48, 49, 73, 61]";

    @Command(name = "", hidden = true)
    public void execute(@Sender Player player, @Named("full") @Optional String text2) {
        player.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player, MessageConfig.PHOENIX.GIT_ID)
                                .replace("<id>", this.plugin.getDescription().getVersion())));
        if ("true72".equals(text2)) {
            Clickable clickable;
            (clickable = new Clickable())
                    .addUrl(
                            "&fFull Git ID: &7" + this.plugin.getDescription().getVersion(),
                            "",
                            this.text);
            clickable.sendToPlayer(player);
        }
    }

    public GitdebugCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
