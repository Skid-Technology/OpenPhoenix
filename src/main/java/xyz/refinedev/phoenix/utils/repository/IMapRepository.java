package xyz.refinedev.phoenix.utils.repository;

public interface IMapRepository<K, V>
        extends BulkWritable<V>, Queryable<K, V>, Readable<K, V>, Writable<K, V> {}
