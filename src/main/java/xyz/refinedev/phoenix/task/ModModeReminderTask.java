package xyz.refinedev.phoenix.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.xseries.messages.ActionBar;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.StringUtils;

import java.util.ArrayList;

public class ModModeReminderTask implements Runnable {

    private final CommonPlatform platform;

    @Override
    public void run() {
        if (SettingsConfig.SERVER.ACTIONBARS) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                IProfile profile;
                if ((profile =
                                this.platform
                                        .getProfileHandler()
                                        .getCachedProfile(player.getUniqueId()))
                        != null) {
                    boolean vanished = profile.isVanished();
                    boolean matches = player.hasMetadata("ModMode");
                    boolean disguised = profile.getDisguiseData().isDisguised();
                    if (vanished || matches || disguised) {
                        ArrayList<String> values = new ArrayList<>();
                        if (vanished) {
                            values.add(
                                    MessageConfig.ACTIONBARS.TYPE_COLOR
                                            + LocaleUtil.tr(
                                                    player,
                                                    MessageConfig.ACTIONBARS.TYPES.VANISHED));
                        }
                        if (matches) {
                            values.add(
                                    MessageConfig.ACTIONBARS.TYPE_COLOR
                                            + LocaleUtil.tr(
                                                    player,
                                                    MessageConfig.ACTIONBARS.TYPES.MODMODE));
                        }
                        if (disguised) {
                            values.add(
                                    MessageConfig.ACTIONBARS.TYPE_COLOR
                                            + LocaleUtil.tr(
                                                    player,
                                                    MessageConfig.ACTIONBARS.TYPES.DISGUISED));
                        }
                        ActionBar.sendActionBar(
                                player,
                                Color.translate(
                                        LocaleUtil.tr(player, MessageConfig.ACTIONBARS.CURRENTLY)
                                                .replace(
                                                        "<types>",
                                                        StringUtils.niceBuilderWithAnd(
                                                                values,
                                                                MessageConfig.ACTIONBARS
                                                                        .SEPARATOR_COLOR,
                                                                MessageConfig.ACTIONBARS.TYPE_COLOR,
                                                                " "
                                                                        + MessageConfig.GENERAL
                                                                                .GENERIC_LIST_AND
                                                                        + " "))));
                    }
                }
            }
        }
    }

    public ModModeReminderTask(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
