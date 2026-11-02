package xyz.refinedev.phoenix.packet.impl.profile;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.profile.IProfile;

import java.util.Set;

public class ProfileUpdatePacket implements Packet {

    private IProfile profile;

    private boolean permissions;

    private Set<String> fields;

    @Override
    public boolean shouldRunOnSameServer() {
        return false;
    }

    @Override
    public void onReceive() {
        IProfile profile2;
        if ((profile2 =
                        PlatformGetter.getInstance()
                                .getProfileHandler()
                                .getCachedProfile(this.profile.getUuid()))
                != null) {
            if (this.fields == null) {
                profile2.loadProfile(this.profile);
            } else {
                profile2.applyFields(this.profile, this.fields);
            }
            if (this.permissions) {
                profile2.updatePermissions();
            }
        }
    }

    public ProfileUpdatePacket(IProfile profile2, boolean permissions, Set<String> fields) {
        this.profile = profile2;
        this.permissions = permissions;
        this.fields = fields;
    }

    public ProfileUpdatePacket() {}
}
