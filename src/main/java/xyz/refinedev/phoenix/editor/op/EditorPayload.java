package xyz.refinedev.phoenix.editor.op;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.UUID;

public final class EditorPayload {

    private EditorPayload() {}

    public static String formatString(JsonObject jsonObject, String text) {
        JsonElement jsonElement;
        if ((jsonElement = jsonObject.get(text)) != null && !jsonElement.isJsonNull()) {
            String text2;
            if ((text2 = jsonElement.getAsString()).trim().isEmpty()) {
                throw new EditorOperationException("BAD_REQUEST", "field " + text + " was blank");
            } else {
                return text2;
            }
        } else {
            throw new EditorOperationException("BAD_REQUEST", "missing field " + text);
        }
    }

    public static UUID findUUID(JsonObject jsonObject, String text) {
        String text2 = formatString(jsonObject, text);
        try {
            return UUID.fromString(text2);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new EditorOperationException("BAD_REQUEST", "field " + text + " was not a uuid");
        }
    }

    public static String formatString(JsonObject jsonObject, String text, String text2) {
        JsonElement jsonElement;
        return (jsonElement = jsonObject.get(text)) != null && !jsonElement.isJsonNull()
                ? jsonElement.getAsString()
                : text2;
    }

    public static boolean matchesEnabled(JsonObject jsonObject, String text, boolean matches) {
        JsonElement jsonElement;
        return (jsonElement = jsonObject.get(text)) != null && !jsonElement.isJsonNull()
                ? jsonElement.getAsBoolean()
                : matches;
    }

    public static long calculateTime(JsonObject jsonObject, String text, long value) {
        JsonElement jsonElement;
        return (jsonElement = jsonObject.get(text)) != null && !jsonElement.isJsonNull()
                ? jsonElement.getAsLong()
                : value;
    }
}
