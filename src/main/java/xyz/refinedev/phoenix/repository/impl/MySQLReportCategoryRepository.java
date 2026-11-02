package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.report.IReportCategory;
import xyz.refinedev.phoenix.report.ReportCategory;
import xyz.refinedev.phoenix.repository.IReportCategoryRepository;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.repository.mysql.MySQLListRepository;

public class MySQLReportCategoryRepository extends MySQLListRepository<String, IReportCategory>
        implements IReportCategoryRepository {

    public MySQLReportCategoryRepository(Table table) {
        super(ReportCategory.class, table, IReportCategory::getId);
    }
}
