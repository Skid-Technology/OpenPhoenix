package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Default;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.impl.FiltersHardTraitMenu;
import xyz.refinedev.phoenix.profile.filter.Filter;
import xyz.refinedev.phoenix.profile.filter.FilterType;
import xyz.refinedev.phoenix.profile.filter.IFilter;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PaginatedListClickable;
import xyz.refinedev.phoenix.utils.chat.Clickable;

import java.util.ArrayList;

@Help
@Register(name = "filter", aliases = "filters")
@Requires("core.command.filter")
public class FilterCommand {

    private final Phoenix plugin;

    @Command(name = "list")
    public void list(@Sender CommandSender sender, @Default("1") @Named("page") int value) {
        PaginatedListClickable paginatedListClickable =
                new PaginatedListClickable("Filters", null, "/filters list");
        ArrayList<Clickable> values = new ArrayList<>();
        for (IFilter filter : this.plugin.getFilterHandler().getRepository().getCache()) {
            Clickable clickable =
                    new Clickable(
                            LocaleUtil.tr(sender, MessageConfig.FILTER.FORMAT)
                                    .replace("<name>", filter.getName())
                                    .replace("<type>", filter.getFilterType().getDisplayName())
                                    .replace("<filter>", filter.getFilter()));
            values.add(clickable);
        }
        paginatedListClickable.display(sender, value, values);
    }

    @Command(name = "create")
    public void create(
            @Sender CommandSender sender,
            @Named("name") String text,
            @Named("filter") String text2,
            FilterType filterType,
            @Optional IPunishmentLadder punishmentLadder) {
        if (this.plugin.getFilterHandler().getRepository().getFromCache(text).isPresent()) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.FILTER.ALREADY_EXISTS));
        }
        Filter filter = new Filter(text, text2, filterType);
        if (punishmentLadder != null) {
            filter.setPunishmentLadderId(punishmentLadder.getId());
            filter.setAutoPunish(true);
        }
        this.plugin.getFilterHandler().updateFilter(filter);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.FILTER.CREATE)
                                .replace("<filter>", filter.getFilter())
                                .replace("<type>", filter.getFilterType().getDisplayName())));
    }

    @Command(name = "edit")
    public void edit(@Sender Player player, IFilter filter) {
        this.plugin.getMenuHandler().openMenu(new FiltersHardTraitMenu(filter), player);
    }

    @Command(name = "delete")
    public void delete(@Sender CommandSender sender, IFilter filter) {
        this.plugin.getFilterHandler().removeFilter(filter);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.FILTER.DELETED)
                                .replace("<filter>", filter.getName())));
    }

    @Command(name = "hard")
    public void hard(@Sender CommandSender sender, IFilter filter, @Named("hard") boolean matches) {
        filter.setHard(matches);
        this.plugin.getFilterHandler().updateFilter(filter);
        String text =
                matches ? MessageConfig.FILTER.HARD_ON + " ;)" : MessageConfig.FILTER.HARD_OFF;
        sender.sendMessage(Color.translate(text.replace("<filter>", filter.getName())));
    }

    @Command(name = "test")
    public void test(@Sender CommandSender sender, @Named("text") @Text String text) {
        if (!this.plugin.getFilterHandler().isFiltered(text)) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.FILTER.IS_NOT_FILTERED));
        }
        IFilter filter;
        if ((filter = this.plugin.getFilterHandler().getFilterFromMessage(text)) == null) {
            for (String text2 : LocaleUtil.tr(sender, MessageConfig.FILTER.FILTER_TEST)) {
                sender.sendMessage(
                        Color.translate(
                                text2.replace("<name>", text)
                                        .replace("<filter>", "Links")
                                        .replace("<type>", "Regex")));
            }
        } else {
            for (String text3 : LocaleUtil.tr(sender, MessageConfig.FILTER.FILTER_TEST)) {
                sender.sendMessage(
                        Color.translate(
                                text3.replace("<name>", filter.getName())
                                        .replace("<filter>", filter.getFilter())
                                        .replace(
                                                "<type>",
                                                filter.getFilterType().getDisplayName())));
            }
        }
    }

    public FilterCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
