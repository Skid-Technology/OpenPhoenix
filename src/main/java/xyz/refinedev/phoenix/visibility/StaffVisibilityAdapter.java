package xyz.refinedev.phoenix.visibility;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;

import java.util.UUID;

public class StaffVisibilityAdapter implements IVisibilityAdapter {

    private final Phoenix phoenix;

    @Override
    public boolean canSee(Player player, Player player2) {
        return this.canSee(player.getUniqueId(), player2.getUniqueId());
    }

    @Override
    public boolean canSee(UUID uUID, UUID uUID2) {
        if (!Phoenix.getInstance().getVanishHandler().isVanished(uUID2)) {
            return true;
        }
        IProfile profile = this.phoenix.getProfileHandler().getProfile(uUID);
        IProfile profile2 = this.phoenix.getProfileHandler().getProfile(uUID2);
        return SettingsConfig.VANISH.SEE_ALL
                ? profile.isStaff()
                : profile.getPriority() >= profile2.getPriority();
    }

    public StaffVisibilityAdapter(Phoenix phoenix2) {
        this.phoenix = phoenix2;
    }
}
