package xyz.refinedev.phoenix.utils.server;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationSerilization {

    public static Location deserializeLocation(String text) {
        String[] texts = text.split(":");
        World world = null;
        Double value = null;
        Double value2 = null;
        Double value3 = null;
        Float value4 = null;
        Float value5 = null;
        String[] texts2 = texts;
        int value6 = texts.length;
        for (int index = 0; index < value6; index++) {
            String[] texts3;
            if ((texts3 = texts2[index].split(";"))[0].equalsIgnoreCase("#w")) {
                world = Bukkit.getWorld(texts3[1]);
            } else if (texts3[0].equalsIgnoreCase("#x")) {
                value = Double.parseDouble(texts3[1]);
            } else if (texts3[0].equalsIgnoreCase("#y")) {
                value2 = Double.parseDouble(texts3[1]);
            } else if (texts3[0].equalsIgnoreCase("#z")) {
                value3 = Double.parseDouble(texts3[1]);
            } else if (texts3[0].equalsIgnoreCase("#p")) {
                value4 = Float.parseFloat(texts3[1]);
            } else if (texts3[0].equalsIgnoreCase("#yaw")) {
                value5 = Float.parseFloat(texts3[1]);
            }
        }
        return world != null
                        && value != null
                        && value2 != null
                        && value3 != null
                        && value4 != null
                        && value5 != null
                ? new Location(world, value, value2, value3, value5, value4)
                : null;
    }

    public static String serializeLocation(Location location) {
        return "#w;"
                + location.getWorld().getName()
                + ":#x;"
                + location.getX()
                + ":#y;"
                + location.getY()
                + ":#z;"
                + location.getZ()
                + ":#p;"
                + location.getPitch()
                + ":#yaw;"
                + location.getYaw();
    }
}
