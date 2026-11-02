package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Default;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PaginatedListClickable;
import xyz.refinedev.phoenix.utils.chat.Clickable;

import java.util.ArrayList;

@Register(
        name = "warnings",
        aliases = {"warns", "checkwarns"})
@Requires("core.command.checkwarns")
public class WarningsCommand {

    private final CommonPlatform platform;

    @Command(name = "", async = true)
    public void execute(
            @Sender CommandSender sender,
            @Option({"disguise", "multiple"}) IProfile profile,
            @Default("1") @Named("page") int value) {
        PaginatedListClickable paginatedListClickable =
                new PaginatedListClickable(
                        "Warns", profile.getNameWithColorNoDisguise(), "/checkwarns");
        ArrayList<Clickable> values = new ArrayList<>();
        for (IPunishment punishment :
                this.platform
                        .getPunishmentHandler()
                        .getSortedPunishments(profile.getUuid(), PunishmentType.WARN)) {
            String text =
                    MessageConfig.STAFF
                            .WARNS
                            .FORMAT
                            .replace(
                                    "<active>",
                                    punishment.isActive()
                                            ? MessageConfig.STAFF.WARNS.ACTIVE
                                            : MessageConfig.STAFF.WARNS.INACTIVE)
                            .replace("<reason>", punishment.getReason())
                            .replace("<date>", DateUtils.getDate(punishment.getIssuedAt()))
                            .replace("<issuedBy>", punishment.getNiceIssuedBy())
                            .replace("<expire>", punishment.getNiceExpire())
                            .replace("<duration>", punishment.getNiceDuration())
                            .replace("<id>", punishment.getPunishmentID());
            Clickable clickable = new Clickable(text);
            if (punishment.isActive()) {
                clickable.add(
                        LocaleUtil.tr(sender, MessageConfig.STAFF.WARNS.REMOVE_TEXT),
                        LocaleUtil.tr(sender, MessageConfig.STAFF.WARNS.REMOVE_HOVER)
                                .replace("<id>", punishment.getPunishmentID()),
                        "/removewarn " + punishment.getPunishmentID());
            }
            values.add(clickable);
        }
        paginatedListClickable.display(sender, value, values);
    }

    public WarningsCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
