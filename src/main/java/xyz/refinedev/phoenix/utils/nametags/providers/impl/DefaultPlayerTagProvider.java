package xyz.refinedev.phoenix.utils.nametags.providers.impl;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;

import xyz.refinedev.phoenix.utils.nametags.providers.PlayerTagProvider;
import xyz.refinedev.phoenix.utils.nametags.tag.PlayerTag;
import xyz.refinedev.phoenix.utils.nametags.tag.impl.DefaultPlayerTag;

import java.util.Optional;

public class DefaultPlayerTagProvider implements PlayerTagProvider {

    private final PlayerTag defaultTag =
            new DefaultPlayerTag("&c[Refine] ", "", 0, NameTagVisibility.ALWAYS);

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public Optional<PlayerTag> buildTag(Player player, Player player2) {
        return Optional.of(this.defaultTag);
    }
}
