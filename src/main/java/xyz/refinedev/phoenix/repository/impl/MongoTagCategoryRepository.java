package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.profile.tag.ITagCategory;
import xyz.refinedev.phoenix.profile.tag.TagCategory;
import xyz.refinedev.phoenix.repository.ITagCategoryRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.MongoListRepository;

public class MongoTagCategoryRepository extends MongoListRepository<String, ITagCategory>
        implements ITagCategoryRepository {

    private static final String NAME_TEXT = "name";

    public MongoTagCategoryRepository(MongoCollection<Document> mongoCollection) {
        super(TagCategory.class, mongoCollection, ITagCategory::getName, NAME_TEXT);
    }
}
