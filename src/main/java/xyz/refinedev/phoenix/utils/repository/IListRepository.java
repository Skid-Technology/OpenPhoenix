package xyz.refinedev.phoenix.utils.repository;

public interface IListRepository<K, V>
        extends BulkWritable<V>, ListReadable<K, V>, ListWritable<K, V>, Queryable<K, V> {}
