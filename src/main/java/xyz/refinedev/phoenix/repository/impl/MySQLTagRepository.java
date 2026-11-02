package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.profile.tag.ITag;
import xyz.refinedev.phoenix.profile.tag.Tag;
import xyz.refinedev.phoenix.repository.ITagRepository;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.repository.mysql.MySQLListRepository;

public class MySQLTagRepository extends MySQLListRepository<String, ITag>
        implements ITagRepository {

    public MySQLTagRepository(Table table) {
        super(Tag.class, table, ITag::getName);
    }
}
