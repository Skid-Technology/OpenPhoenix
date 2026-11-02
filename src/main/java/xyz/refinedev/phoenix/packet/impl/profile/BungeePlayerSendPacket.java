package xyz.refinedev.phoenix.packet.impl.profile;

import xyz.refinedev.phoenix.packet.api.Packet;

import java.util.UUID;

public class BungeePlayerSendPacket implements Packet {

    private UUID uuid;

    private String server;

    @Override
    public void onReceive() {}

    public BungeePlayerSendPacket(UUID uuid, String server) {
        this.uuid = uuid;
        this.server = server;
    }
}
