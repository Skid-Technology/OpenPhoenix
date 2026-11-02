package xyz.refinedev.phoenix.packet.impl.punishments;

import xyz.refinedev.phoenix.packet.api.Packet;

public class BanWaveReleasedPacket implements Packet {

    protected String wave;

    protected int amount;

    @Override
    public void onReceive() {}

    public BanWaveReleasedPacket(String wave, int amount) {
        this.wave = wave;
        this.amount = amount;
    }

    public BanWaveReleasedPacket() {}
}
