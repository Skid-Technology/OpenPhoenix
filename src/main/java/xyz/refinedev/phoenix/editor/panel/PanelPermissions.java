package xyz.refinedev.phoenix.editor.panel;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.editor.op.EditorOperationException;
import xyz.refinedev.phoenix.editor.protocol.EditorOp;
import xyz.refinedev.phoenix.profile.IProfile;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class PanelPermissions {

    private static final Map<String, String> stringsByName = new LinkedHashMap<>();

    public static final char character = ':';

    private PanelPermissions() {}

    public static Set<String> keySet() {
        return stringsByName.keySet();
    }

    public static boolean ordinal(EditorOp editorOp) {
        switch (editorOp) {
            case DEFINITION_LIST:
            case DEFINITION_FETCH:
            case DEFINITION_SAVE:
            case DEFINITION_DELETE:
            case DEFINITION_RESTORE:
                return true;
            default:
                return false;
        }
    }

    public static String formatString(EditorOp editorOp, String text) {
        String text2;
        if ((text2 = stringsByName.get(text)) == null) {
            return null;
        }
        String text3;
        return (text3 = ordinalByCounts(editorOp)) != null && text3.endsWith(".edit")
                ? text2 + ".edit"
                : text2;
    }

    public static String formatStringByString(EditorOp editorOp, String text) {
        return editorOp.name() + ':' + text;
    }

    public static Set<String> findSet(CommonPlatform commonPlatform, UUID uUID) {
        Set<String> values;
        if ((values = PanelPermissionCache.currentTimeMillis(uUID)) != null) {
            return values;
        }
        IProfile profile;
        if ((profile = commonPlatform.getProfileHandler().getProfile(uUID)) == null) {
            return null;
        }
        values = findSet(profile);
        PanelPermissionCache.applyUUID(uUID, values);
        return values;
    }

    public static boolean contains(
            CommonPlatform commonPlatform, UUID uUID, EditorOp editorOp, String text) {
        Set<String> values;
        return (values = findSet(commonPlatform, uUID)) != null
                && values.contains(formatStringByString(editorOp, text));
    }

    public static String ordinalByCounts(EditorOp editorOp) {
        switch (editorOp) {
            case DEFINITION_LIST:
            case DEFINITION_FETCH:
            case DEFINITION_HISTORY:
                return "core.panel.definitions";
            case DEFINITION_SAVE:
            case DEFINITION_DELETE:
            case DEFINITION_RESTORE:
                return "core.panel.definitions.edit";
            case PROFILE_SEARCH:
            case PROFILE_FETCH:
                return "core.panel.lookup";
            case PUNISHMENT_LIST:
                return "core.panel.punishments";
            case SERVER_STATUS:
                return "core.panel.status";
            case ALT_LINKS:
                return "core.panel.alts";
            case REPORT_LIST:
                return "core.panel.reports";
            case REPORT_STATUS:
                return "core.panel.reports.manage";
            case CHATLOG_FETCH:
                return "core.panel.chatlogs";
            case SNAPSHOT_LIST:
            case SNAPSHOT_FETCH:
                return "core.panel.snapshots";
            case COMMAND_LOG:
                return "core.panel.commandlogs";
            case RANK_LIST:
            case GRANT_ADD:
            case GRANT_REMOVE:
                return "core.panel.grant";
            case PUNISH:
            case UNPUNISH:
                return "core.panel.punish";
            case SERVER_COMMAND:
                return "core.panel.command";
            case SERVER_ALERT:
                return "core.panel.alert";
            case SERVER_WHITELIST:
            case SERVER_REBOOT:
            case SERVER_MOVE:
                return "core.panel.servers.manage";
            case SERVER_HEALTH:
                return "core.panel.health";
            case PUNISHMENT_EDIT:
                return "core.panel.punish.edit";
            case GDPR_EXPORT:
                return "core.panel.gdpr";
            case GDPR_ERASE:
                return "core.panel.gdpr.erase";
            case STAFF_ROSTER:
                return "core.panel.staff";
            case PERMISSION_BROWSE:
                return "core.panel.permissions";
            case PERMISSION_APPLY:
                return "core.panel.permissions.edit";
            case CONFIG_FILE_LIST:
            case CONFIG_FILE_FETCH:
                return "core.panel.config";
            case CONFIG_FILE_SAVE:
                return "core.panel.config.edit";
            case WAVE_LIST:
                return "core.panel.waves";
            case WAVE_CREATE:
            case WAVE_SCHEDULE:
            case WAVE_STAGE:
            case WAVE_RELEASE:
            case WAVE_VOID:
                return "core.panel.waves.manage";
            default:
                return null;
        }
    }

    public static Set<String> findSet(IProfile profile) {
        profile.updatePermissions();
        HashSet<String> values2 = new HashSet<>();
        EditorOp[] editorOps;
        int value = (editorOps = EditorOp.values()).length;
        for (int index = 0; index < value; index++) {
            EditorOp editorOp;
            String text;
            if ((text = ordinalByCounts(editorOp = editorOps[index])) != null) {
                boolean matches = profile.checkPermission(text, false);
                if (!ordinal(editorOp)) {
                    if (matches) {
                        values2.add(editorOp.name());
                    }
                } else {
                    for (String text2 : stringsByName.keySet()) {
                        if (matches
                                || profile.checkPermission(formatString(editorOp, text2), false)) {
                            values2.add(formatStringByString(editorOp, text2));
                            values2.add(editorOp.name());
                        }
                    }
                }
            }
        }
        return values2;
    }

    public static boolean matchesEnabled(CommonPlatform commonPlatform, UUID uUID, String text) {
        IProfile profile;
        if ((profile = commonPlatform.getProfileHandler().getProfile(uUID)) == null) {
            return false;
        }
        if (profile.checkPermission(text, false)) {
            return true;
        }
        profile.updatePermissions();
        return profile.checkPermission(text, false);
    }

    public static UUID findUUID(JsonObject jsonObject) {
        JsonElement jsonElement;
        if ((jsonElement = jsonObject.get("actorUuid")) != null && !jsonElement.isJsonNull()) {
            try {
                return UUID.fromString(jsonElement.getAsString());
            } catch (IllegalArgumentException illegalArgumentException) {
                throw new EditorOperationException("FORBIDDEN", "request named no operator");
            }
        } else {
            throw new EditorOperationException("FORBIDDEN", "request named no operator");
        }
    }

    static {
        stringsByName.put("rank", "core.panel.ranks");
        stringsByName.put("tag", "core.panel.tags");
        stringsByName.put("filter", "core.panel.filters");
        stringsByName.put("ladder", "core.panel.ladders");
        stringsByName.put("category", "core.panel.reportcategories");
        stringsByName.put("customtag", "core.panel.customtags");
    }
}
