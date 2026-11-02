package xyz.refinedev.phoenix.nametag;

import org.bukkit.entity.Player;

import java.util.List;

public interface NameTagBackend {

    void refresh(Player player, Player player2);

    void refreshFor(Player player, List<Player> values);

    void refreshTargets(Player player, List<Player> values);

    void remove(Player player, Player player2);
}
