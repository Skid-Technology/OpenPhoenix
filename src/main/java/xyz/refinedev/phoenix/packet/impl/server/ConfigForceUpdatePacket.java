package xyz.refinedev.phoenix.packet.impl.server;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.packet.api.Packet;

public class ConfigForceUpdatePacket implements Packet {

    @Override
    public void onReceive() {
        Phoenix.getInstance().getConfigSyncHandler().loadFromRedis();
    }
}
