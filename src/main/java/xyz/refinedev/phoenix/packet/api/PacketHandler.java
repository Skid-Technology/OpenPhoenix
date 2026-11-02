package xyz.refinedev.phoenix.packet.api;

import xyz.refinedev.lib.io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.handler.IDatabaseHandler;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PacketHandler implements IPacketHandler {

    private final CommonPlatform api;

    private final String instanceId = UUID.randomUUID().toString();

    private boolean processPackets = false;

    public PacketHandler(CommonPlatform commonPlatform) {
        this.api = commonPlatform;
    }

    @Override
    public void enable() {
        StatefulRedisPubSubConnection<String, String> statefulRedisPubSubConnection;
        (statefulRedisPubSubConnection =
                        this.api.getDatabaseHandler().getRedisClient().connectPubSub())
                .addListener(new PacketListener(this.api));
        statefulRedisPubSubConnection.async().subscribe(new String[] {"Packet:All"});
        this.api.getLogger().info("Connected to Redis & Subscribed.");
    }

    @Override
    public void disable() {
        this.stopProcessingPackets();
    }

    @Override
    public void startProcessingPackets() {
        this.processPackets = true;
    }

    @Override
    public void stopProcessingPackets() {
        this.processPackets = false;
    }

    @Override
    public void send(Packet packet) {
        packet.onSend();
        long now = System.currentTimeMillis();
        String text = this.encodePacket(packet);
        IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
        databaseHandler.runRedisCommandAsync(result -> result.publish("Packet:All", text));
        this.logSent(packet, now, "Async");
    }

    @Override
    public CompletableFuture<Void> sendAsFuture(Packet packet) {
        packet.onSend();
        return CompletableFuture.runAsync(
                () -> {
                    long now = System.currentTimeMillis();
                    String text = this.encodePacket(packet);
                    IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
                    databaseHandler.runRedisCommand(result -> result.publish("Packet:All", text));
                    this.logSent(packet, now, "Async");
                });
    }

    @Override
    public void sendSync(Packet packet) {
        packet.onSend();
        long now = System.currentTimeMillis();
        IDatabaseHandler databaseHandler = this.api.getDatabaseHandler();
        databaseHandler.executeRedisCommand(
                result -> {
                    String text = this.encodePacket(packet);
                    result.publish("Packet:All", text);
                    this.logSent(packet, now, null);
                });
    }

    private void logSent(Packet packet, long value, String text) {
        if (this.api.isDebugMode()) {
            String text2 = text;
            this.api
                    .getLogger()
                    .info(
                            "[Packet Log]"
                                    + text2
                                    + " A "
                                    + packet.getClass().getSimpleName()
                                    + " packet has been sent in "
                                    + (System.currentTimeMillis() - value)
                                    + "ms.");
        }
    }

    public String encodePacket(Packet packet) {
        return PacketEnvelope.encode(
                PacketTypes.wireName(packet), this.instanceId, this.api.getGson().toJson(packet));
    }

    @Override
    public String getInstanceId() {
        return this.instanceId;
    }

    @Override
    public boolean isProcessPackets() {
        return this.processPackets;
    }
}
