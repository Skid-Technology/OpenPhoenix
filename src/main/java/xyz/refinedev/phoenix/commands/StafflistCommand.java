package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.ServerSelectionUtils;
import xyz.refinedev.phoenix.utils.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Register(
        name = "stafflist",
        aliases = {"slist", "sl"})
@Requires("core.command.stafflist")
public class StafflistCommand {

    private final Phoenix plugin;

    public static String formatString(IProfile profile) {
        return !profile.isVanished()
                ? profile.getNameWithColorNoDisguise()
                : SettingsConfig.FORMATS
                                .STAFF_NAMETAGS
                                .VANISHED_PREFIX
                                .replace("<prefix>", "")
                                .replace("<color>", "")
                        + profile.getNameWithColorNoDisguise()
                        + SettingsConfig.FORMATS
                                .STAFF_NAMETAGS
                                .VANISHED_SUFFIX
                                .replace("<prefix>", "")
                                .replace("<color>", "");
    }

    @Command(name = "", async = true)
    public void execute(@Sender Player player) {
        Map<String, List<String>> entries =
                ServerSelectionUtils.findMap(
                        player, IProfile::isStaff, StafflistCommand::formatString);
        Iterator<String> iterator2 =
                LocaleUtil.tr(player, MessageConfig.GENERAL.STAFF_LIST.MESSAGE).iterator();
        while (iterator2.hasNext()) {
            String text;
            if ((text = (String) iterator2.next()).contains("<servers>")) {
                Iterator iterator3 = entries.entrySet().iterator();
                while (iterator3.hasNext()) {
                    Entry entry;
                    if (!((List) (entry = (Entry) iterator3.next()).getValue()).isEmpty()) {
                        String text2 = (String) entry.getKey();
                        List values = (List) entry.getValue();
                        player.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(
                                                        player,
                                                        MessageConfig.GENERAL.STAFF_LIST.FORMAT)
                                                .replace("<server>", text2)
                                                .replace(
                                                        "<players>",
                                                        StringUtils.joinCustomSpacer(
                                                                values,
                                                                MessageConfig.GENERAL
                                                                        .STAFF_LIST
                                                                        .SEPERATOR))));
                    }
                }
            } else {
                player.sendMessage(Color.translate(text));
            }
        }
    }

    public StafflistCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
