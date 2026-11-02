package xyz.refinedev.phoenix.packet.impl.staff;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.packet.api.Packet;

import java.util.UUID;

public class StaffStatePacket implements Packet {

    private StaffStatePacket.State state;

    private String uniqueId;

    private boolean set;

    @Override
    public boolean async() {
        return false;
    }

    @Override
    public void onReceive() {
        Phoenix instance = Phoenix.getInstance();
        UUID uUID;
        try {
            uUID = UUID.fromString(this.uniqueId);
        } catch (IllegalArgumentException illegalArgumentException) {
            return;
        }
        switch (this.state) {
            case VANISH:
                instance.getVanishHandler().apply(uUID, this.set);
                return;
            case FREEZE:
                instance.getFreezeHandler().apply(uUID, this.set);
                return;
            case PANIC:
                instance.getPanicHandler().apply(uUID, this.set);
        }
    }

    public StaffStatePacket(StaffStatePacket.State state2, String uniqueId, boolean set) {
        this.state = state2;
        this.uniqueId = uniqueId;
        this.set = set;
    }

    public StaffStatePacket() {}

    public enum State {
        VANISH,
        FREEZE,
        PANIC;

        private static final StaffStatePacket.State[] $VALUES = $values();

        private static StaffStatePacket.State[] $values() {
            return new StaffStatePacket.State[] {VANISH, FREEZE, PANIC};
        }
    }
}
