package xyz.refinedev.phoenix.packet.impl.profile;

import xyz.refinedev.phoenix.packet.api.Packet;

import java.util.UUID;

public class SecurityExecutePunishmentPacket implements Packet {

    protected UUID uuid;

    protected String server;

    @Override
    public void onReceive() {}

    public SecurityExecutePunishmentPacket(UUID uuid, String server) {
        this.uuid = uuid;
        this.server = server;
    }
}
