package xyz.refinedev.phoenix.packet.api;

import xyz.refinedev.lib.io.lettuce.core.pubsub.RedisPubSubListener;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.utils.ExecuteOnMainThread;

public final class PacketListener implements RedisPubSubListener<String, String> {

    private final CommonPlatform api;

    public void message(String text, String text2) {
        if (this.api.getPacketHandler().isProcessPackets()) {
            long now = System.currentTimeMillis();
            PacketEnvelope packetEnvelope;
            String wireName = (packetEnvelope = PacketEnvelope.decode(text2)).getWireName();
            Packet packet;
            boolean matches;
            try {
                String json = packetEnvelope.getJson();
                Class classValue = PacketTypes.resolve(wireName);
                matches =
                        (packet = (Packet) this.api.getGson().fromJson(json, classValue))
                                .shouldRunOnSameServer();
            } catch (ClassNotFoundException classNotFoundException) {
                return;
            } catch (ClassCastException | IndexOutOfBoundsException exception) {
                this.api
                        .getLogger()
                        .severe(
                                "An invalid packet was received on the Packet:ALL channel and has been voided.");
                return;
            } catch (Throwable throwable) {
                this.formatThrowable(throwable, wireName);
                return;
            }
            if (matches) {
                this.api
                        .getLogger()
                        .info(
                                "[Packet Log] Received & Executed a "
                                        + packet.getClass().getSimpleName()
                                        + " packet in "
                                        + (System.currentTimeMillis() - now)
                                        + "ms.");
                ExecuteOnMainThread executeOnMainThread = this.api.getExecuteOnMainThread();
                Packet packet2 = packet;
                executeOnMainThread.run(
                        () -> {
                            packet2.onReceive();
                            this.api
                                    .getLogger()
                                    .info(
                                            "[Packet Log] (Sync) Received & Executed a "
                                                    + packet2.getClass().getSimpleName()
                                                    + " packet in "
                                                    + (System.currentTimeMillis() - now)
                                                    + "ms.");
                        });
            }
        }
    }

    public void message(String text, String text2, String text3) {}

    public void subscribed(String text, long value) {}

    public void psubscribed(String text, long value) {}

    public void unsubscribed(String text, long value) {}

    public void punsubscribed(String text, long value) {}

    public void formatThrowable(Throwable throwable, String text) {}

    public PacketListener(CommonPlatform commonPlatform) {
        this.api = commonPlatform;
    }
}
