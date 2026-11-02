package xyz.refinedev.phoenix.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.handler.FreezeHandler;
import xyz.refinedev.phoenix.packet.impl.server.SendToStaffPacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatSnapshot;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.general.Tasks;

@Register(
        name = "freeze",
        aliases = {"ss", "screenshare"})
@Requires("core.command.freeze")
public class FreezeCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(@Sender CommandSender sender, Player player) {
        IProfile profile =
                this.plugin
                        .getProfileHandler()
                        .getProfile(
                                sender instanceof Player
                                        ? ((Player) sender).getUniqueId()
                                        : IProfile.getConsoleUuid());
        IProfile profile2;
        if ((profile2 = this.plugin.getProfileHandler().getProfile(player.getUniqueId()))
                                .getGeneralRank()
                                .getPriority()
                        > profile.getGeneralRank().getPriority()
                && !profile.getUniqueId().toString().equals(IProfile.getConsoleUuid().toString())) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.FREEZE.HIGHER_RANK));
        }
        FreezeHandler freezeHandler;
        if ((freezeHandler = this.plugin.getFreezeHandler()).isFrozen(player.getUniqueId())) {
            freezeHandler.setFrozen(player.getUniqueId(), false);
            player.sendMessage(
                    Color.translate(LocaleUtil.tr(player, MessageConfig.FREEZE.UNFROZEN)));
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.FREEZE.UNFROZE)
                                    .replace("<target>", profile2.getNameWithColor())));
            this.plugin
                    .getPacketHandler()
                    .send(
                            new SendToStaffPacket(
                                    MessageConfig.STAFF.UNFREEZE_ALERT,
                                    LocaleUtil.replacements(
                                            "<server>",
                                            this.plugin.getNetworkHandler().getDisplayName(),
                                            "<player>",
                                            profile.getNameWithColorNoDisguise(),
                                            "<target>",
                                            profile2.getNameWithColorNoDisguise()),
                                    false,
                                    true));
            player.closeInventory();
            Location location;
            if (SettingsConfig.FREEZE.TELEPORT.ENABLED
                    && (location = freezeHandler.getPreviousLocations().get(player.getUniqueId()))
                            != null) {
                freezeHandler.getPreviousLocations().remove(player.getUniqueId());
                Phoenix.getInstance().getNmsHandler().getTeleportAPI().teleport(player, location);
            }
        } else {
            freezeHandler.setFrozen(player.getUniqueId(), true);
            player.sendMessage(Color.translate(LocaleUtil.tr(player, MessageConfig.FREEZE.FROZEN)));
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.FREEZE.FROZE)
                                    .replace("<target>", profile2.getNameWithColor())));
            if (SettingsConfig.FREEZE.CHAT_SNAPSHOT) {
                IProfile profile3 = profile2;
                Tasks.runAsync(
                        this.plugin,
                        () -> {
                            IChatSnapshot chatSnapshot;
                            if ((chatSnapshot =
                                            this.plugin
                                                    .getChatSnapshotHandler()
                                                    .createSnapshot(
                                                            profile.getUniqueId(),
                                                            player.getUniqueId()))
                                    != null) {
                                sender.sendMessage(
                                        Color.translate(
                                                LocaleUtil.tr(
                                                                sender,
                                                                MessageConfig.FREEZE.SNAPSHOT_TAKEN)
                                                        .replace("<id>", chatSnapshot.getNiceId())
                                                        .replace(
                                                                "<target>",
                                                                profile3
                                                                        .getNameWithColorNoDisguise())));
                            }
                        });
            }
            this.plugin
                    .getPacketHandler()
                    .send(
                            new SendToStaffPacket(
                                    MessageConfig.STAFF.FREEZE_ALERT,
                                    LocaleUtil.replacements(
                                            "<server>",
                                            this.plugin.getNetworkHandler().getDisplayName(),
                                            "<player>",
                                            profile.getNameWithColorNoDisguise(),
                                            "<target>",
                                            profile2.getNameWithColorNoDisguise()),
                                    false,
                                    true));
            if (SettingsConfig.FREEZE.TELEPORT.ENABLED) {
                Location location2;
                if ((location2 = SettingsConfig.FREEZE.TELEPORT.LOCATION.toBukkit()) == null) {
                    this.plugin
                            .getLogger()
                            .warning(
                                    "The freeze location's world '"
                                            + SettingsConfig.FREEZE.TELEPORT.LOCATION.getWorldName()
                                            + "' isn't loaded, so "
                                            + player.getName()
                                            + " was not teleported. Set it again with /setfreezeloc.");
                } else {
                    freezeHandler
                            .getPreviousLocations()
                            .put(player.getUniqueId(), player.getLocation());
                    Phoenix.getInstance()
                            .getNmsHandler()
                            .getTeleportAPI()
                            .teleport(player, location2);
                }
            }
            for (String text : LocaleUtil.tr(player, MessageConfig.FREEZE.MESSAGE)) {
                player.sendMessage(Color.translate(text));
            }
        }
    }

    public FreezeCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
