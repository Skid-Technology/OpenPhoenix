package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.report.IReportCategory;
import xyz.refinedev.phoenix.repository.IReportCategoryRepository;

public interface IReportCategoryHandler {

    @Nullable
    IReportCategory getCategory(String text);

    IReportCategory createCategory(String text, String text2, String text3, String text4);

    void updateCategory(IReportCategory reportCategory);

    void deleteCategory(IReportCategory reportCategory);

    IReportCategoryRepository getRepository();
}
