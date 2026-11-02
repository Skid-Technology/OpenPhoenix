package xyz.refinedev.phoenix.setup;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.handler.database.AbstractDatabaseHandler;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.profile.security.ISecurityUser;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.scope.Scope;
import xyz.refinedev.phoenix.utils.auth.api.Authenticator;
import xyz.refinedev.phoenix.utils.general.Tasks;
import xyz.refinedev.phoenix.utils.repository.filters.api.FilterOptions;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class OwnerClaim {

    private final Phoenix plugin;

    private volatile ClaimCode claimCode;

    private boolean enabled;

    public void initialize() {
        if (SettingsConfig.SERVER.OWNER_CLAIM) {
            List<IRank> sortedRanksReversed = this.plugin.getRankHandler().getSortedRanksReversed();
            try {
                ArrayList<UUID> values = new ArrayList<>();
                for (ISecurityUser securityUser :
                        this.plugin
                                .getSecurityHandler()
                                .getRepository()
                                .fetchAll()
                                .get(10L, TimeUnit.SECONDS)) {
                    values.add(securityUser.getUuid());
                }
                this.enabled =
                        CompletableFuture.<Boolean>supplyAsync(
                                        () -> this.matchesEnabled(values, sortedRanksReversed),
                                        AbstractDatabaseHandler.DB_EXECUTOR)
                                .get(10L, TimeUnit.SECONDS);
            } catch (TimeoutException timeoutException) {
                this.plugin
                        .getLogger()
                        .warning(
                                "Checking whether this server has an owner took over 10 seconds, so /pxclaim is off this boot.");
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
                this.plugin
                        .getLogger()
                        .warning(
                                "Checking whether this server has an owner was interrupted, so /pxclaim is off this boot.");
            } catch (Exception exception) {
                this.plugin
                        .getLogger()
                        .warning(
                                "Could not check whether this server has an owner, so /pxclaim is off this boot: "
                                        + exception);
            }
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void initializeByEnabled() {
        if (this.enabled) {
            this.claimCode = new ClaimCode(new SecureRandom());
            this.plugin.getLogger().warning("==================================================");
            this.plugin.getLogger().warning(" Nobody owns this server in Phoenix yet.");
            this.plugin
                    .getLogger()
                    .warning(" Join the server and type:  /pxclaim " + this.claimCode.getText());
            this.plugin
                    .getLogger()
                    .warning(" You get the top rank and security access, then 2FA setup.");
            this.plugin.getLogger().warning(" The code works once and changes on every restart.");
            this.plugin
                    .getLogger()
                    .warning(" After it's claimed, /pxclaim is gone from the next restart on.");
            this.plugin.getLogger().warning("==================================================");
        }
    }

    public OwnerClaim.Entry create(Player player, String text) {
        ClaimCode claimCode2 = this.claimCode;
        if (this.claimCode == null) {
            return new OwnerClaim.Entry(OwnerClaim.Outcome.NOTHING_TO_CLAIM);
        }
        List<IRank> sortedRanksReversed = this.plugin.getRankHandler().getSortedRanksReversed();
        if (!this.matchesEnabled(sortedRanksReversed)) {
            this.claimCode = null;
            return new OwnerClaim.Entry(OwnerClaim.Outcome.NOTHING_TO_CLAIM);
        }
        if (sortedRanksReversed.isEmpty()) {
            return new OwnerClaim.Entry(OwnerClaim.Outcome.NO_RANKS);
        }
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                == null) {
            return new OwnerClaim.Entry(OwnerClaim.Outcome.PROFILE_NOT_LOADED);
        }
        switch (claimCode2.findResult(text)) {
            case REJECTED:
                return new OwnerClaim.Entry(OwnerClaim.Outcome.WRONG_CODE);
            case LOCKED:
                this.claimCode = null;
                this.plugin
                        .getLogger()
                        .warning(
                                "The owner claim code was locked after too many wrong tries. Restart for a new one.");
                return new OwnerClaim.Entry(OwnerClaim.Outcome.LOCKED);
            case CLOSED:
                return new OwnerClaim.Entry(OwnerClaim.Outcome.NOTHING_TO_CLAIM);
            default:
                this.claimCode = null;
                IRank rank = (IRank) sortedRanksReversed.get(0);
                boolean matches = this.matchesEnabled(player, profile, rank);
                return new OwnerClaim.Entry(OwnerClaim.Outcome.CLAIMED, rank, matches);
        }
    }

    public int getCount() {
        ClaimCode claimCode2 = this.claimCode;
        return this.claimCode == null ? 0 : claimCode2.max();
    }

    private boolean matchesEnabled(List<IRank> values) {
        return this.matchesEnabled(
                this.plugin.getSecurityHandler().getRepository().getMap().keySet(), values);
    }

    private boolean matchesEnabled(Collection<UUID> values, List<IRank> values2) {
        ArrayList<UUID> values3 = new ArrayList<>();
        for (IRank rank : values2) {
            values3.add(rank.getUuid());
        }
        IRank defaultRank = this.plugin.getRankHandler().getDefaultRank();
        UUID consoleUuid = IProfile.getConsoleUuid();
        UUID uUID = defaultRank == null ? null : defaultRank.getUuid();
        return SetupPlan.matchesEnabled(
                values,
                consoleUuid,
                values3,
                uUID,
                result ->
                        matchesEnabledByEnabled(
                                this.plugin
                                        .getGrantHandler()
                                        .getRepository()
                                        .fetchAllSync(findFilterOptions(result))));
    }

    private static FilterOptions findFilterOptions(UUID uUID) {
        return Filters.and(Filters.eq("rankId", uUID.toString()), Filters.eq("active", true));
    }

    private static boolean matchesEnabledByEnabled(List<IGrant> values) {
        Iterator<IGrant> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            if (((IGrant) iterator2.next()).isActiveOnOneScope()) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesEnabled(Player player, IProfile profile, IRank rank) {
        UUID consoleUuid = IProfile.getConsoleUuid();
        this.plugin.getSecurityHandler().addSecurity(player.getUniqueId(), consoleUuid, true);
        this.plugin
                .getGrantHandler()
                .grant(
                        consoleUuid,
                        profile,
                        -5L,
                        true,
                        "Claimed server ownership",
                        rank,
                        Collections.singletonList(Scope.scopeScope));
        this.plugin
                .getLogger()
                .warning(
                        player.getName()
                                + " claimed ownership of this server with the claim code.");
        if (!SettingsConfig.AUTH.ENABLED) {
            return false;
        }
        Authenticator authenticator;
        if ((authenticator =
                        this.plugin
                                .getAuthHandler()
                                .getAuthenticatorByType(SettingsConfig.AUTH.DEFAULT_METHOD))
                == null) {
            return false;
        }
        Authenticator authenticator2 = authenticator;
        Tasks.runLater(
                () -> {
                    if (player.isOnline() && authenticator2.canSetup(player)) {
                        authenticator2.setup(player);
                    }
                },
                40L);
        return true;
    }

    public OwnerClaim(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    public static final class Entry {

        private final OwnerClaim.Outcome outcome;

        private final IRank rank;

        private final boolean enabled;

        private Entry(OwnerClaim.Outcome outcome) {
            this(outcome, null, false);
        }

        private Entry(OwnerClaim.Outcome outcome2, IRank rank2, boolean enabled) {
            this.outcome = outcome2;
            this.rank = rank2;
            this.enabled = enabled;
        }

        public OwnerClaim.Outcome getOutcome() {
            return this.outcome;
        }

        public IRank getRank() {
            return this.rank;
        }

        public boolean isEnabled() {
            return this.enabled;
        }
    }

    public enum Outcome {
        CLAIMED,
        WRONG_CODE,
        LOCKED,
        NOTHING_TO_CLAIM,
        NO_RANKS,
        PROFILE_NOT_LOADED;

        private static final OwnerClaim.Outcome[] $VALUES = $values();

        private static OwnerClaim.Outcome[] $values() {
            OwnerClaim.Outcome[] outcomes = new OwnerClaim.Outcome[6];
            outcomes[0] = CLAIMED;
            outcomes[1] = WRONG_CODE;
            outcomes[2] = LOCKED;
            outcomes[3] = NOTHING_TO_CLAIM;
            outcomes[4] = NO_RANKS;
            outcomes[5] = PROFILE_NOT_LOADED;
            return outcomes;
        }
    }
}
