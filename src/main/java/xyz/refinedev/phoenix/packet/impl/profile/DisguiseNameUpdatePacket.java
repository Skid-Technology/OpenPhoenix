package xyz.refinedev.phoenix.packet.impl.profile;

import xyz.refinedev.phoenix.packet.api.Packet;

import java.util.UUID;

public class DisguiseNameUpdatePacket implements Packet {

    protected UUID uuid;

    protected String disguiseName;

    @Override
    public void onReceive() {}

    public DisguiseNameUpdatePacket(UUID uuid, String disguiseName) {
        this.uuid = uuid;
        this.disguiseName = disguiseName;
    }
}
