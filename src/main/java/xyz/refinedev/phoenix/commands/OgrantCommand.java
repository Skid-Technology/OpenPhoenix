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
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.scope.Scope;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.Duration;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.util.List;
import java.util.UUID;

@Register(name = "ogrant", aliases = "manualgrant")
@Requires("core.command.ogrant")
public class OgrantCommand {

    private final CommonPlatform platform;

    @Command(name = "", async = true)
    public void execute(
            @Sender ConsoleCommandSender consoleCommandSender,
            @Option({"disguise", "create", "multiple"}) IProfile profile,
            IRank rank,
            @Named("duration") Duration duration,
            @Named("scopes") String text,
            @Named("reason") @Text String text2) {
        if (!IScope.isValidMultiple(text)) {
            throw new ExitMessage(
                    LocaleUtil.tr(consoleCommandSender, MessageConfig.ERROR.INVALID_SCOPES)
                            .replace("<input>", text));
        }
        List<IScope> values = Scope.findList(text);
        if (profile.hasRankAndCantUpgrade(rank)) {
            consoleCommandSender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(
                                            consoleCommandSender,
                                            MessageConfig.GRANT.ALREADY_HAS_RANK)
                                    .replace("<player>", profile.getNameWithColorNoDisguise())));
        } else {
            UUID uUID =
                    consoleCommandSender instanceof Player
                            ? ((Player) consoleCommandSender).getUniqueId()
                            : IProfile.getConsoleUuid();
            Tasks.run(
                    () ->
                            this.platform
                                    .getGrantHandler()
                                    .grant(
                                            uUID,
                                            profile,
                                            duration.getTimeTime(),
                                            duration.isEnabled(),
                                            text2,
                                            rank,
                                            values));
        }
    }

    public OgrantCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
