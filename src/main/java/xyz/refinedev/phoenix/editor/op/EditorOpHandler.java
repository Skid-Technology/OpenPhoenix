package xyz.refinedev.phoenix.editor.op;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import xyz.refinedev.phoenix.editor.EditorSession;

public interface EditorOpHandler {

    JsonElement handle(EditorSession editorSession, JsonObject jsonObject);
}
