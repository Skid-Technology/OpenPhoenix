package xyz.refinedev.phoenix.nametag.packet;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.PacketEvents;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import xyz.refinedev.lib.com.github.retrooper.packetevents.util.Vector3d;
import xyz.refinedev.lib.com.github.retrooper.packetevents.util.Vector3f;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.PacketWrapper;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import xyz.refinedev.lib.kyori.adventure.text.Component;
import xyz.refinedev.phoenix.internal.CachedSettings;

import java.util.ArrayList;
import java.util.UUID;

public final class NameTagTeam {

    private final NameTagPackets nameTagPackets;

    public void applyPlayer(Player player, Player player2, int value) {
        Location location = player2.getLocation();
        xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.world.Location location2 =
                new xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.world.Location(
                        location.getX(),
                        location.getY() + player2.getEyeHeight(),
                        location.getZ(),
                        location.getYaw(),
                        0.0F);
        applyPlayer(
                player,
                new WrapperPlayServerSpawnEntity(
                        value,
                        UUID.randomUUID(),
                        EntityTypes.TEXT_DISPLAY,
                        location2,
                        0.0F,
                        0,
                        new Vector3d(0.0, 0.0, 0.0)));
    }

    public void applyPlayer(
            Player player,
            int value,
            Component component,
            float value2,
            float value3,
            CachedSettings cachedSettings,
            byte value4) {
        ArrayList<EntityData<?>> values;
        (values = new ArrayList())
                .add(
                        new EntityData(
                                this.nameTagPackets.getCount(),
                                EntityDataTypes.VECTOR3F,
                                new Vector3f(0.0F, value2, 0.0F)));
        values.add(
                new EntityData(
                        this.nameTagPackets.getCountByCount(),
                        EntityDataTypes.VECTOR3F,
                        new Vector3f(value3, value3, value3)));
        values.add(
                new EntityData(
                        this.nameTagPackets.getCount2(),
                        EntityDataTypes.BYTE,
                        cachedSettings.getByteValue8()));
        values.add(
                new EntityData(
                        this.nameTagPackets.getCount3(),
                        EntityDataTypes.FLOAT,
                        cachedSettings.getFloatValue()));
        values.add(
                new EntityData(
                        this.nameTagPackets.getCount4(), EntityDataTypes.ADV_COMPONENT, component));
        values.add(
                new EntityData(
                        this.nameTagPackets.getCount5(),
                        EntityDataTypes.INT,
                        cachedSettings.getCountCount()));
        values.add(new EntityData(this.nameTagPackets.getCount6(), EntityDataTypes.BYTE, value4));
        values.add(
                new EntityData(
                        this.nameTagPackets.getCount7(),
                        EntityDataTypes.BYTE,
                        cachedSettings.getByte()));
        applyPlayer(player, new WrapperPlayServerEntityMetadata(value, values));
    }

    public void applyPlayer(Player player, int value, Component component) {
        ArrayList values;
        (values = new ArrayList())
                .add(
                        new EntityData(
                                this.nameTagPackets.getCount4(),
                                EntityDataTypes.ADV_COMPONENT,
                                component));
        applyPlayer(player, new WrapperPlayServerEntityMetadata(value, values));
    }

    public void applyPlayer(Player player, int value, byte value2) {
        ArrayList values;
        (values = new ArrayList())
                .add(new EntityData(this.nameTagPackets.getCount6(), EntityDataTypes.BYTE, value2));
        applyPlayer(player, new WrapperPlayServerEntityMetadata(value, values));
    }

    public void applyPlayer(Player player, int value, float value2) {
        ArrayList values;
        (values = new ArrayList())
                .add(
                        new EntityData(
                                this.nameTagPackets.getCount(),
                                EntityDataTypes.VECTOR3F,
                                new Vector3f(0.0F, value2, 0.0F)));
        applyPlayer(player, new WrapperPlayServerEntityMetadata(value, values));
    }

    public void applyPlayer(Player player, Player player2, int[] values) {
        applyPlayer(player, new WrapperPlayServerSetPassengers(player2.getEntityId(), values));
    }

    public void applyPlayer(Player player, int[] values) {
        if (values.length != 0) {
            applyPlayer(player, new WrapperPlayServerDestroyEntities(values));
        }
    }

    private static void applyPlayer(Player player, PacketWrapper<?> packetWrapper) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packetWrapper);
    }

    public NameTagTeam(NameTagPackets nameTagPackets2) {
        this.nameTagPackets = nameTagPackets2;
    }
}
