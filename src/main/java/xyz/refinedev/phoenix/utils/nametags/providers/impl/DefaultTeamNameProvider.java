package xyz.refinedev.phoenix.utils.nametags.providers.impl;

import lombok.Generated;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.utils.nametags.NameTagHandler;
import xyz.refinedev.phoenix.utils.nametags.providers.PlayerNameProvider;
import xyz.refinedev.phoenix.utils.nametags.providers.TeamNameProvider;
import xyz.refinedev.phoenix.utils.nametags.util.WeightUtil;

public class DefaultTeamNameProvider implements TeamNameProvider {

    private final NameTagHandler nameTagHandler;

    @Override
    public String buildTeamName(Player player, int value) {
        PlayerNameProvider playerNameProvider;
        String text =
                (playerNameProvider = this.nameTagHandler.getNameProvider()) != null
                        ? playerNameProvider.getRealName(player)
                        : player.getName();
        String text2;
        return (text2 = WeightUtil.getLetterByWeight(value) + text).length() > 16
                ? text2.substring(0, 16)
                : text2;
    }

    @Generated
    public DefaultTeamNameProvider(NameTagHandler nameTagHandler2) {
        this.nameTagHandler = nameTagHandler2;
    }
}
