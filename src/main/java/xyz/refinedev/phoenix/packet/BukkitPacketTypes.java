package xyz.refinedev.phoenix.packet;

import xyz.refinedev.phoenix.packet.api.PacketTypes;
import xyz.refinedev.phoenix.packet.impl.profile.BukkitDisguiseNameUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.profile.BukkitGrantMessagePacket;
import xyz.refinedev.phoenix.packet.impl.profile.BukkitGrantsUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.profile.BukkitSecurityExecutePunishmentPacket;
import xyz.refinedev.phoenix.packet.impl.profile.DisguiseNameUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.profile.GrantMessagePacket;
import xyz.refinedev.phoenix.packet.impl.profile.GrantsUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.profile.SecurityExecutePunishmentPacket;
import xyz.refinedev.phoenix.packet.impl.punishments.BanWaveReleasedPacket;
import xyz.refinedev.phoenix.packet.impl.punishments.BukkitBanWaveReleasedPacket;
import xyz.refinedev.phoenix.packet.impl.punishments.BukkitPunishmentPacket;
import xyz.refinedev.phoenix.packet.impl.punishments.BukkitUnPunishPacket;
import xyz.refinedev.phoenix.packet.impl.punishments.PunishmentPacket;
import xyz.refinedev.phoenix.packet.impl.punishments.UnPunishPacket;
import xyz.refinedev.phoenix.packet.impl.security.BukkitSecurityAlertPacket;
import xyz.refinedev.phoenix.packet.impl.security.SecurityAlertPacket;
import xyz.refinedev.phoenix.packet.impl.server.BukkitServerStatusPacket;
import xyz.refinedev.phoenix.packet.impl.server.ServerStatusPacket;

public final class BukkitPacketTypes {

    private BukkitPacketTypes() {}

    public static void registerAll() {
        PacketTypes.override(DisguiseNameUpdatePacket.class, BukkitDisguiseNameUpdatePacket.class);
        PacketTypes.override(GrantMessagePacket.class, BukkitGrantMessagePacket.class);
        PacketTypes.override(GrantsUpdatePacket.class, BukkitGrantsUpdatePacket.class);
        PacketTypes.override(
                SecurityExecutePunishmentPacket.class, BukkitSecurityExecutePunishmentPacket.class);
        PacketTypes.override(SecurityAlertPacket.class, BukkitSecurityAlertPacket.class);
        PacketTypes.override(PunishmentPacket.class, BukkitPunishmentPacket.class);
        PacketTypes.override(BanWaveReleasedPacket.class, BukkitBanWaveReleasedPacket.class);
        PacketTypes.override(UnPunishPacket.class, BukkitUnPunishPacket.class);
        PacketTypes.override(ServerStatusPacket.class, BukkitServerStatusPacket.class);
    }
}
