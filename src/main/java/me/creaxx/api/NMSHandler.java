package me.creaxx.api;

import me.creaxx.api.nms.INMSImpl;
import me.creaxx.api.nms.ITeleportImpl;
import me.creaxx.api.nms.impl.LegacyTeleportImpl;
import me.creaxx.api.nms.impl.ModernTeleportImpl;
import me.creaxx.api.nms.impl.v1_12_R1;
import me.creaxx.api.nms.impl.v1_16_R3;
import me.creaxx.api.nms.impl.v1_18_R2;
import me.creaxx.api.nms.impl.v1_19_R3;
import me.creaxx.api.nms.impl.v1_20_R3;
import me.creaxx.api.nms.impl.v1_21_R1;
import me.creaxx.api.nms.impl.v1_21_R10;
import me.creaxx.api.nms.impl.v1_7_R4;
import me.creaxx.api.nms.impl.v1_8_R3;
import me.creaxx.api.nms.impl.v_1_20_R4;
import me.creaxx.api.particle.ParticleAPI;
import me.creaxx.api.title.ITitleAPI;
import me.creaxx.api.utils.VersionUtil;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.refinedev.lib.folialib.FoliaLib;
import xyz.refinedev.lib.particlenativeapi.api.ParticleNativeAPI;
import xyz.refinedev.lib.particlenativeapi.core.ParticleNativeCore;

import java.util.logging.Level;

public class NMSHandler {

    public static NMSHandler instance;

    private final JavaPlugin plugin;

    private INMSImpl API;

    private ITeleportImpl teleportAPI;

    private ITitleAPI titleAPI;

    private ParticleAPI particleAPI;

    private boolean registered = false;

    public NMSHandler(JavaPlugin javaPlugin) {
        instance = this;
        this.plugin = javaPlugin;
    }

    public boolean init() {
        FoliaLib foliaLib = new FoliaLib(this.plugin);
        String text;
        try {
            String text2;
            Integer.parseInt(
                    (text =
                                    (text2 = Bukkit.getServer().getClass().getPackage().getName())
                                                            .length()
                                                    < 4
                                            ? text2.split("\\.")[2]
                                            : text2.split("\\.")[3])
                            .split("_")[1]);
        } catch (Exception exception) {
            text =
                    "v"
                            + Bukkit.getServer()
                                    .getBukkitVersion()
                                    .replace("-SNAPSHOT", "")
                                    .replace("-R0.", "_R")
                                    .replace(".", "_");
        }
        if (!VersionUtil.canHex() || !foliaLib.isFolia() && !foliaLib.isPaper()) {
            this.teleportAPI = new LegacyTeleportImpl();
        } else {
            this.teleportAPI = new ModernTeleportImpl();
        }
        this.plugin.getLogger().log(Level.INFO, "Loading NMS Implementation...");
        try {
            this.setupNMS(text);
            this.plugin.getLogger().log(Level.INFO, "Detected version " + text + "!");
            try {
                this.setupParticleAPI();
            } catch (Exception exception2) {
                this.plugin
                        .getLogger()
                        .log(Level.SEVERE, "ParticleAPI is not supported on this version.");
            }
            this.registered = true;
            return true;
        } catch (IllegalArgumentException illegalArgumentException) {
            this.plugin
                    .getLogger()
                    .log(Level.INFO, "This version is not fully supported (" + text + ")!");
            return false;
        }
    }

    public void setupNMS(String text) throws IllegalArgumentException {
        switch (text) {
            case "v1_7_R4":
                this.API = new v1_7_R4();
                break;
            case "v1_8_R3":
                this.API = new v1_8_R3();
                break;
            case "v1_12_R1":
                this.API = new v1_12_R1();
                break;
            case "v1_16_R3":
                this.API = new v1_16_R3();
                break;
            case "v1_18_R2":
                this.API = new v1_18_R2();
                break;
            case "v1_19_R3":
                this.API = new v1_19_R3();
                break;
            case "v1_20_R3":
                this.API = new v1_20_R3();
                break;
            case "v1_20_6_R1":
                this.API = new v_1_20_R4();
                break;
            case "v1_21_11_R1":
            case "v1_21_10_R1":
                this.API = new v1_21_R10();
                break;
            case "v1_21_9_R1":
            case "v1_21_8_R1":
            case "v1_21_7_R1":
            case "v1_21_6_R1":
            case "v1_21_5_R1":
            case "v1_21_4_R1":
            case "v1_21_3_R1":
            case "v1_21_2_R1":
            case "v1_21_1_R1":
            case "v1_21_R1":
                this.API = new v1_21_R1();
                break;
            default:
                throw new IllegalArgumentException("Unsupported Version!");
        }
    }

    public void setupParticleAPI() {
        ParticleNativeAPI particleNativeAPI = ParticleNativeCore.loadAPI(this.plugin);
        this.particleAPI = new ParticleAPI(particleNativeAPI);
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    public INMSImpl getAPI() {
        return this.API;
    }

    public ITeleportImpl getTeleportAPI() {
        return this.teleportAPI;
    }

    public ITitleAPI getTitleAPI() {
        return this.titleAPI;
    }

    public ParticleAPI getParticleAPI() {
        return this.particleAPI;
    }

    public boolean isRegistered() {
        return this.registered;
    }

    public static NMSHandler getInstance() {
        return instance;
    }
}
