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
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PunishmentUtil;

import java.util.concurrent.atomic.AtomicBoolean;

public class PunishmentExecution {

    private final CommonPlatform platform;

    private final PunishmentType punishmentType;

    private final String text;

    private final boolean enabled;

    private final boolean enabledEnabled;

    private static final long time = -5L;

    @Usage("<player> [-s | -p] <reason> [-s | -p]")
    @Command(name = "", async = true)
    public synchronized void execute(
            @Sender CommandSender sender,
            @Option({"disguise", "create", "multiple"}) IProfile profile,
            @Optional @Named("reason") @Text String text2,
            @Flag({"s", "silent"}) boolean matches,
            @Flag({"p", "public"}) boolean matches2) {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        if (PunishmentUtil.canPunish(sender, profile, atomicBoolean)) {
            if (profile.getActivePunishment(this.punishmentType) != null) {
                throw new ExitMessage(
                        LocaleUtil.tr(sender, MessageConfig.PUNISHMENT.ALREADY_PUNISHED)
                                .replace("<player>", profile.getNameWithColorNoDisguise())
                                .replace("<type>", this.punishmentType.getPunished()));
            }
            this.platform.getModUtilsHook().setFrozen(profile.getUniqueId(), false);
            String text3 = PunishmentUtil.sanitizeReason(text2, this.text);
            Punishment punishment;
            (punishment = new Punishment()).setPunishmentType(this.punishmentType);
            punishment.setTarget(profile.getUniqueId());
            punishment.setAddress(profile.getCurrentAddress());
            punishment.setSilent(!matches2 && (atomicBoolean.get() || matches));
            punishment.setPermanent(true);
            punishment.setDuration(time);
            punishment.setIp(this.enabled);
            punishment.setShadow(this.enabledEnabled);
            punishment.setIssuedOn(GlobalConfig.SERVER.NAME);
            punishment.setIssuedAt(System.currentTimeMillis());
            punishment.setReason(text3);
            punishment.setIssuedBy(
                    sender instanceof Player
                            ? ((Player) sender).getUniqueId()
                            : IProfile.getConsoleUuid());
            PunishmentUtil.issue(sender, punishment);
        }
    }

    public PunishmentExecution(
            CommonPlatform commonPlatform,
            PunishmentType punishmentType2,
            String text,
            boolean enabled,
            boolean enabledEnabled) {
        this.platform = commonPlatform;
        this.punishmentType = punishmentType2;
        this.text = text;
        this.enabled = enabled;
        this.enabledEnabled = enabledEnabled;
    }
}
