package xyz.refinedev.phoenix.hook.vault;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.scope.Scope;
import xyz.refinedev.phoenix.utils.FastStreams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PhoenixVaultPermission extends Permission {

    private final Phoenix plugin;

    private static final long time = -5L;

    public PhoenixVaultPermission(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    public String getName() {
        return "phoenix";
    }

    public boolean isEnabled() {
        return true;
    }

    public boolean hasSuperPermsCompat() {
        return true;
    }

    public boolean playerHas(String text, String text2, String text3) {
        Player player;
        return (player = this.plugin.getServer().getPlayer(text2)) != null
                ? player.hasPermission(text3)
                : false;
    }

    public boolean playerAdd(String text, String text2, String text3) {
        Player player;
        if ((player = this.plugin.getServer().getPlayer(text2)) != null) {
            this.plugin
                    .getServer()
                    .dispatchCommand(
                            this.plugin.getServer().getConsoleSender(),
                            "user perm add " + player.getName() + " " + text3);
            return true;
        } else {
            return false;
        }
    }

    public boolean playerRemove(String text, String text2, String text3) {
        Player player;
        if ((player = this.plugin.getServer().getPlayer(text2)) != null) {
            this.plugin
                    .getServer()
                    .dispatchCommand(
                            this.plugin.getServer().getConsoleSender(),
                            "user perm delete " + player.getName() + " " + text3);
            return true;
        } else {
            return false;
        }
    }

    public boolean groupHas(String text, String text2, String text3) {
        IRank rank;
        return (rank = this.plugin.getRankHandler().getRank(text2)) != null
                ? rank.hasPermission(text3)
                : false;
    }

    public boolean groupAdd(String text, String text2, String text3) {
        this.plugin
                .getServer()
                .dispatchCommand(
                        this.plugin.getServer().getConsoleSender(),
                        "rank perm add " + text2 + " " + text3);
        return true;
    }

    public boolean groupRemove(String text, String text2, String text3) {
        this.plugin
                .getServer()
                .dispatchCommand(
                        this.plugin.getServer().getConsoleSender(),
                        "rank perm delete " + text2 + " " + text3);
        return true;
    }

    public boolean playerInGroup(String text, String text2, String text3) {
        Player player;
        if ((player = this.plugin.getServer().getPlayer(text2)) == null) {
            return false;
        } else if (SettingsConfig.SERVER.LUCKPERMS_GROUP_PERMISSION_EMULATOR
                && player.hasPermission("group." + text3)) {
            return true;
        } else {
            IRank rank;
            if ((rank = this.plugin.getRankHandler().getRank(text3)) != null) {
                IProfile profile;
                return (profile =
                                        Phoenix.getInstance()
                                                .getProfileHandler()
                                                .getCachedProfile(player.getUniqueId()))
                                == null
                        ? false
                        : profile.hasRank(rank);
            } else {
                return false;
            }
        }
    }

    public boolean playerAddGroup(String text, String text2, String text3) {
        Player player = this.plugin.getServer().getPlayer(text2);
        IRank rank = this.plugin.getRankHandler().getRank(text3);
        if (player != null && rank != null) {
            IProfile profile;
            if ((profile =
                            Phoenix.getInstance()
                                    .getProfileHandler()
                                    .getCachedProfile(player.getUniqueId()))
                    == null) {
                return false;
            }
            Phoenix.getInstance()
                    .getGrantHandler()
                    .grant(
                            IProfile.getConsoleUuid(),
                            profile,
                            time,
                            true,
                            "Added by Vault",
                            rank,
                            Collections.singletonList(Scope.scopeScope));
        }
        return false;
    }

    public boolean playerRemoveGroup(String text, String text2, String text3) {
        Player player = this.plugin.getServer().getPlayer(text2);
        IRank rank = this.plugin.getRankHandler().getRank(text3);
        if (player != null && rank != null) {
            IProfile profile;
            if ((profile =
                            Phoenix.getInstance()
                                    .getProfileHandler()
                                    .getCachedProfile(player.getUniqueId()))
                    == null) {
                return false;
            }
            IGrant grant;
            if ((grant = profile.getGrantAllScopes(rank)) == null) {
                return false;
            }
            Phoenix.getInstance()
                    .getGrantHandler()
                    .ungrant(IProfile.getConsoleUuid(), profile, grant, "Removed by Vault");
        }
        return false;
    }

    public String[] getPlayerGroups(String text, String text2) {
        Player player;
        if ((player = this.plugin.getServer().getPlayer(text2)) == null) {
            return new String[0];
        }
        ArrayList<String> values =
                FastStreams.mapArrayList(
                        Phoenix.getInstance()
                                .getGrantHandler()
                                .getActiveGrants(player.getUniqueId()),
                        result -> result.getRank().getName());
        if (SettingsConfig.SERVER.LUCKPERMS_GROUP_PERMISSION_EMULATOR) {
            IProfile profile;
            if ((profile =
                            Phoenix.getInstance()
                                    .getProfileHandler()
                                    .getCachedProfile(player.getUniqueId()))
                    == null) {
                return values.toArray(new String[0]);
            }
            Iterator<String> iterator2 = profile.getAttachmentPermissions().keySet().iterator();
            while (iterator2.hasNext()) {
                String text3;
                if ((text3 = (String) iterator2.next()).startsWith("group.")) {
                    values.add(text3.replace("group.", ""));
                }
            }
        }
        return values.toArray(new String[0]);
    }

    public String getPrimaryGroup(String text, String text2) {
        Player player;
        if ((player = this.plugin.getServer().getPlayer(text2)) != null) {
            IProfile profile;
            return (profile =
                                    Phoenix.getInstance()
                                            .getProfileHandler()
                                            .getCachedProfile(player.getUniqueId()))
                            == null
                    ? ""
                    : profile.getHighestRank().getName();
        } else {
            return "";
        }
    }

    public String[] getGroups() {
        List<IRank> values;
        return FastStreams.mapArrayList(
                        values = this.plugin.getRankHandler().getAvailableRanks(), IRank::getName)
                .toArray(new String[values.size()]);
    }

    public boolean hasGroupSupport() {
        return true;
    }
}
