package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.profile.chatsnapshot.ChatSnapshot;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatEntry;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatSnapshot;
import xyz.refinedev.phoenix.repository.IChatSnapshotRepository;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.LimitedSizeList;
import xyz.refinedev.phoenix.utils.repository.RepositoryFactory;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ChatSnapshotHandler implements IChatSnapshotHandler {

    private final IChatSnapshotRepository repository;

    private final LimitedSizeList<IChatEntry> chatEntries;

    public ChatSnapshotHandler(CommonPlatform commonPlatform) {
        this.chatEntries = new LimitedSizeList<>(100);
        this.repository = new RepositoryFactory(commonPlatform).chatSnapshots();
    }

    @NotNull
    @Override
    public List<IChatSnapshot> loadSnapshots(UUID uUID) {
        return this.repository.fetchAllSync(Filters.eq("requestedBy", uUID.toString()));
    }

    @Nullable
    @Override
    public IChatSnapshot createSnapshot(UUID uUID) {
        if (this.chatEntries.isEmpty()) {
            return null;
        }
        ChatSnapshot chatSnapshot = new ChatSnapshot(uUID, this.chatEntries, true);
        this.repository.save(chatSnapshot);
        return chatSnapshot;
    }

    @Nullable
    @Override
    public IChatSnapshot createSnapshot(UUID uUID, UUID uUID2) {
        ArrayList values;
        if ((values =
                        FastStreams.filterArrayList(
                                this.chatEntries, result -> result.getUuid().equals(uUID2)))
                .isEmpty()) {
            return null;
        }
        ChatSnapshot chatSnapshot = new ChatSnapshot(uUID, values, false);
        this.repository.save(chatSnapshot);
        return chatSnapshot;
    }

    @Override
    public void deleteSnapshot(IChatSnapshot chatSnapshot) {
        this.repository.delete(chatSnapshot);
    }

    @NotNull
    @Override
    public CompletableFuture<List<IChatSnapshot>> getByPlayer(UUID uUID) {
        return CompletableFuture.supplyAsync(
                () -> {
                    List<IChatSnapshot> values;
                    (values = this.loadSnapshots(uUID))
                            .sort(Comparator.comparingLong(IChatSnapshot::getCreatedOn).reversed());
                    return values;
                });
    }

    @Nullable
    @Override
    public IChatSnapshot getByNiceId(String text) {
        return this.repository.fetchSync(Filters.eq("niceId", text)).orElse(null);
    }

    @Override
    public IChatSnapshotRepository getRepository() {
        return this.repository;
    }

    @Override
    public LimitedSizeList<IChatEntry> getChatEntries() {
        return this.chatEntries;
    }
}
