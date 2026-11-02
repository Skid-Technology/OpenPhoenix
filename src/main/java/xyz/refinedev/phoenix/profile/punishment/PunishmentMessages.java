package xyz.refinedev.phoenix.profile.punishment;

import org.bukkit.command.CommandSender;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;

public final class PunishmentMessages {

    public static void sendMessage(
            CommonPlatform commonPlatform,
            CommandSender sender,
            PunishmentType punishmentType,
            String text,
            String text2,
            boolean matches) {
        if (matches) {
            long value =
                    commonPlatform
                            .getPunishmentHandler()
                            .getRepository()
                            .deleteSync(Filters.eq("punishmentType", punishmentType.name()));
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, text)
                                    .replace("<amount>", String.valueOf(value))));
        } else {
            for (String text3 : LocaleUtil.tr(sender, MessageConfig.PUNISHMENT.WIPE_CONFIRM)) {
                sender.sendMessage(
                        Color.translate(
                                text3.replace("<type>", punishmentType.getName().toLowerCase())
                                        .replace("<command>", text2)));
            }
        }
    }

    private PunishmentMessages() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
