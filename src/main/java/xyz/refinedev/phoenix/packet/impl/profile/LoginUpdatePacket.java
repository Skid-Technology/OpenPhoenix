package xyz.refinedev.phoenix.packet.impl.profile;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.packet.api.Packet;

import java.util.UUID;

public class LoginUpdatePacket implements Packet {

    private UUID uuid;

    @Override
    public void onReceive() {
        if (PlatformGetter.getInstance().getProfileHandler().getCachedProfile(this.uuid) != null) {
            PlatformGetter.getInstance().getLoginHandler().updateLogins(this.uuid);
        }
    }

    public LoginUpdatePacket(UUID uuid) {
        this.uuid = uuid;
    }

    public LoginUpdatePacket() {}
}
