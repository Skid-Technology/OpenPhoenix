package xyz.refinedev.phoenix.editor.op;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.editor.panel.PanelAudit;
import xyz.refinedev.phoenix.editor.panel.PanelPermissions;
import xyz.refinedev.phoenix.editor.protocol.EditorOp;

import java.util.Set;
import java.util.UUID;

public final class AuthorizedEditorOperations {

    private AuthorizedEditorOperations() {}

    public static void applyEditorOperationRegistry(
            EditorOperationRegistry editorOperationRegistry, CommonPlatform commonPlatform) {
        AltOperations altOperations = new AltOperations(commonPlatform);
        editorOperationRegistry.applyEditorOp(
                EditorOp.PROFILE_SEARCH, altOperations.getEditorOpHandler());
        editorOperationRegistry.applyEditorOp(
                EditorOp.PROFILE_FETCH, altOperations.getEditorOpHandler2());
    }

    public static void applyEditorOperationRegistryByPANEL_AUTH(
            EditorOperationRegistry editorOperationRegistry, CommonPlatform commonPlatform) {
        AltOperations altOperations = new AltOperations(commonPlatform);
        GrantOperations grantOperations = new GrantOperations(commonPlatform);
        StatusOperations statusOperations = new StatusOperations(commonPlatform);
        PanelOperations panelOperations = new PanelOperations(commonPlatform);
        editorOperationRegistry.applyEditorOp(
                EditorOp.PANEL_AUTH, panelOperations.getEditorOpHandler());
        editorOperationRegistry.applyEditorOp(
                EditorOp.PANEL_PERMISSION, panelOperations.getEditorOpHandlerByEditorOpHandler());
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.PROFILE_SEARCH,
                altOperations.getEditorOpHandler());
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.PROFILE_FETCH,
                altOperations.getEditorOpHandler2());
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.ALT_LINKS,
                altOperations.getEditorOpHandlerByEditorOpHandler());
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.PUNISHMENT_LIST,
                new PunishmentOperations(commonPlatform).getEditorOpHandler());
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.SERVER_STATUS,
                statusOperations.getEditorOpHandler());
        ReportOperations reportOperations = new ReportOperations(commonPlatform);
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.REPORT_LIST,
                reportOperations.getEditorOpHandler());
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.REPORT_STATUS,
                reportOperations.getEditorOpHandlerByEditorOpHandler());
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.CHATLOG_FETCH,
                new ChatLogOperations(commonPlatform).getEditorOpHandler());
        ChatSnapshotOperations chatSnapshotOperations = new ChatSnapshotOperations(commonPlatform);
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.SNAPSHOT_LIST,
                chatSnapshotOperations.getEditorOpHandler());
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.SNAPSHOT_FETCH,
                chatSnapshotOperations.getEditorOpHandlerByEditorOpHandler());
        VersionHistoryOperations versionHistoryOperations =
                new VersionHistoryOperations(commonPlatform);
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.DEFINITION_LIST,
                versionHistoryOperations.getEditorOpHandler());
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.DEFINITION_FETCH,
                versionHistoryOperations.getEditorOpHandlerByEditorOpHandler());
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.DEFINITION_SAVE,
                versionHistoryOperations.getEditorOpHandler2());
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.DEFINITION_DELETE,
                versionHistoryOperations.getEditorOpHandler3());
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.DEFINITION_HISTORY,
                versionHistoryOperations.getEditorOpHandler4());
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.DEFINITION_RESTORE,
                versionHistoryOperations.getEditorOpHandler5());
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.STAFF_ROSTER,
                new ProfileOperations(commonPlatform).getEditorOpHandler());
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.COMMAND_LOG,
                new PunishmentStatisticsOperations(commonPlatform).getEditorOpHandler());
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.RANK_LIST,
                new RankOperations(commonPlatform).getEditorOpHandler());
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.GRANT_ADD,
                grantOperations.getEditorOpHandler());
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.GRANT_REMOVE,
                grantOperations.getEditorOpHandlerByEditorOpHandler());
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.PUNISH,
                grantOperations.getEditorOpHandler2());
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.UNPUNISH,
                grantOperations.getEditorOpHandler3());
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry,
                commonPlatform,
                EditorOp.PUNISHMENT_EDIT,
                grantOperations.getEditorOpHandler4());
    }

    public static void applyEditorOperationRegistry(
            EditorOperationRegistry editorOperationRegistry,
            CommonPlatform commonPlatform,
            EditorOp editorOp,
            EditorOpHandler editorOpHandler) {
        applyEditorOperationRegistryByVoid(
                editorOperationRegistry, commonPlatform, editorOp, editorOpHandler);
    }

    private static void applyEditorOperationRegistryByVoid(
            EditorOperationRegistry editorOperationRegistry,
            CommonPlatform commonPlatform,
            EditorOp editorOp,
            EditorOpHandler editorOpHandler) {
        editorOperationRegistry.applyEditorOp(
                editorOp,
                (result, result2) -> {
                    UUID uUID = PanelPermissions.findUUID(result2);
                    if (PanelPermissions.ordinalByCounts(editorOp) != null
                            && !isKind(commonPlatform, uUID, editorOp, result2)) {
                        PanelAudit.applyCommonPlatform(commonPlatform, editorOp, result2, false);
                        throw new EditorOperationException(
                                "FORBIDDEN", "you need panel permission for " + editorOp.name());
                    } else {
                        JsonElement jsonElement = editorOpHandler.handle(result, result2);
                        PanelAudit.applyCommonPlatform(commonPlatform, editorOp, result2, true);
                        return jsonElement;
                    }
                });
    }

    private static boolean isKind(
            CommonPlatform commonPlatform, UUID uUID, EditorOp editorOp, JsonObject jsonObject) {
        Set<String> values;
        if ((values = PanelPermissions.findSet(commonPlatform, uUID)) == null
                || !values.contains(editorOp.name())) {
            return false;
        }
        String text;
        return PanelPermissions.ordinal(editorOp) && editorOp != EditorOp.DEFINITION_RESTORE
                ? (text = EditorPayload.formatString(jsonObject, "kind", null)) != null
                        && values.contains(PanelPermissions.formatStringByString(editorOp, text))
                : true;
    }
}
