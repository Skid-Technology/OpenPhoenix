package xyz.refinedev.phoenix.handler;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.alt.AddressDensity;
import xyz.refinedev.phoenix.alt.AltEvidenceAccumulator;
import xyz.refinedev.phoenix.alt.AltEvidenceEvaluator;
import xyz.refinedev.phoenix.alt.AltLink;
import xyz.refinedev.phoenix.alt.AltLinkStatus;
import xyz.refinedev.phoenix.alt.AltScoreCalculator;
import xyz.refinedev.phoenix.alt.AltScoringWeights;
import xyz.refinedev.phoenix.alt.AltSignals;
import xyz.refinedev.phoenix.alt.IAddressStats;
import xyz.refinedev.phoenix.alt.IAltLink;
import xyz.refinedev.phoenix.alt.IClientFingerprint;
import xyz.refinedev.phoenix.alt.LoginIndexMigration;
import xyz.refinedev.phoenix.alt.ScoreResult;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.handler.database.AbstractDatabaseHandler;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.antivpn.IAntiVPNData;
import xyz.refinedev.phoenix.profile.login.ILogin;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.repository.IAltLinkRepository;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.repository.RepositoryFactory;
import xyz.refinedev.phoenix.utils.repository.filters.api.FilterOptions;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;
import xyz.refinedev.phoenix.utils.repository.filters.impl.InFilterOption;
import xyz.refinedev.phoenix.utils.repository.sorting.api.Sorts;
import xyz.refinedev.phoenix.utils.repository.sorting.impl.SortsDescendingOptions;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class MongoAltHandler implements IAltHandler {

    private final IAltLinkRepository repository;

    private final CommonPlatform platform;

    private final AltScoreCalculator altScoreCalculator = new AltScoreCalculator();

    private final LoginIndexMigration loginIndexMigration;

    public MongoAltHandler(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
        this.repository = new RepositoryFactory(commonPlatform).altLinks();
        this.loginIndexMigration =
                commonPlatform.isMongoDB() ? null : new LoginIndexMigration(commonPlatform);
        GlobalConfig.validateAltConfig(commonPlatform.getLogger());
        if (this.loginIndexMigration != null) {
            CompletableFuture.runAsync(
                    this.loginIndexMigration::initialize, AbstractDatabaseHandler.DB_EXECUTOR);
        }
        CompletableFuture.runAsync(this::initialize, AbstractDatabaseHandler.DB_EXECUTOR);
    }

    public AltScoringWeights create() {
        AltScoringWeights altScoringWeights;
        (altScoringWeights = new AltScoringWeights())
                .setCount(GlobalConfig.SERVER.ALTS.SCORE.HOUSEHOLD_SIZE);
        altScoringWeights.setValue(GlobalConfig.SERVER.ALTS.SCORE.IP_WEIGHT);
        altScoringWeights.setValueValue(GlobalConfig.SERVER.ALTS.SCORE.MULTI_ADDRESS_WEIGHT);
        altScoringWeights.setValue2(GlobalConfig.SERVER.ALTS.SCORE.SUBNET_STACK_DECAY);
        altScoringWeights.setValue3(GlobalConfig.SERVER.ALTS.SCORE.SUBNET_WEIGHT);
        altScoringWeights.setValue4(GlobalConfig.SERVER.ALTS.SCORE.VPN_TRUST_MULTIPLIER);
        altScoringWeights.setValue5(GlobalConfig.SERVER.ALTS.SCORE.MOBILE_ASN_MULTIPLIER);
        altScoringWeights.setValue6(GlobalConfig.SERVER.ALTS.SCORE.FORCED_SHARED_TRUST);
        altScoringWeights.setValue7(GlobalConfig.SERVER.ALTS.SCORE.MIN_OVERLAP_FACTOR);
        altScoringWeights.setCountCount(GlobalConfig.SERVER.ALTS.SCORE.FULL_SESSION_COUNT);
        altScoringWeights.setValue8(GlobalConfig.SERVER.ALTS.SCORE.RECURSIVE_DECAY);
        long value;
        if ((value = DateUtils.getDuration(GlobalConfig.SERVER.ALTS.SCORE.CLOSE_WINDOW)) > 0L) {
            altScoringWeights.setTime(value);
        }
        long value2;
        if ((value2 = DateUtils.getDuration(GlobalConfig.SERVER.ALTS.SCORE.REASSIGN_WINDOW)) > 0L) {
            altScoringWeights.setTimeTime(value2);
        }
        altScoringWeights.setEnabled(GlobalConfig.SERVER.ALTS.STRICT_IP.ENABLED);
        altScoringWeights.setEnabledEnabled(
                GlobalConfig.SERVER.ALTS.STRICT_IP.REQUIRE_CURRENT_ADDRESS);
        long value3;
        if ((value3 = GlobalConfig.strictIpWindowMillis()) >= 0L) {
            altScoringWeights.setTime2(value3);
        }
        altScoringWeights.setValue9(GlobalConfig.SERVER.ALTS.SIGNALS.NON_IP_CAP);
        altScoringWeights.setValue10(GlobalConfig.SERVER.ALTS.SIGNALS.ALTERNATION_WEIGHT);
        altScoringWeights.setCount2(GlobalConfig.SERVER.ALTS.SIGNALS.FULL_HANDOFFS);
        altScoringWeights.setValue11(GlobalConfig.SERVER.ALTS.SIGNALS.FULL_HANDOFF_RATIO);
        altScoringWeights.setValue12(GlobalConfig.SERVER.ALTS.SIGNALS.BAN_APPEAR_WEIGHT);
        long value4;
        if ((value4 = DateUtils.getDuration(GlobalConfig.SERVER.ALTS.SIGNALS.BAN_APPEAR_WINDOW))
                > 0L) {
            altScoringWeights.setTime4(value4);
        }
        altScoringWeights.setValue13(GlobalConfig.SERVER.ALTS.SIGNALS.CONCURRENCY_PENALTY);
        altScoringWeights.setCount3(GlobalConfig.SERVER.ALTS.SIGNALS.FULL_CONCURRENT);
        altScoringWeights.setValue14(GlobalConfig.SERVER.ALTS.SIGNALS.FINGERPRINT_WEIGHT);
        altScoringWeights.setCount4(GlobalConfig.SERVER.ALTS.SIGNALS.FINGERPRINT_RARE_AT);
        altScoringWeights.setValue15(GlobalConfig.SERVER.ALTS.SIGNALS.NAME_WEIGHT);
        altScoringWeights.setValue16(GlobalConfig.SERVER.ALTS.SIGNALS.NAME_FLOOR);
        altScoringWeights.setValue17(GlobalConfig.SERVER.ALTS.SIGNALS.RHYTHM_WEIGHT);
        altScoringWeights.setValue18(GlobalConfig.SERVER.ALTS.SIGNALS.RHYTHM_FLOOR);
        altScoringWeights.setValue19(GlobalConfig.SERVER.ALTS.SIGNALS.SKIN_WEIGHT);
        altScoringWeights.setCount5(GlobalConfig.SERVER.ALTS.SIGNALS.SKIN_RARE_AT);
        altScoringWeights.setValue20(GlobalConfig.SERVER.ALTS.SIGNALS.LATENCY_WEIGHT);
        altScoringWeights.setTime5(GlobalConfig.SERVER.ALTS.SIGNALS.LATENCY_TOLERANCE_MS);
        altScoringWeights.setValue21(GlobalConfig.SERVER.ALTS.SIGNALS.LOCATION_WEIGHT);
        altScoringWeights.setCount6(GlobalConfig.SERVER.ALTS.SIGNALS.LOCATION_RARE_AT);
        altScoringWeights.setCount7(GlobalConfig.SERVER.ALTS.SIGNALS.FULL_SHARED_LOCATIONS);
        long value5;
        if ((value5 = DateUtils.getDuration(GlobalConfig.SERVER.ALTS.SIGNALS.HANDOFF_WINDOW))
                > 0L) {
            altScoringWeights.setTime3(value5);
        }
        return altScoringWeights;
    }

    public ScoreResult evaluate(UUID uUID, UUID uUID2) {
        List<ILogin> logins = this.platform.getLoginHandler().getLogins(uUID);
        List<ILogin> logins2 = this.platform.getLoginHandler().getLogins(uUID2);
        IProfile profile = this.platform.getProfileHandler().getProfile(uUID);
        IProfile profile2 = this.platform.getProfileHandler().getProfile(uUID2);
        Set<String> values;
        (values = findSet(logins, logins2)).addAll(this.findSet(profile));
        values.addAll(this.findSet(profile2));
        Map<String, IAddressStats> entries = this.platform.getAddressStatsHandler().fetch(values);
        HashSet<UUID> values2;
        (values2 = new HashSet()).add(uUID);
        values2.add(uUID2);
        Map entries2 = this.findMapByPlatform(values2);
        AltScoringWeights altScoringWeights = this.create();
        AltEvidenceAccumulator altEvidenceAccumulator =
                this.create(
                        logins,
                        logins2,
                        entries,
                        profile,
                        profile2,
                        (List<Long>) entries2.get(uUID),
                        (List<Long>) entries2.get(uUID2),
                        altScoringWeights);
        return this.altScoreCalculator.create(altEvidenceAccumulator, altScoringWeights);
    }

    @Override
    public List<IAltLink> refreshLinks(UUID uUID) {
        if (!GlobalConfig.SERVER.ALTS.ENABLED) {
            return Collections.emptyList();
        }
        List<IAltLink> links = this.getLinks(uUID);
        long value;
        if ((value = DateUtils.getDuration(GlobalConfig.SERVER.ALTS.REFRESH_THROTTLE)) > 0L
                && !links.isEmpty()) {
            long value2 = 0L;
            for (IAltLink altLink : links) {
                value2 = Math.max(value2, altLink.getLastEvaluated());
            }
            if (System.currentTimeMillis() - value2 < value) {
                return links;
            }
        }
        List<ILogin> logins = this.platform.getLoginHandler().getLogins(uUID);
        Set<UUID> values2 = this.findSet(uUID, logins);
        for (IAltLink altLink2 : links) {
            values2.add(altLink2.other(uUID));
        }
        values2.addAll(
                findSet(
                        this.findSet(uUID, values2),
                        GlobalConfig.SERVER.ALTS.MIGRATION.MAX_LEGACY_RESCORE_PER_LOAD));
        values2.remove(uUID);
        if (values2.isEmpty()) {
            return links;
        }
        Map<UUID, List<ILogin>> entries = this.findMap(values2);
        Map<UUID, IProfile> entries2;
        IProfile profile = (IProfile) (entries2 = this.findMap(uUID, values2)).get(uUID);
        HashSet values3 = new HashSet<>(this.findSet(profile));
        for (List<ILogin> values4 : entries.values()) {
            values3.addAll(findSet(logins, values4));
        }
        for (UUID uUID2 : values2) {
            values3.addAll(this.findSet((IProfile) entries2.get(uUID2)));
        }
        Map entries3 = this.platform.getAddressStatsHandler().fetch(values3);
        HashSet values5;
        (values5 = new HashSet(values2)).add(uUID);
        Map entries4 = this.findMapByPlatform(values5);
        AltScoringWeights altScoringWeights = this.create();
        ArrayList<String> values6 = new ArrayList<>();
        for (UUID uUID3 : values2) {
            values6.add(AltLink.formatString(uUID, uUID3));
        }
        HashMap entries5 = new HashMap();
        for (IAltLink altLink3 : this.repository.fetchAllSync(Filters.in("_id", values6))) {
            entries5.put(altLink3.getId(), altLink3);
        }
        ArrayList<IAltLink> values7 = new ArrayList<>();
        for (UUID uUID4 : values2) {
            List values8;
            if ((values8 = (List) entries.get(uUID4)) == null) {
                values8 = Collections.emptyList();
            }
            AltEvidenceAccumulator altEvidenceAccumulator =
                    this.create(
                            logins,
                            values8,
                            entries3,
                            profile,
                            (IProfile) entries2.get(uUID4),
                            (List<Long>) entries4.get(uUID),
                            (List<Long>) entries4.get(uUID4),
                            altScoringWeights);
            ScoreResult scoreResult =
                    this.altScoreCalculator.create(altEvidenceAccumulator, altScoringWeights);
            IAltLink altLink4;
            if ((altLink4 =
                            this.findAltLink(
                                    uUID,
                                    uUID4,
                                    (IAltLink) entries5.get(AltLink.formatString(uUID, uUID4)),
                                    scoreResult,
                                    altEvidenceAccumulator.getAddressDensities(),
                                    logins))
                    != null) {
                values7.add(altLink4);
            }
        }
        values7.sort(Comparator.comparingInt(IAltLink::getScore).reversed());
        return values7;
    }

    @Override
    public List<IAltLink> getLinks(UUID uUID) {
        List<IAltLink> values;
        if ((values =
                        this.loginIndexMigration == null
                                ? null
                                : this.loginIndexMigration.findList(uUID))
                == null) {
            values =
                    this.repository.fetchAllSync(
                            Filters.or(
                                    Filters.eq("a", uUID.toString()),
                                    Filters.eq("b", uUID.toString())));
        }
        values.sort(Comparator.comparingInt(IAltLink::getScore).reversed());
        return values;
    }

    @Override
    public Set<UUID> altsAtOrAbove(UUID uUID, int value) {
        HashMap entries = new HashMap();
        ArrayDeque values = new ArrayDeque();
        entries.put(uUID, Double.MAX_VALUE);
        values.add(uUID);
        boolean matches = GlobalConfig.SERVER.ALTS.RECURSIVE_ALTS;
        double value2 = GlobalConfig.SERVER.ALTS.SCORE.RECURSIVE_DECAY;
        while (!values.isEmpty()) {
            UUID uUID2 = (UUID) values.poll();
            double value3 = (Double) entries.get(uUID2);
            boolean matches2 = uUID.equals(uUID2);
            Iterator<IAltLink> iterator2 = this.getLinks(uUID2).iterator();
            while (iterator2.hasNext()) {
                IAltLink altLink;
                if ((altLink = (IAltLink) iterator2.next()).getStatus() == AltLinkStatus.ACTIVE) {
                    double value4 = Math.min(value3, altLink.getScore());
                    if (!matches2) {
                        value4 *= value2;
                    }
                    if (!(value4 < value)) {
                        UUID uUID3 = altLink.other(uUID2);
                        Double value5;
                        if ((value5 = (Double) entries.get(uUID3)) == null || !(value5 >= value4)) {
                            entries.put(uUID3, value4);
                            if (matches) {
                                values.add(uUID3);
                            }
                        }
                    }
                }
            }
        }
        entries.remove(uUID);
        return entries.keySet();
    }

    @Override
    public IAltLink setStatus(UUID uUID, UUID uUID2, AltLinkStatus altLinkStatus) {
        String text = AltLink.formatString(uUID, uUID2);
        IAltLink altLink;
        if ((altLink = this.repository.fetchSync(text).orElse(null)) == null) {
            AltLink altLink2 = new AltLink(uUID, uUID2);
            ScoreResult scoreResult = this.evaluate(uUID, uUID2);
            altLink2.setScore(scoreResult.getScore());
            altLink2.setSignals(scoreResult.getSignals());
            altLink = altLink2;
        }
        altLink.setStatus(altLinkStatus);
        altLink.setLastEvaluated(System.currentTimeMillis());
        this.repository.saveSync(altLink.getId(), altLink);
        return altLink;
    }

    public static Set<UUID> findSet(Set<UUID> values, int value) {
        HashSet<UUID> values2 = new HashSet<>();
        for (UUID uUID : values) {
            if (values2.size() >= Math.max(0, value)) {
                break;
            }
            values2.add(uUID);
        }
        return values2;
    }

    private Set<UUID> findSet(UUID uUID, List<ILogin> values) {
        HashSet<String> values2 = new HashSet<>();
        Iterator<ILogin> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            ILogin login;
            if ((login = (ILogin) iterator2.next()).getIp() != null) {
                values2.add(login.getIp());
            }
        }
        if (values2.isEmpty()) {
            return new HashSet<>();
        }
        int value = GlobalConfig.SERVER.ALTS.SHARED_ADDRESS.DISCOVERY_DENSITY_CUTOFF;
        if (GlobalConfig.SERVER.ALTS.SHARED_ADDRESS.DISCOVERY_DENSITY_CUTOFF > 0) {
            Map entries = this.platform.getAddressStatsHandler().fetch(values2);
            values2.removeIf(
                    result -> {
                        IAddressStats addressStats;
                        return (addressStats = (IAddressStats) entries.get(result)) != null
                                && addressStats.getAccounts() >= value;
                    });
            if (values2.isEmpty()) {
                return new HashSet<>();
            }
        }
        List values3 = this.findList(values2);
        int value2 =
                Math.max(1, GlobalConfig.SERVER.ALTS.SHARED_ADDRESS.MAX_CANDIDATES_PER_REFRESH);
        HashSet<UUID> values4 = new HashSet<>();
        boolean matches = false;
        Iterator iterator3 = values3.iterator();
        while (iterator3.hasNext()) {
            UUID uUID2;
            if ((uUID2 = fromString((String) iterator3.next())) != null && !uUID.equals(uUID2)) {
                values4.add(uUID2);
                if (values4.size() >= value2) {
                    matches = true;
                    break;
                }
            }
        }
        if (matches) {
            this.platform
                    .getLogger()
                    .warning(
                            "(Alts) Discovery for "
                                    + uUID
                                    + " hit the "
                                    + value2
                                    + " candidate cap across "
                                    + values2.size()
                                    + " address(es); the rest were not scored.");
        }
        if (this.platform.isDebugMode()) {
            this.platform
                    .getLogger()
                    .info(
                            "(Alts) Discovery for "
                                    + uUID
                                    + ": "
                                    + values2.size()
                                    + " address(es), "
                                    + values3.size()
                                    + " login rows, "
                                    + values4.size()
                                    + " candidates (cap "
                                    + value2
                                    + ")");
        }
        return values4;
    }

    private List<String> findList(Set<String> values) {
        InFilterOption inFilterOption = Filters.in("ip", new ArrayList<>(values));
        SortsDescendingOptions sortsDescendingOptions = Sorts.descending("login");
        List<String> values2;
        if (this.loginIndexMigration == null
                && (values2 =
                                this.platform
                                        .getLoginHandler()
                                        .getRepository()
                                        .fieldValuesSync(
                                                "target", inFilterOption, sortsDescendingOptions))
                        != null) {
            return values2;
        }
        ArrayList<ILogin> values3;
        (values3 = new ArrayList<>(this.findList("ip", values, inFilterOption)))
                .sort(Comparator.comparingLong(ILogin::getLogin).reversed());
        return FastStreams.mapArrayList(
                values3,
                result -> result.getTarget() == null ? null : result.getTarget().toString());
    }

    private static UUID fromString(String text) {
        if (text == null) {
            return null;
        }
        try {
            return UUID.fromString(text);
        } catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
    }

    private Map<UUID, List<ILogin>> findMap(Set<UUID> values) {
        ArrayList<String> values2 = new ArrayList<>();
        for (UUID uUID : values) {
            values2.add(uUID.toString());
        }
        HashMap<UUID, List<ILogin>> entries = new HashMap<>();
        for (ILogin login : this.findList("target", values2, Filters.in("target", values2))) {
            List<ILogin> values3;
            if ((values3 = (List) entries.get(login.getTarget())) == null) {
                values3 = new ArrayList();
                entries.put(login.getTarget(), values3);
            }
            values3.add(login);
        }
        return entries;
    }

    private Set<UUID> findSet(UUID uUID, Set<UUID> values) {
        IProfile profile;
        if ((profile = this.platform.getProfileHandler().getProfile(uUID)) != null
                && profile.getAlts() != null) {
            HashSet<UUID> values2 = new HashSet<>();
            Iterator<UUID> iterator2 = profile.getAlts().iterator();
            while (iterator2.hasNext()) {
                UUID uUID2;
                if ((uUID2 = (UUID) iterator2.next()) != null && !values.contains(uUID2)) {
                    values2.add(uUID2);
                }
            }
            return values2;
        } else {
            return new HashSet<>();
        }
    }

    private List<ILogin> findList(
            String text, Collection<String> values, FilterOptions filterOptions) {
        if (values.isEmpty()) {
            return Collections.emptyList();
        }
        List<ILogin> values2;
        return this.loginIndexMigration != null
                        && (values2 = this.loginIndexMigration.findList(text, values)) != null
                ? values2
                : this.platform
                        .getLoginHandler()
                        .getRepository()
                        .fetchAllDetachedSync(filterOptions);
    }

    public static long calculateTime(
            List<Long> values, long value, List<Long> values2, long value2) {
        long value3 = calculateTime(values, value, -1L);
        return calculateTime(values2, value2, value3);
    }

    private static long calculateTime(List<Long> values, long value, long value2) {
        if (values != null && value > 0L) {
            for (long value3 : values) {
                long value4;
                if ((value4 = value - value3) >= 0L && (value2 < 0L || value4 < value2)) {
                    value2 = value4;
                }
            }
            return value2;
        } else {
            return value2;
        }
    }

    private static long calculateTime(List<ILogin> values) {
        long value = 0L;
        for (ILogin login : values) {
            if (value == 0L || login.getLogin() < value) {
                value = login.getLogin();
            }
        }
        return value;
    }

    public static boolean matchesEnabled(IPunishment punishment) {
        return punishment != null && !punishment.isVoided()
                ? punishment.getPunishmentType() == PunishmentType.BAN
                        || punishment.getPunishmentType() == PunishmentType.BLACKLIST
                : false;
    }

    private Map<UUID, List<Long>> findMapByPlatform(Set<UUID> values) {
        ArrayList<String> values2 = new ArrayList<>();
        for (UUID uUID : values) {
            values2.add(uUID.toString());
        }
        HashMap<UUID, List<Long>> entries = new HashMap<>();
        Iterator<IPunishment> iterator2 =
                this.platform
                        .getPunishmentHandler()
                        .getRepository()
                        .fetchAllSync(Filters.in("target", values2))
                        .iterator();
        while (iterator2.hasNext()) {
            IPunishment punishment;
            if (matchesEnabled(punishment = (IPunishment) iterator2.next())) {
                entries.computeIfAbsent(punishment.getTarget(), result -> new ArrayList<>())
                        .add(punishment.getIssuedAt());
            }
        }
        return entries;
    }

    private Map<UUID, IProfile> findMap(UUID uUID, Set<UUID> values) {
        HashSet values2;
        (values2 = new HashSet(values)).add(uUID);
        HashMap<UUID, IProfile> entries = new HashMap<>();
        for (IProfile profile :
                (Iterable<IProfile>)
                        ((Iterable) this.platform.getProfileHandler().getProfiles(values2))) {
            entries.put(profile.getUniqueId(), profile);
        }
        return entries;
    }

    public static List<AddressDensity> findList(
            List<ILogin> values,
            List<ILogin> values2,
            Map<String, IAddressStats> entries,
            Map<String, String> entries2) {
        Map<String, List<ILogin>> entries3 = findMap(values);
        Map<String, List<ILogin>> entries4 = findMap(values2);
        ArrayList<AddressDensity> values3 = new ArrayList<>();
        for (Entry<String, List<ILogin>> entry : entries3.entrySet()) {
            List values4;
            if ((values4 = (List) entries4.get(entry.getKey())) != null) {
                String text = (String) entry.getKey();
                List values5 = (List) entry.getValue();
                IAddressStats addressStats;
                int value =
                        (addressStats = (IAddressStats) entries.get(text)) == null
                                ? 1
                                : Math.max(1, addressStats.getAccounts());
                String text2;
                if ((text2 = (String) entries2.get(text)) == null) {
                    text2 = "unknown";
                }
                AddressDensity addressDensity =
                        new AddressDensity(
                                text,
                                text2,
                                value,
                                values5.size(),
                                values4.size(),
                                calculateTime(values5, values4));
                if (addressStats != null) {
                    addressDensity.setEnabled(addressStats.isVpn());
                    addressDensity.setEnabledEnabled(addressStats.isMobile());
                    addressDensity.setAddressClassification(addressStats.getClassification());
                }
                values3.add(addressDensity);
            }
        }
        return values3;
    }

    public static Map<String, String> findMap(List<ILogin> values, List<ILogin> values2) {
        HashMap<String, String> entries = new HashMap<>();
        Iterator iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            ILogin login;
            if ((login = (ILogin) iterator2.next()).getSubnet() != null
                    && !entries.containsKey(login.getIp())) {
                entries.put(login.getIp(), login.getSubnet());
            }
        }
        iterator2 = values2.iterator();
        while (iterator2.hasNext()) {
            ILogin login2;
            if ((login2 = (ILogin) iterator2.next()).getSubnet() != null
                    && !entries.containsKey(login2.getIp())) {
                entries.put(login2.getIp(), login2.getSubnet());
            }
        }
        return entries;
    }

    static long calculateTime(List<ILogin> values, List<ILogin> values2) {
        if (!values.isEmpty() && !values2.isEmpty()) {
            long now = System.currentTimeMillis();
            long[][] values3 = findTimeArrayArray(values, now);
            long[][] values4 = findTimeArrayArray(values2, now);
            long value = Long.MAX_VALUE;
            long value2 = Long.MIN_VALUE;
            long value3 = Long.MIN_VALUE;
            int value4 = 0;
            int value5 = 0;
            while (value4 < values3.length || value5 < values4.length) {
                if (value5 >= values4.length
                        || value4 < values3.length && values3[value4][0] <= values4[value5][0]) {
                    if (value3 != Long.MIN_VALUE) {
                        value = Math.min(value, Math.max(0L, values3[value4][0] - value3));
                    }
                    value2 = Math.max(value2, values3[value4][1]);
                    value4++;
                } else {
                    if (value2 != Long.MIN_VALUE) {
                        value = Math.min(value, Math.max(0L, values4[value5][0] - value2));
                    }
                    value3 = Math.max(value3, values4[value5][1]);
                    value5++;
                }
            }
            return value;
        } else {
            return 0L;
        }
    }

    private AltEvidenceAccumulator create(
            List<ILogin> values,
            List<ILogin> values2,
            Map<String, IAddressStats> entries,
            IProfile profile,
            IProfile profile2,
            List<Long> values3,
            List<Long> values4,
            AltScoringWeights altScoringWeights) {
        AltEvidenceAccumulator altEvidenceAccumulator;
        (altEvidenceAccumulator = new AltEvidenceAccumulator())
                .setTime(
                        calculateTime(
                                values3, calculateTime(values2), values4, calculateTime(values)));
        List<AddressDensity> values5 = findList(values, values2, entries, findMap(values, values2));
        this.applyList(values5);
        if (altScoringWeights.isEnabled()) {
            values5 =
                    AltSignals.findList(
                            values5,
                            altScoringWeights.getTime2(),
                            altScoringWeights.isEnabledEnabled(),
                            getCurrentAddress(profile),
                            getCurrentAddress(profile2));
        }
        altEvidenceAccumulator.setAddressDensities(values5);
        if (values5.isEmpty()
                && GlobalConfig.SERVER.ALTS.IP_SUBNET_CHECK
                && !altScoringWeights.isEnabled()) {
            altEvidenceAccumulator.setAddressDensity(create(values, values2, entries));
        }
        altEvidenceAccumulator.setCount(
                AltEvidenceEvaluator.calculateCount(values, values2, altScoringWeights.getTime3()));
        altEvidenceAccumulator.setCountCount(values.size());
        altEvidenceAccumulator.setCount2(values2.size());
        altEvidenceAccumulator.setCount3(
                AltEvidenceEvaluator.calculateCountByList(
                        values, values2, System.currentTimeMillis()));
        altEvidenceAccumulator.setValueValue(AltEvidenceEvaluator.calculateValue(values, values2));
        altEvidenceAccumulator.setValue(
                AltEvidenceEvaluator.calculateValue(
                        profile == null ? null : profile.getName(),
                        profile2 == null ? null : profile2.getName()));
        this.applyAltEvidenceAccumulator(altEvidenceAccumulator, profile, profile2, entries);
        return altEvidenceAccumulator;
    }

    private static String getCurrentAddress(IProfile profile) {
        return profile == null ? null : profile.getCurrentAddress();
    }

    private void applyAltEvidenceAccumulator(
            AltEvidenceAccumulator altEvidenceAccumulator,
            IProfile profile,
            IProfile profile2,
            Map<String, IAddressStats> entries) {
        if (profile != null && profile2 != null) {
            IClientFingerprint clientFingerprint = profile.getClientFingerprint();
            IClientFingerprint clientFingerprint2 = profile2.getClientFingerprint();
            if (clientFingerprint != null
                    && clientFingerprint2 != null
                    && !clientFingerprint.isEmpty()
                    && !clientFingerprint2.isEmpty()) {
                altEvidenceAccumulator.setCount5(
                        clientFingerprint.comparedCount(clientFingerprint2));
                altEvidenceAccumulator.setCount4(clientFingerprint.matchCount(clientFingerprint2));
                if (clientFingerprint.signature().equals(clientFingerprint2.signature())) {
                    altEvidenceAccumulator.setCount6(
                            this.calculateCount(entries, this.formatString(clientFingerprint)));
                }
            }
            if (!GlobalConfig.SERVER.ALTS.CRACKED_SERVER
                    && profile.getSkinHash() != null
                    && profile.getSkinHash().equals(profile2.getSkinHash())) {
                altEvidenceAccumulator.setEnabled(true);
                altEvidenceAccumulator.setCount7(
                        this.calculateCount(entries, "skin:" + profile.getSkinHash()));
            }
            List<Integer> recentPings = profile.getRecentPings();
            List<Integer> recentPings2 = profile2.getRecentPings();
            if (recentPings != null
                    && recentPings2 != null
                    && recentPings.size() >= 5
                    && recentPings2.size() >= 5) {
                altEvidenceAccumulator.setEnabledEnabled(true);
                altEvidenceAccumulator.setTimeTime(
                        Math.abs(
                                AltEvidenceEvaluator.calculateCount(recentPings)
                                        - AltEvidenceEvaluator.calculateCount(recentPings2)));
            }
            if (profile.getLogoutBuckets() != null && profile2.getLogoutBuckets() != null) {
                HashSet<String> values;
                (values = new HashSet<>(profile.getLogoutBuckets()))
                        .retainAll(profile2.getLogoutBuckets());
                if (!values.isEmpty()) {
                    altEvidenceAccumulator.setCount8(values.size());
                    int value = 1;
                    for (String text : values) {
                        value = Math.max(value, this.calculateCount(entries, text));
                    }
                    altEvidenceAccumulator.setCount9(value);
                }
            }
        }
    }

    private void applyList(List<AddressDensity> values) {
        IAntiVPNHandler antiVPNHandler;
        if ((antiVPNHandler = this.platform.getAntiVPNHandler()) != null
                && antiVPNHandler.isEnabled()) {
            for (AddressDensity addressDensity : values) {
                IAntiVPNData antiVPNData;
                if ((antiVPNData = antiVPNHandler.getAntiVPNData(addressDensity.getText()))
                        != null) {
                    if (antiVPNData.isVpn()) {
                        addressDensity.setEnabled(true);
                    }
                    if (antiVPNData.isMobile()) {
                        addressDensity.setEnabledEnabled(true);
                    }
                }
            }
        }
    }

    private static AddressDensity create(
            List<ILogin> values, List<ILogin> values2, Map<String, IAddressStats> entries) {
        Map<String, List<ILogin>> entries2 = findMapByMap(values);
        Map<String, List<ILogin>> entries3 = findMapByMap(values2);
        AddressDensity addressDensity = null;
        for (Entry<String, List<ILogin>> entry : entries2.entrySet()) {
            List values3;
            if ((values3 = (List) entries3.get(entry.getKey())) != null) {
                String text = (String) entry.getKey();
                IAddressStats addressStats;
                int value =
                        (addressStats = (IAddressStats) entries.get(text)) == null
                                ? 1
                                : Math.max(1, addressStats.getAccounts());
                AddressDensity addressDensity2 =
                        new AddressDensity(
                                text,
                                text,
                                value,
                                ((List) entry.getValue()).size(),
                                values3.size(),
                                calculateTime((List<ILogin>) entry.getValue(), values3));
                if (addressStats != null) {
                    addressDensity2.setEnabled(addressStats.isVpn());
                    addressDensity2.setEnabledEnabled(addressStats.isMobile());
                    addressDensity2.setAddressClassification(addressStats.getClassification());
                }
                if (addressDensity == null
                        || addressDensity2.getTime() < addressDensity.getTime()) {
                    addressDensity = addressDensity2;
                }
            }
        }
        return addressDensity;
    }

    private static Set<String> findSet(List<ILogin> values, List<ILogin> values2) {
        HashSet<String> values3 = new HashSet<>();
        for (ILogin login : values) {
            values3.add(login.getIp());
        }
        HashSet<String> values4 = new HashSet<>();
        for (ILogin login2 : values2) {
            if (values3.contains(login2.getIp())) {
                values4.add(login2.getIp());
            }
        }
        Iterator iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            ILogin login3;
            if ((login3 = (ILogin) iterator2.next()).getSubnet() != null) {
                values4.add(login3.getSubnet());
            }
        }
        iterator2 = values2.iterator();
        while (iterator2.hasNext()) {
            ILogin login4;
            if ((login4 = (ILogin) iterator2.next()).getSubnet() != null) {
                values4.add(login4.getSubnet());
            }
        }
        return values4;
    }

    private Set<String> findSet(IProfile profile) {
        HashSet<String> values = new HashSet<>();
        if (profile == null) {
            return values;
        }
        IClientFingerprint clientFingerprint;
        if ((clientFingerprint = profile.getClientFingerprint()) != null
                && !clientFingerprint.isEmpty()) {
            values.add(this.formatString(clientFingerprint));
        }
        if (!GlobalConfig.SERVER.ALTS.CRACKED_SERVER && profile.getSkinHash() != null) {
            values.add("skin:" + profile.getSkinHash());
        }
        if (profile.getLogoutBuckets() != null) {
            values.addAll(profile.getLogoutBuckets());
        }
        return values;
    }

    private String formatString(IClientFingerprint clientFingerprint) {
        return "fp:" + this.platform.getHashHandler().hash(clientFingerprint.signature());
    }

    private int calculateCount(Map<String, IAddressStats> entries, String text) {
        IAddressStats addressStats;
        return (addressStats = (IAddressStats) entries.get(text)) == null
                ? 1
                : Math.max(1, addressStats.getAccounts());
    }

    private static Map<String, List<ILogin>> findMap(List<ILogin> values) {
        HashMap<String, List<ILogin>> entries = new HashMap<>();
        Iterator<ILogin> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            ILogin login;
            if ((login = (ILogin) iterator2.next()).getIp() != null) {
                entries.computeIfAbsent(login.getIp(), result -> new ArrayList<>()).add(login);
            }
        }
        return entries;
    }

    private static Map<String, List<ILogin>> findMapByMap(List<ILogin> values) {
        HashMap<String, List<ILogin>> entries = new HashMap<>();
        Iterator<ILogin> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            ILogin login;
            String text;
            if ((text = (login = (ILogin) iterator2.next()).getSubnet()) != null
                    && !"unknown".equals(text)) {
                entries.computeIfAbsent(text, result -> new ArrayList<>()).add(login);
            }
        }
        return entries;
    }

    private static long[][] findTimeArrayArray(List<ILogin> values, long value) {
        long[][] values2 = new long[values.size()][];
        for (int index = 0; index < values.size(); index++) {
            ILogin login;
            long value2 =
                    (login = (ILogin) values.get(index)).hasLoggedOut() ? login.getLogout() : value;
            values2[index] = new long[] {login.getLogin(), Math.max(login.getLogin(), value2)};
        }
        Arrays.sort(values2, Comparator.comparingLong(result -> result[0]));
        return values2;
    }

    private IAltLink findAltLink(
            UUID uUID,
            UUID uUID2,
            IAltLink altLink,
            ScoreResult scoreResult,
            List<AddressDensity> values,
            List<ILogin> values2) {
        if (altLink == null) {
            if (scoreResult.getScore() < GlobalConfig.SERVER.ALTS.THRESHOLDS.DISPLAY) {
                return null;
            }
            altLink = new AltLink(uUID, uUID2);
        }
        HashSet<String> values3 = new HashSet<>();
        long value = 0L;
        for (AddressDensity addressDensity : values) {
            values3.add(addressDensity.getTextString());
            for (ILogin login : values2) {
                if (addressDensity.getText().equals(login.getIp())) {
                    value = Math.max(value, login.getLogin());
                }
            }
        }
        altLink.setScore(scoreResult.getScore());
        altLink.setSignals(scoreResult.getSignals());
        altLink.setSharedAddresses(values.size());
        altLink.setIndependentSubnets(values3.size());
        if (value > 0L) {
            altLink.setLastSharedLogin(value);
        }
        altLink.setLastEvaluated(System.currentTimeMillis());
        this.repository.saveSync(altLink.getId(), altLink);
        return altLink;
    }

    private void initialize() {
        long value;
        if ((value = DateUtils.getDuration(GlobalConfig.SERVER.ALTS.PRUNE_STALE_LINKS_AFTER))
                > 0L) {
            try {
                long value2 = System.currentTimeMillis() - value;
                long value3;
                if ((value3 =
                                this.repository.deleteSync(
                                        Filters.and(
                                                Filters.eq("status", AltLinkStatus.ACTIVE.name()),
                                                Filters.lt(
                                                        "score",
                                                        GlobalConfig.SERVER
                                                                .ALTS
                                                                .THRESHOLDS
                                                                .DISPLAY),
                                                Filters.lt(
                                                        "lastEvaluated",
                                                        Filters.storedLong(value2)))))
                        > 0L) {
                    this.platform
                            .getLogger()
                            .info(
                                    "(Alts) Pruned "
                                            + value3
                                            + " stale links below the display threshold.");
                }
            } catch (Exception exception) {
                this.platform
                        .getLogger()
                        .log(Level.WARNING, "(Alts) Stale link pruning failed.", exception);
            }
        }
    }

    @Override
    public IAltLinkRepository getRepository() {
        return this.repository;
    }

    public CommonPlatform getPlatform() {
        return this.platform;
    }

    public AltScoreCalculator getAltScoreCalculator() {
        return this.altScoreCalculator;
    }

    public LoginIndexMigration getLoginIndexMigration() {
        return this.loginIndexMigration;
    }
}
