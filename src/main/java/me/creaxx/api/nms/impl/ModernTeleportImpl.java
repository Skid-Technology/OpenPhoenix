package me.creaxx.api.nms.impl;

import me.creaxx.api.nms.ITeleportImpl;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ModernTeleportImpl implements ITeleportImpl {

    @Override
    public void teleport(Player player, Location location) {
        player.teleportAsync(location);
    }
}
