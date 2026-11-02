package xyz.refinedev.phoenix.packet.impl.server;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.handler.IFilterHandler;
import xyz.refinedev.phoenix.packet.api.EntityUpdatePacket;
import xyz.refinedev.phoenix.profile.filter.IFilter;

public class FilterUpdatePacket extends EntityUpdatePacket<IFilter> {

    public FilterUpdatePacket(IFilter filter, boolean matches) {
        super(filter, matches);
    }

    protected IFilter find(IFilter filter) {
        return handler().getFilterByName(filter.getName());
    }

    protected void add(IFilter filter) {
        handler().getRepository().addToCache(filter);
    }

    protected void remove(IFilter filter) {
        handler().getRepository().removeFromCache(filter);
    }

    private static IFilterHandler handler() {
        return PlatformGetter.getInstance().getFilterHandler();
    }

    public FilterUpdatePacket() {}
}
