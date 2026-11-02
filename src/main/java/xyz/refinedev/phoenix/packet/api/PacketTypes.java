package xyz.refinedev.phoenix.packet.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PacketTypes {

    private static final Map<String, Class<?>> OVERRIDES = new ConcurrentHashMap<>();

    private static final Map<Class<?>, String> WIRE_NAMES = new ConcurrentHashMap<>();

    private PacketTypes() {}

    public static void override(
            Class<? extends Packet> classValue, Class<? extends Packet> classValue2) {
        OVERRIDES.put(classValue.getName(), classValue2);
        WIRE_NAMES.put(classValue2, classValue.getName());
    }

    public static String wireName(Packet packet) {
        return WIRE_NAMES.get(packet.getClass());
    }

    public static Class<?> resolve(String text) throws ClassNotFoundException {
        return OVERRIDES.get(text);
    }
}
