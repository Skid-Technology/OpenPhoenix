package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Register(name = "timeline", description = "Check for how long you've been staff")
@Requires("core.command.timeline")
public class TimelineCommand {

    public static String text = "\u258c";

    private static final long time = 7L;

    @Command(name = "")
    public void execute(
            @Sender CommandSender sender,
            @Option({"disguise", "multiple"}) @Named("player") IProfile profile) {
        List<IGrant> sortedGrantsReversed = profile.getSortedGrantsReversed();
        StringBuilder stringBuilder = new StringBuilder();
        boolean matches = true;
        int value = 0;
        Calendar instance = Calendar.getInstance();
        Calendar instance2 = Calendar.getInstance();
        LinkedList values = new LinkedList();
        Iterator iterator2 = sortedGrantsReversed.iterator();
        while (iterator2.hasNext()) {
            IGrant grant;
            if ((grant = (IGrant) iterator2.next()).getRank() != null
                    && grant.getRank().isVisible()
                    && grant.getRank().isStaff()) {
                values.add(grant);
            }
        }
        if (values.isEmpty()) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.TIMELINE.NEVER_BEEN_STAFF));
        }
        instance.setTime(new Date(((IGrant) values.get(0)).getIssuedAt()));
        IGrant grant2;
        long value2 =
                (grant2 = (IGrant) values.get(values.size() - 1)).isActive()
                        ? 0L
                        : grant2.getRemovedAt();
        instance2.setTime(new Date(value2));
        for (IGrant grant3 : (Iterable<IGrant>) ((Iterable) values)) {
            stringBuilder.append(grant3.getRank().getColor()).append(text);
            for (int index = 0;
                    index
                            < Math.abs(
                                    TimeUnit.MILLISECONDS.toDays(
                                                    grant3.isActive()
                                                            ? System.currentTimeMillis()
                                                                    - grant3.getIssuedAt()
                                                            : (grant3.isPermanent()
                                                                    ? grant3.getRemovedAt()
                                                                            - grant3.getIssuedAt()
                                                                    : grant3.getDuration()))
                                            / time);
                    index++) {
                stringBuilder.append(grant3.getRank().getColor()).append(text);
            }
            if (matches) {
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.TIMELINE.JOINED)
                                        .replace("<player>", profile.getNameWithColorNoDisguise())
                                        .replace("<date>", DateUtils.getDate(grant3.getIssuedAt()))
                                        .replace("<granter>", grant3.getNiceIssuedBy())
                                        .replace("<rank>", grant3.getRank().getDisplayName())));
                matches = false;
            } else if (value > grant3.getRank().getPriority()) {
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.TIMELINE.DEMOTED)
                                        .replace("<player>", profile.getNameWithColorNoDisguise())
                                        .replace("<date>", DateUtils.getDate(grant3.getRemovedAt()))
                                        .replace("<granter>", grant3.getNiceIssuedBy())
                                        .replace("<rank>", grant3.getRank().getDisplayName())));
            } else {
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.TIMELINE.PROMOTED)
                                        .replace("<player>", profile.getNameWithColorNoDisguise())
                                        .replace("<date>", DateUtils.getDate(grant3.getIssuedAt()))
                                        .replace("<granter>", grant3.getNiceIssuedBy())
                                        .replace("<rank>", grant3.getRank().getDisplayName())));
            }
            value = grant3.getRank().getPriority();
        }
        sender.sendMessage(" ");
        sender.sendMessage(Color.translate(LocaleUtil.tr(sender, MessageConfig.TIMELINE.TIMELINE)));
        sender.sendMessage(Color.translate(stringBuilder.toString()));
        sender.sendMessage(" ");
        if (value2 != 0L && !profile.isStaff()) {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.TIMELINE.PARTED_WAYS)
                                    .replace("<player>", profile.getNameWithColorNoDisguise())
                                    .replace("<date>", DateUtils.getDate(value2))));
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.TIMELINE.HELPED_FOR)
                                    .replace("<player>", profile.getNameWithColorNoDisguise())
                                    .replace(
                                            "<time>",
                                            DateUtils.formatDateDiff(instance, instance2))));
        } else {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.TIMELINE.HELPING_FOR)
                                    .replace("<player>", profile.getNameWithColorNoDisguise())
                                    .replace(
                                            "<time>",
                                            DateUtils.formatTimeMillis(
                                                    System.currentTimeMillis()
                                                            - ((IGrant) values.get(0))
                                                                    .getIssuedAt()))));
        }
    }
}
