package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.packet.impl.report.ReportCategoryUpdatePacket;
import xyz.refinedev.phoenix.report.IReportCategory;
import xyz.refinedev.phoenix.report.ReportCategory;
import xyz.refinedev.phoenix.repository.IReportCategoryRepository;
import xyz.refinedev.phoenix.utils.repository.RepositoryFactory;

import java.util.Collection;

public class ReportCategoryHandler implements IReportCategoryHandler {

    private final CommonPlatform platform;

    private final IReportCategoryRepository repository;

    public ReportCategoryHandler(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
        this.repository = new RepositoryFactory(commonPlatform).reportCategories();
        this.repository
                .fetchAll()
                .whenCompleteAsync(
                        (result, result2) ->
                                this.repository
                                        .getCache()
                                        .addAll((Collection<? extends IReportCategory>) result));
    }

    @Nullable
    @Override
    public IReportCategory getCategory(String text) {
        return this.repository.getFromCache(text).orElse(null);
    }

    @Override
    public IReportCategory createCategory(String text, String text2, String text3, String text4) {
        ReportCategory reportCategory = new ReportCategory(text, text2, text3, text4);
        this.updateCategory(reportCategory);
        return reportCategory;
    }

    @Override
    public void updateCategory(IReportCategory reportCategory) {
        this.repository.save(reportCategory);
        this.platform
                .getPacketHandler()
                .send(new ReportCategoryUpdatePacket(reportCategory, false));
    }

    @Override
    public void deleteCategory(IReportCategory reportCategory) {
        this.repository.delete(reportCategory);
        this.platform.getPacketHandler().send(new ReportCategoryUpdatePacket(reportCategory, true));
    }

    public CommonPlatform getPlatform() {
        return this.platform;
    }

    @Override
    public IReportCategoryRepository getRepository() {
        return this.repository;
    }
}
