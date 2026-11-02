package xyz.refinedev.phoenix.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class ConfigLocation {

    private final String worldName;

    private final double x;

    private final double y;

    private final double z;

    private final float yaw;

    private final float pitch;

    public ConfigLocation(String text, double value, double value2, double value3) {
        this(text, value, value2, value3, 0.0F, 0.0F);
    }

    public Location toBukkit() {
        World world;
        return (world = Bukkit.getWorld(this.worldName)) == null
                ? null
                : new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public boolean isWorldLoaded() {
        return Bukkit.getWorld(this.worldName) != null;
    }

    public static ConfigLocation of(Location location) {
        World world = location.getWorld();
        return new ConfigLocation(
                world == null ? "world" : world.getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch());
    }

    public ConfigLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }
}
