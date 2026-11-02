package xyz.refinedev.phoenix.packet.impl.profile;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.profile.IProfile;

public class ProfileDeletePacket implements Packet {

    private IProfile profile;

    @Override
    public void onReceive() {
        if (PlatformGetter.getInstance()
                        .getProfileHandler()
                        .getCachedProfile(this.profile.getUniqueId())
                != null) {
            PlatformGetter.getInstance()
                    .getProfileHandler()
                    .getRepository()
                    .getOnlineCache()
                    .remove(this.profile.getUniqueId());
            PlatformGetter.getInstance()
                    .getProfileHandler()
                    .getRepository()
                    .removeFromCache(this.profile.getUniqueId());
        }
    }

    public ProfileDeletePacket(IProfile profile2) {
        this.profile = profile2;
    }

    public ProfileDeletePacket() {}
}
