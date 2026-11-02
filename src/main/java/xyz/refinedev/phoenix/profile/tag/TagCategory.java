package xyz.refinedev.phoenix.profile.tag;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.profile.TrackedEntity;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.Processable;

import java.util.ArrayList;
import java.util.List;

public class TagCategory extends TrackedEntity implements ITagCategory, Processable {

    @SerializedName("name")
    private final String name;

    @SerializedName("priority")
    private int priority = 0;

    @SerializedName("tags")
    private List<String> asTags = new ArrayList<>();

    @SerializedName("def")
    private boolean def = false;

    @SerializedName("visible")
    private boolean visible = false;

    @Override
    public List<ITag> getAsTags() {
        return FastStreams.mapArrayList(
                this.asTags, result -> PlatformGetter.getInstance().getTagHandler().getTag(result));
    }

    @Override
    public void postProcess() {
        this.visible = false;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object result) {
        if (this == result) {
            return true;
        } else if (result != null && this.getClass() == result.getClass()) {
            TagCategory tagCategory = (TagCategory) result;
            return this.name.equals(tagCategory.name);
        } else {
            return false;
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public List<String> getTags() {
        return this.asTags;
    }

    @Override
    public boolean isDef() {
        return this.def;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public void setTags(List<String> asTags) {
        this.asTags = asTags;
    }

    @Override
    public void setDef(boolean def) {
        this.def = def;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public TagCategory(String name) {
        this.name = name;
    }
}
