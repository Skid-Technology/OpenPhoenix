package xyz.refinedev.phoenix.packet.impl.security;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.packet.api.Packet;

import java.util.UUID;

public class SecurityRemovePacket implements Packet {

    private UUID uuid;

    @Override
    public void onReceive() {
        PlatformGetter.getInstance()
                .getSecurityHandler()
                .getRepository()
                .removeFromCache(this.uuid);
    }

    public SecurityRemovePacket(UUID uuid) {
        this.uuid = uuid;
    }

    public SecurityRemovePacket() {}
}
