package xyz.refinedev.phoenix.listener.packet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerAbstract;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerPriority;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketReceiveEvent;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.TextureProperty;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.User;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSettings;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.alt.ClientFingerprint;
import xyz.refinedev.phoenix.alt.IClientFingerprint;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.profile.IProfile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ClientBrandPacketListener extends PacketListenerAbstract {

    private final Phoenix plugin;

    public ClientBrandPacketListener(Phoenix phoenix) {
        super(PacketListenerPriority.MONITOR);
        this.plugin = phoenix;
    }

    public void onPacketReceive(PacketReceiveEvent packetReceiveEvent) {
        if (GlobalConfig.SERVER.ALTS.ENABLED) {
            try {
                if (packetReceiveEvent.getPacketType() == Client.CLIENT_SETTINGS) {
                    this.handlePacketReceiveEvent(packetReceiveEvent);
                } else if (packetReceiveEvent.getPacketType() == Client.PLUGIN_MESSAGE) {
                    this.handlePacketReceiveEventByVoid(packetReceiveEvent);
                }
            } catch (Throwable throwable) {
            }
        }
    }

    private void handlePacketReceiveEvent(PacketReceiveEvent packetReceiveEvent) {
        IProfile profile;
        if ((profile = this.findProfile(packetReceiveEvent.getUser())) != null) {
            WrapperPlayClientSettings wrapperPlayClientSettings =
                    new WrapperPlayClientSettings(packetReceiveEvent);
            IClientFingerprint clientFingerprint;
            if ((clientFingerprint = profile.getClientFingerprint()) == null) {
                clientFingerprint = new ClientFingerprint();
            }
            String text = clientFingerprint.isEmpty() ? null : clientFingerprint.signature();
            clientFingerprint.setLocale(wrapperPlayClientSettings.getLocale());
            clientFingerprint.setViewDistance(wrapperPlayClientSettings.getViewDistance());
            clientFingerprint.setChatVisibility(
                    wrapperPlayClientSettings.getVisibility() == null
                            ? -1
                            : wrapperPlayClientSettings.getVisibility().ordinal());
            clientFingerprint.setSkinParts(
                    wrapperPlayClientSettings.getVisibleSkinSectionMask() & 255);
            clientFingerprint.setMainHand(
                    wrapperPlayClientSettings.getMainHand() == null
                            ? -1
                            : wrapperPlayClientSettings.getMainHand().ordinal());
            if (packetReceiveEvent.getUser().getClientVersion() != null) {
                clientFingerprint.setProtocolVersion(
                        packetReceiveEvent.getUser().getClientVersion().getProtocolVersion());
            }
            clientFingerprint.setCapturedAt(System.currentTimeMillis());
            profile.setClientFingerprint(clientFingerprint);
            this.applyString(text, clientFingerprint);
            this.applyUser(packetReceiveEvent.getUser(), profile);
        }
    }

    private void handlePacketReceiveEventByVoid(PacketReceiveEvent packetReceiveEvent) {
        WrapperPlayClientPluginMessage wrapperPlayClientPluginMessage;
        String channelName =
                (wrapperPlayClientPluginMessage =
                                new WrapperPlayClientPluginMessage(packetReceiveEvent))
                        .getChannelName();
        if ("minecraft:register".equals(channelName) || "REGISTER".equals(channelName)) {
            this.handlePacketReceiveEvent(packetReceiveEvent, wrapperPlayClientPluginMessage);
        } else if ("minecraft:brand".equals(channelName) || "MC|Brand".equals(channelName)) {
            byte[] values;
            if ((values = wrapperPlayClientPluginMessage.getData()) != null && values.length != 0) {
                IProfile profile;
                if ((profile = this.findProfile(packetReceiveEvent.getUser())) != null) {
                    String text;
                    if (!(text =
                                    (values.length > 1
                                                    ? new String(
                                                            values,
                                                            1,
                                                            values.length - 1,
                                                            StandardCharsets.UTF_8)
                                                    : new String(values, StandardCharsets.UTF_8))
                                            .replaceAll("[^\\x20-\\x7E]", ""))
                            .isEmpty()) {
                        IClientFingerprint clientFingerprint;
                        if ((clientFingerprint = profile.getClientFingerprint()) == null) {
                            clientFingerprint = new ClientFingerprint();
                        }
                        if (!text.equalsIgnoreCase(clientFingerprint.getBrand())) {
                            String text2 =
                                    clientFingerprint.isEmpty()
                                            ? null
                                            : clientFingerprint.signature();
                            clientFingerprint.setBrand(text);
                            profile.setClientFingerprint(clientFingerprint);
                            this.applyString(text2, clientFingerprint);
                        }
                    }
                }
            }
        }
    }

    private void handlePacketReceiveEvent(
            PacketReceiveEvent packetReceiveEvent,
            WrapperPlayClientPluginMessage wrapperPlayClientPluginMessage) {
        byte[] values;
        if ((values = wrapperPlayClientPluginMessage.getData()) != null && values.length != 0) {
            IProfile profile;
            if ((profile = this.findProfile(packetReceiveEvent.getUser())) != null) {
                IClientFingerprint clientFingerprint;
                if ((clientFingerprint = profile.getClientFingerprint()) == null) {
                    clientFingerprint = new ClientFingerprint();
                }
                TreeSet<String> treeSet = new TreeSet<>();
                if (clientFingerprint.getChannels() != null) {
                    treeSet.addAll(clientFingerprint.getChannels());
                }
                String[] texts;
                int value =
                        (texts = new String(values, StandardCharsets.UTF_8).split("\u0000")).length;
                for (int index = 0; index < value; index++) {
                    String text;
                    if (!(text = texts[index].trim().toLowerCase().replaceAll("[^a-z0-9_:./-]", ""))
                                    .isEmpty()
                            && text.length() <= 64) {
                        treeSet.add(text);
                    }
                }
                if (!treeSet.isEmpty() && treeSet.size() <= 128) {
                    if (clientFingerprint.getChannels() == null
                            || treeSet.size() != clientFingerprint.getChannels().size()) {
                        String text2 =
                                clientFingerprint.isEmpty() ? null : clientFingerprint.signature();
                        clientFingerprint.setChannels(new ArrayList<>(treeSet));
                        profile.setClientFingerprint(clientFingerprint);
                        this.applyString(text2, clientFingerprint);
                    }
                }
            }
        }
    }

    private void applyUser(User user, IProfile profile) {
        if (!GlobalConfig.SERVER.ALTS.CRACKED_SERVER) {
            if (user.getProfile() != null && user.getProfile().getTextureProperties() != null) {
                for (TextureProperty textureProperty : user.getProfile().getTextureProperties()) {
                    if ("textures".equals(textureProperty.getName())) {
                        String text;
                        if ((text = this.create(textureProperty.getValue())) == null) {
                            return;
                        }
                        String text2;
                        if ((text2 = this.plugin.getHashHandler().hash(text))
                                .equals(profile.getSkinHash())) {
                            return;
                        }
                        profile.setSkinHash(text2);
                        this.applyString("skin:" + text2);
                        return;
                    }
                }
            }
        }
    }

    private String create(String text) {
        try {
            String text2 = new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
            JsonObject jsonObject;
            if ((jsonObject = (JsonObject) this.plugin.getGson().fromJson(text2, JsonObject.class))
                    == null) {
                return null;
            }
            JsonObject jsonObject2;
            if ((jsonObject2 = jsonObject.getAsJsonObject("textures")) == null) {
                return null;
            }
            JsonObject jsonObject3;
            if ((jsonObject3 = jsonObject2.getAsJsonObject("SKIN")) == null) {
                return null;
            }
            JsonElement jsonElement;
            return (jsonElement = jsonObject3.get("url")) == null
                    ? null
                    : jsonElement.getAsString();
        } catch (Exception exception) {
            return null;
        }
    }

    private void applyString(String text, IClientFingerprint clientFingerprint) {
        String text2;
        if (!(text2 = clientFingerprint.signature()).equals(text)) {
            this.applyString("fp:" + this.plugin.getHashHandler().hash(text2));
        }
    }

    private void applyString(String text) {
        CompletableFuture.runAsync(
                () -> this.plugin.getAddressStatsHandler().recordNewAccount(text, null));
    }

    private IProfile findProfile(User user) {
        UUID uUID;
        return (uUID = user == null ? null : user.getUUID()) == null
                ? null
                : this.plugin.getProfileHandler().getCachedProfile(uUID);
    }

    public static void applyPhoenix(Phoenix phoenix, Player player, IProfile profile) {
        if (GlobalConfig.SERVER.ALTS.ENABLED) {
            try {
                int value;
                if ((value = calculateCount(player)) > 0) {
                    ArrayList values;
                    (values =
                                    profile.getRecentPings() == null
                                            ? new ArrayList()
                                            : new ArrayList<>(profile.getRecentPings()))
                            .add(value);
                    while (values.size() > 20) {
                        values.remove(0);
                    }
                    profile.setRecentPings(values);
                }
            } catch (Throwable throwable) {
            }
            try {
                Location location;
                if ((location = player.getLocation()) == null || location.getWorld() == null) {
                    return;
                }
                String text =
                        "loc:"
                                + phoenix.getHashHandler()
                                        .hash(
                                                location.getWorld().getName()
                                                        + ":"
                                                        + (location.getBlockX() >> 6)
                                                        + ":"
                                                        + (location.getBlockZ() >> 6));
                Set<String> values2;
                if ((values2 = profile.getLogoutBuckets()) != null && values2.contains(text)) {
                    return;
                }
                LinkedHashSet<String> linkedHashSet;
                if ((linkedHashSet =
                                        values2 == null
                                                ? new LinkedHashSet()
                                                : new LinkedHashSet(values2))
                                .size()
                        >= 12) {
                    Iterator iterator2;
                    (iterator2 = linkedHashSet.iterator()).next();
                    iterator2.remove();
                }
                linkedHashSet.add(text);
                profile.setLogoutBuckets(linkedHashSet);
                CompletableFuture.runAsync(
                        () -> phoenix.getAddressStatsHandler().recordNewAccount(text, null));
            } catch (Throwable throwable2) {
            }
        }
    }

    private static int calculateCount(Player player) {
        try {
            return (Integer) player.getClass().getMethod("getPing").invoke(player);
        } catch (Throwable throwable) {
            try {
                Object result;
                return (result = player.getClass().getMethod("getHandle").invoke(player))
                        .getClass()
                        .getField("ping")
                        .getInt(result);
            } catch (Throwable throwable2) {
                return -1;
            }
        }
    }
}
