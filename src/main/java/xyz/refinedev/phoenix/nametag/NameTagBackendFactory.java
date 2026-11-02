package xyz.refinedev.phoenix.nametag;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.PacketEvents;
import xyz.refinedev.lib.com.github.retrooper.packetevents.manager.server.ServerVersion;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.ClientVersion;
import xyz.refinedev.phoenix.utils.BedrockUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NameTagBackendFactory {

    private final Map<UUID, NameTagBackendType> nameTagBackendTypesById = new ConcurrentHashMap<>();

    public static NameTagBackendType findNameTagBackendType(
            ServerVersion serverVersion, ClientVersion clientVersion, boolean matches) {
        if (matches) {
            return NameTagBackendType.TEAM;
        } else if (serverVersion == null || clientVersion == null) {
            return NameTagBackendType.TEAM;
        } else if (!serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
            return NameTagBackendType.TEAM;
        } else {
            return !clientVersion.isNewerThanOrEquals(ClientVersion.V_1_20_2)
                    ? NameTagBackendType.TEAM
                    : NameTagBackendType.DISPLAY;
        }
    }

    public NameTagBackendType findNameTagBackendType(Player player) {
        ClientVersion clientVersion;
        try {
            clientVersion = PacketEvents.getAPI().getPlayerManager().getClientVersion(player);
        } catch (Throwable throwable) {
            clientVersion = null;
        }
        if (clientVersion != null && clientVersion != ClientVersion.UNKNOWN) {
            NameTagBackendType nameTagBackendType;
            try {
                nameTagBackendType =
                        findNameTagBackendType(
                                PacketEvents.getAPI().getServerManager().getVersion(),
                                clientVersion,
                                BedrockUtils.getUniqueId(player));
            } catch (Throwable throwable2) {
                nameTagBackendType = NameTagBackendType.TEAM;
            }
            this.nameTagBackendTypesById.put(player.getUniqueId(), nameTagBackendType);
            return nameTagBackendType;
        } else {
            return NameTagBackendType.TEAM;
        }
    }

    public NameTagBackendType getUniqueId(Player player) {
        NameTagBackendType nameTagBackendType;
        return (nameTagBackendType = this.nameTagBackendTypesById.get(player.getUniqueId())) != null
                ? nameTagBackendType
                : this.findNameTagBackendType(player);
    }

    public boolean matchesEnabled(Player player) {
        return this.getUniqueId(player) == NameTagBackendType.DISPLAY;
    }

    public void applyUUID(UUID uUID) {
        this.nameTagBackendTypesById.remove(uUID);
    }

    public void applyUUIDByNameTagBackendTypesById(UUID uUID) {
        this.nameTagBackendTypesById.put(uUID, NameTagBackendType.TEAM);
    }

    public void initialize() {
        this.nameTagBackendTypesById.clear();
    }
}
