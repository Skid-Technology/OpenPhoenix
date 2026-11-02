package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Default;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.alt.AddressClassification;
import xyz.refinedev.phoenix.alt.AltLinkStatus;
import xyz.refinedev.phoenix.alt.IAddressStats;
import xyz.refinedev.phoenix.alt.IAltLink;
import xyz.refinedev.phoenix.alt.IAltSignal;
import xyz.refinedev.phoenix.alt.IScoreResult;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.impl.AltsMenu;
import xyz.refinedev.phoenix.menu.impl.LocationLogMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.antivpn.IAntiVPNData;
import xyz.refinedev.phoenix.profile.login.ILogin;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.utils.AltLinkFormatter;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PaginatedListClickable;
import xyz.refinedev.phoenix.utils.PunishmentUtil;
import xyz.refinedev.phoenix.utils.chat.Clickable;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Help
@Register(name = "alts", aliases = "alt")
@Requires("core.command.alts")
public class AltsCommand {

    private final CommonPlatform platform;

    private static final long timeTime = 80L;

    @Command(
            name = "",
            description = "Accounts linked to this player, grouped by confidence",
            async = true)
    public void execute(
            @Sender CommandSender sender, @Option({"disguise", "multiple"}) IProfile profile) {
        if (!GlobalConfig.SERVER.ALTS.ENABLED) {
            sender.sendMessage(Color.translate(LocaleUtil.tr(sender, MessageConfig.ALTS.DISABLED)));
        } else {
            profile.loadAlts();
            LinkedHashMap entries = new LinkedHashMap();
            for (IAltLink altLink : this.platform.getAltHandler().getLinks(profile.getUniqueId())) {
                entries.put(altLink.other(profile.getUniqueId()), altLink);
            }
            Set<UUID> values;
            int size2 = (values = AltLinkFormatter.findSet(profile, entries)).size();
            List<ILogin> logins = this.platform.getLoginHandler().getLogins(profile.getUniqueId());
            Iterator<String> iterator2 =
                    LocaleUtil.tr(sender, MessageConfig.ALTS.FORMAT).iterator();
            while (iterator2.hasNext()) {
                String text;
                if (!(text = (String) iterator2.next()).contains("<alts>")) {
                    sender.sendMessage(
                            Color.translate(
                                    text.replace("<name>", profile.getNameWithColorNoDisguise())
                                            .replace("<amount>", "" + size2)));
                } else if (values.isEmpty()) {
                    sender.sendMessage(
                            Color.translate(LocaleUtil.tr(sender, MessageConfig.ALTS.NONE)));
                } else {
                    if (values.size() > 80) {
                        sender.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(sender, MessageConfig.ALTS.TRUNCATED)
                                                .replace("<amount>", "80")
                                                .replace("<player>", profile.getName())));
                        values =
                                values.stream()
                                        .limit(timeTime)
                                        .collect(Collectors.toCollection(LinkedHashSet::new));
                    }
                    Map<UUID, List<ILogin>> loginsSync =
                            this.platform.getLoginHandler().getLoginsSync(values);
                    Map<String, List<UUID>> entries2 = new LinkedHashMap();
                    for (UUID uUID : values) {
                        entries2.computeIfAbsent(
                                        AltLinkFormatter.formatString(
                                                sender, (IAltLink) entries.get(uUID)),
                                        result -> new ArrayList<>())
                                .add(uUID);
                    }
                    for (Entry entry : entries2.entrySet()) {
                        sender.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(sender, MessageConfig.ALTS.GROUP_HEADER)
                                                .replace("<tier>", (CharSequence) entry.getKey())
                                                .replace(
                                                        "<amount>",
                                                        String.valueOf(
                                                                ((List) entry.getValue())
                                                                        .size()))));
                        Clickable clickable = new Clickable();
                        int size3 = ((List) entry.getValue()).size();
                        int value = 0;
                        for (UUID uUID2 : (Iterable<UUID>) ((Iterable) (List) entry.getValue())) {
                            value++;
                            IProfile profile2;
                            if ((profile2 = this.platform.getProfileHandler().getProfile(uUID2))
                                    != null) {
                                IAntiVPNData antiVPNData = profile2.fetchLatestAntiVPNData();
                                List<ILogin> values2;
                                if ((values2 = (List) loginsSync.get(uUID2)) != null
                                        && !values2.isEmpty()) {
                                    IProfile profile3 = profile2;
                                    ArrayList values3;
                                    if ((values3 =
                                                    FastStreams.filterArrayList(
                                                            values2,
                                                            result2 ->
                                                                    result2.getIp()
                                                                            .equals(
                                                                                    profile3
                                                                                            .getCurrentAddress())))
                                            .isEmpty()) {
                                        clickable.add(
                                                Color.translate(
                                                        PunishmentUtil.getAltColor(
                                                                        profile, profile2)
                                                                + profile2.getName()),
                                                Color.translate(
                                                        profile2.getNameWithColorNoDisguise()
                                                                + "&c has no current ip logins."),
                                                "/history " + profile2.getName());
                                        clickable.add(
                                                Color.translate(size3 == value ? "&7." : "&7, "));
                                    } else {
                                        ArrayList<String> values4 = new ArrayList<>();
                                        Iterator<String> iterator3 =
                                                LocaleUtil.tr(
                                                                sender,
                                                                MessageConfig.ALTS.HOVER_FORMAT)
                                                        .iterator();
                                        while (iterator3.hasNext()) {
                                            String text2;
                                            if ((text2 = (String) iterator3.next())
                                                    .contains("<score>")) {
                                                values4.addAll(
                                                        AltLinkFormatter.findList(
                                                                sender,
                                                                (IAltLink) entries.get(uUID2)));
                                            } else if (text2.contains("<matching>")) {
                                                String text3 =
                                                        profile2.getCurrentAddress()
                                                                        .equals(
                                                                                profile
                                                                                        .getCurrentAddress())
                                                                ? LocaleUtil.tr(
                                                                        sender,
                                                                        MessageConfig.ALTS
                                                                                .IP_MATCHING)
                                                                : LocaleUtil.tr(
                                                                        sender,
                                                                        MessageConfig.ALTS
                                                                                .IP_NOT_MATCHING);
                                                values4.add(
                                                        text3.replace(
                                                                "<player>",
                                                                profile
                                                                        .getNameWithColorNoDisguise()));
                                            } else if (text2.contains("<subnetMatch>")) {
                                                if (!profile2.getCurrentAddress()
                                                                .equals(profile.getCurrentAddress())
                                                        && profile2.getCurrentSubnet()
                                                                .equals(
                                                                        profile
                                                                                .getCurrentSubnet())) {
                                                    values4.addAll(
                                                            LocaleUtil.tr(
                                                                    sender,
                                                                    MessageConfig.ALTS
                                                                            .SUBNET_ONLY_MATCH));
                                                }
                                            } else if (text2.contains("<punishments>")) {
                                                IPunishment activeBlacklist =
                                                        profile2.getActiveBlacklist();
                                                IPunishment activeBan = profile2.getActiveBan();
                                                IPunishment activeMute = profile2.getActiveMute();
                                                if (activeBlacklist != null) {
                                                    String text4 =
                                                            activeBlacklist
                                                                            .getAddress()
                                                                            .equals(
                                                                                    profile
                                                                                            .getCurrentAddress())
                                                                    ? LocaleUtil.tr(
                                                                            sender,
                                                                            MessageConfig.ALTS
                                                                                    .PUNISHMENT_MATCHING)
                                                                    : LocaleUtil.tr(
                                                                            sender,
                                                                            MessageConfig.ALTS
                                                                                    .PUNISHMENT_NOT_MATCHING);
                                                    values4.add(
                                                            text4.replace(
                                                                            "<type>",
                                                                            LocaleUtil.tr(
                                                                                    sender,
                                                                                    MessageConfig
                                                                                            .ALTS
                                                                                            .STATUS
                                                                                            .BLACKLIST
                                                                                            .NAME))
                                                                    .replace(
                                                                            "<player>",
                                                                            profile
                                                                                    .getNameWithColorNoDisguise()));
                                                }
                                                if (activeBan != null) {
                                                    String text5 =
                                                            activeBan
                                                                            .getAddress()
                                                                            .equals(
                                                                                    profile
                                                                                            .getCurrentAddress())
                                                                    ? LocaleUtil.tr(
                                                                            sender,
                                                                            MessageConfig.ALTS
                                                                                    .PUNISHMENT_MATCHING)
                                                                    : LocaleUtil.tr(
                                                                            sender,
                                                                            MessageConfig.ALTS
                                                                                    .PUNISHMENT_NOT_MATCHING);
                                                    values4.add(
                                                            text5.replace(
                                                                            "<type>",
                                                                            LocaleUtil.tr(
                                                                                            sender,
                                                                                            MessageConfig
                                                                                                    .ALTS
                                                                                                    .STATUS
                                                                                                    .BANNED
                                                                                                    .COLOR)
                                                                                    + (activeBan
                                                                                                    .isIp()
                                                                                            ? "IP Ban"
                                                                                            : "Ban"))
                                                                    .replace(
                                                                            "<player>",
                                                                            profile
                                                                                    .getNameWithColorNoDisguise()));
                                                }
                                                if (activeMute != null) {
                                                    String text6 =
                                                            activeMute
                                                                            .getAddress()
                                                                            .equals(
                                                                                    profile
                                                                                            .getCurrentAddress())
                                                                    ? LocaleUtil.tr(
                                                                            sender,
                                                                            MessageConfig.ALTS
                                                                                    .PUNISHMENT_MATCHING)
                                                                    : LocaleUtil.tr(
                                                                            sender,
                                                                            MessageConfig.ALTS
                                                                                    .PUNISHMENT_NOT_MATCHING);
                                                    values4.add(
                                                            text6.replace(
                                                                            "<type>",
                                                                            LocaleUtil.tr(
                                                                                            sender,
                                                                                            MessageConfig
                                                                                                    .ALTS
                                                                                                    .STATUS
                                                                                                    .MUTED_ONLINE
                                                                                                    .COLOR)
                                                                                    + (activeMute
                                                                                                    .isIp()
                                                                                            ? "IP Mute"
                                                                                            : "Mute"))
                                                                    .replace(
                                                                            "<player>",
                                                                            profile
                                                                                    .getNameWithColorNoDisguise()));
                                                }
                                            } else if (text2.contains("<matchingIPInfo>")) {
                                                if (logins.isEmpty()) {
                                                    values4.add(
                                                            Color.translate(
                                                                    profile
                                                                                    .getNameWithColorNoDisguise()
                                                                            + "&c has no registered logins."));
                                                } else {
                                                    IProfile profile4 = profile2;
                                                    ArrayList values5;
                                                    if ((values5 =
                                                                    FastStreams.filterArrayList(
                                                                            logins,
                                                                            result2 ->
                                                                                    profile4.getKnownAddresses()
                                                                                            .contains(
                                                                                                    result2
                                                                                                            .getIp())))
                                                            .isEmpty()) {
                                                        values4.add(
                                                                Color.translate(
                                                                        "&cNo Matching IP Info."));
                                                    } else {
                                                        long size4 =
                                                                FastStreams.mapArrayListToSet(
                                                                                values5,
                                                                                ILogin::getIp)
                                                                        .size();
                                                        Iterator<String> iterator4 =
                                                                LocaleUtil.tr(
                                                                                sender,
                                                                                MessageConfig.ALTS
                                                                                        .MATCHING_IP_INFO)
                                                                        .iterator();
                                                        while (iterator4.hasNext()) {
                                                            String text7 =
                                                                    ((String) iterator4.next())
                                                                            .replace(
                                                                                    "<name>",
                                                                                    profile
                                                                                            .getNameWithColorNoDisguise())
                                                                            .replace(
                                                                                    "<country>",
                                                                                    antiVPNData
                                                                                                            != null
                                                                                                    && antiVPNData
                                                                                                                    .getCountry()
                                                                                                            != null
                                                                                            ? antiVPNData
                                                                                                    .getCountry()
                                                                                            : "N/A")
                                                                            .replace(
                                                                                    "<isp>",
                                                                                    antiVPNData
                                                                                                            != null
                                                                                                    && antiVPNData
                                                                                                                    .getIsp()
                                                                                                            != null
                                                                                            ? antiVPNData
                                                                                                    .getIsp()
                                                                                            : "N/A")
                                                                            .replace(
                                                                                    "<amount>",
                                                                                    String.valueOf(
                                                                                            size4))
                                                                            .replace(
                                                                                    "<logins>",
                                                                                    String.valueOf(
                                                                                            values5
                                                                                                    .size()))
                                                                            .replace(
                                                                                    "<firstLogin>",
                                                                                    DateUtils
                                                                                            .getDate(
                                                                                                    ((ILogin)
                                                                                                                    values5
                                                                                                                            .get(
                                                                                                                                    0))
                                                                                                            .getLogin()))
                                                                            .replace(
                                                                                    "<lastLogin>",
                                                                                    DateUtils
                                                                                            .getDate(
                                                                                                    ((ILogin)
                                                                                                                    values5
                                                                                                                            .get(
                                                                                                                                    values5
                                                                                                                                                    .size()
                                                                                                                                            - 1))
                                                                                                            .getLogin()));
                                                            values4.add(text7);
                                                        }
                                                    }
                                                }
                                            } else {
                                                text2 =
                                                        text2.replace(
                                                                        "<name>",
                                                                        profile2
                                                                                .getNameWithColorNoDisguise())
                                                                .replace(
                                                                        "<country>",
                                                                        antiVPNData != null
                                                                                        && antiVPNData
                                                                                                        .getCountry()
                                                                                                != null
                                                                                ? antiVPNData
                                                                                        .getCountry()
                                                                                : "N/A")
                                                                .replace(
                                                                        "<isp>",
                                                                        antiVPNData != null
                                                                                        && antiVPNData
                                                                                                        .getIsp()
                                                                                                != null
                                                                                ? antiVPNData
                                                                                        .getIsp()
                                                                                : "N/A")
                                                                .replace(
                                                                        "<sibling>",
                                                                        AltLinkFormatter.getStatus(
                                                                                        (IAltLink)
                                                                                                entries
                                                                                                        .get(
                                                                                                                uUID2))
                                                                                ? LocaleUtil.tr(
                                                                                        sender,
                                                                                        MessageConfig
                                                                                                .ALTS
                                                                                                .TRUSTED_TAG)
                                                                                : "")
                                                                .replace(
                                                                        "<lastSeen>",
                                                                        profile2.getLastSeenAgo(
                                                                                values2))
                                                                .replace(
                                                                        "<logins>",
                                                                        String.valueOf(
                                                                                values3.size()))
                                                                .replace(
                                                                        "<firstLogin>",
                                                                        DateUtils.getDate(
                                                                                ((ILogin)
                                                                                                values3
                                                                                                        .get(
                                                                                                                0))
                                                                                        .getLogin()))
                                                                .replace(
                                                                        "<lastLogin>",
                                                                        DateUtils.getDate(
                                                                                ((ILogin)
                                                                                                values3
                                                                                                        .get(
                                                                                                                values3
                                                                                                                                .size()
                                                                                                                        - 1))
                                                                                        .getLogin()));
                                                values4.add(text2);
                                            }
                                        }
                                        clickable.add(
                                                Color.translate(
                                                        PunishmentUtil.getAltColor(
                                                                        profile, profile2)
                                                                + profile2.getName()),
                                                Color.translate(BukkitAPI.getKickMessage(values4)),
                                                "/history " + profile2.getName());
                                        clickable.add(
                                                Color.translate(size3 == value ? "&7." : "&7, "));
                                    }
                                } else {
                                    clickable.add(
                                            Color.translate(
                                                    PunishmentUtil.getAltColor(profile, profile2)
                                                            + profile2.getName()),
                                            Color.translate(
                                                    profile2.getNameWithColorNoDisguise()
                                                            + "&c has no logins."),
                                            "/history " + profile2.getName());
                                    clickable.add(Color.translate(size3 == value ? "&7." : "&7, "));
                                }
                            }
                        }
                        clickable.sendToPlayer(sender);
                    }
                }
            }
        }
    }

    @Command(
            name = "ips",
            description = "Every address this account has used, and who else used them",
            async = true)
    @Requires("core.command.alts.ips")
    public void ips(
            @Sender CommandSender sender, @Option({"disguise", "multiple"}) IProfile profile) {
        Set<String> values;
        if ((values = profile.getKnownAddresses()) != null && !values.isEmpty()) {
            List<ILogin> values2 =
                    Phoenix.getInstance()
                            .getLoginHandler()
                            .getRepository()
                            .fetchAllSync(Filters.in("ip", new ArrayList<>(values)));
            LinkedHashMap entries = new LinkedHashMap();
            for (String text : values) {
                entries.put(text, new HashMap());
            }
            for (ILogin login : values2) {
                Map<UUID, ILogin> entries2;
                ILogin login2;
                if ((entries2 = (Map) entries.get(login.getIp())) != null
                        && ((login2 = (ILogin) entries2.get(login.getTarget())) == null
                                || login.getLogin() > login2.getLogin())) {
                    entries2.put(login.getTarget(), login);
                }
            }
            SimpleDateFormat simpleDateFormat;
            try {
                simpleDateFormat = new SimpleDateFormat(MessageConfig.SEARCH_IPS.DATE_FORMAT);
            } catch (IllegalArgumentException illegalArgumentException) {
                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            }
            sender.sendMessage(
                    Color.translate(LocaleUtil.tr(sender, MessageConfig.SEARCH_IPS.HEADER)));
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.SEARCH_IPS.TITLE)
                                    .replace("<player>", profile.getName())));
            Iterator iterator2 = entries.entrySet().iterator();
            while (iterator2.hasNext()) {
                Entry entry;
                String text2 = (String) (entry = (Entry) iterator2.next()).getKey();
                Map entries3 = (Map) entry.getValue();
                sender.sendMessage("");
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.SEARCH_IPS.IP_HEADER)
                                        .replace("<ip>", text2)));
                if (entries3.isEmpty()) {
                    sender.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(sender, MessageConfig.SEARCH_IPS.NO_LOGINS)));
                } else {
                    ArrayList<Entry> values3;
                    (values3 = new ArrayList(entries3.entrySet()))
                            .sort(
                                    (result, result2) ->
                                            Long.compare(
                                                    ((ILogin) result2.getValue()).getLogin(),
                                                    ((ILogin) result.getValue()).getLogin()));
                    for (Entry entry2 : values3) {
                        String text3;
                        if ((text3 =
                                        Phoenix.getInstance()
                                                .getUuidCache()
                                                .getName((UUID) entry2.getKey()))
                                == null) {
                            text3 = ((UUID) entry2.getKey()).toString();
                        }
                        String text4 =
                                simpleDateFormat.format(
                                        new Date(((ILogin) entry2.getValue()).getLogin()));
                        String text5 =
                                ((UUID) entry2.getKey()).equals(profile.getUuid())
                                        ? MessageConfig.SEARCH_IPS.ENTRY_COLOR_TARGET
                                        : MessageConfig.SEARCH_IPS.ENTRY_COLOR_OTHER;
                        sender.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(sender, MessageConfig.SEARCH_IPS.ENTRY)
                                                .replace("<color>", text5)
                                                .replace("<name>", text3)
                                                .replace("<lastSeen>", text4)));
                    }
                }
            }
            sender.sendMessage(
                    Color.translate(LocaleUtil.tr(sender, MessageConfig.SEARCH_IPS.FOOTER)));
        } else {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.SEARCH_IPS.NO_IPS)
                            .replace("<player>", profile.getNameWithColorNoDisguise()));
        }
    }

    @Command(
            name = "locations",
            description = "Where this account has connected from over time",
            async = true)
    @Requires("core.command.alts.locations")
    public void locations(
            @Sender Player player, @Option({"disguise", "multiple"}) IProfile profile) {
        List<IAntiVPNData> values =
                Phoenix.getInstance()
                        .getAntiVPNHandler()
                        .fetchVpnDataRedis(profile.getUuid())
                        .stream()
                        .sorted(Comparator.comparingLong(IAntiVPNData::getTime).reversed())
                        .collect(Collectors.toList());
        Phoenix.getInstance()
                .getMenuHandler()
                .openMenuSync(new LocationLogMenu(profile, values), player);
    }

    @Command(
            name = "lookup",
            description = "Which accounts have used a given address",
            async = true)
    @Requires("core.command.alts.lookup")
    public void lookup(
            @Sender CommandSender sender,
            @Named("ip") String text,
            @Default("1") @Named("page") int value) {
        PaginatedListClickable paginatedListClickable =
                new PaginatedListClickable("Profiles", null, "/alts lookup " + text);
        String text2 = Phoenix.getInstance().getHashHandler().hash(text);
        ArrayList<Clickable> values = new ArrayList<>();
        for (IProfile profile :
                Phoenix.getInstance()
                        .getProfileHandler()
                        .getRepository()
                        .fetchAllSync(Filters.eq("currentAddress", text2))) {
            Clickable clickable;
            (clickable = new Clickable())
                    .add(LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_LIST_FORMAT));
            clickable.add(profile.getNameWithColorNoDisguise());
            clickable.add(
                    Phoenix.getInstance().getNetworkHandler().isOnline(profile.getUuid())
                            ? " &a(Online)"
                            : " &c(Offline)");
            values.add(clickable);
        }
        paginatedListClickable.display(sender, value, values);
    }

    @Command(
            name = "trust",
            description = "Mark two accounts as known siblings, not alts",
            async = true)
    @Requires("core.command.alts.trust")
    public void trust(
            @Sender CommandSender sender,
            @Option({"disguise", "multiple"}) IProfile profile,
            @Option({"disguise", "multiple"}) IProfile profile2) {
        this.platform
                .getAltHandler()
                .setStatus(profile.getUniqueId(), profile2.getUniqueId(), AltLinkStatus.TRUSTED);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.ALTS.TRUST.TRUSTED)
                                .replace("<first>", profile.getNameWithColorNoDisguise())
                                .replace("<second>", profile2.getNameWithColorNoDisguise())));
    }

    @Command(name = "untrust", description = "Undo a trust between two accounts", async = true)
    @Requires("core.command.alts.untrust")
    public void untrust(
            @Sender CommandSender sender,
            @Option({"disguise", "multiple"}) IProfile profile,
            @Option({"disguise", "multiple"}) IProfile profile2) {
        this.platform
                .getAltHandler()
                .setStatus(profile.getUniqueId(), profile2.getUniqueId(), AltLinkStatus.ACTIVE);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.ALTS.TRUST.UNTRUSTED)
                                .replace("<first>", profile.getNameWithColorNoDisguise())
                                .replace("<second>", profile2.getNameWithColorNoDisguise())));
    }

    @Command(
            name = "suppress",
            description = "Hide the link between two accounts from alt lists",
            async = true)
    @Requires("core.command.alts.suppress")
    public void suppress(
            @Sender CommandSender sender,
            @Option({"disguise", "multiple"}) IProfile profile,
            @Option({"disguise", "multiple"}) IProfile profile2) {
        this.platform
                .getAltHandler()
                .setStatus(profile.getUniqueId(), profile2.getUniqueId(), AltLinkStatus.SUPPRESSED);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.ALTS.TRUST.SUPPRESSED)
                                .replace("<first>", profile.getNameWithColorNoDisguise())
                                .replace("<second>", profile2.getNameWithColorNoDisguise())));
    }

    @Command(
            name = "suppressed",
            description = "Links hidden from this player's alt list",
            async = true)
    @Requires("core.command.alts.suppressed")
    public void suppressed(
            @Sender CommandSender sender,
            @Option({"disguise", "multiple"}) IProfile profile,
            @Default("1") @Named("page") int value) {
        ArrayList<Clickable> values = new ArrayList<>();
        Iterator<IAltLink> iterator2 =
                this.platform.getAltHandler().getLinks(profile.getUniqueId()).iterator();
        while (iterator2.hasNext()) {
            IAltLink altLink;
            if ((altLink = (IAltLink) iterator2.next()).getStatus() == AltLinkStatus.SUPPRESSED) {
                UUID uUID = altLink.other(profile.getUniqueId());
                IProfile profile2;
                String text =
                        (profile2 = this.platform.getProfileHandler().getProfile(uUID)) == null
                                ? Phoenix.getInstance().getUuidCache().getName(uUID)
                                : profile2.getName();
                String text2 =
                        profile2 == null
                                ? (text == null ? uUID.toString() : text)
                                : profile2.getNameWithColorNoDisguise();
                Clickable clickable;
                (clickable = new Clickable())
                        .add(
                                Color.translate(
                                        LocaleUtil.tr(
                                                        sender,
                                                        MessageConfig.ALTS.SUPPRESSED_LIST.ENTRY)
                                                .replace("<name>", text2)
                                                .replace(
                                                        "<score>",
                                                        String.valueOf(altLink.getScore()))
                                                .replace(
                                                        "<tier>",
                                                        AltLinkFormatter.tr(
                                                                sender, altLink.getScore()))),
                                Color.translate(
                                        BukkitAPI.getKickMessage(
                                                AltLinkFormatter.findList(sender, altLink))),
                                text == null
                                        ? null
                                        : "/alts debug " + profile.getName() + " " + text);
                values.add(clickable);
            }
        }
        if (values.isEmpty()) {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.ALTS.SUPPRESSED_LIST.NONE)
                                    .replace("<player>", profile.getNameWithColorNoDisguise())));
        } else {
            new PaginatedListClickable(
                            "Suppressed Links", null, "/alts suppressed " + profile.getName())
                    .display(sender, value, values);
        }
    }

    @Command(
            name = "address",
            description = "Details on the address this account is using",
            async = true)
    @Requires("core.command.alts.address")
    public void address(
            @Sender CommandSender sender, @Option({"disguise", "multiple"}) IProfile profile) {
        String text;
        if ((text = profile.getCurrentAddress()) != null && !text.isEmpty()) {
            IAddressStats orEmpty = this.platform.getAddressStatsHandler().getOrEmpty(text);
            sender.sendMessage(
                    Color.translate(LocaleUtil.tr(sender, MessageConfig.ALTS.ADDRESS.HEADER)));
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.ALTS.ADDRESS.TITLE)
                                    .replace("<player>", profile.getNameWithColorNoDisguise())));
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.ALTS.ADDRESS.ACCOUNTS)
                                    .replace("<accounts>", String.valueOf(orEmpty.getAccounts()))));
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.ALTS.ADDRESS.CLASSIFICATION)
                                    .replace(
                                            "<classification>",
                                            String.valueOf(orEmpty.getClassification()))));
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.ALTS.ADDRESS.FIRST_SEEN)
                                    .replace(
                                            "<firstSeen>",
                                            DateUtils.getDate(orEmpty.getFirstSeen()))));
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.ALTS.ADDRESS.LAST_SEEN)
                                    .replace(
                                            "<lastSeen>",
                                            DateUtils.getDate(orEmpty.getLastSeen()))));
            if (orEmpty.isVpn()) {
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.ALTS.ADDRESS.VPN_FLAG)));
            }
            if (orEmpty.isMobile()) {
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.ALTS.ADDRESS.MOBILE_FLAG)));
            }
            if (orEmpty.getIsp() != null) {
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.ALTS.ADDRESS.ISP)
                                        .replace("<isp>", orEmpty.getIsp())));
            }
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.ALTS.ADDRESS.FOOTNOTE)
                                    .replace(
                                            "<sharedAt>",
                                            String.valueOf(
                                                    GlobalConfig.SERVER
                                                            .ALTS
                                                            .SHARED_ADDRESS
                                                            .AUTO_CLASSIFY_AT))));
            sender.sendMessage(
                    Color.translate(LocaleUtil.tr(sender, MessageConfig.ALTS.ADDRESS.HEADER)));
        } else {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.ALTS.ADDRESS.NO_ADDRESS)
                                    .replace("<player>", profile.getNameWithColorNoDisguise())));
        }
    }

    @Command(
            name = "address force-shared",
            description = "Treat this account's address as shared, like a school or VPN",
            async = true)
    @Requires("core.command.alts.address.force-shared")
    public void addressForceShared(
            @Sender CommandSender sender, @Option({"disguise", "multiple"}) IProfile profile) {
        this.sendMessage(
                sender,
                profile,
                AddressClassification.FORCED_SHARED,
                MessageConfig.ALTS.ADDRESS.FORCED_SHARED);
    }

    @Command(
            name = "address force-trusted",
            description = "Treat this account's address as one trusted household",
            async = true)
    @Requires("core.command.alts.address.force-trusted")
    public void addressForceTrusted(
            @Sender CommandSender sender, @Option({"disguise", "multiple"}) IProfile profile) {
        this.sendMessage(
                sender,
                profile,
                AddressClassification.FORCED_TRUSTED,
                MessageConfig.ALTS.ADDRESS.FORCED_TRUSTED);
    }

    @Command(
            name = "address clear",
            description = "Drop a forced classification off this account's address",
            async = true)
    @Requires("core.command.alts.address.clear")
    public void addressClear(
            @Sender CommandSender sender, @Option({"disguise", "multiple"}) IProfile profile) {
        this.sendMessage(
                sender, profile, AddressClassification.UNKNOWN, MessageConfig.ALTS.ADDRESS.CLEARED);
    }

    private void sendMessage(
            CommandSender sender,
            IProfile profile,
            AddressClassification addressClassification,
            String text) {
        String text2;
        if ((text2 = profile.getCurrentAddress()) != null && !text2.isEmpty()) {
            this.platform
                    .getAddressStatsHandler()
                    .forceClassification(text2, addressClassification);
            sender.sendMessage(Color.translate(text));
        } else {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.ALTS.ADDRESS.NO_ADDRESS)
                                    .replace("<player>", profile.getNameWithColorNoDisguise())));
        }
    }

    @Command(
            name = "debug",
            description = "Why two accounts scored as linked, signal by signal",
            async = true)
    @Requires("core.command.alts.debug")
    public void debug(
            @Sender CommandSender sender,
            @Option({"disguise", "multiple"}) IProfile profile,
            @Option({"disguise", "multiple"}) IProfile profile2) {
        if (!GlobalConfig.SERVER.ALTS.ENABLED) {
            sender.sendMessage(Color.translate(LocaleUtil.tr(sender, MessageConfig.ALTS.DISABLED)));
        } else if (profile.getUniqueId().equals(profile2.getUniqueId())) {
            sender.sendMessage(
                    Color.translate(LocaleUtil.tr(sender, MessageConfig.ALTS.DEBUG.SAME_ACCOUNT)));
        } else {
            IScoreResult scoreResult =
                    this.platform
                            .getAltHandler()
                            .evaluate(profile.getUniqueId(), profile2.getUniqueId());
            sender.sendMessage(
                    Color.translate(LocaleUtil.tr(sender, MessageConfig.ALTS.DEBUG.HEADER)));
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.ALTS.DEBUG.TITLE)
                                    .replace("<first>", profile.getNameWithColorNoDisguise())
                                    .replace("<second>", profile2.getNameWithColorNoDisguise())));
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.ALTS.DEBUG.TOTAL)
                                    .replace("<score>", String.valueOf(scoreResult.getScore()))
                                    .replace(
                                            "<tier>",
                                            AltLinkFormatter.formatString(
                                                    scoreResult.getScore()))));
            sender.sendMessage("");
            if (scoreResult.getSignals().isEmpty()) {
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.ALTS.DEBUG.NO_EVIDENCE)));
            } else {
                for (IAltSignal altSignal : scoreResult.getSignals()) {
                    sender.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(sender, MessageConfig.ALTS.DEBUG.SIGNAL)
                                            .replace("<signal>", altSignal.getName())
                                            .replace(
                                                    "<points>",
                                                    AltLinkFormatter.formatString(
                                                            altSignal.getPoints()))
                                            .replace(
                                                    "<detail>",
                                                    altSignal.getDetail() == null
                                                            ? ""
                                                            : altSignal.getDetail())));
                }
            }
            sender.sendMessage("");
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.ALTS.DEBUG.THRESHOLDS)
                                    .replace(
                                            "<display>",
                                            String.valueOf(
                                                    GlobalConfig.SERVER.ALTS.THRESHOLDS.DISPLAY))
                                    .replace(
                                            "<link>",
                                            String.valueOf(
                                                    GlobalConfig.SERVER.ALTS.THRESHOLDS.LINK))
                                    .replace(
                                            "<alert>",
                                            String.valueOf(
                                                    GlobalConfig.SERVER.ALTS.THRESHOLDS.ALERT))
                                    .replace(
                                            "<enforce>",
                                            String.valueOf(
                                                    GlobalConfig.SERVER.ALTS.THRESHOLDS.ENFORCE))));
            String text;
            if (scoreResult.getScore() >= GlobalConfig.SERVER.ALTS.THRESHOLDS.ENFORCE) {
                text = MessageConfig.ALTS.DEBUG.VERDICT_ENFORCE;
            } else if (scoreResult.getScore() >= GlobalConfig.SERVER.ALTS.THRESHOLDS.ALERT) {
                text = MessageConfig.ALTS.DEBUG.VERDICT_ALERT;
            } else if (scoreResult.getScore() >= GlobalConfig.SERVER.ALTS.THRESHOLDS.DISPLAY) {
                text = MessageConfig.ALTS.DEBUG.VERDICT_DISPLAY;
            } else {
                text = MessageConfig.ALTS.DEBUG.VERDICT_NONE;
            }
            sender.sendMessage(Color.translate(text));
            sender.sendMessage(
                    Color.translate(LocaleUtil.tr(sender, MessageConfig.ALTS.DEBUG.HEADER)));
        }
    }

    @Command(name = "menu", description = "Browse this player's alts in a menu", async = true)
    @Requires("core.command.alts.menu")
    public void menu(@Sender Player player, @Option({"disguise", "multiple"}) IProfile profile) {
        if (!GlobalConfig.SERVER.ALTS.ENABLED) {
            player.sendMessage(Color.translate(LocaleUtil.tr(player, MessageConfig.ALTS.DISABLED)));
        } else {
            profile.loadAlts();
            LinkedHashMap entries = new LinkedHashMap();
            for (IAltLink altLink :
                    Phoenix.getInstance().getAltHandler().getLinks(profile.getUniqueId())) {
                entries.put(altLink.other(profile.getUniqueId()), altLink);
            }
            Set<UUID> values = AltLinkFormatter.findSet(profile, entries);
            Set profiles = Phoenix.getInstance().getProfileHandler().getProfiles(values);
            List<ILogin> logins =
                    Phoenix.getInstance().getLoginHandler().getLogins(profile.getUniqueId());
            Map<UUID, List<ILogin>> loginsSync =
                    Phoenix.getInstance().getLoginHandler().getLoginsSync(values);
            Map<UUID, IAntiVPNData> entries2 =
                    Phoenix.getInstance().getAntiVPNHandler().fetchLatestVpnDataRedis(profiles);
            Phoenix.getInstance()
                    .getMenuHandler()
                    .openMenuAsync(
                            new AltsMenu(profile, profiles, logins, loginsSync, entries2, entries),
                            player);
        }
    }

    public AltsCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
