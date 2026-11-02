package me.creaxx.api.nms.impl;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

import me.creaxx.api.NMSHandler;
import me.creaxx.api.nms.INMSImpl;

import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.Team;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.disguise.IDisguiseData;
import xyz.refinedev.phoenix.profile.disguise.ISkin;
import xyz.refinedev.phoenix.profile.disguise.Skin;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DisguiseUtil;
import xyz.refinedev.phoenix.utils.ExceptionUtil;
import xyz.refinedev.phoenix.utils.Utilities;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class v1_21_R10 implements INMSImpl {

    @Override
    public void disguise(IProfile profile, boolean matches) {
        Player player = Bukkit.getPlayer(profile.getUniqueId());
        IDisguiseData disguiseData = profile.getDisguiseData();
        ServerPlayer entity = this.getEntity(player);
        DisguiseUtil.updateName(player.getPlayerProfile(), disguiseData.getDisguiseName());
        entity.displayName = disguiseData.getRealName();
        try {
            DedicatedPlayerList playerList =
                    ((CraftServer) Bukkit.getServer()).getHandle().getServer().getPlayerList();
            Field field;
            (field = PlayerList.class.getDeclaredField("playersByName")).setAccessible(true);
            Map entries;
            (entries = (Map) field.get(playerList)).remove(profile.getName());
            entries.put(disguiseData.getDisguiseName(), entity);
        } catch (Exception exception) {
            Phoenix.getInstance()
                    .getLogger()
                    .log(Level.SEVERE, ExceptionUtil.getStackTrace(exception));
        }
        this.updateSkin(player, disguiseData.getDisguiseSkin(), true);
    }

    @Override
    public void undisguise(IProfile profile) {
        Player player = Bukkit.getPlayer(profile.getUniqueId());
        IDisguiseData disguiseData = profile.getDisguiseData();
        ServerPlayer entity = this.getEntity(player);
        DisguiseUtil.updateName(player.getPlayerProfile(), disguiseData.getRealName());
        entity.displayName = disguiseData.getRealName();
        try {
            DedicatedPlayerList playerList =
                    ((CraftServer) Bukkit.getServer()).getHandle().getServer().getPlayerList();
            Field field;
            (field = PlayerList.class.getDeclaredField("playersByName")).setAccessible(true);
            Map entries;
            (entries = (Map) field.get(playerList)).remove(disguiseData.getDisguiseName());
            entries.put(disguiseData.getRealName(), entity);
        } catch (Exception exception) {
            Phoenix.getInstance()
                    .getLogger()
                    .log(Level.SEVERE, ExceptionUtil.getStackTrace(exception));
        }
        this.updateSkin(player, disguiseData.getRealSkin(), false);
    }

    @Override
    public void updateSkin(Player player, ISkin skin, boolean matches) {
        ServerPlayer entity = this.getEntity(player);
        Location location = player.getLocation().clone();
        PlayerProfile playerProfile;
        (playerProfile = player.getPlayerProfile()).getProperties().clear();
        playerProfile.setProperty(
                new ProfileProperty("textures", skin.getValue(), skin.getSignature()));
        entity.connection.send(
                new ClientboundPlayerInfoRemovePacket(
                        Collections.singletonList(player.getPlayerProfile().getId())));
        entity.connection.send(
                ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(
                        Collections.singletonList(entity)));
        player.spigot().respawn();
        player.getInventory().setItemInHand(player.getItemInHand());
        player.updateInventory();
        NMSHandler.getInstance().getTeleportAPI().teleport(player, location);
        Iterator<org.bukkit.entity.Player> iterator2 = Utilities.getOnlinePlayers().iterator();
        while (iterator2.hasNext()) {
            Player player2;
            if ((player2 = (Player) iterator2.next()).canSee(player)) {
                player2.hidePlayer(player);
                player2.showPlayer(player);
            }
        }
    }

    @Override
    public void updateHeaderFooter(Player player, String text, String text2) {
        player.setPlayerListHeaderFooter(Color.translate(text), Color.translate(text2));
    }

    @Override
    public ItemStack itemWithBase64(ItemStack itemStack, String text) {
        if (itemStack == null || text == null || text.isEmpty()) {
            return itemStack;
        }
        if (!(itemStack.getItemMeta() instanceof SkullMeta)) {
            return itemStack;
        }
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        PlayerProfile playerProfile;
        (playerProfile =
                        Bukkit.createProfile(
                                new UUID(
                                        text.substring(text.length() - 20).hashCode(),
                                        text.substring(text.length() - 10).hashCode()),
                                "Player"))
                .setProperty(new ProfileProperty("textures", text));
        skullMeta.setPlayerProfile(playerProfile);
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }

    @Override
    public ISkin getPlayerSkin(Player player) {
        String text = "";
        String text2 = "";
        Iterator iterator2 = player.getPlayerProfile().getProperties().iterator();
        while (iterator2.hasNext()) {
            ProfileProperty profileProperty;
            if ((profileProperty = (ProfileProperty) iterator2.next())
                    .getName()
                    .equals("textures")) {
                text = profileProperty.getValue();
                text2 = profileProperty.getSignature();
            }
        }
        return new Skin(player.getName(), text, text2);
    }

    @Override
    public void sitPlayer(Player player) {}

    @Override
    public void unsitPlayer(Player player) {}

    @Override
    public void removePlayerFromTeam(String text, Team team) {
        CraftScoreboard craftScoreboard = (CraftScoreboard) team.getScoreboard();
        try {
            Field field;
            (field = Scoreboard.class.getDeclaredField("teamsByPlayer")).setAccessible(true);
            ((Object2ObjectMap) field.get(craftScoreboard.getHandle())).remove(text);
            Field field2;
            (field2 =
                            Class.forName("org.bukkit.craftbukkit.scoreboard.CraftTeam")
                                    .getDeclaredField("team"))
                    .setAccessible(true);
            ((PlayerTeam) field2.get(team)).getPlayers().remove(text);
        } catch (Exception exception) {
        }
    }

    public ServerPlayer getEntity(Player player) {
        return ((CraftPlayer) player).getHandle();
    }
}
