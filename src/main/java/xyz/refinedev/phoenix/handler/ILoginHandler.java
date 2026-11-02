package xyz.refinedev.phoenix.handler;

import xyz.refinedev.phoenix.profile.login.ILogin;
import xyz.refinedev.phoenix.repository.ILoginRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ILoginHandler {

    void updateLogins(UUID uUID);

    CompletableFuture<List<ILogin>> updateLoginsAsync(UUID uUID);

    CompletableFuture<List<ILogin>> getDatabaseLogins(UUID uUID);

    List<ILogin> getDatabaseLoginsSync(UUID uUID);

    Map<UUID, List<ILogin>> getLoginsSync(Set<UUID> values);

    List<ILogin> getCachedLogins(UUID uUID);

    List<ILogin> getLogins(UUID uUID);

    CompletableFuture<Void> markPreviousAsLoggedOut(UUID uUID);

    void logoutPlayers(List<UUID> values);

    void logoutPlayer(UUID uUID);

    void logoutPlayerSync(UUID uUID);

    ILoginRepository getRepository();
}
