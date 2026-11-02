package me.creaxx.api.title;

import org.bukkit.entity.Player;

public interface ITitleAPI {

    void sendTitle(Player player, String text, String text2, int value, int value2, int value3);

    void clearTitle(Player player);
}
