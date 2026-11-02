package xyz.refinedev.phoenix.utils.nametags.providers;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.utils.nametags.tag.PlayerTag;

import java.util.Optional;

public interface PlayerTagProvider extends Comparable<PlayerTagProvider> {

    int getPriority();

    Optional<PlayerTag> buildTag(Player player, Player player2);

    default int compareTo(PlayerTagProvider playerTagProvider) {
        int value;
        return (value = Integer.compare(playerTagProvider.getPriority(), this.getPriority())) != 0
                ? value
                : this.getClass().getName().compareTo(playerTagProvider.getClass().getName());
    }
}
