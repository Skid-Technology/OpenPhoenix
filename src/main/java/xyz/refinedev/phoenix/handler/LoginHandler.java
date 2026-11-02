package xyz.refinedev.phoenix.handler;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.handler.database.AbstractDatabaseHandler;
import xyz.refinedev.phoenix.packet.impl.profile.LoginUpdatePacket;
import xyz.refinedev.phoenix.profile.login.ILogin;
import xyz.refinedev.phoenix.repository.ILoginRepository;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.repository.RepositoryFactory;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;
import xyz.refinedev.phoenix.utils.repository.sorting.api.Sorts;
import xyz.refinedev.phoenix.utils.repository.updates.api.Updates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LoginHandler implements ILoginHandler {

    private final ILoginRepository repository;

    private final CommonPlatform platform;

    private static final long time = 1000L;

    public LoginHandler(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
        this.repository = new RepositoryFactory(commonPlatform).logins();
    }

    @Override
    public void updateLogins(UUID uUID) {
        List<ILogin> databaseLoginsSync = this.getDatabaseLoginsSync(uUID);
        this.repository.putInCache(uUID, databaseLoginsSync);
    }

    @Override
    public CompletableFuture<List<ILogin>> updateLoginsAsync(UUID uUID) {
        CompletableFuture<List<ILogin>> databaseLogins = this.getDatabaseLogins(uUID);
        return databaseLogins.handleAsync(
                (result, result2) -> {
                    this.repository.putInCache(uUID, (List<ILogin>) result);
                    return (List<ILogin>) result;
                });
    }

    @Override
    public CompletableFuture<List<ILogin>> getDatabaseLogins(UUID uUID) {
        return this.repository.fetchAll(Filters.eq("target", uUID.toString()));
    }

    @Override
    public List<ILogin> getDatabaseLoginsSync(UUID uUID) {
        return this.repository.fetchAllSync(Filters.eq("target", uUID.toString()));
    }

    @Override
    public Map<UUID, List<ILogin>> getLoginsSync(Set<UUID> values) {
        ArrayList values2 = FastStreams.mapArrayList(values, UUID::toString);
        HashMap<UUID, List<ILogin>> entries = new HashMap<>();
        for (ILogin login : this.repository.fetchAllSync(Filters.in("target", values2))) {
            entries.computeIfAbsent(login.getTarget(), result -> new ArrayList<>()).add(login);
        }
        return entries;
    }

    @Override
    public List<ILogin> getCachedLogins(UUID uUID) {
        List<ILogin> values;
        return (values = this.repository.getListFromCache(uUID)) == null
                ? Collections.emptyList()
                : values;
    }

    @Override
    public List<ILogin> getLogins(UUID uUID) {
        List<ILogin> values;
        if ((values = this.repository.getListFromCache(uUID)) != null) {
            return values;
        }
        values = this.getDatabaseLoginsSync(uUID);
        this.repository.putInCache(uUID, values);
        return values;
    }

    @Override
    public CompletableFuture<Void> markPreviousAsLoggedOut(UUID uUID) {
        return CompletableFuture.runAsync(
                () ->
                        this.repository.deleteSync(
                                Filters.and(
                                        Filters.eq("target", uUID.toString()),
                                        Filters.eq("logout", "0"))),
                AbstractDatabaseHandler.DB_EXECUTOR);
    }

    @Override
    public void logoutPlayers(List<UUID> values) {
        ArrayList values2 = FastStreams.mapArrayList(values, UUID::toString);
        this.repository.updateMany(
                Filters.and(Filters.in("target", values2), Filters.eq("logout", "0")),
                Updates.set("logout", String.valueOf(System.currentTimeMillis())));
    }

    @Override
    public void logoutPlayer(UUID uUID) {
        CompletableFuture.runAsync(
                () -> this.logoutPlayerSync(uUID), AbstractDatabaseHandler.DB_EXECUTOR);
    }

    @Override
    public void logoutPlayerSync(UUID uUID) {
        ILogin login;
        if ((login =
                        this.repository
                                .fetchSync(
                                        Filters.eq("target", uUID.toString()),
                                        Sorts.descending("login"))
                                .orElse(null))
                != null) {
            if (!login.hasLoggedOut()) {
                long now = System.currentTimeMillis();
                login.setLogout(now);
                this.repository.saveSync(login.getId(), login);
                this.platform.getPacketHandler().sendSync(new LoginUpdatePacket(uUID));
                if (login.getLogin() > 0L) {
                    this.platform
                            .getMetricsHandler()
                            .metric("phoenix_session")
                            .findMetric("seconds", (now - login.getLogin()) / time)
                            .initializeByMetricsHandler();
                }
            }
        }
    }

    @Override
    public ILoginRepository getRepository() {
        return this.repository;
    }

    public CommonPlatform getPlatform() {
        return this.platform;
    }
}
