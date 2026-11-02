package me.creaxx.api.nms.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

import me.creaxx.api.nms.INMSImpl;

import net.minecraft.network.chat.IChatBaseComponent.ChatSerializer;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.a;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardTeam;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.profile.CraftPlayerProfile;
import org.bukkit.craftbukkit.v1_20_R3.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.Team;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.disguise.IDisguiseData;
import xyz.refinedev.phoenix.profile.disguise.ISkin;
import xyz.refinedev.phoenix.profile.disguise.Skin;
import xyz.refinedev.phoenix.utils.DisguiseUtil;
import xyz.refinedev.phoenix.utils.ExceptionUtil;
import xyz.refinedev.phoenix.utils.Utilities;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class v1_20_R3 implements INMSImpl {

    @Override
    public void disguise(IProfile profile, boolean matches) {
        Player player = Bukkit.getPlayer(profile.getUniqueId());
        IDisguiseData disguiseData = profile.getDisguiseData();
        EntityPlayer entityPlayer;
        DisguiseUtil.updateName(
                (entityPlayer = this.getEntity(player)).fR(), disguiseData.getDisguiseName());
        entityPlayer.displayName = disguiseData.getRealName();
        try {
            PlayerList playerList = ((CraftServer) Bukkit.getServer()).getHandle().b().ae();
            Field field;
            (field = PlayerList.class.getDeclaredField("playersByName")).setAccessible(true);
            Map entries;
            (entries = (Map) field.get(playerList)).remove(profile.getName());
            entries.put(disguiseData.getDisguiseName(), entityPlayer);
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
        EntityPlayer entityPlayer;
        DisguiseUtil.updateName(
                (entityPlayer = this.getEntity(player)).fR(), disguiseData.getRealName());
        entityPlayer.displayName = disguiseData.getRealName();
        try {
            PlayerList playerList = ((CraftServer) Bukkit.getServer()).getHandle().b().ae();
            Field field;
            (field = PlayerList.class.getDeclaredField("playersByName")).setAccessible(true);
            Map entries;
            (entries = (Map) field.get(playerList)).remove(disguiseData.getDisguiseName());
            entries.put(disguiseData.getRealName(), entityPlayer);
        } catch (Exception exception) {
            Phoenix.getInstance()
                    .getLogger()
                    .log(Level.SEVERE, ExceptionUtil.getStackTrace(exception));
        }
        this.updateSkin(player, disguiseData.getRealSkin(), false);
    }

    @Override
    public void updateSkin(Player player, ISkin skin, boolean matches) {
        EntityPlayer entity = this.getEntity(player);
        Location location = player.getLocation().clone();
        GameProfile gameProfile;
        (gameProfile = entity.fR()).getProperties().clear();
        gameProfile
                .getProperties()
                .put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
        entity.c.a(
                new ClientboundPlayerInfoRemovePacket(
                        Collections.singletonList(player.getUniqueId())));
        entity.c.a(new ClientboundPlayerInfoUpdatePacket(a.b, entity));
        entity.c.a(new ClientboundPlayerInfoUpdatePacket(a.d, entity));
        player.spigot().respawn();
        player.getInventory().setItemInHand(player.getItemInHand());
        player.updateInventory();
        player.teleport(location);
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
        EntityPlayer handle = ((CraftPlayer) player).getHandle();
        PacketPlayOutPlayerListHeaderFooter packetPlayOutPlayerListHeaderFooter =
                new PacketPlayOutPlayerListHeaderFooter(
                        ChatSerializer.a("{\"text\":\"" + text + "\"}"),
                        ChatSerializer.a("{\"text\":\"" + text2 + "\"}"));
        handle.c.a(packetPlayOutPlayerListHeaderFooter);
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
        UUID uUID =
                new UUID(
                        text.substring(text.length() - 20).hashCode(),
                        text.substring(text.length() - 10).hashCode());
        GameProfile gameProfile;
        (gameProfile = new GameProfile(uUID, "Player"))
                .getProperties()
                .put("textures", new Property("textures", text));
        skullMeta.setOwnerProfile(new CraftPlayerProfile(gameProfile));
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }

    @Override
    public ISkin getPlayerSkin(Player player) {
        String text = "";
        String text2 = "";
        Iterator<com.mojang.authlib.properties.Property> iterator2 =
                ((CraftPlayer) player).getProfile().getProperties().get("textures").iterator();
        while (iterator2.hasNext()) {
            Property property;
            text = (property = (Property) iterator2.next()).value();
            text2 = property.signature();
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
            (field = Scoreboard.class.getDeclaredField("h")).setAccessible(true);
            ((Object2ObjectMap) field.get(craftScoreboard.getHandle())).remove(text);
            Field field2;
            (field2 =
                            Class.forName("org.bukkit.craftbukkit.v1_20_R3.scoreboard.CraftTeam")
                                    .getDeclaredField("team"))
                    .setAccessible(true);
            ((ScoreboardTeam) field2.get(team)).g().remove(text);
        } catch (Exception exception) {
        }
    }

    public EntityPlayer getEntity(Player player) {
        return ((CraftPlayer) player).getHandle();
    }
}
