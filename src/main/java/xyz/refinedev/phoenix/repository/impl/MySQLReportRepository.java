package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.report.IReport;
import xyz.refinedev.phoenix.report.Report;
import xyz.refinedev.phoenix.repository.IReportRepository;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.repository.mysql.MySQLListRepository;

import java.util.UUID;

public class MySQLReportRepository extends MySQLListRepository<UUID, IReport>
        implements IReportRepository {

    public MySQLReportRepository(Table table) {
        super(Report.class, table, IReport::getId);
    }
}
