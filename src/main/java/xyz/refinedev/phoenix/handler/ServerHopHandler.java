package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.profile.hop.IServerHop;
import xyz.refinedev.phoenix.profile.hop.ServerHop;
import xyz.refinedev.phoenix.repository.IServerHopRepository;
import xyz.refinedev.phoenix.utils.repository.RepositoryFactory;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ServerHopHandler implements IServerHopHandler {

    private final IServerHopRepository repository;

    private final CommonPlatform platform;

    private static final String TARGET_TEXT = "target";

    public ServerHopHandler(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
        this.repository = new RepositoryFactory(commonPlatform).serverHops();
    }

    @Override
    public void recordHop(UUID uUID, @Nullable String text, String text2) {
        CompletableFuture.runAsync(() -> this.recordHopSync(uUID, text, text2));
    }

    @Override
    public void recordHopSync(UUID uUID, @Nullable String text, String text2) {
        ServerHop serverHop = new ServerHop(uUID, text, text2);
        this.repository.saveSync(serverHop.getId(), serverHop);
    }

    @Override
    public List<IServerHop> getHops(UUID uUID) {
        List<IServerHop> values;
        if ((values = this.repository.getListFromCache(uUID)) != null) {
            return values;
        }
        values = this.repository.fetchAllSync(Filters.eq(TARGET_TEXT, uUID.toString()));
        this.repository.putInCache(uUID, values);
        return values;
    }

    @Override
    public IServerHopRepository getRepository() {
        return this.repository;
    }

    public CommonPlatform getPlatform() {
        return this.platform;
    }
}
