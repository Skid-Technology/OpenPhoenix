package xyz.refinedev.phoenix.packet.impl.punishments;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;

public class PunishmentPacket implements Packet {

    private IPunishment punishment;

    @Override
    public boolean async() {
        return true;
    }

    @Override
    public void onReceive() {
        PlatformGetter.getInstance()
                .getPunishmentHandler()
                .updatePunishments(this.punishment.getTarget());
    }

    public PunishmentPacket(IPunishment punishment2) {
        this.punishment = punishment2;
    }

    public PunishmentPacket() {}
}
