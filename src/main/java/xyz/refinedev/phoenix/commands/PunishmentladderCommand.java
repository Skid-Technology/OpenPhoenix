package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Default;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.commands.api.annotations.DurationInput;
import xyz.refinedev.phoenix.commands.provider.DurationProvider;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.packet.impl.punishments.PunishmentLadderUpdatePacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadderType;
import xyz.refinedev.phoenix.profile.punishment.ladder.PunishmentLadderType;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.Duration;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PaginatedListClickable;
import xyz.refinedev.phoenix.utils.PunishmentUtil;
import xyz.refinedev.phoenix.utils.TimeConstants;
import xyz.refinedev.phoenix.utils.chat.Clickable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Help
@Register(
        name = "punishmentladder",
        aliases = {"punishmentladders", "pladders", "pladder"})
@Requires("core.command.punishmentladder")
public class PunishmentladderCommand {

    private final CommonPlatform platform;

    @Command(name = "punish", async = true)
    @Requires("core.command.punishmentladder.punish")
    public void punish(
            @Sender CommandSender sender,
            @Option({"disguise", "multiple"}) @Named("player") IProfile profile,
            IPunishmentLadder punishmentLadder,
            @Optional @Named("silent") Boolean matches) {
        if (PunishmentUtil.canPunish(sender, profile, new AtomicBoolean(false))) {
            this.platform.getModUtilsHook().setFrozen(profile.getUniqueId(), false);
            List<IPunishment> punishments = profile.getPunishments();
            IPunishmentLadderType nextLadder = punishmentLadder.getNextLadder(punishments);
            if (profile.hasActivePunishment(nextLadder.getType())
                    && nextLadder.getType() != PunishmentType.KICK
                    && nextLadder.getType() != PunishmentType.WARN) {
                throw new ExitMessage(
                        LocaleUtil.tr(sender, MessageConfig.PUNISHMENT.ALREADY_PUNISHED)
                                .replace("<player>", profile.getNameWithColorNoDisguise())
                                .replace("<type>", nextLadder.getType().getPunished()));
            }
            UUID uUID =
                    sender instanceof Player
                            ? ((Player) sender).getUniqueId()
                            : IProfile.getConsoleUuid();
            boolean matches2 =
                    matches != null
                            ? matches
                            : SettingsConfig.SERVER.PUNISHMENTS.AUTO_SILENT_PUNISHMENT_LADDERS;
            IPunishment punishment =
                    this.platform
                            .getPunishmentLadderHandler()
                            .createPunishment(uUID, profile, punishmentLadder, matches2);
            PunishmentUtil.issue(sender, punishment);
        }
    }

    @Command(name = "create")
    @Requires("core.command.punishmentladder.create")
    public void create(
            @Sender CommandSender sender,
            @Named("id") String text,
            @Named("requireChatSnapshot") boolean matches,
            @Named("reason") @Text String text2) {
        if (this.platform.getPunishmentLadderHandler().getByName(text) != null) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.PUNISHMENT_LADDER.ALREADY_EXISTS)
                            .replace("<ladder>", text));
        }
        this.platform.getPunishmentLadderHandler().createLadder(text, text2, matches);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.PUNISHMENT_LADDER.CREATED)
                                .replace("<ladder>", text)));
    }

    @Command(name = "delete")
    @Requires("core.command.punishmentladder.delete")
    public void delete(@Sender CommandSender sender, IPunishmentLadder punishmentLadder) {
        this.platform.getPunishmentLadderHandler().getRepository().delete(punishmentLadder);
        this.platform
                .getPacketHandler()
                .send(new PunishmentLadderUpdatePacket(punishmentLadder, true));
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.PUNISHMENT_LADDER.DELETED)
                                .replace("<ladder>", punishmentLadder.getReason())));
    }

    @Command(name = "list")
    @Requires("core.command.punishmentladder.list")
    public void list(@Sender CommandSender sender, @Default("1") @Named("page") int value) {
        PaginatedListClickable paginatedListClickable =
                new PaginatedListClickable("Punishment Ladders", null, "/punishmentladder list");
        ArrayList<Clickable> values = new ArrayList<>();
        for (IPunishmentLadder punishmentLadder :
                this.platform.getPunishmentLadderHandler().getRepository().getCache()) {
            Clickable clickable;
            (clickable = new Clickable())
                    .add(LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_LIST_FORMAT));
            clickable.add(
                    punishmentLadder.getId() + " &7(Reason: " + punishmentLadder.getReason() + ")");
            values.add(clickable);
        }
        paginatedListClickable.display(sender, value, values);
    }

    @Command(name = "priority")
    @Requires("core.command.punishmentladder.priority")
    public void priority(
            @Sender CommandSender sender,
            IPunishmentLadder punishmentLadder,
            @Named("priority") int value) {
        punishmentLadder.setPriority(value);
        this.platform.getPunishmentLadderHandler().getRepository().save(punishmentLadder);
        this.platform
                .getPacketHandler()
                .send(new PunishmentLadderUpdatePacket(punishmentLadder, false));
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.PUNISHMENT_LADDER.PRIORITY_CHANGED)
                                .replace("<priority>", value + "")
                                .replace("<ladder>", punishmentLadder.getId())));
    }

    @Command(name = "hidden")
    @Requires("core.command.punishmentladder.hidden")
    public void hidden(
            @Sender CommandSender sender,
            IPunishmentLadder punishmentLadder,
            @Optional @Named("hidden") String text) {
        boolean matches = !punishmentLadder.isHidden();
        if (text != null) {
            matches = Boolean.parseBoolean(text);
        }
        punishmentLadder.setHidden(matches);
        this.platform.getPunishmentLadderHandler().getRepository().save(punishmentLadder);
        this.platform
                .getPacketHandler()
                .send(new PunishmentLadderUpdatePacket(punishmentLadder, false));
        String text2 =
                MessageConfig.PUNISHMENT_LADDER.HIDDEN_ENABLED.replace(
                        "<ladder>", punishmentLadder.getId());
        String text3 =
                MessageConfig.PUNISHMENT_LADDER.HIDDEN_DISABLED.replace(
                        "<ladder>", punishmentLadder.getId());
        sender.sendMessage(Color.translate(matches ? text2 : text3));
    }

    @Command(name = "addpunishment")
    @Requires("core.command.punishmentladder.addpunishment")
    public void addpunishment(
            @Sender CommandSender sender,
            IPunishmentLadder punishmentLadder,
            PunishmentType punishmentType,
            @Named("duration") Duration duration,
            @Named("ip") boolean matches,
            @Named("shadow (Mute Only)") boolean matches2,
            @Optional @Named("decay") @DurationInput String text) {
        int value = punishmentLadder.getLadder().size() + 1;
        long timeTime = duration.getTimeTime();
        String text2 = duration.formatTimeMillis();
        long value2 = this.calculateTime(sender, text);
        PunishmentLadderType punishmentLadderType =
                new PunishmentLadderType(
                        value, punishmentType, timeTime, matches, matches2, value2);
        punishmentLadder.getLadder().add(punishmentLadderType);
        this.platform.getPunishmentLadderHandler().getRepository().save(punishmentLadder);
        this.platform
                .getPacketHandler()
                .send(new PunishmentLadderUpdatePacket(punishmentLadder, false));
        for (String text3 :
                LocaleUtil.tr(sender, MessageConfig.PUNISHMENT_LADDER.PUNISHMENT_ADDED)) {
            sender.sendMessage(
                    Color.translate(
                            text3.replace("<ladder>", punishmentLadder.getId())
                                    .replace("<reason>", punishmentLadder.getReason())
                                    .replace("<type>", punishmentType.getNiceName())
                                    .replace("<ip>", matches ? "&atrue" : "&cfalse")
                                    .replace("<shadow>", matches2 ? "&atrue" : "&cfalse")
                                    .replace("<duration>", text2)
                                    .replace(
                                            "<decay>",
                                            value2 != -5L
                                                    ? DateUtils.formatTimeMillis(value2)
                                                    : TimeConstants.NEVER)));
        }
    }

    @Command(name = "editpunishment")
    @Requires("core.command.punishmentladder.editpunishment")
    public void editpunishment(
            @Sender CommandSender sender,
            IPunishmentLadder punishmentLadder,
            @Named("id") int value,
            PunishmentType punishmentType,
            @Named("duration") Duration duration,
            @Named("ip") boolean matches,
            @Named("shadow (Mute Only)") boolean matches2,
            @Optional @Named("decay") @DurationInput String text) {
        IPunishmentLadderType punishmentLadderType;
        if ((punishmentLadderType = punishmentLadder.getByOrder(value)) == null) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.PUNISHMENT_LADDER.ID_DOESNT_EXIST));
        }
        long timeTime = duration.getTimeTime();
        String text2 = duration.formatTimeMillis();
        long value2 = this.calculateTime(sender, text);
        punishmentLadderType.setType(punishmentType);
        punishmentLadderType.setShadow(matches2);
        punishmentLadderType.setDuration(timeTime);
        punishmentLadderType.setIp(matches);
        punishmentLadderType.setDecay(value2);
        this.platform.getPunishmentLadderHandler().getRepository().save(punishmentLadder);
        this.platform
                .getPacketHandler()
                .send(new PunishmentLadderUpdatePacket(punishmentLadder, false));
        for (String text3 :
                LocaleUtil.tr(sender, MessageConfig.PUNISHMENT_LADDER.PUNISHMENT_EDITED)) {
            sender.sendMessage(
                    Color.translate(
                            text3.replace("<ladder>", punishmentLadder.getId())
                                    .replace("<reason>", punishmentLadder.getReason())
                                    .replace("<type>", punishmentType.getNiceName())
                                    .replace("<ip>", matches ? "&atrue" : "&cfalse")
                                    .replace("<shadow>", matches2 ? "&atrue" : "&cfalse")
                                    .replace("<duration>", text2)
                                    .replace(
                                            "<decay>",
                                            value2 != -5L
                                                    ? DateUtils.formatTimeMillis(value2)
                                                    : TimeConstants.NEVER)));
        }
    }

    private long calculateTime(CommandSender sender, String text) {
        return text != null && !text.isEmpty() && !text.equalsIgnoreCase("never")
                ? DurationProvider.tr(sender, text, true).getTimeTime()
                : -5L;
    }

    @Command(name = "deletepunishment")
    @Requires("core.command.punishmentladder.deletepunishment")
    public void deletepunishment(
            @Sender CommandSender sender,
            IPunishmentLadder punishmentLadder,
            @Named("id") int value) {
        String id = punishmentLadder.getId();
        IPunishmentLadderType punishmentLadderType;
        if ((punishmentLadderType = punishmentLadder.getByOrder(value)) == null) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.PUNISHMENT_LADDER.ID_DOESNT_EXIST));
        }
        punishmentLadder.getLadder().remove(punishmentLadderType);
        int value2 = 1;
        List<IPunishmentLadderType> values;
        for (Iterator iterator2 = (values = punishmentLadder.getSortedLadders()).iterator();
                iterator2.hasNext();
                value2++) {
            ((IPunishmentLadderType) iterator2.next()).setOrder(value2);
        }
        punishmentLadder.setLadder(values);
        this.platform.getPunishmentLadderHandler().getRepository().save(punishmentLadder);
        this.platform
                .getPacketHandler()
                .send(new PunishmentLadderUpdatePacket(punishmentLadder, false));
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.PUNISHMENT_LADDER.PUNISHMENT_REMOVED)
                                .replace("<type>", punishmentLadderType.getType().getName())
                                .replace("<ladder>", id)));
    }

    @Command(name = "listpunishments")
    @Requires("core.command.punishmentladder.listpunishments")
    public void listpunishments(@Sender CommandSender sender, IPunishmentLadder punishmentLadder) {
        Iterator<String> iterator2 =
                LocaleUtil.tr(sender, MessageConfig.PUNISHMENT_LADDER.LIST).iterator();
        while (iterator2.hasNext()) {
            String text;
            if ((text = (String) iterator2.next()).contains("<list>")) {
                for (IPunishmentLadderType punishmentLadderType :
                        punishmentLadder.getSortedLadders()) {
                    sender.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(sender, MessageConfig.PUNISHMENT_LADDER.FORMAT)
                                            .replace(
                                                    "<order>",
                                                    String.valueOf(punishmentLadderType.getOrder()))
                                            .replace("<reason>", punishmentLadder.getReason())
                                            .replace(
                                                    "<duration>",
                                                    punishmentLadderType.getDuration() == -5L
                                                            ? TimeConstants.PERMANENT
                                                            : DateUtils.formatTimeMillis(
                                                                    punishmentLadderType
                                                                            .getDuration()))
                                            .replace(
                                                    "<type>",
                                                    punishmentLadderType.getType().getNiceName())
                                            .replace(
                                                    "<ip>",
                                                    String.valueOf(punishmentLadderType.isIp()))
                                            .replace(
                                                    "<shadow>",
                                                    String.valueOf(punishmentLadderType.isShadow()))
                                            .replace(
                                                    "<decay>",
                                                    punishmentLadderType.getDecay() != -5L
                                                            ? DateUtils.formatTimeMillis(
                                                                    punishmentLadderType.getDecay())
                                                            : TimeConstants.NEVER)));
                }
            } else {
                sender.sendMessage(
                        Color.translate(text.replace("<ladder>", punishmentLadder.getReason())));
            }
        }
    }

    public PunishmentladderCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
