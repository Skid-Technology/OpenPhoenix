package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.profile.tag.CustomTag;
import xyz.refinedev.phoenix.profile.tag.ICustomTag;
import xyz.refinedev.phoenix.profile.tag.ITag;
import xyz.refinedev.phoenix.repository.ICustomTagRepository;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.repository.mysql.MySQLListRepository;

public class MySQLCustomTagRepository extends MySQLListRepository<String, ICustomTag>
        implements ICustomTagRepository {

    public MySQLCustomTagRepository(Table table) {
        super(CustomTag.class, table, ITag::getName);
    }
}
