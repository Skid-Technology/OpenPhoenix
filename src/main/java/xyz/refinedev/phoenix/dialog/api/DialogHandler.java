package xyz.refinedev.phoenix.dialog.api;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.PacketEventsAPI;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.Dialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.nbt.NBT;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.ClientVersion;
import xyz.refinedev.lib.com.github.retrooper.packetevents.resources.ResourceLocation;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerClearDialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerShowDialog;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.dialog.bedrock.IBedrockDialogRenderer;
import xyz.refinedev.phoenix.utils.version.ClientVersionUtil;
import xyz.refinedev.phoenix.utils.version.ServerVersion;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class DialogHandler {

    private static final ServerVersion serverVersion = ServerVersion.V_1_21_6;

    private static final ClientVersion clientVersion = ClientVersion.V_1_21_6;

    private final Phoenix plugin;

    private volatile IBedrockDialogRenderer bedrockDialogRenderer;

    private final Map<String, PhoenixDialog> phoenixDialogsByName = new ConcurrentHashMap<>();

    private final Map<Class<?>, PhoenixDialog> phoenixDialogsByClass = new ConcurrentHashMap<>();

    private final Map<UUID, UUID> uUIDsById = new ConcurrentHashMap<>();

    private final Set<UUID> uUIDs = ConcurrentHashMap.newKeySet();

    private final Map<UUID, String> stringsById = new ConcurrentHashMap<>();

    public DialogHandler(Phoenix phoenix) {
        this.plugin = phoenix;
        try {
            PacketEventsAPI packetEventsAPI;
            if ((packetEventsAPI = phoenix.getPacketEvents()) != null) {
                packetEventsAPI.getEventManager().registerListener(new DialogPacketListener(this));
            }
        } catch (Throwable throwable) {
            phoenix.getLogger()
                    .log(
                            Level.WARNING,
                            "Dialogs are unavailable, the response listener did not register.",
                            throwable);
        }
    }

    public void applyPhoenixDialog(PhoenixDialog phoenixDialog) {
        this.phoenixDialogsByName.put(phoenixDialog.getKey(), phoenixDialog);
        this.phoenixDialogsByClass.put(phoenixDialog.getClass(), phoenixDialog);
    }

    public <T extends PhoenixDialog> T findPhoenixDialog(Class<T> classValue) {
        return (T) this.phoenixDialogsByClass.get(classValue);
    }

    public PhoenixDialog findPhoenixDialog(String text) {
        return this.phoenixDialogsByName.get(text);
    }

    public boolean matchesEnabled(Player player) {
        if (!SettingsConfig.MODERN.DIALOGS.ENABLED) {
            return false;
        }
        IBedrockDialogRenderer bedrockDialogRenderer2 = this.bedrockDialogRenderer;
        return this.bedrockDialogRenderer != null && bedrockDialogRenderer2.isBedrock(player)
                ? true
                : ClientVersionUtil.serverSupports(serverVersion)
                        && ClientVersionUtil.supports(player, clientVersion);
    }

    public boolean matchesEnabled(Player player, PhoenixDialog phoenixDialog, UUID uUID) {
        return this.matchesEnabled(player, phoenixDialog, uUID, null);
    }

    public boolean matchesEnabled(
            Player player, PhoenixDialog phoenixDialog, UUID uUID, Dialog dialog) {
        if (phoenixDialog != null && this.matchesEnabled(player)) {
            if (uUID != null) {
                this.uUIDsById.put(player.getUniqueId(), uUID);
            } else {
                this.uUIDsById.remove(player.getUniqueId());
            }
            try {
                Dialog dialog2;
                if ((dialog2 = dialog != null ? dialog : phoenixDialog.build(player)) == null) {
                    return false;
                }
                IBedrockDialogRenderer bedrockDialogRenderer2 = this.bedrockDialogRenderer;
                if (this.bedrockDialogRenderer != null
                        && bedrockDialogRenderer2.isBedrock(player)) {
                    if (!bedrockDialogRenderer2.show(player, dialog2)) {
                        return false;
                    }
                } else if (!ClientVersionUtil.sendPacket(
                        player, new WrapperPlayServerShowDialog(dialog2))) {
                    return false;
                }
                this.uUIDs.add(player.getUniqueId());
                this.stringsById.put(player.getUniqueId(), phoenixDialog.getKey());
                return true;
            } catch (Throwable throwable) {
                this.plugin
                        .getLogger()
                        .log(
                                Level.WARNING,
                                "Failed to show the '" + phoenixDialog.getKey() + "' dialog.",
                                throwable);
                return false;
            }
        } else {
            return false;
        }
    }

    public void applyPlayer(Player player) {
        if (SettingsConfig.MODERN.DIALOGS.ENABLED) {
            ClientVersionUtil.sendPacket(player, new WrapperPlayServerClearDialog());
        }
    }

    public void applyPlayer(Player player, ResourceLocation resourceLocation, NBT nBT) {
        if (resourceLocation != null && "phoenix".equals(resourceLocation.getNamespace())) {
            PhoenixDialog phoenixDialog;
            if ((phoenixDialog = this.phoenixDialogsByName.get(resourceLocation.getKey()))
                    != null) {
                UUID uniqueId = player.getUniqueId();
                if (resourceLocation.getKey().equals(this.stringsById.remove(uniqueId))) {
                    this.uUIDs.remove(uniqueId);
                    try {
                        phoenixDialog.handle(player, new DialogResponse(nBT));
                    } catch (Throwable throwable) {
                        this.plugin
                                .getLogger()
                                .log(
                                        Level.WARNING,
                                        "Failed to handle the '"
                                                + phoenixDialog.getKey()
                                                + "' dialog response.",
                                        throwable);
                    } finally {
                        if (!this.uUIDs.remove(uniqueId)) {
                            this.uUIDsById.remove(uniqueId);
                        }
                    }
                }
            }
        }
    }

    public UUID getUniqueId(Player player) {
        return this.uUIDsById.get(player.getUniqueId());
    }

    public void applyUUID(UUID uUID) {
        this.uUIDsById.remove(uUID);
        this.uUIDs.remove(uUID);
        this.stringsById.remove(uUID);
        Iterator<xyz.refinedev.phoenix.dialog.api.PhoenixDialog> iterator2 =
                this.phoenixDialogsByName.values().iterator();
        while (iterator2.hasNext()) {
            ((PhoenixDialog) iterator2.next()).forget(uUID);
        }
    }

    public void setBedrockDialogRenderer(IBedrockDialogRenderer bedrockDialogRenderer2) {
        this.bedrockDialogRenderer = bedrockDialogRenderer2;
    }
}
