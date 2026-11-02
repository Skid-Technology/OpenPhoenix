package xyz.refinedev.phoenix.repository;

import xyz.refinedev.phoenix.report.IReport;
import xyz.refinedev.phoenix.utils.repository.IListRepository;

import java.util.UUID;

public interface IReportRepository extends IListRepository<UUID, IReport> {}
