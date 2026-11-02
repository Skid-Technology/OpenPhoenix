package xyz.refinedev.phoenix.packet.impl.server;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.chat.Clickable;

public class SendToStaffClickablePacket implements Packet {

    private String message;

    private String hover;

    private String cmd;

    private boolean admin;

    private boolean staff;

    @Override
    public void onReceive() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.admin) {
                IProfile cachedProfile =
                        Phoenix.getInstance()
                                .getProfileHandler()
                                .getCachedProfile(player.getUniqueId());
                if (player.hasPermission("core.admin") && cachedProfile.isSetting("admin-alerts")) {
                    new Clickable(this.message, this.hover, this.cmd).sendToPlayer(player);
                }
            } else if (this.staff) {
                IProfile cachedProfile2 =
                        Phoenix.getInstance()
                                .getProfileHandler()
                                .getCachedProfile(player.getUniqueId());
                if (player.hasPermission("core.staff")
                        && cachedProfile2.isSetting("staff-messages")) {
                    new Clickable(this.message, this.hover, this.cmd).sendToPlayer(player);
                }
            } else if (player.hasPermission("core.staff")) {
                new Clickable(this.message, this.hover, this.cmd).sendToPlayer(player);
            }
        }
        Bukkit.getConsoleSender().sendMessage(Color.translate(this.message));
    }

    public SendToStaffClickablePacket(
            String message, String hover, String cmd, boolean admin, boolean staff) {
        this.message = message;
        this.hover = hover;
        this.cmd = cmd;
        this.admin = admin;
        this.staff = staff;
    }

    public SendToStaffClickablePacket() {}
}
