package xyz.refinedev.phoenix.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.chat.Clickable;

import java.util.List;

public class PaginatedListClickable {

    private final String type;

    private final String otherType;

    private final String listCmd;

    public void display(CommandSender sender, int value, List<Clickable> values) {
        if (value == 0) {
            value = 1;
        }
        if (values.isEmpty()) {
            sender.sendMessage(
                    ChatColor.RED
                            + "There aren't any results for "
                            + ChatColor.YELLOW
                            + this.type
                            + ChatColor.RED
                            + "!");
        } else {
            int value2 = values.size() / 16 + 1;
            if (value > 0 && value <= value2) {
                if (this.otherType != null) {
                    sender.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(
                                                    sender,
                                                    MessageConfig.GENERAL
                                                            .GENERIC_LIST_HEADER_ADVANCED_FORMAT)
                                            .replace("<otherType>", this.otherType)
                                            .replace("<type>", this.type)
                                            .replace("<page>", "" + value)
                                            .replace("<pages>", "" + value2)));
                } else {
                    sender.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(
                                                    sender,
                                                    MessageConfig.GENERAL
                                                            .GENERIC_LIST_HEADER_FORMAT)
                                            .replace("<type>", this.type)
                                            .replace("<page>", "" + value)
                                            .replace("<pages>", "" + value2)));
                }
                for (int value3 = 15 * (value - 1);
                        value3 < 15 * value && value3 < values.size();
                        value3++) {
                    ((Clickable) values.get(value3)).sendToPlayer(sender);
                }
                Clickable clickable;
                if (value - 1 > 0) {
                    clickable =
                            new Clickable(
                                    LocaleUtil.tr(
                                                    sender,
                                                    MessageConfig.GENERAL
                                                            .GENERIC_LIST_COLOR_HAS_OTHER_PAGE)
                                            + LocaleUtil.tr(
                                                    sender,
                                                    MessageConfig.GENERAL
                                                            .GENERIC_LIST_PREVIOUS_PAGE_FORMAT),
                                    "&aClick to view page &f" + (value - 1) + "&a.",
                                    this.listCmd + " " + (value - 1));
                } else {
                    clickable =
                            new Clickable(
                                    LocaleUtil.tr(
                                                    sender,
                                                    MessageConfig.GENERAL
                                                            .GENERIC_LIST_COLOR_NO_OTHER_PAGE)
                                            + LocaleUtil.tr(
                                                    sender,
                                                    MessageConfig.GENERAL
                                                            .GENERIC_LIST_PREVIOUS_PAGE_FORMAT));
                }
                clickable.add(
                        LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_LIST_FOOTER_FORMAT)
                                .replace("<page>", "" + value)
                                .replace("<pages>", "" + value2)
                                .replace("<results>", "" + values.size()));
                if (value + 1 <= value2) {
                    clickable.add(
                            LocaleUtil.tr(
                                            sender,
                                            MessageConfig.GENERAL.GENERIC_LIST_COLOR_HAS_OTHER_PAGE)
                                    + LocaleUtil.tr(
                                            sender,
                                            MessageConfig.GENERAL.GENERIC_LIST_NEXT_PAGE_FORMAT),
                            "&aClick to view page &f" + (value + 1) + "&a.",
                            this.listCmd + " " + (value + 1));
                } else {
                    clickable.add(
                            LocaleUtil.tr(
                                            sender,
                                            MessageConfig.GENERAL.GENERIC_LIST_COLOR_NO_OTHER_PAGE)
                                    + LocaleUtil.tr(
                                            sender,
                                            MessageConfig.GENERAL.GENERIC_LIST_NEXT_PAGE_FORMAT));
                }
                clickable.sendToPlayer(sender);
            } else {
                sender.sendMessage(
                        ChatColor.RED
                                + "The page number "
                                + ChatColor.YELLOW
                                + value
                                + ChatColor.RED
                                + " does not exist!");
            }
        }
    }

    public PaginatedListClickable(String type, String otherType, String listCmd) {
        this.type = type;
        this.otherType = otherType;
        this.listCmd = listCmd;
    }
}
