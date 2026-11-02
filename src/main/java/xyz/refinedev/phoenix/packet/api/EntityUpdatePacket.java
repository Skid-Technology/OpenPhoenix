package xyz.refinedev.phoenix.packet.api;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.profile.TrackedEntity;

import java.util.Set;

public abstract class EntityUpdatePacket<T> implements Packet {

    private T entity;

    private Set<String> fields;

    private boolean delete;

    public EntityUpdatePacket() {}

    public EntityUpdatePacket(T t, boolean matches) {
        this.fields = TrackedEntity.changedFieldsFor(t, PlatformGetter.getInstance().getGson());
    }

    protected abstract T find(T t);

    protected abstract void add(T t);

    protected abstract void remove(T t);

    @Override
    public boolean async() {
        return false;
    }

    @Override
    public boolean shouldRunOnSameServer() {
        return false;
    }

    @Override
    public void onSend() {
        T result = this.entity;
        if (this.entity != null) {
            if (this == null) {
                this.add((T) result);
            }
        }
    }

    @Override
    public void onReceive() {
        T result = this.entity;
        if (this.entity != null) {
            this.remove((T) result);
        }
    }
}
