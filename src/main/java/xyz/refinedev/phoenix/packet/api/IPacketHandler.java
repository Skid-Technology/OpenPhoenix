package xyz.refinedev.phoenix.packet.api;

import java.util.concurrent.CompletableFuture;

public interface IPacketHandler {

    String getInstanceId();

    void enable();

    void disable();

    void startProcessingPackets();

    void stopProcessingPackets();

    void send(Packet packet);

    CompletableFuture<Void> sendAsFuture(Packet packet);

    void sendSync(Packet packet);

    boolean isProcessPackets();
}
