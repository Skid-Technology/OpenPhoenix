package xyz.refinedev.phoenix.editor.panel;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.profile.IProfile;

import java.util.Map;
import java.util.UUID;

public class PanelSecret {

    private static final String text = "panelSecret";

    private final CommonPlatform platform;

    public PanelSecret(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }

    public String formatString(UUID uUID) {
        IProfile profile;
        if ((profile = this.platform.getProfileHandler().getProfile(uUID)) == null) {
            return null;
        }
        String text;
        if ((text = profile.getMetadata("panelSecret", null)) != null && !text.isEmpty()) {
            return text;
        }
        String string = PanelLinkCode.getString();
        this.applyProfile(profile, string);
        return string;
    }

    public boolean matchesEnabled(UUID uUID) {
        IProfile profile;
        if ((profile = this.platform.getProfileHandler().getProfile(uUID)) == null) {
            return false;
        }
        this.applyProfile(profile, PanelLinkCode.getString());
        return true;
    }

    public String formatStringByPlatform(UUID uUID) {
        IProfile profile;
        if ((profile = this.platform.getProfileHandler().getProfile(uUID)) == null) {
            return null;
        }
        String text;
        return (text = profile.getMetadata("panelSecret", null)) != null && !text.isEmpty()
                ? text
                : null;
    }

    private void applyProfile(IProfile profile, String text) {
        Map<String, String> entries;
        (entries = profile.getMetadata()).put("panelSecret", text);
        profile.setMetadata(entries);
        this.platform.getProfileHandler().getRepository().save(profile.getUniqueId(), profile);
    }
}
