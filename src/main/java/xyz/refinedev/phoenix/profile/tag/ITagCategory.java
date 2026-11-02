package xyz.refinedev.phoenix.profile.tag;

import java.util.List;

public interface ITagCategory {

    List<ITag> getAsTags();

    @Override
    int hashCode();

    @Override
    boolean equals(Object result);

    String getName();

    int getPriority();

    List<String> getTags();

    boolean isDef();

    boolean isVisible();

    void setPriority(int value);

    void setTags(List<String> values);

    void setDef(boolean matches);

    void setVisible(boolean matches);
}
