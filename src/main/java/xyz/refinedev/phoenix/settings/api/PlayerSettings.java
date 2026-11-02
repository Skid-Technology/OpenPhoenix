package xyz.refinedev.phoenix.settings.api;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.menu.api.Button;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class PlayerSettings {

    private static final String GENERAL_TEXT = "General";

    public abstract Map<Integer, Button> getButtons(Player player);

    public abstract boolean hasPermission(Player player);

    public abstract int getPriority();

    public String name() {
        return GENERAL_TEXT;
    }

    public List<String> description() {
        return Collections.emptyList();
    }

    public static void addSetting(PlayerSettings playerSettings) {
        Phoenix.getInstance().getPlayerSettings().add(playerSettings);
    }
}
