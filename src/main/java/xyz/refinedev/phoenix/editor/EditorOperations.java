package xyz.refinedev.phoenix.editor;

import com.google.gson.annotations.SerializedName;

public class EditorOperations {

    @SerializedName("id")
    private final String id;

    @SerializedName("at")
    private final long at;

    @SerializedName("source")
    private final String source;

    @SerializedName("action")
    private final String action;

    @SerializedName("entity")
    private final String entity;

    @SerializedName("name")
    private final String name;

    @SerializedName("actor")
    private final String actor;

    @SerializedName("detail")
    private final String detail;

    @SerializedName("restored")
    private final boolean restored;

    public String getId() {
        return this.id;
    }

    public long getAt() {
        return this.at;
    }

    public String getSource() {
        return this.source;
    }

    public String getAction() {
        return this.action;
    }

    public String getEntity() {
        return this.entity;
    }

    public String getName() {
        return this.name;
    }

    public String getActor() {
        return this.actor;
    }

    public String getDetail() {
        return this.detail;
    }

    public boolean isRestored() {
        return this.restored;
    }

    public EditorOperations(
            String id,
            long at,
            String source,
            String action,
            String entity,
            String name,
            String actor,
            String detail,
            boolean restored) {
        this.id = id;
        this.at = at;
        this.source = source;
        this.action = action;
        this.entity = entity;
        this.name = name;
        this.actor = actor;
        this.detail = detail;
        this.restored = restored;
    }
}
