package xyz.refinedev.phoenix.utils.repository.updates.impl;

import xyz.refinedev.phoenix.utils.repository.updates.api.UpdateOptions;
import xyz.refinedev.phoenix.utils.repository.updates.api.UpdateVisitor;

public class SetUpdateOption<T> implements UpdateOptions {

    private final String fieldName;

    private final T obj;

    @Override
    public <R> R accept(UpdateVisitor<R> updateVisitor) {
        return (R) updateVisitor.visitSet(this.fieldName, this.obj);
    }

    public SetUpdateOption(String fieldName, T t) {
        this.fieldName = fieldName;
        this.obj = (T) t;
    }
}
