package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.profile.filter.Filter;
import xyz.refinedev.phoenix.profile.filter.IFilter;
import xyz.refinedev.phoenix.repository.IFilterRepository;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.repository.mysql.MySQLListRepository;

public class MySQLFilterRepository extends MySQLListRepository<String, IFilter>
        implements IFilterRepository {

    public MySQLFilterRepository(Table table) {
        super(Filter.class, table, IFilter::getName);
    }
}
