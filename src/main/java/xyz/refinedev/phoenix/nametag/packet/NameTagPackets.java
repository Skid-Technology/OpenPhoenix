package xyz.refinedev.phoenix.nametag.packet;

import xyz.refinedev.lib.com.github.retrooper.packetevents.manager.server.ServerVersion;

public final class NameTagPackets {

    public static final ServerVersion serverVersion = ServerVersion.V_1_20_2;

    private static final NameTagPackets nameTagPackets = new NameTagPackets();

    private NameTagPackets() {}

    public static NameTagPackets getNameTagPackets() {
        return nameTagPackets;
    }

    public int getCount() {
        return 11;
    }

    public int getCountByCount() {
        return 12;
    }

    public int getCount2() {
        return 15;
    }

    public int getCount3() {
        return 17;
    }

    public int getCount4() {
        return 23;
    }

    public int getCount5() {
        return 25;
    }

    public int getCount6() {
        return 26;
    }

    public int getCount7() {
        return 27;
    }
}
