package xyz.refinedev.phoenix.nametag.packet;

import xyz.refinedev.lib.io.github.retrooper.packetevents.util.SpigotReflectionUtil;

import java.util.concurrent.atomic.AtomicInteger;

public final class NameTagViewerState {

    private final AtomicInteger counter = new AtomicInteger(Integer.MAX_VALUE);

    private volatile boolean enabled;

    public int getCount() {
        if (!this.enabled) {
            try {
                return SpigotReflectionUtil.generateEntityId();
            } catch (Throwable throwable) {
                this.enabled = true;
            }
        }
        return this.counter.updateAndGet(
                result -> result <= 1073741823 ? Integer.MAX_VALUE : result - 1);
    }
}
