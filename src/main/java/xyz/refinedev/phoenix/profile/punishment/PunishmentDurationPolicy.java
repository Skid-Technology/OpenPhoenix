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
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.commands.api.annotations.DurationInput;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.Pair;
import xyz.refinedev.phoenix.utils.PunishmentUtil;

import java.util.concurrent.atomic.AtomicBoolean;

public class PunishmentDurationPolicy {

    private final CommonPlatform platform;

    private final PunishmentType punishmentType;

    private final String text;

    private final boolean enabled;

    private final boolean enabledEnabled;

    @Usage("<player> [-s | -p] <duration> [-s | -p] <reason> [-s | -p]")
    @Command(name = "", async = true)
    public synchronized void execute(
            @Sender CommandSender sender,
            @Option({"disguise", "create", "multiple"}) IProfile profile,
            @Named("duration") @DurationInput String text2,
            @Optional @Named("reason") @Text String text3,
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
            Pair pair;
            long value = (Long) (pair = PunishmentUtil.sanitizeDuration(text2, text3)).getSecond();
            text3 = (String) pair.getFirst();
            if (value == -3L) {
                throw new ExitMessage(
                        LocaleUtil.tr(sender, MessageConfig.PUNISHMENT.INVALID_DURATION));
            }
            long value2;
            if (sender.hasPermission("core.punish.duration.bypass")
                    || (value2 =
                                    BukkitAPI.getDuration(
                                            SettingsConfig.SERVER.PUNISHMENTS.MAX_TEMP_DURATION))
                            == -5L
                    || value != -5L && value <= value2) {
                this.platform.getModUtilsHook().setFrozen(profile.getUniqueId(), false);
                String text4 = PunishmentUtil.sanitizeReason(text3, this.text);
                Punishment punishment;
                (punishment = new Punishment()).setPunishmentType(this.punishmentType);
                punishment.setTarget(profile.getUniqueId());
                punishment.setAddress(profile.getCurrentAddress());
                punishment.setSilent(!matches2 && (atomicBoolean.get() || matches));
                punishment.setPermanent(value == -5L);
                punishment.setDuration(value);
                punishment.setIp(this.enabled);
                punishment.setShadow(this.enabledEnabled);
                punishment.setIssuedOn(GlobalConfig.SERVER.NAME);
                punishment.setIssuedAt(System.currentTimeMillis());
                punishment.setReason(text4);
                punishment.setIssuedBy(
                        sender instanceof Player
                                ? ((Player) sender).getUniqueId()
                                : IProfile.getConsoleUuid());
                PunishmentUtil.issue(sender, punishment);
            } else {
                throw new ExitMessage(
                        LocaleUtil.tr(
                                sender, MessageConfig.PUNISHMENT.PUNISHMENT_DURATION_TOO_LARGE));
            }
        }
    }

    public PunishmentDurationPolicy(
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
