package xyz.refinedev.phoenix.repository;

import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.utils.repository.IOnlineCacheListRepository;

import java.util.UUID;

public interface IGrantRepository extends IOnlineCacheListRepository<UUID, IGrant> {}
