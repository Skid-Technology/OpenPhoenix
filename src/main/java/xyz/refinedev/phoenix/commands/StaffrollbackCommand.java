package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.packet.impl.punishments.BukkitUnPunishPacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.profile.punishment.VoidReason;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.Duration;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Register(name = "staffrollback")
@Requires("core.command.staffrollback")
public class StaffrollbackCommand {

    private final CommonPlatform platform;

    @Command(name = "")
    public synchronized void execute(
            @Sender Player player,
            @Option("multiple") IProfile profile,
            @Named("time") Duration duration,
            PunishmentType punishmentType,
            @Optional @Named("void reason") VoidReason voidReason) {
        VoidReason voidReason2 = voidReason == null ? VoidReason.COMPROMISED_ACCOUNT : voidReason;
        long value =
                duration.isEnabled() ? 0L : System.currentTimeMillis() - duration.getTimeTime();
        AtomicInteger atomicInteger = new AtomicInteger(0);
        AtomicInteger atomicInteger2 = new AtomicInteger(0);
        player.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player, MessageConfig.STAFF.ROLLBACK.WIPING)
                                .replace("<type>", punishmentType.getName())));
        CompletableFuture<java.util.List<IPunishment>> completableFuture =
                this.platform.getPunishmentHandler().findByIssuedBy(profile.getUuid());
        completableFuture.whenCompleteAsync(
                (result, result2) -> {
                    for (IPunishment punishment : (Iterable<IPunishment>) ((Iterable) result)) {
                        if (value <= punishment.getIssuedAt()
                                && punishment.getPunishmentType().equals(punishmentType)) {
                            if (punishment.isActive()) {
                                atomicInteger2.getAndIncrement();
                            } else {
                                atomicInteger.getAndIncrement();
                            }
                            if (!punishment.isVoided()) {
                                punishment.voidAs(player.getUniqueId(), voidReason2, "Rollback");
                                CompletableFuture<Boolean> completableFuture2 =
                                        this.platform
                                                .getPunishmentHandler()
                                                .getRepository()
                                                .save(punishment.getUuid(), punishment);
                                completableFuture2.whenCompleteAsync(
                                        (result3, result4) ->
                                                this.platform
                                                        .getPacketHandler()
                                                        .sendSync(
                                                                new BukkitUnPunishPacket(
                                                                        punishment)));
                            }
                        }
                    }
                    if (atomicInteger.get() + atomicInteger2.get() == 0) {
                        player.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(
                                                        player,
                                                        MessageConfig.STAFF.ROLLBACK.NO_HISTORY)
                                                .replace("<type>", punishmentType.getName())
                                                .replace(
                                                        "<name>",
                                                        profile.getNameWithColorNoDisguise())));
                    } else {
                        player.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(player, MessageConfig.STAFF.ROLLBACK.WIPED)
                                                .replace(
                                                        "<amount>",
                                                        String.valueOf(
                                                                atomicInteger.get()
                                                                        + atomicInteger2.get()))
                                                .replace("<type>", punishmentType.getName())
                                                .replace(
                                                        "<name>",
                                                        profile.getNameWithColorNoDisguise())
                                                .replace(
                                                        "<active>",
                                                        String.valueOf(atomicInteger2.get()))
                                                .replace(
                                                        "<inactive>",
                                                        String.valueOf(atomicInteger.get()))));
                    }
                });
    }

    public StaffrollbackCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
