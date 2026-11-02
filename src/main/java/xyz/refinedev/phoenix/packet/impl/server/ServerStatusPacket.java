package xyz.refinedev.phoenix.packet.impl.server;

import xyz.refinedev.phoenix.packet.api.Packet;

public class ServerStatusPacket implements Packet {

    protected String server;

    protected String region;

    protected String time;

    protected boolean online;

    @Override
    public void onReceive() {}

    public ServerStatusPacket(String server, String region, String time, boolean online) {
        this.server = server;
        this.region = region;
        this.time = time;
        this.online = online;
    }

    public ServerStatusPacket() {}
}
