package xyz.refinedev.phoenix.utils.nametags.adapter;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.nametags.providers.PlayerNameProvider;

public class PhoenixPlayerNameProvider implements PlayerNameProvider {

    private final CommonPlatform api;

    @Override
    public String getRealName(Player player) {
        IProfile profile;
        return (profile = this.api.getProfileHandler().getCachedProfile(player.getUniqueId()))
                        == null
                ? player.getName()
                : profile.getName();
    }

    public PhoenixPlayerNameProvider(CommonPlatform commonPlatform) {
        this.api = commonPlatform;
    }
}
