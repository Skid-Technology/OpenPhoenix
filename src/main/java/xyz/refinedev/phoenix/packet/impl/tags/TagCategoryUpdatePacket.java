package xyz.refinedev.phoenix.packet.impl.tags;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.handler.ITagHandler;
import xyz.refinedev.phoenix.packet.api.EntityUpdatePacket;
import xyz.refinedev.phoenix.profile.tag.ITagCategory;

public class TagCategoryUpdatePacket extends EntityUpdatePacket<ITagCategory> {

    public TagCategoryUpdatePacket(ITagCategory tagCategory, boolean matches) {
        super(tagCategory, matches);
    }

    protected ITagCategory find(ITagCategory tagCategory) {
        return handler().getTagCategory(tagCategory.getName());
    }

    protected void add(ITagCategory tagCategory) {
        handler().getCategoryRepository().addToCache(tagCategory);
    }

    protected void remove(ITagCategory tagCategory) {
        handler().getCategoryRepository().removeFromCache(tagCategory);
    }

    private static ITagHandler handler() {
        return PlatformGetter.getInstance().getTagHandler();
    }

    public TagCategoryUpdatePacket() {}
}
