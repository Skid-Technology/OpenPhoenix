package xyz.refinedev.phoenix.utils.server;

import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TPSUtils {

    private static Object minecraftServer;

    private static Field recentTps;

    private static Method serverTps;

    private static boolean lookedForServerTps;

    private static double[] fromServer() {
        try {
            if (!lookedForServerTps) {
                lookedForServerTps = true;
                try {
                    serverTps = Bukkit.getServer().getClass().getMethod("getTPS");
                } catch (NoSuchMethodException noSuchMethodException) {
                    serverTps = null;
                }
            }
            if (serverTps == null) {
                return null;
            }
            Object result;
            return (result = serverTps.invoke(Bukkit.getServer())) instanceof double[]
                            && ((double[]) result).length > 0
                    ? (double[]) result
                    : null;
        } catch (Exception exception) {
            return null;
        }
    }

    private static double[] getTps() {
        double[] values;
        if ((values = fromServer()) != null) {
            return values;
        }
        try {
            if (minecraftServer == null) {
                Server server;
                Field field;
                (field = (server = Bukkit.getServer()).getClass().getDeclaredField("console"))
                        .setAccessible(true);
                minecraftServer = field.get(server);
            }
            if (recentTps == null) {
                recentTps =
                        minecraftServer.getClass().getSuperclass().getDeclaredField("recentTps");
                recentTps.setAccessible(true);
            }
            return (double[]) recentTps.get(minecraftServer);
        } catch (IllegalAccessException | NoSuchFieldException exception) {
            return new double[] {-1.0, -1.0, -1.0};
        }
    }

    public static double getRecentTps() {
        double[] values;
        if ((values = getTps()) != null && values.length != 0) {
            double value;
            return (value = values[0]) < 0.0 ? -1.0 : Math.round(Math.min(value, 20.0));
        } else {
            return -1.0;
        }
    }
}
