package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.profile.tag.ITagCategory;
import xyz.refinedev.phoenix.profile.tag.TagCategory;
import xyz.refinedev.phoenix.repository.ITagCategoryRepository;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.repository.mysql.MySQLListRepository;

public class MySQLTagCategoryRepository extends MySQLListRepository<String, ITagCategory>
        implements ITagCategoryRepository {

    public MySQLTagCategoryRepository(Table table) {
        super(TagCategory.class, table, ITagCategory::getName);
    }
}
