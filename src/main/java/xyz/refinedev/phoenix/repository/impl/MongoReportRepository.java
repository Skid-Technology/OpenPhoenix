package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.report.IReport;
import xyz.refinedev.phoenix.report.Report;
import xyz.refinedev.phoenix.repository.IReportRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.MongoListRepository;

import java.util.UUID;

public class MongoReportRepository extends MongoListRepository<UUID, IReport>
        implements IReportRepository {

    public MongoReportRepository(MongoCollection<Document> mongoCollection) {
        super(Report.class, mongoCollection, IReport::getId);
    }
}
