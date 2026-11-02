package xyz.refinedev.phoenix.editor;

import java.util.UUID;

public class EditorSession {

    private final String text;

    private final String textString;

    private volatile UUID id;

    private volatile String text2;

    private volatile long time;

    public EditorSession(String text, String textString, UUID id, String text2) {
        this.text = text;
        this.textString = textString;
        this.id = id;
        this.text2 = text2;
        this.time = System.currentTimeMillis();
    }

    public void updateUUID(UUID id, String text2) {
        this.id = id;
        this.text2 = text2;
    }

    public void initialize() {
        this.setTime(System.currentTimeMillis());
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean matchesEnabled(long value, long value2) {
        return value - this.time > value2;
    }

    public String getText() {
        return this.text;
    }

    public String getTextString() {
        return this.textString;
    }

    public UUID getId() {
        return this.id;
    }

    public String getText2() {
        return this.text2;
    }

    public long getTime() {
        return this.time;
    }
}
