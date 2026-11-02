package me.creaxx.api.particle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.particlenativeapi.api.ParticleNativeAPI;
import xyz.refinedev.lib.particlenativeapi.api.packet.ParticlePacket;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.handler.IProfileHandler;

import java.util.List;

public class ParticleAPI {

    private final ParticleNativeAPI particleAPI;

    public void sendParticlePacket(
            ParticleEffect.ParticlePacket particlePacket, Location location, Player player) {
        particlePacket.toNativePacket(this.particleAPI, location).sendTo(player);
    }

    public void sendParticlePacket(
            Player player,
            ParticleEffect.ParticlePacket particlePacket,
            Location location,
            List<Player> values) {
        ParticlePacket particlePacket2 = particlePacket.toNativePacket(this.particleAPI, location);
        for (Player player2 : values) {
            if (player == null || player2.canSee(player)) {
                particlePacket2.sendTo(player2);
            }
        }
    }

    public void sendParticlePacket(
            Player player,
            ParticleEffect.ParticlePacket particlePacket,
            Location location,
            double value) {
        ParticlePacket particlePacket2 = particlePacket.toNativePacket(this.particleAPI, location);
        String name = location.getWorld().getName();
        double value2 = value * value;
        IProfileHandler profileHandler = Phoenix.getInstance().getProfileHandler();
        for (Player player2 : Bukkit.getOnlinePlayers()) {
            if ((player == null || player2.canSee(player))
                    && player2.getWorld().getName().equals(name)
                    && !(player2.getLocation().distanceSquared(location) > value2)
                    && profileHandler.getProfile(player2.getUniqueId()).isSetting("particles")) {
                particlePacket2.sendTo(player2);
            }
        }
    }

    public ParticleAPI(ParticleNativeAPI particleNativeAPI) {
        this.particleAPI = particleNativeAPI;
    }
}
