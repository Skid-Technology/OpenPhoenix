package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatEntry;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatSnapshot;
import xyz.refinedev.phoenix.repository.IChatSnapshotRepository;
import xyz.refinedev.phoenix.utils.LimitedSizeList;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IChatSnapshotHandler {

    @NotNull
    List<IChatSnapshot> loadSnapshots(UUID uUID);

    @Nullable
    IChatSnapshot createSnapshot(UUID uUID);

    @Nullable
    IChatSnapshot createSnapshot(UUID uUID, UUID uUID2);

    void deleteSnapshot(IChatSnapshot chatSnapshot);

    @NotNull
    CompletableFuture<List<IChatSnapshot>> getByPlayer(UUID uUID);

    @Nullable
    IChatSnapshot getByNiceId(String text);

    IChatSnapshotRepository getRepository();

    LimitedSizeList<IChatEntry> getChatEntries();
}
