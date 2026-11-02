package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.impl.GrantsHistoryMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Register(name = "granthistory")
@Requires("core.command.granthistory")
public class GranthistoryCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(@Sender Player player, @Option({"disguise", "multiple"}) IProfile profile) {
        player.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player, MessageConfig.GENERAL.GENERIC_VIEW_MESSAGE)
                                .replace("<type>", "grant history")
                                .replace("<player>", profile.getNameWithColorNoDisguise())));
        CompletableFuture<java.util.List<IGrant>> grantsIssued = profile.getGrantsIssued();
        grantsIssued.whenCompleteAsync(
                (result, result2) ->
                        this.plugin
                                .getMenuHandler()
                                .openMenuAsync(
                                        new GrantsHistoryMenu(
                                                profile,
                                                result.stream()
                                                        .sorted(IGrant.COMPARATOR)
                                                        .collect(Collectors.toList())),
                                        player));
    }

    public GranthistoryCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
