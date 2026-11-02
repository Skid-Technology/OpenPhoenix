package xyz.refinedev.phoenix.repository;

import xyz.refinedev.phoenix.profile.punishment.banwave.IBanWave;
import xyz.refinedev.phoenix.utils.repository.IListRepository;

import java.util.UUID;

public interface IBanWaveRepository extends IListRepository<UUID, IBanWave> {}
