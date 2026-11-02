package xyz.refinedev.phoenix.editor.protocol;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import xyz.refinedev.phoenix.utils.JsonParser;

public final class EditorFrameCodec {

    private EditorFrameCodec() {}

    public static String formatString(EditorFrame editorFrame) {
        JsonObject jsonObject = new JsonObject();
        if (editorFrame.getText() != null) {
            jsonObject.addProperty("id", editorFrame.getText());
        }
        jsonObject.addProperty("kind", editorFrame.getEditorFrameKind().name());
        if (editorFrame.getTextString() != null) {
            jsonObject.addProperty("op", editorFrame.getTextString());
        }
        jsonObject.add(
                "payload",
                (JsonElement)
                        (editorFrame.getJsonElement() == null
                                ? new JsonObject()
                                : editorFrame.getJsonElement()));
        return jsonObject.toString();
    }

    public static EditorFrame create(String text) {
        JsonObject jsonObject;
        try {
            JsonElement jsonElement;
            if (!(jsonElement = JsonParser.findJsonElement(text)).isJsonObject()) {
                throw new EditorProtocolException("frame was not an object");
            }
            jsonObject = jsonElement.getAsJsonObject();
        } catch (JsonParseException jsonParseException) {
            throw new EditorProtocolException("frame was not valid json", jsonParseException);
        }
        JsonElement jsonElement2;
        if ((jsonElement2 = jsonObject.get("kind")) != null && !jsonElement2.isJsonNull()) {
            EditorFrameKind editorFrameKind;
            try {
                editorFrameKind = EditorFrameKind.valueOf(jsonElement2.getAsString());
            } catch (IllegalArgumentException illegalArgumentException) {
                throw new EditorProtocolException(
                        "unknown frame kind " + jsonElement2.getAsString(),
                        illegalArgumentException);
            }
            String text2 = formatString(jsonObject, "op");
            JsonElement jsonElement3 = jsonObject.get("payload");
            return new EditorFrame(
                    formatString(jsonObject, "id"),
                    editorFrameKind,
                    EditorOp.parse(text2),
                    text2,
                    (JsonElement) (jsonElement3 == null ? new JsonObject() : jsonElement3));
        } else {
            throw new EditorProtocolException("frame had no kind");
        }
    }

    private static String formatString(JsonObject jsonObject, String text) {
        JsonElement jsonElement;
        return (jsonElement = jsonObject.get(text)) != null && !jsonElement.isJsonNull()
                ? jsonElement.getAsString()
                : null;
    }
}
