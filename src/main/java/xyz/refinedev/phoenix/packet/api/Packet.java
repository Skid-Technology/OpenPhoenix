package xyz.refinedev.phoenix.packet.api;

public interface Packet {

    default boolean async() {
        return true;
    }

    default boolean shouldRunOnSameServer() {
        return true;
    }

    default void onSend() {}

    void onReceive();
}
