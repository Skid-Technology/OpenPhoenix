package xyz.refinedev.phoenix.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.lib.kyori.adventure.text.Component;
import xyz.refinedev.lib.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.LimitedSizeList;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Register(name = "deletemessage")
@Requires("core.command.deletemessage")
public class DeletemessageCommand {

    private final Phoenix plugin;

    private final LegacyComponentSerializer legacyComponentSerializer =
            LegacyComponentSerializer.legacySection();

    private final LimitedSizeList<UUID> limitedSizeList = new LimitedSizeList<>(500);

    private List<Component> components;

    @Command(name = "", async = true)
    public void execute(@Sender Player player, @Named("uuid") UUID uUID) {
        if (this.limitedSizeList.contains(uUID)) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.ERROR.MESSAGE_ALREADY_DELETED));
        }
        this.limitedSizeList.add(uUID);
        int value = 0;
        for (Player player2 :
                (Iterable<Player>) ((Iterable) new ArrayList(Bukkit.getOnlinePlayers()))) {
            if (this.matchesEnabled(player2, uUID)) {
                value++;
            }
        }
        if (this.plugin.isDebugMode()) {
            this.plugin
                    .getLogger()
                    .info(
                            player.getName()
                                    + " deleted a chat message for "
                                    + value
                                    + " player(s).");
        }
    }

    private boolean matchesEnabled(Player player2, UUID uUID) {
        LimitedSizeList limitedSizeList;
        if ((limitedSizeList =
                                this.plugin
                                        .getChatHandler()
                                        .getLastChatMessages()
                                        .get(player2.getUniqueId()))
                        != null
                && !limitedSizeList.isEmpty()) {
            ArrayList<Pair> values;
            Stream<Pair> stream2 = (values = new ArrayList(limitedSizeList)).stream();
            if (stream2.noneMatch(result -> uUID.equals(result.first))) {
                return false;
            }
            for (Component component : this.getList()) {
                this.plugin.getAudience().player(player2).sendMessage(component);
            }
            LimitedSizeList limitedSizeList2 = new LimitedSizeList(100);
            for (Pair pair : values) {
                if (!uUID.equals(pair.first)) {
                    limitedSizeList2.add(pair);
                    this.plugin.getAudience().player(player2).sendMessage((Component) pair.second);
                }
            }
            limitedSizeList.clear();
            limitedSizeList.addAll(limitedSizeList2);
            return true;
        } else {
            return false;
        }
    }

    private List<Component> getList() {
        if (this.components != null) {
            return this.components;
        }
        ArrayList<Component> values2 = new ArrayList<>(100);
        for (int index = 0; index < 100; index++) {
            values2.add(
                    this.legacyComponentSerializer.deserialize(
                            " " + ChatColor.values()[index % 8]));
        }
        return this.components = values2;
    }

    public DeletemessageCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
