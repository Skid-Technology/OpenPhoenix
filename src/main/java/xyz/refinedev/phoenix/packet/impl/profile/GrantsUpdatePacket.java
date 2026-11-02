package xyz.refinedev.phoenix.packet.impl.profile;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.editor.panel.PanelPermissionCache;
import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.profile.IProfile;

import java.util.UUID;

public class GrantsUpdatePacket implements Packet {

    protected UUID uuid;

    @Override
    public void onReceive() {
        this.updateGrants();
    }

    protected IProfile updateGrants() {
        PanelPermissionCache.applyUUID(this.uuid);
        IProfile profile;
        if ((profile = PlatformGetter.getInstance().getProfileHandler().getCachedProfile(this.uuid))
                == null) {
            return null;
        }
        PlatformGetter.getInstance().getGrantHandler().updateGrants(this.uuid);
        profile.updatePermissions();
        return profile;
    }

    public GrantsUpdatePacket(UUID uuid) {
        this.uuid = uuid;
    }

    public GrantsUpdatePacket() {}
}
