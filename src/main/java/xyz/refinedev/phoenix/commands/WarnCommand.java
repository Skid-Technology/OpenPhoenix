package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.command.Usage;
import xyz.refinedev.lib.commands.annotation.parameter.Flag;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.Punishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.utils.PunishmentUtil;

import java.util.concurrent.atomic.AtomicBoolean;

@Register(name = "warn")
@Requires("core.command.warn")
public class WarnCommand {

    private final CommonPlatform platform;

    private static final long time = -5L;

    @Usage("<player> [-s | -p] <reason> [-s | -p]")
    @Command(name = "", async = true)
    public void execute(
            @Sender CommandSender sender,
            @Option({"disguise", "create", "multiple"}) IProfile profile,
            @Optional @Named("reason") @Text String text,
            @Flag({"s", "silent"}) boolean matches,
            @Flag({"p", "public"}) boolean matches2) {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        if (PunishmentUtil.canPunish(sender, profile, atomicBoolean)) {
            this.platform.getModUtilsHook().setFrozen(profile.getUniqueId(), false);
            String text2 =
                    PunishmentUtil.sanitizeReason(
                            text, MessageConfig.PUNISHMENT.WARN.DEFAULT_REASON);
            long duration = BukkitAPI.getDuration(SettingsConfig.SERVER.PUNISHMENTS.WARN_EXPIRE);
            Punishment punishment;
            (punishment = new Punishment()).setPunishmentType(PunishmentType.WARN);
            punishment.setTarget(profile.getUniqueId());
            punishment.setAddress(profile.getCurrentAddress());
            punishment.setSilent(!matches2 && (atomicBoolean.get() || matches));
            punishment.setPermanent(duration == time);
            punishment.setDuration(duration);
            punishment.setIssuedOn(GlobalConfig.SERVER.NAME);
            punishment.setIssuedAt(System.currentTimeMillis());
            punishment.setReason(text2);
            punishment.setIssuedBy(
                    sender instanceof Player
                            ? ((Player) sender).getUniqueId()
                            : IProfile.getConsoleUuid());
            PunishmentUtil.issue(sender, punishment);
        }
    }

    public WarnCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
