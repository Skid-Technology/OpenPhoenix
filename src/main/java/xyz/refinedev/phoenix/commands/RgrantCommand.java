package xyz.refinedev.phoenix.commands;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.util.UUID;

@Register(name = "rgrant")
@Requires("core.command.rgrant")
public class RgrantCommand {

    private final CommonPlatform platform;

    private static final String text = "<player>";

    @Command(name = "")
    public void execute(
            @Sender ConsoleCommandSender consoleCommandSender,
            @Option({"disguise", "multiple"}) IProfile profile,
            IRank rank,
            @Named("reason") @Text String text2) {
        IGrant grant;
        if ((grant = profile.getGrantAllScopes(rank)) == null) {
            consoleCommandSender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(
                                            consoleCommandSender,
                                            MessageConfig.GRANT.DOESNT_HAVE_RANK)
                                    .replace(text, profile.getNameWithColorNoDisguise())));
        } else {
            UUID uUID =
                    consoleCommandSender instanceof Player
                            ? ((Player) consoleCommandSender).getUniqueId()
                            : IProfile.getConsoleUuid();
            IGrant grant2 = grant;
            Tasks.run(() -> this.platform.getGrantHandler().ungrant(uUID, profile, grant2, text2));
        }
    }

    public RgrantCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
