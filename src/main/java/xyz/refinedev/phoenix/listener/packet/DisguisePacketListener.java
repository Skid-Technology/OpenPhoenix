package xyz.refinedev.phoenix.listener.packet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerAbstract;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerPriority;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketSendEvent;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.UserDisconnectEvent;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_16;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.User;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.UserProfile;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo.Action;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo.PlayerData;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate.PlayerInfo;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.disguise.IDisguiseData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class DisguisePacketListener extends PacketListenerAbstract {

    private final Phoenix plugin;

    private final Map<UUID, Map<UUID, UUID>> uUIDsById = new ConcurrentHashMap<>();

    public DisguisePacketListener(Phoenix phoenix) {
        super(PacketListenerPriority.HIGHEST);
        this.plugin = phoenix;
    }

    public void onPacketSend(PacketSendEvent packetSendEvent) {
        if (!this.uUIDsById.isEmpty() || this.plugin.getDisguiseHandler().hasLocalDisguises()) {
            User user;
            if ((user = packetSendEvent.getUser()) != null && user.getUUID() != null) {
                PacketTypeCommon packetType = packetSendEvent.getPacketType();
                UUID uUID = user.getUUID();
                try {
                    if (packetType == Server.PLAYER_INFO) {
                        this.handlePacketSendEvent(packetSendEvent, uUID);
                    } else if (packetType == Server.PLAYER_INFO_UPDATE) {
                        this.handlePacketSendEventByVoid(packetSendEvent, uUID);
                    } else if (packetType == Server.PLAYER_INFO_REMOVE) {
                        this.handlePacketSendEventByPacketSendEvent(packetSendEvent, uUID);
                    } else if (packetType == Server.SPAWN_PLAYER) {
                        this.handlePacketSendEventByUUID(packetSendEvent, uUID);
                    } else if (packetType == Server.SPAWN_ENTITY) {
                        this.handlePacketSendEvent2(packetSendEvent, uUID);
                    } else if (packetType == Server.CHAT_MESSAGE) {
                        this.handlePacketSendEvent3(packetSendEvent, uUID);
                    }
                } catch (Exception exception) {
                    this.plugin
                            .getLogger()
                            .log(
                                    Level.SEVERE,
                                    "Error whilst intercepting packets for disguise: ",
                                    exception);
                }
            }
        }
    }

    private UUID findUUID(UUID uUID, UUID uUID2) {
        if (uUID != null && !uUID.equals(uUID2)) {
            IProfile profile;
            if ((profile = this.plugin.getProfileHandler().getCachedProfile(uUID)) == null) {
                return null;
            }
            IDisguiseData disguiseData;
            return (disguiseData = profile.getDisguiseData()) != null && disguiseData.isDisguised()
                    ? disguiseData.getDisguiseUuid()
                    : null;
        } else {
            return null;
        }
    }

    private void applyUUID(UUID uUID, UUID uUID2, UUID uUID3) {
        if (uUID2 != null) {
            if (uUID3 == null) {
                this.computeIfPresent(uUID, uUID2);
            } else {
                this.uUIDsById
                        .computeIfAbsent(uUID, result -> new ConcurrentHashMap<>())
                        .put(uUID2, uUID3);
            }
        }
    }

    private UUID findUUIDByUUIDsById(UUID uUID, UUID uUID2) {
        if (uUID2 != null && !uUID2.equals(uUID)) {
            Map<UUID, UUID> entries;
            return (entries = this.uUIDsById.get(uUID)) == null ? null : (UUID) entries.get(uUID2);
        } else {
            return null;
        }
    }

    private UUID computeIfPresent(UUID uUID, UUID uUID2) {
        if (uUID2 != null && !uUID2.equals(uUID)) {
            UUID[] uUIDs = new UUID[1];
            this.uUIDsById.computeIfPresent(
                    uUID,
                    (result, result2) -> {
                        uUIDs[0] = result2.remove(uUID2);
                        return result2.isEmpty() ? null : result2;
                    });
            return uUIDs[0];
        } else {
            return null;
        }
    }

    public void onUserDisconnect(UserDisconnectEvent userDisconnectEvent) {
        User user;
        if ((user = userDisconnectEvent.getUser()) != null && user.getUUID() != null) {
            this.uUIDsById.remove(user.getUUID());
        }
    }

    private void applyString(String text, UUID uUID, UUID uUID2, UUID uUID3, String text2) {
        if (SettingsConfig.DISGUISE.DEBUG) {
            Player player = Bukkit.getPlayer(uUID);
            Bukkit.getLogger()
                    .info(
                            "[PhoenixDisguise] "
                                    + text
                                    + " -> "
                                    + (player == null ? uUID : player.getName())
                                    + " name="
                                    + text2
                                    + " uuid="
                                    + uUID2
                                    + " sent="
                                    + (uUID3 == null ? uUID2 : uUID3));
        }
    }

    private void handlePacketSendEvent(PacketSendEvent packetSendEvent, UUID uUID) {
        WrapperPlayServerPlayerInfo wrapperPlayServerPlayerInfo;
        Action action;
        boolean matches =
                (action =
                                (wrapperPlayServerPlayerInfo =
                                                new WrapperPlayServerPlayerInfo(packetSendEvent))
                                        .getAction())
                        == Action.ADD_PLAYER;
        List<PlayerData> playerDataList = wrapperPlayServerPlayerInfo.getPlayerDataList();
        ArrayList<PlayerData> values = new ArrayList<>(playerDataList.size());
        boolean matches2 = false;
        Iterator iterator2 = playerDataList.iterator();
        while (iterator2.hasNext()) {
            PlayerData playerData;
            UserProfile userProfile;
            UUID uUID2 =
                    (userProfile = (playerData = (PlayerData) iterator2.next()).getUserProfile())
                                    == null
                            ? null
                            : userProfile.getUUID();
            UUID uUID3;
            if (matches) {
                uUID3 = this.findUUID(uUID2, uUID);
                if (uUID2 != null && !uUID2.equals(uUID)) {
                    this.applyUUID(uUID, uUID2, uUID3);
                }
            } else if (action == Action.REMOVE_PLAYER) {
                uUID3 = this.computeIfPresent(uUID, uUID2);
            } else {
                uUID3 = this.findUUIDByUUIDsById(uUID, uUID2);
            }
            this.applyString(
                    String.valueOf(action),
                    uUID,
                    uUID2,
                    uUID3,
                    userProfile == null ? "?" : userProfile.getName());
            if (uUID3 == null) {
                values.add(playerData);
            } else {
                values.add(
                        new PlayerData(
                                playerData.getDisplayName(),
                                new UserProfile(
                                        uUID3,
                                        userProfile.getName(),
                                        userProfile.getTextureProperties()),
                                playerData.getGameMode(),
                                null,
                                playerData.getPing()));
                matches2 = true;
            }
        }
        if (matches2) {
            wrapperPlayServerPlayerInfo.setPlayerDataList(values);
            packetSendEvent.markForReEncode(true);
        }
    }

    private void handlePacketSendEventByVoid(PacketSendEvent packetSendEvent, UUID uUID) {
        WrapperPlayServerPlayerInfoUpdate wrapperPlayServerPlayerInfoUpdate;
        boolean matches =
                (wrapperPlayServerPlayerInfoUpdate =
                                new WrapperPlayServerPlayerInfoUpdate(packetSendEvent))
                        .getActions()
                        .contains(
                                xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play
                                        .server.WrapperPlayServerPlayerInfoUpdate.Action
                                        .ADD_PLAYER);
        List<PlayerInfo> entries = wrapperPlayServerPlayerInfoUpdate.getEntries();
        ArrayList<PlayerInfo> values = new ArrayList<>(entries.size());
        boolean matches2 = false;
        Iterator iterator2 = entries.iterator();
        while (iterator2.hasNext()) {
            PlayerInfo playerInfo;
            UserProfile userProfile;
            UUID uUID2 =
                    (userProfile = (playerInfo = (PlayerInfo) iterator2.next()).getGameProfile())
                                    == null
                            ? null
                            : userProfile.getUUID();
            UUID uUID3 = this.findUUID(uUID2, uUID);
            if (matches && uUID2 != null && !uUID2.equals(uUID)) {
                this.applyUUID(uUID, uUID2, uUID3);
            }
            this.applyString(
                    wrapperPlayServerPlayerInfoUpdate.getActions().toString(),
                    uUID,
                    uUID2,
                    uUID3,
                    userProfile == null ? "?" : userProfile.getName());
            if (uUID3 == null) {
                values.add(playerInfo);
            } else {
                PlayerInfo playerInfo2;
                (playerInfo2 = new PlayerInfo(playerInfo))
                        .setGameProfile(
                                new UserProfile(
                                        uUID3,
                                        userProfile.getName(),
                                        userProfile.getTextureProperties()));
                playerInfo2.setChatSession(null);
                values.add(playerInfo2);
                matches2 = true;
            }
        }
        if (matches2) {
            wrapperPlayServerPlayerInfoUpdate.setEntries(values);
            packetSendEvent.markForReEncode(true);
        }
    }

    private void handlePacketSendEventByPacketSendEvent(
            PacketSendEvent packetSendEvent, UUID uUID) {
        WrapperPlayServerPlayerInfoRemove wrapperPlayServerPlayerInfoRemove;
        List<UUID> profileIds =
                (wrapperPlayServerPlayerInfoRemove =
                                new WrapperPlayServerPlayerInfoRemove(packetSendEvent))
                        .getProfileIds();
        ArrayList<UUID> values = new ArrayList<>(profileIds.size());
        boolean matches = false;
        for (UUID uUID2 : profileIds) {
            UUID uUID3 = this.computeIfPresent(uUID, uUID2);
            this.applyString("REMOVE", uUID, uUID2, uUID3, "-");
            if (uUID3 == null) {
                values.add(uUID2);
            } else {
                values.add(uUID3);
                matches = true;
            }
        }
        if (matches) {
            wrapperPlayServerPlayerInfoRemove.setProfileIds(values);
            packetSendEvent.markForReEncode(true);
        }
    }

    private void handlePacketSendEventByUUID(PacketSendEvent packetSendEvent, UUID uUID) {
        WrapperPlayServerSpawnPlayer wrapperPlayServerSpawnPlayer =
                new WrapperPlayServerSpawnPlayer(packetSendEvent);
        UUID uUID2;
        if ((uUID2 = this.findUUID(wrapperPlayServerSpawnPlayer.getUUID(), uUID)) != null) {
            wrapperPlayServerSpawnPlayer.setUUID(uUID2);
            packetSendEvent.markForReEncode(true);
        }
    }

    private void handlePacketSendEvent2(PacketSendEvent packetSendEvent, UUID uUID) {
        WrapperPlayServerSpawnEntity wrapperPlayServerSpawnEntity;
        if ((wrapperPlayServerSpawnEntity = new WrapperPlayServerSpawnEntity(packetSendEvent))
                        .getEntityType()
                == EntityTypes.PLAYER) {
            Optional<UUID> optional;
            if ((optional = wrapperPlayServerSpawnEntity.getUUID()).isPresent()) {
                UUID uUID2;
                if ((uUID2 = this.findUUID((UUID) optional.get(), uUID)) != null) {
                    wrapperPlayServerSpawnEntity.setUUID(Optional.of(uUID2));
                    packetSendEvent.markForReEncode(true);
                }
            }
        }
    }

    private void handlePacketSendEvent3(PacketSendEvent packetSendEvent, UUID uUID) {
        WrapperPlayServerChatMessage wrapperPlayServerChatMessage;
        if ((wrapperPlayServerChatMessage = new WrapperPlayServerChatMessage(packetSendEvent))
                        .getMessage()
                instanceof ChatMessage_v1_16) {
            ChatMessage_v1_16 chatMessage_v1_16 =
                    (ChatMessage_v1_16) wrapperPlayServerChatMessage.getMessage();
            UUID uUID2;
            if ((uUID2 = this.findUUID(chatMessage_v1_16.getSenderUUID(), uUID)) != null) {
                chatMessage_v1_16.setSenderUUID(uUID2);
                packetSendEvent.markForReEncode(true);
            }
        }
    }
}
