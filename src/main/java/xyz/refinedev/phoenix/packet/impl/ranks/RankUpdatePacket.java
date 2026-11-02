package xyz.refinedev.phoenix.packet.impl.ranks;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.handler.IRankHandler;
import xyz.refinedev.phoenix.packet.api.EntityUpdatePacket;
import xyz.refinedev.phoenix.profile.ProfileLookup;
import xyz.refinedev.phoenix.rank.IRank;

public class RankUpdatePacket extends EntityUpdatePacket<IRank> {

    public RankUpdatePacket(IRank rank, boolean matches) {
        super(rank, matches);
    }

    @Override
    public void onSend() {
        super.onSend();
        ProfileLookup.initialize();
    }

    @Override
    public void onReceive() {
        super.onReceive();
        ProfileLookup.initialize();
    }

    protected IRank find(IRank rank) {
        return handler().getRank(rank.getUuid());
    }

    protected void add(IRank rank) {
        handler().getRepository().addToCache(rank);
    }

    protected void remove(IRank rank) {
        handler().getRepository().removeFromCache(rank);
    }

    private static IRankHandler handler() {
        return PlatformGetter.getInstance().getRankHandler();
    }

    public RankUpdatePacket() {}
}
