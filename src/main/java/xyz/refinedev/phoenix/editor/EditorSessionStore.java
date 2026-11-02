package xyz.refinedev.phoenix.editor;

import java.util.List;

public class EditorSessionStore {

    private final EditorSessionFactory editorSessionFactory;

    private final List<EditorOperations> editorOperationses;

    public EditorSessionFactory getEditorSessionFactory() {
        return this.editorSessionFactory;
    }

    public List<EditorOperations> getEditorOperationses() {
        return this.editorOperationses;
    }

    public EditorSessionStore(
            EditorSessionFactory editorSessionFactory2, List<EditorOperations> editorOperationses) {
        this.editorSessionFactory = editorSessionFactory2;
        this.editorOperationses = editorOperationses;
    }
}
