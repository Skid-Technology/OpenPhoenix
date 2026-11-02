package xyz.refinedev.phoenix.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;

import org.bukkit.Bukkit;

import java.util.UUID;

public class PhoenixPlaceholderAdapter implements PlaceholderAdapter {

    private static final String identifier = "placeholderapi";

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getPlaceholder(UUID uUID, String text) {
        return PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(uUID), text);
    }
}
