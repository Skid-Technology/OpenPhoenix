package xyz.refinedev.phoenix.handler;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.io.lettuce.core.SetArgs.Builder;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.packet.impl.punishments.BukkitBanWaveReleasedPacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.Punishment;
import xyz.refinedev.phoenix.profile.punishment.VoidReason;
import xyz.refinedev.phoenix.profile.punishment.banwave.BanWave;
import xyz.refinedev.phoenix.profile.punishment.banwave.BanwaveCommands;
import xyz.refinedev.phoenix.profile.punishment.banwave.IBanWave;
import xyz.refinedev.phoenix.profile.punishment.banwave.IBanWaveEntry;
import xyz.refinedev.phoenix.repository.IBanWaveRepository;
import xyz.refinedev.phoenix.repository.impl.MongoBanWaveRepository;
import xyz.refinedev.phoenix.repository.impl.MySQLBanWaveRepository;
import xyz.refinedev.phoenix.utils.PunishmentUtil;
import xyz.refinedev.phoenix.utils.general.Tasks;
import xyz.refinedev.phoenix.utils.repository.RepositoryFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class BanWaveHandler {

    private static final long CLAIM_SECONDS = 300L;

    private final Phoenix plugin;

    private final IBanWaveRepository repository;

    public BanWaveHandler(Phoenix phoenix) {
        this.plugin = phoenix;
        this.repository =
                (IBanWaveRepository)
                        new RepositoryFactory(phoenix)
                                .create(
                                        result ->
                                                new MongoBanWaveRepository(
                                                        result.getDatabase()
                                                                .getCollection("px-waves")),
                                        result -> new MySQLBanWaveRepository());
        this.repository
                .fetchAll()
                .thenAccept(
                        result2 ->
                                this.repository
                                        .getCache()
                                        .addAll((Collection<? extends IBanWave>) result2));
    }

    public IBanWave byName(String text) {
        Iterator<IBanWave> iterator2 = this.repository.getCache().iterator();
        while (iterator2.hasNext()) {
            IBanWave banWave;
            if ((banWave = (IBanWave) iterator2.next()).getName().equalsIgnoreCase(text)) {
                return banWave;
            }
        }
        return null;
    }

    public List<IBanWave> pending() {
        ArrayList<IBanWave> values = new ArrayList<>();
        Iterator<IBanWave> iterator2 = this.repository.getCache().iterator();
        while (iterator2.hasNext()) {
            IBanWave banWave;
            if (!(banWave = (IBanWave) iterator2.next()).isReleased()) {
                values.add(banWave);
            }
        }
        values.sort(Comparator.comparingLong(IBanWave::getCreatedAt));
        return values;
    }

    public void save(IBanWave banWave) {
        this.repository.addToCache(banWave);
        this.repository.save(banWave);
    }

    public void delete(IBanWave banWave) {
        this.repository.removeFromCache(banWave);
        this.repository.delete(banWave);
    }

    public void releaseDue() {
        if (SettingsConfig.BAN_WAVES.ENABLED) {
            Iterator<IBanWave> iterator2 = this.pending().iterator();
            while (iterator2.hasNext()) {
                IBanWave banWave;
                if ((banWave = (IBanWave) iterator2.next()).isDue() && this.claim(banWave)) {
                    this.release(banWave, Bukkit.getConsoleSender());
                }
            }
            this.openNext();
        }
    }

    public void openNext() {
        if (SettingsConfig.BAN_WAVES.ENABLED && SettingsConfig.BAN_WAVES.RECURRING.ENABLED) {
            Iterator<IBanWave> iterator2 = this.pending().iterator();
            while (iterator2.hasNext()) {
                if (((IBanWave) iterator2.next()).getReleaseAt() > 0L) {
                    return;
                }
            }
            long value;
            if ((value = BanwaveCommands.calculateTime(SettingsConfig.BAN_WAVES.RECURRING.EVERY))
                    <= 0L) {
                this.plugin
                        .getLogger()
                        .warning(
                                "Ban waves are set to recur on '"
                                        + SettingsConfig.BAN_WAVES.RECURRING.EVERY
                                        + "', which is neither a day and time like FRIDAY 18:00 nor a duration like 7d. No wave was opened.");
            } else {
                String text =
                        SettingsConfig.BAN_WAVES.RECURRING.NAME.replace(
                                "<date>",
                                new SimpleDateFormat("yyyy-MM-dd").format(new Date(value)));
                if (this.byName(text) == null) {
                    if (this.claim("wave:opening:" + text)) {
                        BanWave banWave;
                        (banWave =
                                        new BanWave(
                                                text,
                                                IProfile.getConsoleUuid(),
                                                System.currentTimeMillis()))
                                .setReleaseAt(value);
                        this.save(banWave);
                        this.plugin
                                .getLogger()
                                .info(
                                        "Opened the ban wave '"
                                                + text
                                                + "', going out "
                                                + new Date(value)
                                                + ".");
                    }
                }
            }
        }
    }

    private boolean claim(IBanWave banWave) {
        return this.claim("wave:releasing:" + banWave.getId());
    }

    private boolean claim(String text) {
        IDatabaseHandler databaseHandler = this.plugin.getDatabaseHandler();
        String text2 =
                databaseHandler.runRedisCommand(
                        result ->
                                result.set(text, GlobalConfig.SERVER.NAME, Builder.nx().ex(300L)));
        return "OK".equalsIgnoreCase(text2);
    }

    public void release(IBanWave banWave, CommandSender sender) {
        if (!banWave.isReleased()) {
            banWave.setReleased(true);
            banWave.setReleasedAt(System.currentTimeMillis());
            banWave.setReleasedBy(this.actorOf(sender));
            this.save(banWave);
            this.punish(banWave, sender, new ArrayList<>(banWave.getEntries()), 0, 0);
        }
    }

    private void punish(
            IBanWave banWave,
            CommandSender sender,
            List<IBanWaveEntry> values,
            int value,
            int value2) {
        int value3 = value;
        int value4 = value2;
        int value5 = Math.max(1, SettingsConfig.BAN_WAVES.BATCH_SIZE);
        for (int index = 0; index < value5 && !values.isEmpty(); index++) {
            IBanWaveEntry banWaveEntry = (IBanWaveEntry) values.remove(0);
            if (this.apply(banWave, sender, banWaveEntry)) {
                value3++;
            } else {
                value4++;
            }
        }
        if (!values.isEmpty()) {
            int value6 = value3;
            int value7 = value4;
            Tasks.runAsyncLater(
                    this.plugin, () -> this.punish(banWave, sender, values, value6, value7), 1L);
        } else {
            this.announce(banWave, value3, value4);
        }
    }

    private boolean apply(IBanWave banWave, CommandSender sender, IBanWaveEntry banWaveEntry) {
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getProfile(banWaveEntry.getTarget()))
                == null) {
            return false;
        }
        if (profile.hasActivePunishment(banWaveEntry.getType())) {
            return false;
        }
        if (!banWaveEntry.isIp()
                || profile.getCurrentAddress() != null && !profile.getCurrentAddress().isEmpty()) {
            if (this.retracted(profile, banWaveEntry.getStagedAt())) {
                return false;
            }
            if (!PunishmentUtil.canPunish(sender, profile, new AtomicBoolean(false))) {
                return false;
            }
            Punishment punishment;
            (punishment = new Punishment()).setPunishmentType(banWaveEntry.getType());
            punishment.setTarget(banWaveEntry.getTarget());
            punishment.setAddress(profile.getCurrentAddress());
            punishment.setPermanent(banWaveEntry.isPermanent());
            punishment.setDuration(banWaveEntry.getDuration());
            punishment.setIp(banWaveEntry.isIp());
            punishment.setShadow(banWaveEntry.isShadow());
            punishment.setIssuedOn(GlobalConfig.SERVER.NAME);
            punishment.setIssuedAt(System.currentTimeMillis());
            punishment.setReason(banWaveEntry.getReason());
            punishment.setIssuedBy(banWaveEntry.getStagedBy());
            punishment.getProof().addAll(banWaveEntry.getProof());
            punishment.setSilent(banWave.isSummarised());
            this.plugin
                    .getPunishmentHandler()
                    .getRepository()
                    .saveSync(punishment.getUuid(), punishment);
            PunishmentUtil.executePunishment(sender, punishment);
            banWave.getIssued().add(punishment.getUuid());
            this.save(banWave);
            return true;
        } else {
            return false;
        }
    }

    private boolean retracted(IProfile profile, long value) {
        Iterator<IPunishment> iterator2 = profile.getPunishments().iterator();
        while (iterator2.hasNext()) {
            IPunishment punishment;
            VoidReason voidReason;
            if ((punishment = (IPunishment) iterator2.next()).isVoided()
                    && punishment.getRemovedAt() >= value
                    && ((voidReason = punishment.getVoidReason()) == null
                            || voidReason.isStaffFault())) {
                return true;
            }
        }
        return false;
    }

    public int voidWave(IBanWave banWave, CommandSender sender, VoidReason voidReason) {
        UUID uUID = this.actorOf(sender);
        int value = 0;
        for (UUID uUID2 : (Iterable<UUID>) ((Iterable) new ArrayList<>(banWave.getIssued()))) {
            IPunishment punishment;
            if ((punishment =
                                    this.plugin
                                            .getPunishmentHandler()
                                            .getRepository()
                                            .fetchSync(uUID2)
                                            .orElse(null))
                            != null
                    && !punishment.isVoided()) {
                punishment.voidAs(uUID, voidReason, "Wave " + banWave.getName() + " taken back");
                this.plugin
                        .getPunishmentHandler()
                        .getRepository()
                        .saveSync(punishment.getUuid(), punishment);
                this.plugin.getPunishmentHandler().executeRevokePunishment(punishment);
                value++;
            }
        }
        return value;
    }

    private UUID actorOf(CommandSender sender) {
        return sender instanceof Player
                ? ((Player) sender).getUniqueId()
                : IProfile.getConsoleUuid();
    }

    private void announce(IBanWave banWave, int value, int value2) {
        this.plugin
                .getLogger()
                .info(
                        "Ban wave '"
                                + banWave.getName()
                                + "' released: "
                                + value
                                + " applied, "
                                + value2
                                + " skipped.");
        if (banWave.isSummarised()) {
            this.plugin
                    .getPacketHandler()
                    .send(new BukkitBanWaveReleasedPacket(banWave.getName(), value));
        }
    }

    public Phoenix getPlugin() {
        return this.plugin;
    }

    public IBanWaveRepository getRepository() {
        return this.repository;
    }
}
