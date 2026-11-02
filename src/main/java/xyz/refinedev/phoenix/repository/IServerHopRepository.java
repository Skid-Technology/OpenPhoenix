package xyz.refinedev.phoenix.repository;

import xyz.refinedev.phoenix.profile.hop.IServerHop;
import xyz.refinedev.phoenix.utils.repository.ICacheListRepository;

import java.util.UUID;

public interface IServerHopRepository extends ICacheListRepository<UUID, IServerHop> {}
