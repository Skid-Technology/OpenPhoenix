package xyz.refinedev.phoenix.packet.impl.profile;

import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.profile.grant.IGrant;

public class GrantMessagePacket implements Packet {

    protected IGrant grant;

    protected boolean remove;

    @Override
    public void onReceive() {}

    public GrantMessagePacket(IGrant grant2, boolean remove) {
        this.grant = grant2;
        this.remove = remove;
    }
}
