package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.phoenix.profile.tag.ITag;
import xyz.refinedev.phoenix.profile.tag.Tag;
import xyz.refinedev.phoenix.repository.ITagRepository;
import xyz.refinedev.phoenix.utils.repository.mongo.MongoListRepository;

public class MongoTagRepository extends MongoListRepository<String, ITag>
        implements ITagRepository {

    private static final String NAME_TEXT = "name";

    public MongoTagRepository(MongoCollection<Document> mongoCollection) {
        super(Tag.class, mongoCollection, ITag::getName, NAME_TEXT);
    }
}
