package xyz.refinedev.phoenix.packet.impl.profile;

import xyz.refinedev.phoenix.packet.api.Packet;

import java.util.List;
import java.util.UUID;

public class BungeePlayerSendMultiplePacket implements Packet {

    private List<UUID> uuids;

    private String server;

    @Override
    public void onReceive() {}

    public BungeePlayerSendMultiplePacket(List<UUID> uuids, String server) {
        this.uuids = uuids;
        this.server = server;
    }
}
