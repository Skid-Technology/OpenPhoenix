package xyz.refinedev.phoenix.utils.repository.updates.api;

public interface UpdateOptions {

    <R> R accept(UpdateVisitor<R> updateVisitor);
}
