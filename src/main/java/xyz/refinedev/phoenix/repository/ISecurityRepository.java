package xyz.refinedev.phoenix.repository;

import xyz.refinedev.phoenix.profile.security.ISecurityUser;
import xyz.refinedev.phoenix.utils.repository.IMapRepository;

import java.util.UUID;

public interface ISecurityRepository extends IMapRepository<UUID, ISecurityUser> {}
