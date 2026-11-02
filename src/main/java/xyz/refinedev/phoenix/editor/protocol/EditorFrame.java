package xyz.refinedev.phoenix.editor.protocol;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class EditorFrame {

    private final String text;

    private final EditorFrameKind editorFrameKind;

    private final EditorOp editorOp;

    private final String textString;

    private final JsonElement jsonElement;

    EditorFrame(
            String text,
            EditorFrameKind editorFrameKind2,
            EditorOp editorOp2,
            String textString,
            JsonElement jsonElement2) {
        this.text = text;
        this.editorFrameKind = editorFrameKind2;
        this.editorOp = editorOp2;
        this.textString = textString;
        this.jsonElement = jsonElement2;
    }

    public static EditorFrame of(String text, EditorOp editorOp, JsonElement jsonElement) {
        return new EditorFrame(
                text, EditorFrameKind.REQUEST, editorOp, editorOp.name(), jsonElement);
    }

    public static EditorFrame of(String text, JsonElement jsonElement) {
        return new EditorFrame(text, EditorFrameKind.RESPONSE, null, null, jsonElement);
    }

    public static EditorFrame of(EditorOp editorOp, JsonElement jsonElement) {
        return new EditorFrame(null, EditorFrameKind.EVENT, editorOp, editorOp.name(), jsonElement);
    }

    public static EditorFrame of(String text, String text2, String text3) {
        JsonObject jsonObject;
        (jsonObject = new JsonObject()).addProperty("code", text2);
        jsonObject.addProperty("message", text3);
        return new EditorFrame(text, EditorFrameKind.ERROR, null, null, jsonObject);
    }

    public String getString() {
        return this.formatString("code");
    }

    public String getStringByString() {
        return this.formatString("message");
    }

    private String formatString(String text) {
        if (this.jsonElement != null && this.jsonElement.isJsonObject()) {
            JsonElement jsonElement2;
            return (jsonElement2 = this.jsonElement.getAsJsonObject().get(text)) != null
                            && !jsonElement2.isJsonNull()
                    ? jsonElement2.getAsString()
                    : null;
        } else {
            return null;
        }
    }

    public String getText() {
        return this.text;
    }

    public EditorFrameKind getEditorFrameKind() {
        return this.editorFrameKind;
    }

    public EditorOp getEditorOp() {
        return this.editorOp;
    }

    public String getTextString() {
        return this.textString;
    }

    public JsonElement getJsonElement() {
        return this.jsonElement;
    }
}
