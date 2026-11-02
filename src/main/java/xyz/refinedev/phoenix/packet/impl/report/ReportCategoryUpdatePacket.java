package xyz.refinedev.phoenix.packet.impl.report;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.packet.api.EntityUpdatePacket;
import xyz.refinedev.phoenix.report.IReportCategory;
import xyz.refinedev.phoenix.repository.IReportCategoryRepository;

public class ReportCategoryUpdatePacket extends EntityUpdatePacket<IReportCategory> {

    public ReportCategoryUpdatePacket(IReportCategory reportCategory, boolean matches) {
        super(reportCategory, matches);
    }

    protected IReportCategory find(IReportCategory reportCategory) {
        return repository().getFromCache(reportCategory.getId()).orElse(null);
    }

    protected void add(IReportCategory reportCategory) {
        repository().addToCache(reportCategory);
    }

    protected void remove(IReportCategory reportCategory) {
        repository().removeFromCache(reportCategory);
    }

    private static IReportCategoryRepository repository() {
        return PlatformGetter.getInstance().getReportCategoryHandler().getRepository();
    }

    public ReportCategoryUpdatePacket() {}
}
