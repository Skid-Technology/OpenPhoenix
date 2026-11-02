package xyz.refinedev.phoenix.editor.panel;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.profile.IProfile;

import java.util.UUID;
import java.util.logging.Level;

public final class PanelSessions {

    private static final String text = "core.command.panel.code";

    private static volatile PanelSessions.Revoker revoker;

    private PanelSessions() {}

    public static void setRevoker(PanelSessions.Revoker revoker2) {
        revoker = revoker2;
    }

    public static void setRevoker() {
        revoker = null;
    }

    public static void applyCommonPlatform(CommonPlatform commonPlatform, UUID uUID) {
        PanelSessions.Revoker revoker2 = revoker;
        if (revoker != null && uUID != null) {
            try {
                if (new PanelSecret(commonPlatform).formatStringByPlatform(uUID) == null) {
                    return;
                }
                PanelPermissionCache.applyUUID(uUID);
                PanelIdentity.applyUUIDByLongsById(uUID);
                IProfile profile;
                if ((profile = commonPlatform.getProfileHandler().getProfile(uUID)) == null) {
                    return;
                }
                profile.updatePermissions();
                if (profile.checkPermission("core.command.panel.code", false)) {
                    return;
                }
                revoker2.revoke(uUID);
            } catch (Exception exception) {
                commonPlatform
                        .getLogger()
                        .log(Level.WARNING, "Could not review panel access for " + uUID, exception);
            }
        }
    }

    public interface Revoker {

        void revoke(UUID uUID);
    }
}
