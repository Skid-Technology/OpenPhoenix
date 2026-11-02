package xyz.refinedev.phoenix.packet.impl.server;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.Map;
import java.util.Map.Entry;

public class SendToStaffPacket implements Packet {

    private String message;

    private boolean admin;

    private boolean staff;

    private boolean force;

    private String path;

    private Map<String, String> replacements;

    public SendToStaffPacket(String message, boolean admin, boolean staff) {
        this.message = message;
        this.admin = admin;
        this.staff = staff;
    }

    public SendToStaffPacket(
            String text, Map<String, String> entries, boolean matches, boolean matches2) {
        this(text, entries, matches, matches2, false);
    }

    public SendToStaffPacket(
            String text, Map<String, String> entries, boolean admin, boolean staff, boolean force) {
        this.path = LocaleUtil.pathOf(text);
        this.replacements = entries;
        this.message = this.apply(text);
        this.admin = admin;
        this.staff = staff;
        this.force = force;
    }

    @Override
    public void onReceive() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.admin) {
                IProfile cachedProfile =
                        Phoenix.getInstance()
                                .getProfileHandler()
                                .getCachedProfile(player.getUniqueId());
                if (player.hasPermission("core.admin")
                        && (this.force || cachedProfile.isSetting("admin-alerts"))) {
                    player.sendMessage(Color.translate(this.render(player)));
                }
            } else if (this.staff) {
                IProfile cachedProfile2 =
                        Phoenix.getInstance()
                                .getProfileHandler()
                                .getCachedProfile(player.getUniqueId());
                if (player.hasPermission("core.staff")
                        && (this.force || cachedProfile2.isSetting("staff-messages"))) {
                    player.sendMessage(Color.translate(this.render(player)));
                }
            } else if (player.hasPermission("core.staff")) {
                player.sendMessage(Color.translate(this.render(player)));
            }
        }
        Bukkit.getConsoleSender().sendMessage(Color.translate(this.message));
    }

    private String render(Player player) {
        String text;
        return (text = LocaleUtil.translation(player, this.path)) == null
                ? this.message
                : this.apply(text);
    }

    private String apply(String text) {
        if (text != null && this.replacements != null) {
            for (Entry<String, String> entry : this.replacements.entrySet()) {
                text = text.replace((CharSequence) entry.getKey(), (CharSequence) entry.getValue());
            }
            return text;
        } else {
            return text;
        }
    }

    public SendToStaffPacket() {}
}
