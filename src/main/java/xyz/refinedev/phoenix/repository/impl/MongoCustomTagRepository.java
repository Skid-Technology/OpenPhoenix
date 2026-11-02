package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.profile.tag.CustomTag;
import xyz.refinedev.phoenix.profile.tag.ICustomTag;
import xyz.refinedev.phoenix.profile.tag.ITag;
import xyz.refinedev.phoenix.repository.ICustomTagRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.MongoListRepository;

public class MongoCustomTagRepository extends MongoListRepository<String, ICustomTag>
        implements ICustomTagRepository {

    private static final String NAME_TEXT = "name";

    public MongoCustomTagRepository(MongoCollection<Document> mongoCollection) {
        super(CustomTag.class, mongoCollection, ITag::getName, NAME_TEXT);
    }
}
