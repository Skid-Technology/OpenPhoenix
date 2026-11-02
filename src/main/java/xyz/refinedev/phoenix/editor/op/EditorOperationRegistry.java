package xyz.refinedev.phoenix.editor.op;

import com.google.gson.JsonElement;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.editor.EditorSession;
import xyz.refinedev.phoenix.editor.protocol.EditorFrame;
import xyz.refinedev.phoenix.editor.protocol.EditorOp;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;

public class EditorOperationRegistry {

    private final Map<EditorOp, EditorOpHandler> editorOpHandlersByEditorOp =
            new EnumMap<>(EditorOp.class);

    public void applyEditorOp(EditorOp editorOp, EditorOpHandler editorOpHandler) {
        this.editorOpHandlersByEditorOp.put(editorOp, editorOpHandler);
    }

    public EditorFrame findEditorFrame(EditorSession editorSession, EditorFrame editorFrame) {
        EditorOpHandler editorOpHandler;
        if ((editorOpHandler =
                        editorFrame.getEditorOp() == null
                                ? null
                                : this.editorOpHandlersByEditorOp.get(editorFrame.getEditorOp()))
                == null) {
            return EditorFrame.of(
                    editorFrame.getText(),
                    "UNKNOWN_OP",
                    "no handler for op " + editorFrame.getTextString());
        }
        if (editorFrame.getJsonElement() != null && editorFrame.getJsonElement().isJsonObject()) {
            try {
                JsonElement jsonElement =
                        editorOpHandler.handle(
                                editorSession, editorFrame.getJsonElement().getAsJsonObject());
                return EditorFrame.of(editorFrame.getText(), jsonElement);
            } catch (EditorOperationException editorOperationException) {
                return EditorFrame.of(
                        editorFrame.getText(),
                        editorOperationException.getText(),
                        editorOperationException.getMessage());
            } catch (Exception exception) {
                Exception exception3 = exception;
                try {
                    CommonPlatform commonPlatform;
                    if ((commonPlatform = PlatformGetter.getInstance()) != null
                            && commonPlatform.getLogger() != null) {
                        commonPlatform
                                .getLogger()
                                .log(
                                        Level.WARNING,
                                        "Panel op " + editorFrame.getEditorOp() + " failed",
                                        exception3);
                    }
                } catch (Exception exception2) {
                }
                return EditorFrame.of(
                        editorFrame.getText(),
                        "INTERNAL",
                        "Something went wrong on the server. It has been logged.");
            }
        } else {
            return EditorFrame.of(
                    editorFrame.getText(), "BAD_REQUEST", "payload was not an object");
        }
    }
}
