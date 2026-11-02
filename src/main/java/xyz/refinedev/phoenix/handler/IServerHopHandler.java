package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.profile.hop.IServerHop;
import xyz.refinedev.phoenix.repository.IServerHopRepository;

import java.util.List;
import java.util.UUID;

public interface IServerHopHandler {

    void recordHop(UUID uUID, @Nullable String text, String text2);

    void recordHopSync(UUID uUID, @Nullable String text, String text2);

    List<IServerHop> getHops(UUID uUID);

    IServerHopRepository getRepository();
}
