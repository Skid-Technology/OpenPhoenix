package xyz.refinedev.phoenix.packet.impl.profile;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.packet.api.Packet;

import java.util.UUID;

public class AntiVPNWhitelistUpdatePacket implements Packet {

    private UUID uuid;

    private boolean whitelist;

    @Override
    public void onReceive() {
        if (this.whitelist) {
            PlatformGetter.getInstance().getAntiVPNHandler().whitelist(this.uuid, false);
        } else {
            PlatformGetter.getInstance().getAntiVPNHandler().unwhitelist(this.uuid, false);
        }
    }

    public AntiVPNWhitelistUpdatePacket(UUID uuid, boolean whitelist) {
        this.uuid = uuid;
        this.whitelist = whitelist;
    }
}
