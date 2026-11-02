package xyz.refinedev.phoenix.packet.impl.security;

import xyz.refinedev.phoenix.packet.api.Packet;

public class SecurityAlertPacket implements Packet {

    protected String actor;

    protected String target;

    protected String rank;

    protected String server;

    @Override
    public void onReceive() {}

    public SecurityAlertPacket(String actor, String target, String rank, String server) {
        this.actor = actor;
        this.target = target;
        this.rank = rank;
        this.server = server;
    }
}
