package xyz.refinedev.phoenix.editor;

public interface EditorSocketListener {

    void onOpen();

    void onClosed(String text);

    void onFailure(Throwable throwable);

    default void onFailure(Throwable throwable, Integer value) {
        this.onFailure(throwable);
    }
}
