package xyz.refinedev.phoenix.utils.repository;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.handler.database.MongoDatabaseHandler;
import xyz.refinedev.phoenix.handler.database.MySQLDatabaseHandler;
import xyz.refinedev.phoenix.repository.IAddressStatsRepository;
import xyz.refinedev.phoenix.repository.IAltLinkRepository;
import xyz.refinedev.phoenix.repository.IChatLogRepository;
import xyz.refinedev.phoenix.repository.IChatSnapshotRepository;
import xyz.refinedev.phoenix.repository.ICommandLogRepository;
import xyz.refinedev.phoenix.repository.ICustomTagRepository;
import xyz.refinedev.phoenix.repository.IFilterRepository;
import xyz.refinedev.phoenix.repository.IGrantRepository;
import xyz.refinedev.phoenix.repository.ILoginRepository;
import xyz.refinedev.phoenix.repository.INotificationRepository;
import xyz.refinedev.phoenix.repository.IProfileRepository;
import xyz.refinedev.phoenix.repository.IPunishmentLadderRepository;
import xyz.refinedev.phoenix.repository.IPunishmentRepository;
import xyz.refinedev.phoenix.repository.IRankRepository;
import xyz.refinedev.phoenix.repository.IReportCategoryRepository;
import xyz.refinedev.phoenix.repository.IReportRepository;
import xyz.refinedev.phoenix.repository.ISecurityRepository;
import xyz.refinedev.phoenix.repository.IServerHopRepository;
import xyz.refinedev.phoenix.repository.ITagCategoryRepository;
import xyz.refinedev.phoenix.repository.ITagRepository;
import xyz.refinedev.phoenix.repository.impl.MongoAddressStatsRepository;
import xyz.refinedev.phoenix.repository.impl.MongoAltLinkRepository;
import xyz.refinedev.phoenix.repository.impl.MongoChatLogRepository;
import xyz.refinedev.phoenix.repository.impl.MongoChatSnapshotRepository;
import xyz.refinedev.phoenix.repository.impl.MongoCommandLogRepository;
import xyz.refinedev.phoenix.repository.impl.MongoCustomTagRepository;
import xyz.refinedev.phoenix.repository.impl.MongoFilterRepository;
import xyz.refinedev.phoenix.repository.impl.MongoGrantRepository;
import xyz.refinedev.phoenix.repository.impl.MongoLoginRepository;
import xyz.refinedev.phoenix.repository.impl.MongoNotificationRepository;
import xyz.refinedev.phoenix.repository.impl.MongoProfileRepository;
import xyz.refinedev.phoenix.repository.impl.MongoPunishmentLadderRepository;
import xyz.refinedev.phoenix.repository.impl.MongoPunishmentRepository;
import xyz.refinedev.phoenix.repository.impl.MongoRankRepository;
import xyz.refinedev.phoenix.repository.impl.MongoReportCategoryRepository;
import xyz.refinedev.phoenix.repository.impl.MongoReportRepository;
import xyz.refinedev.phoenix.repository.impl.MongoSecurityRepository;
import xyz.refinedev.phoenix.repository.impl.MongoServerHopRepository;
import xyz.refinedev.phoenix.repository.impl.MongoTagCategoryRepository;
import xyz.refinedev.phoenix.repository.impl.MongoTagRepository;
import xyz.refinedev.phoenix.repository.impl.MySQLAddressStatsRepository;
import xyz.refinedev.phoenix.repository.impl.MySQLAltLinkRepository;
import xyz.refinedev.phoenix.repository.impl.MySQLChatLogRepository;
import xyz.refinedev.phoenix.repository.impl.MySQLChatSnapshotRepository;
import xyz.refinedev.phoenix.repository.impl.MySQLCommandLogRepository;
import xyz.refinedev.phoenix.repository.impl.MySQLCustomTagRepository;
import xyz.refinedev.phoenix.repository.impl.MySQLFilterRepository;
import xyz.refinedev.phoenix.repository.impl.MySQLGrantRepository;
import xyz.refinedev.phoenix.repository.impl.MySQLLoginRepository;
import xyz.refinedev.phoenix.repository.impl.MySQLNotificationRepository;
import xyz.refinedev.phoenix.repository.impl.MySQLProfileRepository;
import xyz.refinedev.phoenix.repository.impl.MySQLPunishmentLadderRepository;
import xyz.refinedev.phoenix.repository.impl.MySQLPunishmentRepository;
import xyz.refinedev.phoenix.repository.impl.MySQLRankRepository;
import xyz.refinedev.phoenix.repository.impl.MySQLReportCategoryRepository;
import xyz.refinedev.phoenix.repository.impl.MySQLReportRepository;
import xyz.refinedev.phoenix.repository.impl.MySQLSecurityRepository;
import xyz.refinedev.phoenix.repository.impl.MySQLServerHopRepository;
import xyz.refinedev.phoenix.repository.impl.MySQLTagCategoryRepository;
import xyz.refinedev.phoenix.repository.impl.MySQLTagRepository;

import java.util.function.Function;

public final class RepositoryFactory {

    private final CommonPlatform api;

    public RepositoryFactory(CommonPlatform commonPlatform) {
        this.api = commonPlatform;
    }

    public <R> R create(
            Function<MongoDatabaseHandler, R> function,
            Function<MySQLDatabaseHandler, R> function2) {
        return (R)
                (this.api.isMongoDB()
                        ? function.apply((MongoDatabaseHandler) this.api.getDatabaseHandler())
                        : function2.apply((MySQLDatabaseHandler) this.api.getDatabaseHandler()));
    }

    public IProfileRepository profiles() {
        return (IProfileRepository)
                this.create(
                        result -> new MongoProfileRepository(result.getProfiles()),
                        result -> new MySQLProfileRepository(result.getProfiles()));
    }

    public IGrantRepository grants() {
        return (IGrantRepository)
                this.create(
                        result -> new MongoGrantRepository(result.getGrants()),
                        result -> new MySQLGrantRepository(result.getGrants()));
    }

    public IPunishmentRepository punishments() {
        return (IPunishmentRepository)
                this.create(
                        result -> new MongoPunishmentRepository(result.getPunishments()),
                        result -> new MySQLPunishmentRepository(result.getPunishments()));
    }

    public IRankRepository ranks() {
        return (IRankRepository)
                this.create(
                        result -> new MongoRankRepository(result.getRanks()),
                        result -> new MySQLRankRepository(result.getRanks()));
    }

    public ILoginRepository logins() {
        return (ILoginRepository)
                this.create(
                        result -> new MongoLoginRepository(result.getLogins()),
                        result -> new MySQLLoginRepository(result.getLogins()));
    }

    public IServerHopRepository serverHops() {
        return (IServerHopRepository)
                this.create(
                        result -> new MongoServerHopRepository(result.getServerHops()),
                        result -> new MySQLServerHopRepository(result.getServerHops()));
    }

    public ICommandLogRepository commandLogs() {
        return (ICommandLogRepository)
                this.create(
                        result -> new MongoCommandLogRepository(result.getCommandLogs()),
                        result -> new MySQLCommandLogRepository(result.getCommandLogs()));
    }

    public INotificationRepository notifications() {
        return (INotificationRepository)
                this.create(
                        result -> new MongoNotificationRepository(result.getNotifications()),
                        result -> new MySQLNotificationRepository(result.getNotifications()));
    }

    public IFilterRepository filters() {
        return (IFilterRepository)
                this.create(
                        result -> new MongoFilterRepository(result.getFilters()),
                        result -> new MySQLFilterRepository(result.getFilters()));
    }

    public ITagRepository tags() {
        return (ITagRepository)
                this.create(
                        result -> new MongoTagRepository(result.getTags()),
                        result -> new MySQLTagRepository(result.getTags()));
    }

    public ITagCategoryRepository tagCategories() {
        return (ITagCategoryRepository)
                this.create(
                        result -> new MongoTagCategoryRepository(result.getTagCategories()),
                        result -> new MySQLTagCategoryRepository(result.getTagCategories()));
    }

    public IChatSnapshotRepository chatSnapshots() {
        return (IChatSnapshotRepository)
                this.create(
                        result -> new MongoChatSnapshotRepository(result.getChatSnapshots()),
                        result -> new MySQLChatSnapshotRepository(result.getChatSnapshots()));
    }

    public IChatLogRepository chatLogs() {
        return (IChatLogRepository)
                this.create(
                        result -> new MongoChatLogRepository(result.getChatLogs()),
                        result -> new MySQLChatLogRepository(result.getChatLogs()));
    }

    public IPunishmentLadderRepository punishmentLadders() {
        return (IPunishmentLadderRepository)
                this.create(
                        result ->
                                new MongoPunishmentLadderRepository(result.getPunishmentLadders()),
                        result ->
                                new MySQLPunishmentLadderRepository(result.getPunishmentLadders()));
    }

    public ISecurityRepository security() {
        return (ISecurityRepository)
                this.create(
                        result -> new MongoSecurityRepository(result.getSecurity()),
                        result -> new MySQLSecurityRepository(result.getSecurity()));
    }

    public ICustomTagRepository customTags() {
        return (ICustomTagRepository)
                this.create(
                        result -> new MongoCustomTagRepository(result.getCustomTags()),
                        result -> new MySQLCustomTagRepository(result.getCustomTags()));
    }

    public IAltLinkRepository altLinks() {
        return (IAltLinkRepository)
                this.create(
                        result -> new MongoAltLinkRepository(result.getAltLinks()),
                        result -> new MySQLAltLinkRepository(result.getAltLinks()));
    }

    public IAddressStatsRepository addressStats() {
        return (IAddressStatsRepository)
                this.create(
                        result -> new MongoAddressStatsRepository(result.getAddressStats()),
                        result -> new MySQLAddressStatsRepository(result.getAddressStats()));
    }

    public IReportRepository reports() {
        return (IReportRepository)
                this.create(
                        result -> new MongoReportRepository(result.getReports()),
                        result -> new MySQLReportRepository(result.getReports()));
    }

    public IReportCategoryRepository reportCategories() {
        return (IReportCategoryRepository)
                this.create(
                        result -> new MongoReportCategoryRepository(result.getReportCategories()),
                        result -> new MySQLReportCategoryRepository(result.getReportCategories()));
    }
}
