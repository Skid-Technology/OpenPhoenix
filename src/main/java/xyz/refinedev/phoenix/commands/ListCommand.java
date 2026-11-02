package xyz.refinedev.phoenix.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.ServerSelectionUtils;
import xyz.refinedev.phoenix.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Register(name = "list", aliases = "who")
public class ListCommand {

    private final Phoenix plugin;

    @Command(name = "staff", description = "Staff online across the network", async = true)
    @Requires("core.command.stafflist")
    public void staff(@Sender Player player) {
        this.sendMessage(
                player,
                ServerSelectionUtils.findMap(
                        player, IProfile::isStaff, StafflistCommand::formatString),
                MessageConfig.GENERAL.STAFF_LIST.MESSAGE,
                MessageConfig.GENERAL.STAFF_LIST.FORMAT,
                MessageConfig.GENERAL.STAFF_LIST.SEPERATOR);
    }

    @Command(name = "media", description = "Media players online across the network", async = true)
    @Requires("core.command.medialist")
    public void media(@Sender Player player) {
        this.sendMessage(
                player,
                ServerSelectionUtils.findMap(
                        player, IProfile::isMedia, MedialistCommand::formatString),
                MessageConfig.GENERAL.MEDIA_LIST.MESSAGE,
                MessageConfig.GENERAL.MEDIA_LIST.FORMAT,
                MessageConfig.GENERAL.MEDIA_LIST.SEPERATOR);
    }

    private void sendMessage(
            Player player,
            Map<String, List<String>> entries,
            List<String> values,
            String text,
            String text2) {
        Iterator<String> iterator2 = LocaleUtil.tr(player, values).iterator();
        while (iterator2.hasNext()) {
            String text3;
            if (!(text3 = (String) iterator2.next()).contains("<servers>")) {
                player.sendMessage(Color.translate(text3));
            } else {
                Iterator<Entry<String, List<String>>> iterator3 = entries.entrySet().iterator();
                while (iterator3.hasNext()) {
                    Entry entry;
                    if (!((List) (entry = (Entry) iterator3.next()).getValue()).isEmpty()) {
                        player.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(player, text)
                                                .replace("<server>", (CharSequence) entry.getKey())
                                                .replace(
                                                        "<players>",
                                                        StringUtils.joinCustomSpacer(
                                                                (Collection) entry.getValue(),
                                                                text2))));
                    }
                }
            }
        }
    }

    @Command(name = "")
    public void execute(@Sender CommandSender sender) {
        int value = 0;
        ArrayList<String> values = new ArrayList<>();
        Iterator<IRank> iterator2 =
                this.plugin.getRankHandler().getSortedRanksReversed().iterator();
        while (iterator2.hasNext()) {
            IRank rank;
            if ((rank = (IRank) iterator2.next()).isVisible() && rank.isScope()) {
                values.add(rank.getDisplayName());
            }
        }
        ArrayList values2 = new ArrayList();
        if (sender instanceof Player) {
            for (Player player :
                    this.plugin.getVisibilityHandler().getAllVisibleTo((Player) sender)) {
                values2.add(this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()));
            }
        } else {
            for (Player player2 : Bukkit.getOnlinePlayers()) {
                values2.add(
                        this.plugin.getProfileHandler().getCachedProfile(player2.getUniqueId()));
            }
        }
        values2.sort(Comparator.comparingInt(IProfile::getFakePriority).reversed());
        String text =
                StringUtils.joinCustomSpacer(
                        values,
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.GENERAL.LIST.SEPERATOR)));
        ArrayList<String> values3 = new ArrayList<>();
        for (IProfile profile : (Iterable<IProfile>) ((Iterable) values2)) {
            if (value >= 350) {
                break;
            }
            values3.add(
                    (profile.isVanished()
                                    ? SettingsConfig.FORMATS
                                            .STAFF_NAMETAGS
                                            .VANISHED_PREFIX
                                            .replace("<prefix>", "")
                                            .replace("<color>", "")
                                    : "")
                            + profile.getNameWithColor()
                            + (profile.isVanished()
                                    ? SettingsConfig.FORMATS
                                            .STAFF_NAMETAGS
                                            .VANISHED_SUFFIX
                                            .replace("<prefix>", "")
                                            .replace("<color>", "")
                                    : ""));
            value++;
        }
        String text2 =
                StringUtils.joinCustomSpacer(
                        values3,
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.GENERAL.LIST.SEPERATOR)));
        for (String text3 : LocaleUtil.tr(sender, MessageConfig.GENERAL.LIST.MESSAGE)) {
            sender.sendMessage(
                    Color.translate(
                            text3.replace("<players>", text2)
                                    .replace("<ranks>", text)
                                    .replace("<online>", String.valueOf(values2.size()))
                                    .replace("<max>", String.valueOf(Bukkit.getMaxPlayers()))));
        }
        if (value >= 350) {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.GENERAL.LIST.SHOWING_MAX_PLAYERS)));
        }
    }

    public ListCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
