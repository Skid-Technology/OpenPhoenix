package xyz.refinedev.phoenix.packet.impl.tags;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.handler.ITagHandler;
import xyz.refinedev.phoenix.packet.api.EntityUpdatePacket;
import xyz.refinedev.phoenix.profile.tag.ITag;

public class TagUpdatePacket extends EntityUpdatePacket<ITag> {

    public TagUpdatePacket(ITag tag, boolean matches) {
        super(tag, matches);
    }

    protected ITag find(ITag tag) {
        return handler().getTag(tag.getName());
    }

    protected void add(ITag tag) {
        handler().getTagRepository().addToCache(tag);
    }

    protected void remove(ITag tag) {
        handler().getTagRepository().removeFromCache(tag);
    }

    private static ITagHandler handler() {
        return PlatformGetter.getInstance().getTagHandler();
    }

    public TagUpdatePacket() {}
}
