package xyz.refinedev.phoenix.repository;

import xyz.refinedev.phoenix.profile.chatsnapshot.IChatSnapshot;
import xyz.refinedev.phoenix.utils.repository.IListRepository;

import java.util.UUID;

public interface IChatSnapshotRepository extends IListRepository<UUID, IChatSnapshot> {}
