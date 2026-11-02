package xyz.refinedev.phoenix.utils;

import java.util.ArrayList;
import java.util.Collection;

public class LimitedSizeList<E> extends ArrayList<E> {

    private final int max;

    public LimitedSizeList(Collection<E> values, int max) {
        super(values);
        this.max = max;
    }

    @Override
    public boolean add(E e) {
        if (this.size() >= this.max) {
            this.remove(0);
        }
        return super.add((E) e);
    }

    @Override
    public boolean addAll(Collection<? extends E> values) {
        if (this.size() >= this.max) {
            for (int index = 0; index < values.size(); index++) {
                this.remove(0);
            }
        }
        return super.addAll(values);
    }

    public LimitedSizeList(int max) {
        this.max = max;
    }
}
