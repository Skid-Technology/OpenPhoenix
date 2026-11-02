package xyz.refinedev.phoenix.editor.op;

public class EditorOperationException extends RuntimeException {

    private final String text;

    public EditorOperationException(String text, String text3) {
        super(text3);
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}
