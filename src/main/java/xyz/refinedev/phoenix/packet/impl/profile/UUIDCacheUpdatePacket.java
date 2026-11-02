package xyz.refinedev.phoenix.packet.impl.profile;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.packet.api.Packet;

import java.util.UUID;

public class UUIDCacheUpdatePacket implements Packet {

    private UUID uuid;

    private String name;

    @Override
    public void onReceive() {
        PlatformGetter.getInstance().getUuidCache().addToCache(this.uuid, this.name, false);
    }

    public UUIDCacheUpdatePacket(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }
}
