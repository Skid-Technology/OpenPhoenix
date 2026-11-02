package xyz.refinedev.phoenix.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.commands.api.annotations.ValidName;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.impl.DisguiseHistoryMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.disguise.DisguiseData;
import xyz.refinedev.phoenix.profile.disguise.IDisguiseHistory;
import xyz.refinedev.phoenix.profile.disguise.ISkin;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Help
@Register(name = "disguiseadmin", aliases = "admindisguise")
@Requires("core.command.disguiseadmin")
public class DisguiseadminCommand {

    private final Phoenix plugin;

    @Command(name = "disguise", async = true)
    @Requires("core.command.disguiseadmin.disguise")
    public void disguise(
            @Sender CommandSender sender,
            IRank rank,
            @ValidName @Named("player") String text,
            ISkin skin,
            @Named("target") Player player) {
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                .getDisguiseData()
                .isDisguised()) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.DISGUISE.ALREADY_DISGUISED_OTHER)
                            .replace("<color>", profile.getNameColor())
                            .replace("<name>", profile.getPlayerName()));
        }
        if (Bukkit.getPlayer(text) != null) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.DISGUISE.ONLINE_PLAYER_WITH_THAT_NAME));
        }
        if (this.plugin.getDisguiseHandler().isNameInUse(text).join()) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.DISGUISE.ONLINE_PLAYER_WITH_THAT_NAME));
        }
        DisguiseData disguiseData;
        (disguiseData = new DisguiseData()).setDisguiseName(text);
        disguiseData.setRankId(rank.getUuid());
        disguiseData.setDisguiseSkin(skin);
        DisguiseData disguiseData2 = disguiseData;
        IProfile profile2 = profile;
        Tasks.run(
                () -> {
                    try {
                        this.plugin.getDisguiseHandler().disguise(profile2, disguiseData2, false);
                    } catch (Exception exception) {
                        this.plugin
                                .getLogger()
                                .log(Level.SEVERE, "Error while disguising: ", exception);
                        sender.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(sender, MessageConfig.DISGUISE.FAILED)));
                    }
                });
    }

    @Command(name = "listdisguised", async = true)
    @Requires("core.command.disguiseadmin.listdisguised")
    public void listdisguised(@Sender CommandSender sender) {
        List<IProfile> values;
        if ((values = this.plugin.getDisguiseHandler().getAllDisguisedProfiles()).isEmpty()) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.DISGUISE.NO_DISGUISED_PLAYERS));
        }
        Iterator<String> iterator2 = LocaleUtil.tr(sender, MessageConfig.DISGUISE.LIST).iterator();
        while (iterator2.hasNext()) {
            String text;
            if ((text = (String) iterator2.next()).contains("<format>")) {
                for (IProfile profile : values) {
                    sender.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(sender, MessageConfig.DISGUISE.LIST_FORMAT)
                                            .replace(
                                                    "<player>",
                                                    profile.getNameWithColorNoDisguise())
                                            .replace("<disguise>", profile.getNameWithColor())));
                }
            } else {
                sender.sendMessage(Color.translate(text));
            }
        }
    }

    @Command(name = "undisguise")
    @Requires("core.command.disguiseadmin.undisguise")
    public void undisguise(@Sender CommandSender sender, Player player) {
        IProfile profile;
        if (!(profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                .getDisguiseData()
                .isDisguised()) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.DISGUISE.NOT_DISGUISED));
        }
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.DISGUISE.FORCE_UNDISGUISED)
                                .replace("<player>", profile.getNameWithColor())));
        this.plugin.getDisguiseHandler().undisguise(profile, true);
    }

    @Command(name = "checkdisguise")
    @Requires("core.command.disguiseadmin.checkdisguise")
    public void checkdisguise(
            @Sender CommandSender sender,
            @Option({"disguise", "multiple"}) @Named("player") IProfile profile) {
        if (!profile.getDisguiseData().isDisguised()) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.DISGUISE.NOT_DISGUISED));
        }
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.DISGUISE.DISGUISED)
                                .replace("<player>", profile.getNameWithColorNoDisguise())
                                .replace("<disguised>", profile.getNameWithColor())));
    }

    @Command(name = "history")
    @Requires("core.command.disguiseadmin.history")
    public void history(
            @Sender Player player,
            @Option({"disguise", "multiple"}) @Named("player") IProfile profile) {
        CompletableFuture<List<IDisguiseHistory>> completableFuture =
                this.plugin.getDisguiseHandler().fetchDisguiseHistory(profile.getUuid());
        completableFuture.whenCompleteAsync(
                (result, result2) ->
                        this.plugin
                                .getMenuHandler()
                                .openMenuSync(
                                        new DisguiseHistoryMenu(
                                                profile, (List<IDisguiseHistory>) result),
                                        player));
    }

    public DisguiseadminCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
