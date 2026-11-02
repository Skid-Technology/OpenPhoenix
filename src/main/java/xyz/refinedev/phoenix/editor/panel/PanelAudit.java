package xyz.refinedev.phoenix.editor.panel;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.editor.protocol.EditorOp;
import xyz.refinedev.phoenix.profile.IProfile;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public final class PanelAudit {

    private static final String text = "[panel] ";

    private static final Set<EditorOp> editorOps;

    private PanelAudit() {}

    public static boolean contains(EditorOp editorOp) {
        return editorOps.contains(editorOp);
    }

    public static void applyCommonPlatform(
            CommonPlatform commonPlatform,
            EditorOp editorOp,
            JsonObject jsonObject,
            boolean matches) {
        if (contains(editorOp)) {
            try {
                UUID uUID;
                if ((uUID = fromString(jsonObject, "actorUuid")) == null) {
                    return;
                }
                commonPlatform
                        .getCommandLogHandler()
                        .saveCommandLog(
                                uUID,
                                GlobalConfig.SERVER.NAME,
                                "[panel] "
                                        + (matches ? "" : "refused ")
                                        + formatString(commonPlatform, editorOp, jsonObject));
            } catch (Exception exception) {
                commonPlatform
                        .getLogger()
                        .log(Level.WARNING, "Could not record a panel action", exception);
            }
        }
    }

    private static String formatString(JsonObject jsonObject) {
        if (jsonObject.has("entity") && jsonObject.get("entity").isJsonObject()) {
            JsonObject asJsonObject = jsonObject.getAsJsonObject("entity");
            String[] texts;
            int value = (texts = new String[] {"name", "reason", "filter", "_id"}).length;
            for (int index = 0; index < value; index++) {
                String text = texts[index];
                if (asJsonObject.has(text) && asJsonObject.get(text).isJsonPrimitive()) {
                    return asJsonObject.get(text).getAsString();
                }
            }
            return "?";
        } else {
            return "?";
        }
    }

    private static String formatString(
            CommonPlatform commonPlatform, EditorOp editorOp, JsonObject jsonObject) {
        switch (editorOp) {
            case PUNISH:
                return formatString(jsonObject, "type", "punish").toLowerCase()
                        + " "
                        + formatString(commonPlatform, jsonObject)
                        + append(jsonObject);
            case UNPUNISH:
                return "un"
                        + formatString(jsonObject, "type", "punish").toLowerCase()
                        + " "
                        + formatString(commonPlatform, jsonObject)
                        + append(jsonObject);
            case GRANT_ADD:
                return "grant "
                        + formatString(commonPlatform, jsonObject)
                        + " "
                        + formatString(jsonObject, "rank", "?")
                        + append(jsonObject);
            case GRANT_REMOVE:
                return "ungrant " + formatString(commonPlatform, jsonObject) + append(jsonObject);
            case REPORT_STATUS:
                return "report "
                        + formatString(jsonObject, "id", "?")
                        + " -> "
                        + formatString(jsonObject, "status", "?");
            case WAVE_STAGE:
                return (jsonObject.has("remove") && jsonObject.get("remove").getAsBoolean()
                                ? "unstage "
                                : "stage ")
                        + formatString(commonPlatform, jsonObject)
                        + " in wave "
                        + formatString(jsonObject, "wave", "?");
            case WAVE_CREATE:
                return "create wave " + formatString(jsonObject, "name", "?");
            case WAVE_SCHEDULE:
                return "schedule wave " + formatString(jsonObject, "wave", "?");
            case WAVE_RELEASE:
                return "release wave " + formatString(jsonObject, "wave", "?");
            case WAVE_VOID:
                return "void wave " + formatString(jsonObject, "wave", "?");
            case SERVER_COMMAND:
                return "run on "
                        + formatString(jsonObject, "server", "?")
                        + ": "
                        + formatString(jsonObject, "command", "?");
            case SERVER_ALERT:
                return "alert: " + formatString(jsonObject, "message", "?");
            case GDPR_EXPORT:
                return "export the data held on " + formatString(commonPlatform, jsonObject);
            case GDPR_ERASE:
                return "ERASE all data held on " + formatString(commonPlatform, jsonObject);
            case SERVER_WHITELIST:
                return "whitelist on " + formatString(jsonObject, "server", "?");
            case SERVER_REBOOT:
                return "reboot " + formatString(jsonObject, "server", "?");
            case SERVER_MOVE:
                return "move players to " + formatString(jsonObject, "server", "?");
            case CONFIG_FILE_SAVE:
                return "edit config " + formatString(jsonObject, "name", "?");
            case PUNISHMENT_EDIT:
                return "edit punishment " + formatString(jsonObject, "id", "?");
            case PERMISSION_APPLY:
                return (jsonObject.has("remove") && jsonObject.get("remove").getAsBoolean()
                                ? "remove "
                                : "add ")
                        + formatString(jsonObject, "permission", "?")
                        + " across ranks";
            case DEFINITION_RESTORE:
                return "restore " + formatString(jsonObject, "version", "?");
            case DEFINITION_SAVE:
                return "edit "
                        + formatString(jsonObject, "kind", "?")
                        + " "
                        + formatString(jsonObject);
            case DEFINITION_DELETE:
                return "delete "
                        + formatString(jsonObject, "kind", "?")
                        + " "
                        + formatString(jsonObject, "id", "?");
            default:
                return editorOp.name().toLowerCase();
        }
    }

    private static String formatString(CommonPlatform commonPlatform, JsonObject jsonObject) {
        UUID uUID;
        if ((uUID = fromString(jsonObject, "uuid")) == null) {
            return "?";
        }
        IProfile profile;
        return (profile = commonPlatform.getProfileHandler().getProfile(uUID)) == null
                ? uUID.toString()
                : profile.getName() + " (" + uUID + ")";
    }

    private static String append(JsonObject jsonObject) {
        String text;
        return (text = formatString(jsonObject, "reason", null)) == null ? "" : " for " + text;
    }

    private static String formatString(JsonObject jsonObject, String text, String text2) {
        JsonElement jsonElement;
        return (jsonElement = jsonObject.get(text)) != null && !jsonElement.isJsonNull()
                ? jsonElement.getAsString()
                : text2;
    }

    private static UUID fromString(JsonObject jsonObject, String text) {
        String text2;
        if ((text2 = formatString(jsonObject, text, null)) == null) {
            return null;
        }
        try {
            return UUID.fromString(text2);
        } catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
    }

    static {
        EditorOp editorOp = EditorOp.PUNISH;
        EditorOp[] editorOps2 = new EditorOp[22];
        editorOps2[0] = EditorOp.UNPUNISH;
        editorOps2[1] = EditorOp.GRANT_ADD;
        editorOps2[2] = EditorOp.GRANT_REMOVE;
        editorOps2[3] = EditorOp.REPORT_STATUS;
        editorOps2[4] = EditorOp.WAVE_CREATE;
        editorOps2[5] = EditorOp.WAVE_SCHEDULE;
        editorOps2[6] = EditorOp.WAVE_STAGE;
        editorOps2[7] = EditorOp.WAVE_RELEASE;
        editorOps2[8] = EditorOp.WAVE_VOID;
        editorOps2[9] = EditorOp.SERVER_COMMAND;
        editorOps2[10] = EditorOp.SERVER_ALERT;
        editorOps2[11] = EditorOp.DEFINITION_SAVE;
        editorOps2[12] = EditorOp.DEFINITION_DELETE;
        editorOps2[13] = EditorOp.PERMISSION_APPLY;
        editorOps2[14] = EditorOp.PUNISHMENT_EDIT;
        editorOps2[15] = EditorOp.CONFIG_FILE_SAVE;
        editorOps2[16] = EditorOp.SERVER_WHITELIST;
        editorOps2[17] = EditorOp.SERVER_REBOOT;
        editorOps2[18] = EditorOp.SERVER_MOVE;
        editorOps2[19] = EditorOp.GDPR_EXPORT;
        editorOps2[20] = EditorOp.GDPR_ERASE;
        editorOps2[21] = EditorOp.DEFINITION_RESTORE;
        editorOps = EnumSet.of(editorOp, editorOps2);
    }
}
