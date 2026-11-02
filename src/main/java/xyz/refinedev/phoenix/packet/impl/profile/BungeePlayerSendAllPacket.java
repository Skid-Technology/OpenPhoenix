package xyz.refinedev.phoenix.packet.impl.profile;

import xyz.refinedev.phoenix.packet.api.Packet;

public class BungeePlayerSendAllPacket implements Packet {

    private String server;

    @Override
    public void onReceive() {}

    public BungeePlayerSendAllPacket(String server) {
        this.server = server;
    }
}
