package xyz.refinedev.phoenix.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Default;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Help
@Register(name = "resourcepack", aliases = "pack")
@Requires("core.command.resourcepack")
public class ResourcepackCommand {

    private final Phoenix plugin;

    @Command(name = "push", description = "Pushes a resource pack onto everyone's stack")
    public void push(
            @Sender CommandSender sender,
            @Named("url") String text,
            @Named("hash") String text2,
            @Default("false") @Named("required") boolean matches,
            @Optional @Named("prompt") @Text String text3) {
        int value = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.plugin.getResourcePackHandler().findUUID(player, text, text2, matches, text3)
                    != null) {
                value++;
            }
        }
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RESOURCE_PACK.PUSHED_ALL)
                                .replace("<amount>", String.valueOf(value))
                                .replace("<url>", text)));
    }

    @Command(name = "pushplayer", description = "Pushes a resource pack onto one player's stack")
    public void pushplayer(
            @Sender CommandSender sender,
            @Named("player") Player player,
            @Named("url") String text,
            @Named("hash") String text2,
            @Default("false") @Named("required") boolean matches,
            @Optional @Named("prompt") @Text String text3) {
        if (this.plugin.getResourcePackHandler().findUUID(player, text, text2, matches, text3)
                == null) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.RESOURCE_PACK.PUSH_FAILED)
                            .replace("<player>", player.getName()));
        }
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RESOURCE_PACK.PUSHED)
                                .replace("<player>", player.getName())));
    }

    @Command(
            name = "remove",
            description = "Removes a pack from everyone's stack, by the url it was pushed with")
    public void remove(@Sender CommandSender sender, @Named("url") String text) {
        int value = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.plugin
                    .getResourcePackHandler()
                    .matchesEnabled(player, this.plugin.getResourcePackHandler().findUUID(text))) {
                value++;
            }
        }
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RESOURCE_PACK.REMOVED_ALL)
                                .replace("<amount>", String.valueOf(value))));
    }

    @Command(name = "clear", description = "Removes every pack from everyone's stack")
    public void clear(@Sender CommandSender sender) {
        int value = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.plugin.getResourcePackHandler().matchesEnabled(player, null)) {
                value++;
            }
        }
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RESOURCE_PACK.CLEARED_ALL)
                                .replace("<amount>", String.valueOf(value))));
    }

    public ResourcepackCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
