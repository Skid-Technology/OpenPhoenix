package xyz.refinedev.phoenix.packet.impl.profile;

import xyz.refinedev.phoenix.Phoenix;

import java.util.UUID;

public class BukkitDisguiseNameUpdatePacket extends DisguiseNameUpdatePacket {

    public BukkitDisguiseNameUpdatePacket(UUID uUID, String text) {
        super(uUID, text);
    }

    @Override
    public void onReceive() {
        Phoenix.getInstance()
                .getDisguiseHandler()
                .applyDisguiseName(super.uuid, super.disguiseName);
    }
}
