package xyz.refinedev.phoenix.repository;

import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.utils.repository.IOnlineCacheListRepository;

import java.util.UUID;

public interface IPunishmentRepository extends IOnlineCacheListRepository<UUID, IPunishment> {}
