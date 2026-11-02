package xyz.refinedev.phoenix.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.auth.api.Authenticator;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Register(name = "auth", aliases = "2fa")
@Requires("core.2fa")
public class AuthCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(@Sender Player player, @Named("code") @Optional String text) {
        IProfile cachedProfile =
                this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId());
        Authenticator authenticator;
        if ((authenticator =
                        this.plugin
                                .getAuthHandler()
                                .getAuthenticatorByType(cachedProfile.getAuthType()))
                == null) {
            throw new ExitMessage(LocaleUtil.tr(player, MessageConfig.AUTH.NOT_ENABLED));
        }
        if (authenticator.isRequireCode() && text == null) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.AUTH.CODE_REQUIRED)
                            .replace("<type>", authenticator.getName()));
        }
        if (!cachedProfile.isTwoFactor()) {
            throw new ExitMessage(LocaleUtil.tr(player, MessageConfig.AUTH.NO_NEED_TO));
        }
        if (authenticator.canAuthenticate(player)) {
            Map<java.util.UUID, Integer> entries;
            int value =
                    (entries = this.plugin.getAuthHandler().getAuthAttempts())
                                    .getOrDefault(player.getUniqueId(), 0)
                            + 1;
            entries.put(player.getUniqueId(), value);
            if (authenticator.isAsync()) {
                Authenticator authenticator2 = authenticator;
                CompletableFuture<Boolean> completableFuture =
                        CompletableFuture.supplyAsync(
                                () -> authenticator2.authenticate(player, text));
                completableFuture.whenComplete(
                        (result, result2) -> {
                            if (result2 != null) {
                                this.plugin
                                        .getLogger()
                                        .log(
                                                Level.SEVERE,
                                                "There was an error while authenticating "
                                                        + player.getName()
                                                        + ": ",
                                                result2);
                            } else {
                                Tasks.run(
                                        () ->
                                                this.sendMessage(
                                                        player, cachedProfile, value, result));
                            }
                        });
            } else {
                this.sendMessage(
                        player, cachedProfile, value, authenticator.authenticate(player, text));
            }
        }
    }

    public void sendMessage(Player player, IProfile profile, int value, boolean matches) {
        if (matches) {
            profile.setTwoFactor(false);
            profile.saveAndUpdate();
            if (this.plugin.getNmsHandler().isRegistered()) {
                this.plugin.getNmsHandler().getAPI().unsitPlayer(player);
            }
            this.plugin.getAuthHandler().getAuthAttempts().put(player.getUniqueId(), 0);
            player.sendMessage(
                    Color.translate(LocaleUtil.tr(player, MessageConfig.AUTH.SUCCESSFUL)));
        } else {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.AUTH.INVALID_CODE)
                                    .replace(
                                            "<attempts>",
                                            String.valueOf(SettingsConfig.AUTH.ATTEMPTS - value))));
            if (value >= SettingsConfig.AUTH.ATTEMPTS) {
                Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        SettingsConfig.AUTH.FAILED_COMMAND.replace("<user>", player.getName()));
            }
        }
    }

    public AuthCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
