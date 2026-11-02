package xyz.refinedev.phoenix.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.Tip;
import xyz.refinedev.phoenix.config.TipsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.ArrayList;

public class PunishmentExpiryTask implements Runnable {

    private final Phoenix plugin;

    private int count;

    private static final String TIPS_TEXT = "tips";

    public PunishmentExpiryTask(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    @Override
    public void run() {
        ArrayList values2;
        if (!(values2 = new ArrayList<>(TipsConfig.TIPS.values())).isEmpty()) {
            int value = this.count++;
            for (Player player : Bukkit.getOnlinePlayers()) {
                IProfile profile;
                if ((profile =
                                        this.plugin
                                                .getProfileHandler()
                                                .getCachedProfile(player.getUniqueId()))
                                != null
                        && profile.isSetting(TIPS_TEXT)) {
                    for (String text :
                            LocaleUtil.tr(
                                    player,
                                    ((Tip) values2.get(value % values2.size())).getLines())) {
                        player.sendMessage(Color.translate(text));
                    }
                }
            }
        }
    }
}
