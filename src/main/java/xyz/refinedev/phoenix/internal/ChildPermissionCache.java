package xyz.refinedev.phoenix.internal;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.profile.IProfile;

import java.util.HashMap;
import java.util.Map;

public class ChildPermissionCache {

    private final Phoenix plugin;

    private final Map<String, String> stringsByName = new HashMap<>();

    private boolean enabled = false;

    public boolean hasAccessToPermission(IProfile profile, String text) {
        String text2;
        return (text2 = this.stringsByName.get(text)) != null
                && profile.hasAccessToPermission(text2);
    }

    public String formatString(String text) {
        return this.stringsByName.get(text);
    }

    public void initialize() {
        if (this.enabled) {
            for (String text : this.plugin.getPermissionMap().keySet()) {
                for (String text2 : this.plugin.getPermissionMap().findMap(text, true).keySet()) {
                    this.stringsByName.put(text2, text);
                }
            }
        }
    }

    public ChildPermissionCache(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
