package xyz.refinedev.phoenix.packet.impl.ranks;

import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.profile.ProfileLookup;

public class PermissionsUpdatePacket implements Packet {

    @Override
    public void onReceive() {
        ProfileLookup.initialize();
    }
}
