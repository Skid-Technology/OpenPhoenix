package xyz.refinedev.phoenix.utils.nametags.team;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.utils.nametags.tag.PlayerTag;

public interface TeamService {

    void sendNameTag(Player player, Player player2, String text, PlayerTag playerTag);

    void displayTablist(Player player, Player player2, PlayerTag playerTag);

    void displayLegacyTablist(Player player, Player player2, PlayerTag playerTag);
}
