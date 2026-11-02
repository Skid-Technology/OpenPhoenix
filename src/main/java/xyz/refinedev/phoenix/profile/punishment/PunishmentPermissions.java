package xyz.refinedev.phoenix.profile.punishment;

import org.bukkit.command.ConsoleCommandSender;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.packet.impl.punishments.BukkitUnPunishPacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

public class PunishmentPermissions {

    private final CommonPlatform platform;

    private final PunishmentType punishmentType;

    private static final String text = "<type>";

    @Command(name = "")
    public void execute(
            @Sender ConsoleCommandSender consoleCommandSender,
            @Named("void reason") VoidReason voidReason,
            @Optional @Named("note") @Text String text2) {
        CompletableFuture<java.util.List<xyz.refinedev.phoenix.profile.punishment.IPunishment>>
                allPunishments = this.platform.getPunishmentHandler().getAllPunishments();
        allPunishments.whenCompleteAsync(
                (result, result2) -> {
                    Iterator iterator2 = result.iterator();
                    while (iterator2.hasNext()) {
                        IPunishment punishment;
                        if ((punishment = (IPunishment) iterator2.next()).getRemovedBy() == null
                                && punishment.getPunishmentType().equals(this.punishmentType)) {
                            punishment.voidAs(IProfile.getConsoleUuid(), voidReason, text2);
                            CompletableFuture<Boolean> completableFuture =
                                    this.platform
                                            .getPunishmentHandler()
                                            .getRepository()
                                            .save(punishment.getUuid(), punishment);
                            IPunishment punishment2 = punishment;
                            completableFuture.whenCompleteAsync(
                                    (result3, result4) ->
                                            this.platform
                                                    .getPacketHandler()
                                                    .sendSync(
                                                            new BukkitUnPunishPacket(punishment2)));
                        }
                    }
                    consoleCommandSender.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(
                                                    consoleCommandSender,
                                                    MessageConfig.PUNISHMENT.VOIDED_ALL)
                                            .replace(text, this.punishmentType.getName())));
                });
    }

    public PunishmentPermissions(CommonPlatform commonPlatform, PunishmentType punishmentType2) {
        this.platform = commonPlatform;
        this.punishmentType = punishmentType2;
    }
}
