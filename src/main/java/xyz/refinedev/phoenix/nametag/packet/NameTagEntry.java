package xyz.refinedev.phoenix.nametag.packet;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.PacketEvents;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.GameMode;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.User;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.UserProfile;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.PacketWrapper;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate.Action;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate.PlayerInfo;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.CollisionRule;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.NameTagVisibility;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.OptionData;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.ScoreBoardTeamInfo;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.TeamMode;
import xyz.refinedev.lib.kyori.adventure.text.Component;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.utils.Color;

import java.util.Collections;

public class NameTagEntry {

    private final CommonPlatform platform;

    private static final String PHOENIXNT = "phoenixnt_";

    public void applyPlayer(Player player, Player player2) {
        ScoreBoardTeamInfo scoreBoardTeamInfo =
                new ScoreBoardTeamInfo(
                        Component.empty(),
                        Component.empty(),
                        Component.empty(),
                        NameTagVisibility.NEVER,
                        CollisionRule.ALWAYS,
                        null,
                        OptionData.NONE);
        applyPlayer(
                player,
                new WrapperPlayServerTeams(
                        formatString(player2),
                        TeamMode.CREATE,
                        scoreBoardTeamInfo,
                        Collections.singletonList(player2.getName())));
    }

    public void applyPlayerByVoid(Player player, Player player2) {
        applyPlayer(
                player,
                new WrapperPlayServerTeams(
                        formatString(player2),
                        TeamMode.REMOVE,
                        (WrapperPlayServerTeams.ScoreBoardTeamInfo) null,
                        new String[0]));
    }

    private static String formatString(Player player) {
        return PHOENIXNT + player.getEntityId();
    }

    public void applyPlayer(Player player, Player player2, String text) {
        User user;
        UserProfile userProfile =
                (user = PacketEvents.getAPI().getPlayerManager().getUser(player2)) == null
                        ? new UserProfile(player2.getUniqueId(), player2.getName())
                        : user.getProfile();
        applyPlayer(
                player,
                new WrapperPlayServerPlayerInfoUpdate(
                        Action.UPDATE_DISPLAY_NAME,
                        new PlayerInfo[] {
                            new PlayerInfo(
                                    userProfile,
                                    true,
                                    -1,
                                    GameMode.SURVIVAL,
                                    Color.translateComponent(text),
                                    null)
                        }));
    }

    static String formatString(String text) {
        return text == null ? "" : text;
    }

    private static void applyPlayer(Player player, PacketWrapper<?> packetWrapper) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packetWrapper);
    }

    public NameTagEntry(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
