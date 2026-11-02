package xyz.refinedev.phoenix.nametag.packet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerAbstract;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerPriority;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketSendEvent;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.NameTagVisibility;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.ScoreBoardTeamInfo;
import xyz.refinedev.phoenix.config.NameTagsConfig;
import xyz.refinedev.phoenix.nametag.NameTagBackendFactory;
import xyz.refinedev.phoenix.nametag.NameTagExecutor;

import java.util.Optional;

public class TeamPacketListener extends PacketListenerAbstract {

    private final PacketNameTagBackend packetNameTagBackend;

    private final NameTagBackendFactory nameTagBackendFactory;

    private final NameTagExecutor nameTagExecutor;

    public TeamPacketListener(
            PacketNameTagBackend packetNameTagBackend2,
            NameTagBackendFactory nameTagBackendFactory2,
            NameTagExecutor nameTagExecutor2) {
        super(PacketListenerPriority.HIGHEST);
        this.packetNameTagBackend = packetNameTagBackend2;
        this.nameTagBackendFactory = nameTagBackendFactory2;
        this.nameTagExecutor = nameTagExecutor2;
    }

    public void onPacketSend(PacketSendEvent packetSendEvent) {
        if (packetSendEvent.getPacketType() == Server.TEAMS) {
            this.handlePacketSendEvent(packetSendEvent);
        } else if (packetSendEvent.getPacketType() == Server.RESPAWN) {
            this.handlePacketSendEventByNameTagBackendFactory(packetSendEvent);
        } else if (packetSendEvent.getPacketType() == Server.SPAWN_PLAYER) {
            this.handlePacketSendEvent(
                    packetSendEvent,
                    new WrapperPlayServerSpawnPlayer(packetSendEvent).getEntityId());
        } else if (packetSendEvent.getPacketType() == Server.SPAWN_ENTITY) {
            WrapperPlayServerSpawnEntity wrapperPlayServerSpawnEntity;
            if ((wrapperPlayServerSpawnEntity = new WrapperPlayServerSpawnEntity(packetSendEvent))
                            .getEntityType()
                    == EntityTypes.PLAYER) {
                this.handlePacketSendEvent(
                        packetSendEvent, wrapperPlayServerSpawnEntity.getEntityId());
            }
        } else {
            if (packetSendEvent.getPacketType() == Server.DESTROY_ENTITIES) {
                int[] values;
                int value =
                        (values =
                                        new WrapperPlayServerDestroyEntities(packetSendEvent)
                                                .getEntityIds())
                                .length;
                for (int index = 0; index < value; index++) {
                    int value2 = values[index];
                    this.handlePacketSendEventByPacketNameTagBackend(packetSendEvent, value2);
                }
            }
        }
    }

    private void handlePacketSendEvent(PacketSendEvent packetSendEvent) {
        Player player;
        if ((player = (Player) packetSendEvent.getPlayer()) != null
                && this.nameTagBackendFactory.matchesEnabled(player)) {
            WrapperPlayServerTeams wrapperPlayServerTeams;
            Optional<ScoreBoardTeamInfo> teamInfo =
                    (wrapperPlayServerTeams = new WrapperPlayServerTeams(packetSendEvent))
                            .getTeamInfo();
            if (NameTagsConfig.DISPLAY.DEBUG) {
                Bukkit.getLogger()
                        .info(
                                "[PhoenixNT] TEAMS -> "
                                        + player.getName()
                                        + " name="
                                        + wrapperPlayServerTeams.getTeamName()
                                        + " mode="
                                        + wrapperPlayServerTeams.getTeamMode()
                                        + " entries="
                                        + wrapperPlayServerTeams.getPlayers()
                                        + " visibility="
                                        + (teamInfo.isPresent()
                                                ? ((ScoreBoardTeamInfo) teamInfo.get())
                                                        .getTagVisibility()
                                                : "none"));
            }
            if (teamInfo.isPresent()) {
                if (((ScoreBoardTeamInfo) teamInfo.get()).getTagVisibility()
                        != NameTagVisibility.NEVER) {
                    ((ScoreBoardTeamInfo) teamInfo.get()).setTagVisibility(NameTagVisibility.NEVER);
                    packetSendEvent.markForReEncode(true);
                }
            }
        }
    }

    private void handlePacketSendEvent(PacketSendEvent packetSendEvent, int value) {
        Player player;
        if ((player = (Player) packetSendEvent.getPlayer()) != null) {
            Player player2;
            if ((player2 = this.packetNameTagBackend.getPlayer(value)) != null) {
                Player player3 = player2;
                Player player4 = player;
                this.nameTagExecutor.applyRunnable(
                        () -> {
                            this.packetNameTagBackend.applyPlayer(player4, player3);
                            this.packetNameTagBackend.refresh(player4, player3);
                        },
                        1L);
            }
        }
    }

    private void handlePacketSendEventByNameTagBackendFactory(PacketSendEvent packetSendEvent) {
        Player player;
        if ((player = (Player) packetSendEvent.getPlayer()) != null
                && this.nameTagBackendFactory.matchesEnabled(player)) {
            Player player2 = player;
            this.nameTagExecutor.applyRunnable(
                    () -> this.packetNameTagBackend.applyPlayerByNameTagStatesById(player2), 1L);
        }
    }

    private void handlePacketSendEventByPacketNameTagBackend(
            PacketSendEvent packetSendEvent, int value) {
        Player player;
        if ((player = (Player) packetSendEvent.getPlayer()) != null) {
            Player player2;
            if ((player2 = this.packetNameTagBackend.getPlayer(value)) != null) {
                Player player3 = player2;
                Player player4 = player;
                this.nameTagExecutor.applyRunnable(
                        () -> this.packetNameTagBackend.remove(player4, player3));
            }
        }
    }
}
