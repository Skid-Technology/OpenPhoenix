package xyz.refinedev.phoenix.packet.impl.punishments;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.packet.api.Packet;

import java.util.UUID;

public class PunishmentsUpdatePacket implements Packet {

    private UUID uuid;

    @Override
    public boolean async() {
        return true;
    }

    @Override
    public void onReceive() {
        PlatformGetter.getInstance().getPunishmentHandler().updatePunishments(this.uuid);
    }

    public PunishmentsUpdatePacket(UUID uuid) {
        this.uuid = uuid;
    }

    public PunishmentsUpdatePacket() {}
}
