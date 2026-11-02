package xyz.refinedev.phoenix.packet.impl.tags;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.handler.ICustomTagHandler;
import xyz.refinedev.phoenix.packet.api.EntityUpdatePacket;
import xyz.refinedev.phoenix.profile.tag.ICustomTag;

public class CustomTagUpdatePacket extends EntityUpdatePacket<ICustomTag> {

    public CustomTagUpdatePacket(ICustomTag customTag, boolean matches) {
        super(customTag, matches);
    }

    protected ICustomTag find(ICustomTag customTag) {
        return handler().getRepository().getFromCache(customTag.getName()).orElse(null);
    }

    protected void add(ICustomTag customTag) {
        handler().getRepository().addToCache(customTag);
    }

    protected void remove(ICustomTag customTag) {
        handler().getRepository().removeFromCache(customTag);
    }

    private static ICustomTagHandler handler() {
        return PlatformGetter.getInstance().getCustomTagHandler();
    }

    public CustomTagUpdatePacket() {}
}
