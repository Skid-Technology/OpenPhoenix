package xyz.refinedev.phoenix.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.Iterator;

public class ProfileUpdateTask extends BukkitRunnable {

    private final CommonPlatform platform;

    private static final String PERMISSION = "core.2fa";

    public void run() {
        if (SettingsConfig.AUTH.ENABLED) {
            Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
            while (iterator2.hasNext()) {
                Player player;
                IProfile profile;
                if ((player = (Player) iterator2.next()).hasPermission(PERMISSION)
                        && (profile =
                                        this.platform
                                                .getProfileHandler()
                                                .getCachedProfile(player.getUniqueId()))
                                != null
                        && profile.isTwoFactor()) {
                    if (profile.getSecretKey().isEmpty() && profile.getAuthType() == null) {
                        player.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(
                                                player,
                                                MessageConfig.AUTH.NEED_TO_SETUP_AUTH_ALERT)));
                    } else {
                        player.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(
                                                player, MessageConfig.AUTH.NEED_TO_AUTH_ALERT)));
                    }
                }
            }
        }
    }

    public ProfileUpdateTask(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
