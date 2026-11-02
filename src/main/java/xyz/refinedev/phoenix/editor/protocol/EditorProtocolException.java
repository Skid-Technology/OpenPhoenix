package xyz.refinedev.phoenix.editor.protocol;

public class EditorProtocolException extends RuntimeException {

    public EditorProtocolException(String text) {
        super(text);
    }

    public EditorProtocolException(String text, Throwable throwable) {
        super(text, throwable);
    }
}
