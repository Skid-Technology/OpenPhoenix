package xyz.refinedev.phoenix.packet.impl.profile;

import org.bukkit.Bukkit;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.profile.IProfile;

import java.util.UUID;

public class BukkitSecurityExecutePunishmentPacket extends SecurityExecutePunishmentPacket {

    public BukkitSecurityExecutePunishmentPacket(UUID uUID, String text) {
        super(uUID, text);
    }

    @Override
    public boolean async() {
        return false;
    }

    @Override
    public void onReceive() {
        if (GlobalConfig.SERVER.NAME.equals(super.server)) {
            IProfile profile;
            if ((profile = Phoenix.getInstance().getProfileHandler().getProfile(super.uuid))
                    != null) {
                Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        "blacklist "
                                + profile.getName()
                                + " -s "
                                + GlobalConfig.SECURITY.PUNISHMENT_REASON);
            }
        }
    }
}
