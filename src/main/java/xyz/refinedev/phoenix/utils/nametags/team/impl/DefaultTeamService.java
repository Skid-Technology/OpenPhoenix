package xyz.refinedev.phoenix.utils.nametags.team.impl;

import lombok.Generated;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import xyz.refinedev.lib.com.github.retrooper.packetevents.PacketEvents;
import xyz.refinedev.lib.com.github.retrooper.packetevents.PacketEventsAPI;
import xyz.refinedev.lib.com.github.retrooper.packetevents.manager.player.PlayerManager;
import xyz.refinedev.lib.com.github.retrooper.packetevents.manager.server.ServerManager;
import xyz.refinedev.lib.com.github.retrooper.packetevents.manager.server.ServerVersion;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.GameMode;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.User;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.UserProfile;
import xyz.refinedev.lib.com.github.retrooper.packetevents.util.adventure.AdventureSerializer;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.PacketWrapper;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo.PlayerData;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate.Action;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate.PlayerInfo;
import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.lib.kyori.adventure.text.Component;
import xyz.refinedev.phoenix.utils.nametags.providers.PlayerNameProvider;
import xyz.refinedev.phoenix.utils.nametags.providers.TeamNameProvider;
import xyz.refinedev.phoenix.utils.nametags.tag.PlayerTag;
import xyz.refinedev.phoenix.utils.nametags.team.TeamService;
import xyz.refinedev.phoenix.utils.nametags.util.ColorUtil;
import xyz.refinedev.phoenix.utils.nametags.util.ScoreboardUtil;

public class DefaultTeamService implements TeamService {

    private final TeamNameProvider teamNameProvider;

    @Nullable private final PlayerNameProvider playerNameProvider;

    @Override
    public void sendNameTag(Player player, Player player2, String text, PlayerTag playerTag) {
        Team orCreate = ScoreboardUtil.getOrCreate(player.getScoreboard(), text);
        ChatColor chatColor =
                playerTag.color() != null
                        ? playerTag.color()
                        : (playerTag.prefix().isEmpty()
                                ? ChatColor.WHITE
                                : ColorUtil.getLastColors(playerTag.prefix()));
        String text2 = playerTag.prefix().isEmpty() ? "" : ColorUtil.color(playerTag.prefix());
        ScoreboardUtil.setColorIfChanged(orCreate, chatColor);
        ScoreboardUtil.setPrefixIfChanged(orCreate, text2);
        ScoreboardUtil.setSuffixIfChanged(orCreate, ColorUtil.color(playerTag.suffix()));
        ScoreboardUtil.setNameTagVisibilityIfChanged(orCreate, playerTag.visibility());
        String text3 =
                player2 == player && this.playerNameProvider != null
                        ? this.playerNameProvider.getRealName(player2)
                        : player2.getName();
        orCreate.addEntry(text3);
    }

    @Override
    public void displayTablist(Player player, Player player2, PlayerTag playerTag) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean matches = !playerTag.prefix().isEmpty();
        boolean matches2 = !playerTag.suffix().isEmpty();
        if (matches) {
            stringBuilder.append(playerTag.prefix());
        }
        stringBuilder.append(player2.getName());
        if (matches2) {
            stringBuilder.append(playerTag.suffix());
        }
        PacketEventsAPI packetEventsAPI;
        ServerManager serverManager = (packetEventsAPI = PacketEvents.getAPI()).getServerManager();
        PlayerManager playerManager;
        User user;
        UserProfile userProfile =
                (user = (playerManager = packetEventsAPI.getPlayerManager()).getUser(player2))
                                == null
                        ? new UserProfile(player2.getUniqueId(), player2.getName())
                        : user.getProfile();
        Component component =
                AdventureSerializer.serializer()
                        .fromLegacy(ColorUtil.color(stringBuilder.toString()));
        PacketWrapper packetWrapper = this.getPacketWrapper(serverManager, userProfile, component);
        playerManager.writePacketSilently(player, packetWrapper);
    }

    @NotNull
    private PacketWrapper<?> getPacketWrapper(
            ServerManager serverManager, UserProfile userProfile, Component component) {
        Object result;
        if (serverManager.getVersion().isNewerThanOrEquals(ServerVersion.V_1_19_3)) {
            result =
                    new WrapperPlayServerPlayerInfoUpdate(
                            Action.UPDATE_DISPLAY_NAME,
                            new PlayerInfo[] {
                                new PlayerInfo(
                                        userProfile, true, -1, GameMode.SURVIVAL, component, null)
                            });
        } else {
            result =
                    new WrapperPlayServerPlayerInfo(
                            xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server
                                    .WrapperPlayServerPlayerInfo.Action.UPDATE_DISPLAY_NAME,
                            new PlayerData[] {
                                new PlayerData(component, userProfile, GameMode.SURVIVAL, -1)
                            });
        }
        return (PacketWrapper<?>) result;
    }

    @Override
    public void displayLegacyTablist(Player player, Player player2, PlayerTag playerTag) {
        Scoreboard scoreboard = player.getScoreboard();
        String text = "_" + this.teamNameProvider.buildTeamName(player2, playerTag.priority());
        Team orCreate = ScoreboardUtil.getOrCreate(scoreboard, text);
        boolean matches = !playerTag.prefix().isEmpty();
        ChatColor chatColor =
                playerTag.color() != null
                        ? playerTag.color()
                        : (matches ? ColorUtil.getLastColors(playerTag.prefix()) : ChatColor.WHITE);
        String text2 = matches ? ColorUtil.color(playerTag.prefix()) : "";
        ScoreboardUtil.setColorIfChanged(orCreate, chatColor);
        ScoreboardUtil.setPrefixIfChanged(orCreate, text2);
        if (player2.getName().length() >= 15) {
            Object result = player2.getName().substring(14);
            if (matches) {
                result = String.valueOf(chatColor) + result;
            }
            ScoreboardUtil.setSuffixIfChanged(orCreate, (String) result);
        }
        orCreate.addEntry(player2.getPlayerListName());
    }

    @Generated
    public DefaultTeamService(
            TeamNameProvider teamNameProvider2, @Nullable PlayerNameProvider playerNameProvider2) {
        this.teamNameProvider = teamNameProvider2;
        this.playerNameProvider = playerNameProvider2;
    }
}
