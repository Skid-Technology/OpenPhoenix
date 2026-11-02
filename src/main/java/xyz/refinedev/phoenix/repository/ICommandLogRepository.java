package xyz.refinedev.phoenix.repository;

import xyz.refinedev.phoenix.profile.commandlog.ICommandLog;
import xyz.refinedev.phoenix.utils.repository.IListRepository;

import java.util.UUID;

public interface ICommandLogRepository extends IListRepository<UUID, ICommandLog> {}
