package xyz.refinedev.phoenix.hook.vault;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;

public class PhoenixVaultChat extends Chat {

    private final Phoenix plugin;

    private static final String name = "phoenix";

    public PhoenixVaultChat(Permission permission, Phoenix phoenix) {
        super(permission);
        this.plugin = phoenix;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return true;
    }

    public String getPlayerPrefix(String text, String text2) {
        if (text2 == null) {
            return "";
        } else {
            Player player;
            if ((player = this.plugin.getServer().getPlayer(text2)) != null) {
                IProfile profile;
                return (profile =
                                        Phoenix.getInstance()
                                                .getProfileHandler()
                                                .getCachedProfile(player.getUniqueId()))
                                == null
                        ? ""
                        : profile.getHighestRank().getPrefix();
            } else {
                return "";
            }
        }
    }

    public void setPlayerPrefix(String text, String text2, String text3) {}

    public String getPlayerSuffix(String text, String text2) {
        if (text2 == null) {
            return "";
        } else {
            Player player;
            if ((player = this.plugin.getServer().getPlayer(text2)) != null) {
                IProfile profile;
                return (profile =
                                        Phoenix.getInstance()
                                                .getProfileHandler()
                                                .getCachedProfile(player.getUniqueId()))
                                == null
                        ? ""
                        : profile.getHighestRank().getSuffix();
            } else {
                return "";
            }
        }
    }

    public void setPlayerSuffix(String text, String text2, String text3) {}

    public String getGroupPrefix(String text, String text2) {
        if (text2 == null) {
            return "";
        }
        IRank rank;
        return (rank = this.plugin.getRankHandler().getRank(text2)) != null
                ? rank.getPrefix()
                : null;
    }

    public void setGroupPrefix(String text, String text2, String text3) {}

    public String getGroupSuffix(String text, String text2) {
        if (text2 == null) {
            return "";
        }
        IRank rank;
        return (rank = this.plugin.getRankHandler().getRank(text2)) != null
                ? rank.getSuffix()
                : null;
    }

    public void setGroupSuffix(String text, String text2, String text3) {}

    public int getPlayerInfoInteger(String text, String text2, String text3, int value) {
        return 0;
    }

    public void setPlayerInfoInteger(String text, String text2, String text3, int value) {}

    public int getGroupInfoInteger(String text, String text2, String text3, int value) {
        return 0;
    }

    public void setGroupInfoInteger(String text, String text2, String text3, int value) {}

    public double getPlayerInfoDouble(String text, String text2, String text3, double value) {
        return 0.0;
    }

    public void setPlayerInfoDouble(String text, String text2, String text3, double value) {}

    public double getGroupInfoDouble(String text, String text2, String text3, double value) {
        return 0.0;
    }

    public void setGroupInfoDouble(String text, String text2, String text3, double value) {}

    public boolean getPlayerInfoBoolean(String text, String text2, String text3, boolean matches) {
        return false;
    }

    public void setPlayerInfoBoolean(String text, String text2, String text3, boolean matches) {}

    public boolean getGroupInfoBoolean(String text, String text2, String text3, boolean matches) {
        return false;
    }

    public void setGroupInfoBoolean(String text, String text2, String text3, boolean matches) {}

    public String getPlayerInfoString(String text, String text2, String text3, String text4) {
        return null;
    }

    public void setPlayerInfoString(String text, String text2, String text3, String text4) {}

    public String getGroupInfoString(String text, String text2, String text3, String text4) {
        return null;
    }

    public void setGroupInfoString(String text, String text2, String text3, String text4) {}
}
