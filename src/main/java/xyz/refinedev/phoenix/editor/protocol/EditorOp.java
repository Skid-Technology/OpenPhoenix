package xyz.refinedev.phoenix.editor.protocol;

public enum EditorOp {
    SESSION_OPEN,
    SESSION_RESUME,
    SESSION_PULL,
    SESSION_CLOSE,
    PROFILE_SEARCH,
    PROFILE_FETCH,
    GRANT_ADD,
    GRANT_REMOVE,
    PUNISH,
    UNPUNISH,
    SERVER_STATUS,
    PANEL_ANNOUNCE,
    ALT_LINKS,
    PUNISHMENT_LIST,
    PANEL_AUTH,
    PANEL_REVOKE,
    PANEL_PERMISSION,
    REPORT_LIST,
    REPORT_STATUS,
    CHATLOG_FETCH,
    RANK_LIST,
    WAVE_LIST,
    WAVE_CREATE,
    WAVE_SCHEDULE,
    WAVE_STAGE,
    WAVE_RELEASE,
    WAVE_VOID,
    SERVER_COMMAND,
    SERVER_ALERT,
    SERVER_WHITELIST,
    SERVER_REBOOT,
    SERVER_MOVE,
    SERVER_HEALTH,
    PUNISHMENT_EDIT,
    GDPR_EXPORT,
    GDPR_ERASE,
    STAFF_ROSTER,
    PERMISSION_BROWSE,
    PERMISSION_APPLY,
    CONFIG_FILE_LIST,
    CONFIG_FILE_FETCH,
    CONFIG_FILE_SAVE,
    DEFINITION_HISTORY,
    DEFINITION_RESTORE,
    DEFINITION_LIST,
    DEFINITION_FETCH,
    DEFINITION_SAVE,
    DEFINITION_DELETE,
    SNAPSHOT_LIST,
    SNAPSHOT_FETCH,
    COMMAND_LOG;

    public static EditorOp parse(String text) {
        if (text == null) {
            return null;
        }
        EditorOp[] editorOps;
        int value = (editorOps = values()).length;
        for (int index = 0; index < value; index++) {
            EditorOp editorOp;
            if ((editorOp = editorOps[index]).name().equalsIgnoreCase(text)) {
                return editorOp;
            }
        }
        return null;
    }
}
