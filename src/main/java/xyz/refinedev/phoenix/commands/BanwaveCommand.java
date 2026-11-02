package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Flag;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.ProofType;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.profile.punishment.VoidReason;
import xyz.refinedev.phoenix.profile.punishment.banwave.BanWave;
import xyz.refinedev.phoenix.profile.punishment.banwave.BanWaveEntry;
import xyz.refinedev.phoenix.profile.punishment.banwave.IBanWave;
import xyz.refinedev.phoenix.profile.punishment.banwave.IBanWaveEntry;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.ConfirmationMenus;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.Duration;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PunishmentUtil;
import xyz.refinedev.phoenix.utils.chat.ConversationUtil;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Register(
        name = "banwave",
        aliases = {"wave", "punishwave"})
@Requires("core.command.banwave")
@Help
public class BanwaveCommand {

    private final Phoenix plugin;

    @Command(name = "create", description = "Start a new wave", async = true)
    @Requires("core.command.banwave.create")
    public void create(@Sender CommandSender sender, @Named("name") String text) {
        this.applyCommandSender(sender);
        if (this.plugin.getBanWaveHandler().byName(text) != null) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.BAN_WAVE.ALREADY_EXISTS)
                            .replace("<wave>", text));
        }
        BanWave banWave =
                new BanWave(
                        text,
                        sender instanceof Player
                                ? ((Player) sender).getUniqueId()
                                : IProfile.getConsoleUuid(),
                        System.currentTimeMillis());
        this.plugin.getBanWaveHandler().save(banWave);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.BAN_WAVE.CREATED)
                                .replace("<wave>", text)));
    }

    @Command(name = "add", description = "Stage an account in a wave", async = true)
    @Requires("core.command.banwave.add")
    public void add(
            @Sender CommandSender sender,
            @Named("wave") String text,
            @Option({"disguise", "multiple"}) IProfile profile,
            @Named("type") PunishmentType punishmentType,
            @Named("duration") Duration duration,
            @Named("reason") @Text String text2,
            @Flag("ip") boolean matches,
            @Flag("shadow") boolean matches2) {
        this.applyCommandSender(sender);
        IBanWave banWave;
        if ((banWave = this.findBanWave(sender, text)).holds(profile.getUniqueId())) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.BAN_WAVE.ALREADY_STAGED)
                            .replace("<player>", profile.getNameWithColorNoDisguise()));
        }
        if (PunishmentUtil.canPunish(sender, profile, new AtomicBoolean(false))) {
            if (sender instanceof Player
                    && !sender.hasPermission(
                            "core.punish." + punishmentType.name().toLowerCase())) {
                throw new ExitMessage(
                        LocaleUtil.tr(sender, MessageConfig.BAN_WAVE.NO_PERMISSION)
                                .replace("<type>", punishmentType.getNiceName()));
            }
            long timeTime = duration.getTimeTime();
            BanWaveEntry banWaveEntry;
            (banWaveEntry =
                            new BanWaveEntry(
                                    profile.getUniqueId(),
                                    profile.getName(),
                                    punishmentType,
                                    timeTime,
                                    text2,
                                    sender instanceof Player
                                            ? ((Player) sender).getUniqueId()
                                            : IProfile.getConsoleUuid(),
                                    System.currentTimeMillis()))
                    .setIp(matches);
            banWaveEntry.setShadow(matches2);
            if (!matches
                    || profile.getCurrentAddress() != null
                            && !profile.getCurrentAddress().isEmpty()) {
                if (this.matchesEnabled(sender, punishmentType)) {
                    Player player = (Player) sender;
                    IBanWave banWave2 = banWave;
                    BanWaveEntry banWaveEntry2 = banWaveEntry;
                    Tasks.run(
                            () -> {
                                Phoenix phoenix = this.plugin;
                                String text3 =
                                        LocaleUtil.tr(player, MessageConfig.PUNISHMENT.PROOF_PROMPT)
                                                .replace("<type>", punishmentType.getNiceName())
                                                .replace(
                                                        "<player>",
                                                        profile.getNameWithColorNoDisguise());
                                ConversationUtil.ask(
                                        phoenix,
                                        player,
                                        text3,
                                        result -> {
                                            if (result != null && !result.trim().isEmpty()) {
                                                banWaveEntry2
                                                        .getProof()
                                                        .add(
                                                                this.plugin
                                                                        .getPunishmentHandler()
                                                                        .createProof(
                                                                                ProofType.LINK,
                                                                                result.trim(),
                                                                                player
                                                                                        .getUniqueId()));
                                                Tasks.runAsync(
                                                        this.plugin,
                                                        () ->
                                                                this.sendMessage(
                                                                        player,
                                                                        banWave2,
                                                                        banWaveEntry2,
                                                                        profile));
                                            } else {
                                                player.sendMessage(
                                                        Color.translate(
                                                                LocaleUtil.tr(
                                                                        player,
                                                                        MessageConfig.PUNISHMENT
                                                                                .PROOF_CANCELLED)));
                                            }
                                        });
                            });
                } else {
                    this.sendMessage(sender, banWave, banWaveEntry, profile);
                }
            } else {
                throw new ExitMessage(
                        LocaleUtil.tr(sender, MessageConfig.BAN_WAVE.NO_ADDRESS)
                                .replace("<player>", profile.getNameWithColorNoDisguise()));
            }
        }
    }

    private void sendMessage(
            CommandSender sender, IBanWave banWave, IBanWaveEntry banWaveEntry, IProfile profile) {
        banWave.getEntries().add(banWaveEntry);
        this.plugin.getBanWaveHandler().save(banWave);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.BAN_WAVE.STAGED)
                                .replace("<player>", profile.getNameWithColorNoDisguise())
                                .replace("<wave>", banWave.getName())
                                .replace("<amount>", String.valueOf(banWave.getEntries().size()))));
    }

    private boolean matchesEnabled(CommandSender sender, PunishmentType punishmentType) {
        if (!SettingsConfig.SERVER.PUNISHMENTS.REQUIRE_PROOF.ENABLED) {
            return false;
        }
        if (!(sender instanceof Player)) {
            return false;
        }
        for (String text : SettingsConfig.SERVER.PUNISHMENTS.REQUIRE_PROOF.TYPES) {
            if (punishmentType.name().equalsIgnoreCase(text.trim())) {
                return true;
            }
        }
        return false;
    }

    @Command(name = "remove", description = "Take an account back out of a wave", async = true)
    @Requires("core.command.banwave.remove")
    public void remove(
            @Sender CommandSender sender,
            @Named("wave") String text,
            @Option({"disguise", "multiple"}) IProfile profile) {
        this.applyCommandSender(sender);
        IBanWave banWave;
        List<IBanWaveEntry> entries = (banWave = this.findBanWave(sender, text)).getEntries();
        if (!entries.removeIf(result -> result.getTarget().equals(profile.getUniqueId()))) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.BAN_WAVE.NOT_STAGED)
                            .replace("<player>", profile.getNameWithColorNoDisguise()));
        }
        this.plugin.getBanWaveHandler().save(banWave);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.BAN_WAVE.UNSTAGED)
                                .replace("<player>", profile.getNameWithColorNoDisguise())
                                .replace("<wave>", banWave.getName())));
    }

    @Command(name = "list", description = "The waves still waiting", async = true)
    @Requires("core.command.banwave.list")
    public void list(@Sender CommandSender sender) {
        this.applyCommandSender(sender);
        if (this.plugin.getBanWaveHandler().pending().isEmpty()) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.BAN_WAVE.NONE));
        }
        for (String text : LocaleUtil.tr(sender, MessageConfig.BAN_WAVE.LIST_HEADER)) {
            sender.sendMessage(Color.translate(text));
        }
        for (IBanWave banWave : this.plugin.getBanWaveHandler().pending()) {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.BAN_WAVE.LIST_ENTRY)
                                    .replace("<wave>", banWave.getName())
                                    .replace(
                                            "<amount>", String.valueOf(banWave.getEntries().size()))
                                    .replace("<when>", this.formatString(sender, banWave))));
        }
    }

    @Command(name = "view", description = "What is staged in a wave", async = true)
    @Requires("core.command.banwave.view")
    public void view(@Sender CommandSender sender, @Named("wave") String text) {
        this.applyCommandSender(sender);
        IBanWave banWave = this.findBanWave(sender, text);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.BAN_WAVE.VIEW_HEADER)
                                .replace("<wave>", banWave.getName())
                                .replace("<amount>", String.valueOf(banWave.getEntries().size()))
                                .replace("<when>", this.formatString(sender, banWave))));
        for (IBanWaveEntry banWaveEntry : banWave.getEntries()) {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.BAN_WAVE.VIEW_ENTRY)
                                    .replace("<player>", banWaveEntry.getTargetName())
                                    .replace(
                                            "<type>",
                                            (banWaveEntry.isIp()
                                                            ? LocaleUtil.tr(
                                                                    sender,
                                                                    MessageConfig.BAN_WAVE
                                                                            .IP_PREFIX)
                                                            : "")
                                                    + banWaveEntry.getType().getNiceName())
                                    .replace(
                                            "<time>",
                                            banWaveEntry.isPermanent()
                                                    ? LocaleUtil.tr(
                                                            sender,
                                                            MessageConfig.BAN_WAVE.PERMANENT)
                                                    : DateUtils.formatTimeMillis(
                                                            banWaveEntry.getDuration()))
                                    .replace("<reason>", banWaveEntry.getReason())));
        }
    }

    @Command(name = "schedule", description = "Set the day and time a wave goes out", async = true)
    @Requires("core.command.banwave.schedule")
    public void schedule(
            @Sender CommandSender sender,
            @Named("wave") String text,
            @Named("when (yyyy-MM-dd HH:mm, or a duration)") @Text String text2) {
        this.applyCommandSender(sender);
        IBanWave banWave = this.findBanWave(sender, text);
        long value;
        if ((value = this.calculateTime(text2)) <= 0L) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.BAN_WAVE.BAD_TIME));
        }
        if (value <= System.currentTimeMillis()) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.BAN_WAVE.TIME_PASSED));
        }
        banWave.setReleaseAt(value);
        this.plugin.getBanWaveHandler().save(banWave);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.BAN_WAVE.SCHEDULED)
                                .replace("<wave>", banWave.getName())
                                .replace("<when>", DateUtils.getDate(value))
                                .replace(
                                        "<remaining>",
                                        DateUtils.formatTimeMillis(
                                                value - System.currentTimeMillis()))));
    }

    @Command(name = "unschedule", description = "Stop a wave going out on its own", async = true)
    @Requires("core.command.banwave.unschedule")
    public void unschedule(@Sender CommandSender sender, @Named("wave") String text) {
        this.applyCommandSender(sender);
        IBanWave banWave;
        (banWave = this.findBanWave(sender, text)).setReleaseAt(0L);
        this.plugin.getBanWaveHandler().save(banWave);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.BAN_WAVE.UNSCHEDULED)
                                .replace("<wave>", banWave.getName())));
    }

    @Command(name = "release", description = "Send a wave out now")
    @Requires("core.command.banwave.release")
    public void release(@Sender Player player, @Named("wave") String text) {
        this.applyCommandSender(player);
        IBanWave banWave;
        if ((banWave = this.findBanWave(player, text)).getEntries().isEmpty()) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.BAN_WAVE.EMPTY)
                            .replace("<wave>", banWave.getName()));
        }
        Phoenix phoenix = this.plugin;
        String text2 =
                LocaleUtil.tr(player, MessageConfig.BAN_WAVE.CONFIRM)
                        .replace("<wave>", banWave.getName())
                        .replace("<amount>", String.valueOf(banWave.getEntries().size()));
        IBanWave banWave2 = banWave;
        ConfirmationMenus.applyPhoenix(
                phoenix,
                player,
                text2,
                true,
                result -> {
                    if (result) {
                        this.plugin.getBanWaveHandler().release(banWave2, player);
                    }
                });
    }

    @Command(
            name = "void",
            description = "Take back a wave that has already gone out",
            async = true)
    @Requires("core.command.banwave.void")
    public void voidWave(
            @Sender Player player,
            @Named("wave") String text,
            @Named("void reason") VoidReason voidReason) {
        this.applyCommandSender(player);
        IBanWave banWave;
        if (!(banWave = this.findBanWave(player, text)).isReleased()) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.BAN_WAVE.NOT_RELEASED)
                            .replace("<wave>", banWave.getName()));
        }
        if (banWave.getIssued().isEmpty()) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.BAN_WAVE.NOTHING_TO_VOID)
                            .replace("<wave>", banWave.getName()));
        }
        Phoenix phoenix = this.plugin;
        String text2 =
                LocaleUtil.tr(player, MessageConfig.BAN_WAVE.CONFIRM_VOID)
                        .replace("<wave>", banWave.getName())
                        .replace("<amount>", String.valueOf(banWave.getIssued().size()));
        IBanWave banWave2 = banWave;
        ConfirmationMenus.applyPhoenix(
                phoenix,
                player,
                text2,
                true,
                result -> {
                    if (result) {
                        Tasks.runAsync(
                                this.plugin,
                                () -> {
                                    int value =
                                            this.plugin
                                                    .getBanWaveHandler()
                                                    .voidWave(banWave2, player, voidReason);
                                    player.sendMessage(
                                            Color.translate(
                                                    LocaleUtil.tr(
                                                                    player,
                                                                    MessageConfig.BAN_WAVE.VOIDED)
                                                            .replace("<wave>", banWave2.getName())
                                                            .replace(
                                                                    "<amount>",
                                                                    String.valueOf(value))
                                                            .replace(
                                                                    "<void_reason>",
                                                                    voidReason.getNiceName())));
                                });
                    }
                });
    }

    @Command(name = "cancel", description = "Throw a wave away without releasing it", async = true)
    @Requires("core.command.banwave.cancel")
    public void cancel(@Sender CommandSender sender, @Named("wave") String text) {
        this.applyCommandSender(sender);
        IBanWave banWave = this.findBanWave(sender, text);
        this.plugin.getBanWaveHandler().delete(banWave);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.BAN_WAVE.CANCELLED)
                                .replace("<wave>", banWave.getName())));
    }

    private long calculateTime(String text) {
        String text2 = text.trim();
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return LocalDateTime.parse(text2, dateTimeFormatter)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();
        } catch (DateTimeParseException dateTimeParseException) {
            Duration duration;
            return (duration = Duration.of(text2)) != null && !duration.isEnabled()
                    ? System.currentTimeMillis() + duration.getTimeTime()
                    : 0L;
        }
    }

    private String formatString(CommandSender sender, IBanWave banWave) {
        return banWave.getReleaseAt() <= 0L
                ? LocaleUtil.tr(sender, MessageConfig.BAN_WAVE.MANUAL)
                : DateUtils.getDate(banWave.getReleaseAt());
    }

    private IBanWave findBanWave(CommandSender sender, String text) {
        IBanWave banWave;
        if ((banWave = this.plugin.getBanWaveHandler().byName(text)) != null
                && !banWave.isReleased()) {
            return banWave;
        } else {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.BAN_WAVE.DOESNT_EXIST)
                            .replace("<wave>", text));
        }
    }

    private void applyCommandSender(CommandSender sender) {
        if (!SettingsConfig.BAN_WAVES.ENABLED) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.BAN_WAVE.DISABLED));
        }
    }

    public BanwaveCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
