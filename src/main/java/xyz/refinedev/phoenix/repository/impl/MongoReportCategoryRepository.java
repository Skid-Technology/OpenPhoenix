package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.report.IReportCategory;
import xyz.refinedev.phoenix.report.ReportCategory;
import xyz.refinedev.phoenix.repository.IReportCategoryRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.MongoListRepository;

public class MongoReportCategoryRepository extends MongoListRepository<String, IReportCategory>
        implements IReportCategoryRepository {

    public MongoReportCategoryRepository(MongoCollection<Document> mongoCollection) {
        super(ReportCategory.class, mongoCollection, IReportCategory::getId);
    }
}
