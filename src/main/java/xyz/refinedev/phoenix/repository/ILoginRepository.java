package xyz.refinedev.phoenix.repository;

import xyz.refinedev.phoenix.profile.login.ILogin;
import xyz.refinedev.phoenix.utils.repository.ICacheListRepository;

import java.util.UUID;

public interface ILoginRepository extends ICacheListRepository<UUID, ILogin> {}
