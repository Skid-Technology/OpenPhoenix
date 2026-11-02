package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.profile.filter.Filter;
import xyz.refinedev.phoenix.profile.filter.IFilter;
import xyz.refinedev.phoenix.repository.IFilterRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.MongoListRepository;

public class MongoFilterRepository extends MongoListRepository<String, IFilter>
        implements IFilterRepository {

    public MongoFilterRepository(MongoCollection<Document> mongoCollection) {
        super(Filter.class, mongoCollection, IFilter::getName);
    }
}
