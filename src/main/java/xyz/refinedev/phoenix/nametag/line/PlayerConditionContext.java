package xyz.refinedev.phoenix.nametag.line;

import me.clip.placeholderapi.PlaceholderAPI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;

public class PlayerConditionContext implements ConditionContext {

    private final CommonPlatform platform;

    private final Player player;

    private final Player playerPlayer;

    private final Player player2;

    private final boolean enabled;

    private IProfile profile;

    private boolean enabledEnabled;

    public PlayerConditionContext(
            CommonPlatform commonPlatform,
            Player player3,
            Player player4,
            Player player5,
            boolean enabled) {
        this.platform = commonPlatform;
        this.player = player3;
        this.playerPlayer = player4;
        this.player2 = player5;
        this.enabled = enabled;
    }

    @Override
    public boolean permission(String text) {
        return text != null && !text.isEmpty() && this.player.hasPermission(text);
    }

    @Override
    public String rankName() {
        IRank rank;
        return (rank = this.getHighestRank()) == null ? "" : rank.getName();
    }

    @Override
    public int rankPriority() {
        IRank rank;
        return (rank = this.getHighestRank()) == null ? Integer.MAX_VALUE : rank.getPriority();
    }

    @Override
    public int priorityOf(String text) {
        if (text != null && !text.isEmpty()) {
            IRank rank;
            return (rank = this.platform.getRankHandler().getRank(text)) == null
                    ? Integer.MAX_VALUE
                    : rank.getPriority();
        } else {
            return Integer.MAX_VALUE;
        }
    }

    @Override
    public boolean hasPrefix() {
        IRank rank;
        return (rank = this.getHighestRank()) != null
                && rank.getPlayerListPrefix() != null
                && !rank.getPlayerListPrefix().isEmpty();
    }

    @Override
    public boolean vanished() {
        IProfile profile;
        return (profile = this.getProfile()) != null && profile.isVanished();
    }

    @Override
    public boolean modMode() {
        return this.player.hasMetadata("ModMode");
    }

    @Override
    public boolean self() {
        return this.playerPlayer != null
                && this.player2 != null
                && this.playerPlayer.getUniqueId().equals(this.player2.getUniqueId());
    }

    @Override
    public boolean sameTeam() {
        if (this.playerPlayer != null && this.player2 != null) {
            try {
                Team entryTeam =
                        this.playerPlayer.getScoreboard().getEntryTeam(this.playerPlayer.getName());
                Team entryTeam2 = this.player2.getScoreboard().getEntryTeam(this.player2.getName());
                return entryTeam != null
                        && entryTeam2 != null
                        && entryTeam.getName().equals(entryTeam2.getName());
            } catch (Throwable throwable) {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public String placeholders(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        if (!this.enabled) {
            return text;
        }
        if (text.indexOf(37) < 0) {
            return text;
        }
        try {
            String text2 = PlaceholderAPI.setPlaceholders(this.player, text);
            if (this.playerPlayer != null && this.player2 != null && text2.contains("%rel_")) {
                text2 =
                        PlaceholderAPI.setRelationalPlaceholders(
                                this.player2, this.playerPlayer, text2);
            }
            return text2;
        } catch (Throwable throwable) {
            return text;
        }
    }

    private IRank getHighestRank() {
        IProfile profile;
        return (profile = this.getProfile()) == null ? null : profile.getHighestRank();
    }

    private IProfile getProfile() {
        if (!this.enabledEnabled) {
            this.enabledEnabled = true;
            try {
                this.profile =
                        this.platform
                                .getProfileHandler()
                                .getCachedProfile(this.player.getUniqueId());
            } catch (Throwable throwable) {
                this.profile = null;
            }
        }
        return this.profile;
    }

    public static boolean isEnabled() {
        return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }
}
