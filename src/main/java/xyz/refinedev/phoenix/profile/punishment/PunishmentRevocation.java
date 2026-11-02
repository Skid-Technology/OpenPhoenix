package xyz.refinedev.phoenix.profile.punishment;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Usage;
import xyz.refinedev.lib.commands.annotation.parameter.Flag;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PunishmentUtil;

import java.util.concurrent.atomic.AtomicBoolean;

public class PunishmentRevocation {

    private final CommonPlatform platform;

    private final PunishmentType punishmentType;

    private final String text;

    public PunishmentRevocation(
            CommonPlatform commonPlatform, PunishmentType punishmentType2, String text) {
        if (!punishmentType2.isRevokable()) {
            throw new IllegalArgumentException(
                    punishmentType2 + " cannot be lifted, so it has no unpunish command");
        }
        this.platform = commonPlatform;
        this.punishmentType = punishmentType2;
        this.text = text;
    }

    @Usage("<player> [-s | -p] <reason> [-s | -p]")
    @Command(name = "", async = true)
    public synchronized void execute(
            @Sender CommandSender sender,
            @Option({"disguise", "multiple"}) IProfile profile,
            @Optional @Named("reason") @Text String text2,
            @Flag({"s", "silent"}) boolean matches,
            @Flag({"p", "public"}) boolean matches2) {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        if (PunishmentUtil.canPunish(sender, profile, atomicBoolean)) {
            IPunishment punishment;
            if ((punishment = profile.getActivePunishment(this.punishmentType)) == null) {
                throw new ExitMessage(
                        LocaleUtil.tr(sender, MessageConfig.PUNISHMENT.NOT_PUNISHED)
                                .replace("<player>", profile.getNameWithColorNoDisguise())
                                .replace("<type>", this.punishmentType.getPunished()));
            }
            this.platform.getModUtilsHook().setFrozen(profile.getUniqueId(), false);
            String text3 = PunishmentUtil.sanitizeReason(text2, this.text);
            punishment.setRemovedSilent(!matches2 && (atomicBoolean.get() || matches));
            punishment.remove(
                    sender instanceof Player
                            ? ((Player) sender).getUniqueId()
                            : IProfile.getConsoleUuid(),
                    text3);
            this.platform
                    .getPunishmentHandler()
                    .getRepository()
                    .saveSync(punishment.getUuid(), punishment);
            PunishmentUtil.executeRevokePunishment(sender, punishment);
        }
    }
}
