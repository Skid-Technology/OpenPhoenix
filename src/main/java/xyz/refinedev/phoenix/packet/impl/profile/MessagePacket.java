package xyz.refinedev.phoenix.packet.impl.profile;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.xseries.XSound;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

public class MessagePacket implements Packet {

    private String targetUUID1;

    private String senderUUID1;

    private String msg;

    private String server;

    public static String getFormat(Player player, String text, String text2, String text3) {
        return Color.translate(
                        LocaleUtil.tr(player, MessageConfig.GENERAL.MESSAGE.PRIVATE_MESSAGE_TARGET)
                                .replace("<target>", text2)
                                .replace("<sender>", text))
                .replace("<message>", text3);
    }

    public static String getFormatWithColor(
            Player player, String text, String text2, String text3) {
        return Color.translate(
                LocaleUtil.tr(player, MessageConfig.GENERAL.MESSAGE.PRIVATE_MESSAGE_TARGET)
                        .replace("<target>", text2)
                        .replace("<sender>", text)
                        .replace("<message>", text3));
    }

    @Override
    public void onReceive() {
        UUID uUID = UUID.fromString(this.targetUUID1);
        UUID uUID2 = UUID.fromString(this.senderUUID1);
        IProfile profile = Phoenix.getInstance().getProfileHandler().getProfile(uUID2);
        IProfile profile2 = Phoenix.getInstance().getProfileHandler().getProfile(uUID);
        Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
        while (iterator2.hasNext()) {
            Player player;
            if ((player = (Player) iterator2.next()).hasPermission("core.command.socialspy")
                    && !player.getUniqueId().equals(uUID2)
                    && !player.getUniqueId().equals(uUID)
                    && Phoenix.getInstance()
                            .getProfileHandler()
                            .getCachedProfile(player.getUniqueId())
                            .isMetadata("social-spy", false)) {
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.STAFF.SOCIAL_SPY_ALERT)
                                        .replace("<server>", this.server)
                                        .replace("<sender>", profile.getNameWithColor())
                                        .replace("<target>", profile2.getNameWithColor())
                                        .replace("<message>", this.msg)));
            }
        }
        Player player2;
        if ((player2 = Bukkit.getPlayer(uUID)) != null) {
            profile.updateMetadata("replyTo", profile2.getPlayerName(), true);
            profile2.updateMetadata("replyTo", profile.getPlayerName(), true);
            if (profile.hasAccessToPermission("core.message.colored")) {
                player2.sendMessage(
                        getFormatWithColor(
                                player2,
                                profile.getNameWithColor(),
                                profile2.getNameWithColor(),
                                this.msg));
            } else {
                player2.sendMessage(
                        getFormat(
                                player2,
                                profile.getNameWithColor(),
                                profile2.getNameWithColor(),
                                this.msg));
            }
            if (profile2.isSetting("private-message-sounds")) {
                Optional<XSound> optional =
                        XSound.matchXSound(SettingsConfig.SERVER.SOUNDS.PRIVATE_MESSAGE);
                Player player3 = player2;
                optional.ifPresent(
                        result ->
                                player3.playSound(
                                        player3.getLocation(), result.parseSound(), 2.0F, 2.0F));
            }
        }
    }

    public MessagePacket(String targetUUID1, String senderUUID1, String msg, String server) {
        this.targetUUID1 = targetUUID1;
        this.senderUUID1 = senderUUID1;
        this.msg = msg;
        this.server = server;
    }

    public MessagePacket() {}
}
