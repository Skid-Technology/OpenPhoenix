package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Default;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.commands.api.annotations.RequireSecurity;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.security.ISecurityUser;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PaginatedListClickable;
import xyz.refinedev.phoenix.utils.chat.Clickable;

import java.util.ArrayList;
import java.util.UUID;

@Help
@RequireSecurity
@Register(name = "security")
@Requires("core.command.security")
public class SecurityCommand {

    private final CommonPlatform platform;

    @Command(name = "add")
    public void add(
            @Sender CommandSender sender,
            @Option("multiple") IProfile profile,
            @Named("admin") boolean matches) {
        if (sender instanceof Player
                && !this.platform
                        .getSecurityHandler()
                        .hasSecurity(((Player) sender).getUniqueId(), true)) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.SECURITY.NOT_ADMIN));
        }
        if (this.platform.getSecurityHandler().hasSecurity(profile.getUuid())) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.SECURITY.ALREADY_ADDED)
                            .replace("<player>", profile.getNameWithColorNoDisguise()));
        }
        UUID uUID =
                sender instanceof Player
                        ? ((Player) sender).getUniqueId()
                        : IProfile.getConsoleUuid();
        this.platform.getSecurityHandler().addSecurity(profile.getUuid(), uUID, matches);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.SECURITY.ADDED)
                                .replace("<player>", profile.getNameWithColorNoDisguise())));
    }

    @Command(name = "remove")
    public void remove(@Sender CommandSender sender, @Option("multiple") IProfile profile) {
        if (sender instanceof Player
                && !this.platform
                        .getSecurityHandler()
                        .hasSecurity(((Player) sender).getUniqueId(), true)) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.SECURITY.NOT_ADMIN));
        }
        if (!this.platform.getSecurityHandler().hasSecurity(profile.getUuid())) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.SECURITY.NOT_IN_LIST)
                            .replace("<player>", profile.getNameWithColorNoDisguise()));
        }
        this.platform.getSecurityHandler().removeSecurity(profile.getUuid());
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.SECURITY.REMOVED)
                                .replace("<player>", profile.getNameWithColorNoDisguise())));
    }

    @Command(name = "list")
    public void list(@Sender CommandSender sender, @Default("1") @Named("page") int value) {
        if (sender instanceof Player
                && !this.platform
                        .getSecurityHandler()
                        .hasSecurity(((Player) sender).getUniqueId(), true)) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.SECURITY.NOT_ADMIN));
        }
        PaginatedListClickable paginatedListClickable =
                new PaginatedListClickable("Security", null, "/security list");
        ArrayList<Clickable> values2 = new ArrayList<>();
        for (ISecurityUser securityUser :
                this.platform.getSecurityHandler().getRepository().getMap().values()) {
            IProfile profile = this.platform.getProfileHandler().getProfile(securityUser.getUuid());
            Clickable clickable;
            (clickable = new Clickable())
                    .add(LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_LIST_FORMAT));
            clickable.add(
                    LocaleUtil.tr(sender, MessageConfig.SECURITY.LIST_FORMAT)
                            .replace("<player>", profile.getNameWithColorNoDisguise())
                            .replace("<issuedBy>", securityUser.getNiceIssuedBy())
                            .replace("<admin>", String.valueOf(securityUser.isAdmin())));
            values2.add(clickable);
        }
        paginatedListClickable.display(sender, value, values2);
    }

    public SecurityCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
