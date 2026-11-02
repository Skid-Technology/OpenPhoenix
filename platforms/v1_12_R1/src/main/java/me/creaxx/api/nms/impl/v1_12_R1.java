package me.creaxx.api.nms.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.creaxx.api.nms.INMSImpl;

import net.minecraft.server.v1_12_R1.EntityCreeper;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EnumGamemode;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_12_R1.PacketPlayOutRespawn;
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_12_R1.PlayerList;
import net.minecraft.server.v1_12_R1.Scoreboard;
import net.minecraft.server.v1_12_R1.ScoreboardTeam;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.scoreboard.CraftScoreboard;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class v1_12_R1 implements INMSImpl {

    private Map<UUID, Integer> creeperIds = new HashMap<>();

    @Override
    public void disguise(IProfile profile, boolean matches) {
        Player player = Bukkit.getPlayer(profile.getUniqueId());
        IDisguiseData disguiseData = profile.getDisguiseData();
        EntityPlayer entityPlayer;
        DisguiseUtil.updateName(
                (entityPlayer = this.getEntity(player)).getProfile(),
                disguiseData.getDisguiseName());
        entityPlayer.displayName = disguiseData.getDisguiseName();
        try {
            PlayerList playerList =
                    ((CraftServer) Bukkit.getServer()).getHandle().getServer().getPlayerList();
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
                (entityPlayer = this.getEntity(player)).getProfile(), disguiseData.getRealName());
        entityPlayer.displayName = disguiseData.getRealName();
        try {
            PlayerList playerList =
                    ((CraftServer) Bukkit.getServer()).getHandle().getServer().getPlayerList();
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
        EntityPlayer handle = ((CraftPlayer) player).getHandle();
        Location location = player.getLocation().clone();
        GameProfile gameProfile;
        (gameProfile = handle.getProfile()).getProperties().clear();
        gameProfile
                .getProperties()
                .put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
        handle.playerConnection.sendPacket(
                new PacketPlayOutPlayerInfo(
                        EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[] {handle}));
        handle.playerConnection.sendPacket(
                new PacketPlayOutPlayerInfo(
                        EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[] {handle}));
        handle.playerConnection.sendPacket(
                new PacketPlayOutRespawn(
                        handle.getWorld().worldProvider.getDimensionManager().getDimensionID(),
                        handle.getWorld().worldData.getDifficulty(),
                        handle.getWorld().worldData.getType(),
                        EnumGamemode.valueOf(player.getGameMode().name())));
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
                new PacketPlayOutPlayerListHeaderFooter();
        try {
            Field declaredField =
                    packetPlayOutPlayerListHeaderFooter.getClass().getDeclaredField("a");
            Field declaredField2 =
                    packetPlayOutPlayerListHeaderFooter.getClass().getDeclaredField("b");
            declaredField.setAccessible(true);
            declaredField2.setAccessible(true);
            declaredField.set(
                    packetPlayOutPlayerListHeaderFooter,
                    ChatSerializer.a("{\"text\":\"" + text + "\"}"));
            declaredField2.set(
                    packetPlayOutPlayerListHeaderFooter,
                    ChatSerializer.a("{\"text\":\"" + text2 + "\"}"));
        } catch (IllegalAccessException | NoSuchFieldException exception) {
            Phoenix.getInstance()
                    .getLogger()
                    .log(Level.SEVERE, ExceptionUtil.getStackTrace(exception));
        }
        handle.playerConnection.sendPacket(packetPlayOutPlayerListHeaderFooter);
    }

    @Override
    public ISkin getPlayerSkin(Player player) {
        String text = "";
        String text2 = "";
        Iterator<com.mojang.authlib.properties.Property> iterator2 =
                this.getEntity(player).getProfile().getProperties().get("textures").iterator();
        while (iterator2.hasNext()) {
            Property property;
            text = (property = (Property) iterator2.next()).getValue();
            text2 = property.getSignature();
        }
        return new Skin(player.getName(), text, text2);
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
        try {
            Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, gameProfile);
        } catch (ReflectiveOperationException exception) {
            throw ExceptionUtil.propagate(exception);
        }
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }

    @Override
    public void sitPlayer(Player player) {
        if (!this.creeperIds.containsKey(player.getUniqueId())) {
            player.setAllowFlight(true);
            Location location = player.getLocation();
            EntityPlayer entity = this.getEntity(player);
            EntityCreeper entityCreeper;
            (entityCreeper = new EntityCreeper(entity.getWorld()))
                    .setLocation(
                            location.getX(),
                            location.getY(),
                            location.getX(),
                            location.getYaw(),
                            location.getPitch());
            entityCreeper.setInvisible(true);
            entityCreeper.setCustomNameVisible(false);
            this.creeperIds.put(player.getUniqueId(), entityCreeper.getId());
            PacketPlayOutSpawnEntityLiving packetPlayOutSpawnEntityLiving =
                    new PacketPlayOutSpawnEntityLiving(entityCreeper);
            this.sendPacket(player, packetPlayOutSpawnEntityLiving);
            PacketPlayOutAttachEntity packetPlayOutAttachEntity =
                    new PacketPlayOutAttachEntity(entity, entityCreeper);
            this.sendPacket(player, packetPlayOutAttachEntity);
        }
    }

    @Override
    public void unsitPlayer(Player player) {
        if (this.creeperIds.containsKey(player.getUniqueId())) {
            player.setAllowFlight(false);
            int value = this.creeperIds.get(player.getUniqueId());
            PacketPlayOutEntityDestroy packetPlayOutEntityDestroy =
                    new PacketPlayOutEntityDestroy(new int[] {value});
            this.sendPacket(player, packetPlayOutEntityDestroy);
            this.creeperIds.remove(player.getUniqueId());
        }
    }

    @Override
    public void removePlayerFromTeam(String text, Team team) {
        CraftScoreboard craftScoreboard = (CraftScoreboard) team.getScoreboard();
        try {
            Field field;
            (field = Scoreboard.class.getDeclaredField("teamsByPlayer")).setAccessible(true);
            ((Map) field.get(craftScoreboard.getHandle())).remove(text);
            Field field2;
            (field2 =
                            Class.forName("org.bukkit.craftbukkit.v1_12_R1.scoreboard.CraftTeam")
                                    .getDeclaredField("team"))
                    .setAccessible(true);
            ((ScoreboardTeam) field2.get(team)).getPlayerNameSet().remove(text);
        } catch (Exception exception) {
        }
    }

    private void sendPacket(Player player, Packet packet) {
        this.getEntity(player).playerConnection.sendPacket(packet);
    }

    public EntityPlayer getEntity(Player player) {
        return ((CraftPlayer) player).getHandle();
    }
}
