package xyz.refinedev.phoenix.editor.op;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.editor.panel.PanelAccess;
import xyz.refinedev.phoenix.editor.panel.PanelLinkCode;
import xyz.refinedev.phoenix.editor.panel.PanelPermissionCache;
import xyz.refinedev.phoenix.editor.panel.PanelPermissions;
import xyz.refinedev.phoenix.editor.panel.PanelSecret;
import xyz.refinedev.phoenix.profile.IProfile;

import java.util.Set;
import java.util.UUID;

public class PanelOperations {

    private static final String text = PanelLinkCode.getString();

    private final CommonPlatform platform;

    private final PanelSecret panelSecret;

    private final PanelAccess panelAccess;

    public PanelOperations(CommonPlatform commonPlatform) {
        this(commonPlatform, new PanelAccess(System::currentTimeMillis));
    }

    PanelOperations(CommonPlatform commonPlatform, PanelAccess panelAccess2) {
        this.platform = commonPlatform;
        this.panelSecret = new PanelSecret(commonPlatform);
        this.panelAccess = panelAccess2;
    }

    public EditorOpHandler getEditorOpHandler() {
        return (result, result2) -> {
            String text2 = EditorPayload.formatString(result2, "name").trim();
            String text3 = EditorPayload.formatString(result2, "code").trim().toUpperCase();
            if (!this.panelAccess.matchesEnabled(text2)) {
                throw new EditorOperationException("BAD_CODE", "that code is not valid");
            } else {
                IProfile profile;
                String text4;
                if (PanelLinkCode.matchesEnabled(
                                (text4 =
                                                        (profile =
                                                                                this.platform
                                                                                        .getProfileHandler()
                                                                                        .getProfileFromName(
                                                                                                text2
                                                                                                        .toLowerCase()))
                                                                        == null
                                                                ? null
                                                                : this.panelSecret
                                                                        .formatStringByPlatform(
                                                                                profile
                                                                                        .getUniqueId()))
                                                == null
                                        ? text
                                        : text4,
                                text3,
                                System.currentTimeMillis())
                        && text4 != null) {
                    this.panelAccess.applyStringByEntriesByName(text2);
                    Set<String> values = PanelPermissions.findSet(profile);
                    PanelPermissionCache.applyUUID(profile.getUniqueId(), values);
                    JsonObject jsonObject;
                    (jsonObject = new JsonObject())
                            .addProperty("uuid", profile.getUniqueId().toString());
                    jsonObject.addProperty("name", profile.getName());
                    jsonObject.add("permissions", this.create(values));
                    return jsonObject;
                } else {
                    this.panelAccess.applyString(text2);
                    throw new EditorOperationException("BAD_CODE", "that code is not valid");
                }
            }
        };
    }

    public EditorOpHandler getEditorOpHandlerByEditorOpHandler() {
        return (result, result2) -> {
            String text = EditorPayload.formatString(result2, "node");
            UUID uUID = PanelPermissions.findUUID(result2);
            JsonObject jsonObject;
            (jsonObject = new JsonObject())
                    .addProperty(
                            "allowed", PanelPermissions.matchesEnabled(this.platform, uUID, text));
            return jsonObject;
        };
    }

    private JsonArray create(Set<String> values) {
        JsonArray jsonArray = new JsonArray();
        for (String text : values) {
            jsonArray.add(new JsonPrimitive(text));
        }
        return jsonArray;
    }
}
