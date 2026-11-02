package xyz.refinedev.phoenix.utils;

import org.bukkit.event.Event;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class EventHandUtils {

    private static final Map<Class<?>, Method> methodsByClass = new ConcurrentHashMap<>();

    private static final Method method;

    public static boolean matchesEnabled(Event event) {
        Method method2;
        if ((method2 =
                        methodsByClass.computeIfAbsent(
                                event.getClass(),
                                result -> {
                                    try {
                                        return result.getMethod("getHand");
                                    } catch (NoSuchMethodException noSuchMethodException) {
                                        return method;
                                    }
                                }))
                == method) {
            return false;
        }
        try {
            return "OFF_HAND".equals(String.valueOf(method2.invoke(event)));
        } catch (ReflectiveOperationException reflectiveOperationException) {
            return false;
        }
    }

    private EventHandUtils() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }

    static {
        Method method2;
        try {
            method2 = EventHandUtils.class.getDeclaredMethod("matchesEnabled", Event.class);
        } catch (NoSuchMethodException noSuchMethodException) {
            throw new ExceptionInInitializerError(noSuchMethodException);
        }
        method = method2;
    }
}
