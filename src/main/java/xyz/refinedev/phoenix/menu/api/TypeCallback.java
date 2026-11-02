package xyz.refinedev.phoenix.menu.api;

import java.io.Serializable;

public interface TypeCallback<T> extends Serializable {

    void callback(T t);
}
