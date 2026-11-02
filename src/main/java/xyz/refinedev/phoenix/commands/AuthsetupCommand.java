package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.auth.api.Authenticator;

@Register(
        name = "authsetup",
        aliases = {"setup2fa", "2fasetup"})
@Requires("core.2fa")
public class AuthsetupCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(@Sender Player player, @Optional Authenticator authenticator) {
        this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId());
        if (authenticator == null) {
            authenticator =
                    this.plugin
                            .getAuthHandler()
                            .getAuthenticatorByType(SettingsConfig.AUTH.DEFAULT_METHOD);
        }
        if (authenticator == null) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.AUTH.AUTH_METHOD_NOT_ENABLED));
        }
        if (authenticator.canSetup(player)) {
            authenticator.setup(player);
        }
    }

    public AuthsetupCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
