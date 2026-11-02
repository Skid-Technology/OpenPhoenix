package xyz.refinedev.phoenix.commands.api.usage;

import org.bukkit.command.CommandSender;

import xyz.refinedev.lib.commands.actor.Actor;
import xyz.refinedev.lib.commands.bukkit.actor.BukkitActor;
import xyz.refinedev.lib.commands.execution.usage.UsageHandler;
import xyz.refinedev.lib.commands.wrapper.CommandHandle;
import xyz.refinedev.lib.commands.wrapper.CommandWrapper;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.Utilities;
import xyz.refinedev.phoenix.utils.chat.Clickable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PhoenixUsageHandler implements UsageHandler {

    private static CommandSender recv(Actor actor) {
        return actor instanceof BukkitActor ? ((BukkitActor) actor).getSender() : null;
    }

    public List<String> getUsageMessage(Actor actor, CommandHandle commandHandle, String text) {
        return Collections.singletonList(
                LocaleUtil.tr(recv(actor), MessageConfig.GENERAL.HELP.USAGE)
                        .replace("<usage>", this.getFullUsage(commandHandle, text)));
    }

    public List<String> getHelpMessage(
            Actor actor, CommandWrapper commandWrapper, String text, List<String> values2) {
        String text2 = String.join(" ", values2);
        BukkitActor bukkitActor = (BukkitActor) actor;
        if (!text2.toLowerCase().startsWith("help") && !values2.isEmpty()) {
            Collection<CommandHandle> values3 = commandWrapper.getHandles().values();
            ArrayList values4;
            if ((values4 =
                            FastStreams.filterArrayList(
                                    values3,
                                    result ->
                                            result.getName()
                                                            .toLowerCase()
                                                            .startsWith(text2.toLowerCase())
                                                    && !result.isHidden()
                                                    && actor.hasPermission(result.getPermission())))
                    .isEmpty()) {
                actor.sendMessage(
                        LocaleUtil.tr(
                                        recv(actor),
                                        MessageConfig.GENERAL.HELP.NO_COMMANDS_MATCHED_SEARCH)
                                .replace("<search>", text2));
                return Collections.emptyList();
            }
            actor.sendMessage(
                    LocaleUtil.tr(recv(actor), MessageConfig.GENERAL.HELP.SEARCH_HEADER)
                            .replace("<command>", "/" + text)
                            .replace("<search>", text2));
            Iterator iterator2 = values4.iterator();
            while (iterator2.hasNext()) {
                CommandHandle commandHandle;
                String text3 =
                        !(commandHandle = (CommandHandle) iterator2.next()).getName().isEmpty()
                                ? " "
                                : "";
                if (commandHandle.hasDescription()) {
                    Clickable clickable;
                    (clickable = new Clickable())
                            .add(
                                    LocaleUtil.tr(
                                                    recv(actor),
                                                    MessageConfig.GENERAL
                                                            .HELP
                                                            .FORMAT_WITH_DESCRIPTION)
                                            .replace(
                                                    "<command>",
                                                    "/" + text + text3 + commandHandle.getName())
                                            .replace("<parameters>", commandHandle.getUsage())
                                            .replace(
                                                    "<description>",
                                                    commandHandle.getDescription()),
                                    "",
                                    "",
                                    "/" + text + text3 + commandHandle.getName());
                    clickable.sendToPlayer(bukkitActor.getSender());
                } else {
                    Clickable clickable2;
                    (clickable2 = new Clickable())
                            .add(
                                    LocaleUtil.tr(recv(actor), MessageConfig.GENERAL.HELP.FORMAT)
                                            .replace(
                                                    "<command>",
                                                    "/" + text + text3 + commandHandle.getName())
                                            .replace("<parameters>", commandHandle.getUsage()),
                                    "",
                                    "",
                                    "/" + text + text3 + commandHandle.getName());
                    clickable2.sendToPlayer(bukkitActor.getSender());
                }
            }
            return Collections.emptyList();
        } else {
            Collection<CommandHandle> values5 = commandWrapper.getHandles().values();
            ArrayList values6 =
                    FastStreams.filterArrayList(
                            values5,
                            result2 ->
                                    !result2.isHidden()
                                            && actor.hasPermission(result2.getPermission()));
            int value = 1;
            int value2 = values6.size() / 10 + 1;
            if (Utilities.isNumberInteger(text2.replace("help ", ""))) {
                value = Integer.parseInt(text2.replace("help ", ""));
            }
            actor.sendMessage(
                    LocaleUtil.tr(recv(actor), MessageConfig.GENERAL.HELP.HEADER)
                            .replace("<command>", "/" + text)
                            .replace("<page>", String.valueOf(value))
                            .replace("<totalPages>", String.valueOf(value2))
                            .replace("<results>", String.valueOf(values6.size())));
            if (value >= 0 && value <= value2) {
                for (int value3 = 10 * (value - 1);
                        value3 < 10 * value && value3 < values6.size();
                        value3++) {
                    CommandHandle commandHandle2;
                    if (!(commandHandle2 = (CommandHandle) values6.get(value3)).isHidden()
                            && actor.hasPermission(commandHandle2.getPermission())) {
                        String text4 = !commandHandle2.getName().isEmpty() ? " " : "";
                        if (commandHandle2.hasDescription()) {
                            Clickable clickable3;
                            (clickable3 = new Clickable())
                                    .add(
                                            LocaleUtil.tr(
                                                            recv(actor),
                                                            MessageConfig.GENERAL
                                                                    .HELP
                                                                    .FORMAT_WITH_DESCRIPTION)
                                                    .replace(
                                                            "<command>",
                                                            "/"
                                                                    + text
                                                                    + text4
                                                                    + commandHandle2.getName())
                                                    .replace(
                                                            "<parameters>",
                                                            commandHandle2.getUsage())
                                                    .replace(
                                                            "<description>",
                                                            commandHandle2.getDescription()),
                                            "",
                                            "",
                                            "/" + text + text4 + commandHandle2.getName());
                            clickable3.sendToPlayer(bukkitActor.getSender());
                        } else {
                            Clickable clickable4;
                            (clickable4 = new Clickable())
                                    .add(
                                            LocaleUtil.tr(
                                                            recv(actor),
                                                            MessageConfig.GENERAL.HELP.FORMAT)
                                                    .replace(
                                                            "<command>",
                                                            "/"
                                                                    + text
                                                                    + text4
                                                                    + commandHandle2.getName())
                                                    .replace(
                                                            "<parameters>",
                                                            commandHandle2.getUsage()),
                                            "",
                                            "",
                                            "/" + text + text4 + commandHandle2.getName());
                            clickable4.sendToPlayer(bukkitActor.getSender());
                        }
                    }
                }
                String text5 = LocaleUtil.tr(recv(actor), MessageConfig.GENERAL.HELP.FOOTER);
                Clickable clickable5 = new Clickable();
                String[] texts;
                int value4 = (texts = text5.split(" ")).length;
                for (int index = 0; index < value4; index++) {
                    String text6;
                    switch (text6 = texts[index]) {
                        case "<previousPage>":
                            if (value > 1) {
                                clickable5.add(
                                        LocaleUtil.tr(
                                                recv(actor),
                                                MessageConfig.GENERAL.HELP.PREVIOUS_PAGE),
                                        LocaleUtil.tr(
                                                recv(actor),
                                                MessageConfig.GENERAL.HELP.PREVIOUS_PAGE_HOVER),
                                        "/" + text + " help " + (value - 1));
                            }
                            break;
                        case "<nextPage>":
                            if (value < value2) {
                                clickable5.add(
                                        LocaleUtil.tr(
                                                recv(actor), MessageConfig.GENERAL.HELP.NEXT_PAGE),
                                        LocaleUtil.tr(
                                                recv(actor),
                                                MessageConfig.GENERAL.HELP.NEXT_PAGE_HOVER),
                                        "/" + text + " help " + (value + 1));
                            }
                            break;
                        default:
                            clickable5.add(
                                    text6.replace("<command>", "/" + text)
                                                    .replace("<page>", String.valueOf(value))
                                                    .replace("<totalPages>", String.valueOf(value2))
                                                    .replace(
                                                            "<results>",
                                                            String.valueOf(values6.size()))
                                            + " ");
                    }
                }
                clickable5.sendToPlayer(bukkitActor.getSender());
                return Collections.emptyList();
            } else {
                actor.sendMessage(
                        LocaleUtil.tr(recv(actor), MessageConfig.GENERAL.HELP.NO_RESULTS));
                return Collections.emptyList();
            }
        }
    }

    private String getFullUsage(CommandHandle commandHandle, String text) {
        String text2 = !commandHandle.getName().isEmpty() ? " " : "";
        String text3 = !commandHandle.getUsage().isEmpty() ? " " : "";
        return "/" + text + text2 + commandHandle.getName() + text3 + commandHandle.getUsage();
    }
}
