package xyz.refinedev.phoenix.utils.nametags.util;

import lombok.Generated;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import xyz.refinedev.lib.com.github.retrooper.packetevents.PacketEvents;
import xyz.refinedev.lib.com.github.retrooper.packetevents.manager.server.ServerManager;
import xyz.refinedev.lib.com.github.retrooper.packetevents.manager.server.ServerVersion;

public final class ScoreboardUtil {

    static boolean COLOR_SUPPORTED = true;

    public static Team getOrCreate(Scoreboard scoreboard, String text) {
        text = limitString(ScoreboardUtil.StringType.TEAM_NAME, text);
        Team team;
        return (team = scoreboard.getTeam(text)) != null ? team : scoreboard.registerNewTeam(text);
    }

    public static void setPrefixIfChanged(Team team, String text) {
        text = limitString(ScoreboardUtil.StringType.COMPONENT, text);
        if (!team.getPrefix().equals(text)) {
            team.setPrefix(text);
        }
    }

    public static void setSuffixIfChanged(Team team, String text) {
        text = limitString(ScoreboardUtil.StringType.COMPONENT, text);
        if (!team.getSuffix().equals(text)) {
            team.setSuffix(text);
        }
    }

    public static void setNameTagVisibilityIfChanged(
            Team team, NameTagVisibility nameTagVisibility) {
        if (team.getNameTagVisibility() != nameTagVisibility) {
            team.setNameTagVisibility(nameTagVisibility);
        }
    }

    public static void setColorIfChanged(Team team, ChatColor chatColor) {
        try {
            if (COLOR_SUPPORTED && team.getColor() != chatColor) {
                team.setColor(chatColor);
            }
        } catch (Throwable throwable) {
            COLOR_SUPPORTED = false;
        }
    }

    public static void separateTablistName(Player player) {
        separateTablistName(player, player.getName());
    }

    public static void separateTablistName(Player player, String text) {
        String text2 = text.substring(0, Math.min(text.length(), 14));
        player.setPlayerListName(text2 + '\u00a7' + "7");
    }

    public static String limitString(ScoreboardUtil.StringType stringType, String text) {
        int maxLength = stringType.getMaxLength();
        if (text.length() > maxLength) {
            text = text.substring(0, maxLength);
        }
        return text;
    }

    @Generated
    private ScoreboardUtil() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }

    public enum StringType {
        COMPONENT,
        TEAM_NAME,
        ENTRY;

        private int getMaxLength() {
            ServerManager serverManager = PacketEvents.getAPI().getServerManager();
            switch (this) {
                case COMPONENT:
                    if (serverManager.getVersion().isNewerThanOrEquals(ServerVersion.V_1_13)) {
                        return 32767;
                    }
                    return 16;
                case TEAM_NAME:
                    return 16;
                case ENTRY:
                    return 40;
                default:
                    throw new IllegalStateException("Unexpected value: " + this);
            }
        }
    }
}
