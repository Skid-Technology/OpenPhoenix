package xyz.refinedev.phoenix.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Default;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.dialog.impl.StaffReportsDialog;
import xyz.refinedev.phoenix.menu.api.menus.TextEditorMenu;
import xyz.refinedev.phoenix.menu.impl.report.ProfileReportsMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.WatchlistUtils;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.report.IReport;
import xyz.refinedev.phoenix.report.IReportCategory;
import xyz.refinedev.phoenix.report.IReportStatistics;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Help
@Register(name = "reports")
@Requires("core.command.reports")
public class ReportsCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(@Sender Player player, @Named("target") IProfile profile) {
        CompletableFuture<List<IReport>> reports =
                this.plugin.getReportHandler().getReports(profile.getUniqueId());
        reports.whenCompleteAsync(
                (result, result2) -> {
                    result.sort(Comparator.comparingLong(IReport::getIssuedAt).reversed());
                    if (!SettingsConfig.MODERN.DIALOGS.REPORTS
                            || !this.plugin
                                    .getDialogHandler()
                                    .findPhoenixDialog(StaffReportsDialog.class)
                                    .matchesEnabled(player, profile, result)) {
                        Phoenix.getInstance()
                                .getMenuHandler()
                                .openMenuAsync(
                                        new ProfileReportsMenu(profile, (List<IReport>) result),
                                        player);
                    }
                });
    }

    @Command(name = "all", description = "Every open report on the network")
    @Requires("core.command.reports.all")
    public void all(@Sender Player player) {
        WatchlistUtils.applyPhoenix(this.plugin, player);
    }

    @Command(
            name = "watchlist",
            description = "Players reported often enough to be worth a look",
            async = true)
    @Requires("core.command.reports.watchlist")
    public void watchlist(@Sender Player player) {
        WatchlistUtils.applyPhoenix(this.plugin, Phoenix.getInstance(), player);
    }

    @Command(name = "categories", description = "The categories players report under", async = true)
    @Requires("core.command.reports.categories")
    public void categories(@Sender CommandSender sender) {
        List<IReportCategory> values;
        if ((values = Phoenix.getInstance().getReportCategoryHandler().getRepository().getCache())
                .isEmpty()) {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.REPORT.CATEGORY.LIST_EMPTY)));
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (IReportCategory reportCategory : values) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append("\n");
                }
                stringBuilder.append(
                        LocaleUtil.tr(sender, MessageConfig.REPORT.CATEGORY.LIST_ENTRY)
                                .replace("<category>", reportCategory.getId())
                                .replace("<display>", reportCategory.getDisplayName())
                                .replace(
                                        "<ladder>",
                                        reportCategory.getPunishmentLadderId() == null
                                                ? "none"
                                                : reportCategory.getPunishmentLadderId()));
            }
            for (String text : LocaleUtil.tr(sender, MessageConfig.REPORT.CATEGORY.LIST)) {
                sender.sendMessage(
                        Color.translate(
                                text.replace("<amount>", String.valueOf(values.size()))
                                        .replace("<categories>", stringBuilder.toString())));
            }
        }
    }

    @Command(name = "stats", description = "How the report queue has been going", async = true)
    @Requires("core.command.reports.stats")
    public void stats(@Sender CommandSender sender, @Default("7") @Named("days") int value) {
        IReportStatistics reportStatistics =
                this.plugin.getReportHandler().getStatistics(value).join();
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry<UUID, Integer> entry : reportStatistics.getLeaderboard(5)) {
            IProfile profile;
            if ((profile =
                            Phoenix.getInstance()
                                    .getProfileHandler()
                                    .getProfile((UUID) entry.getKey()))
                    != null) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append("\n");
                }
                stringBuilder.append(
                        LocaleUtil.tr(sender, MessageConfig.REPORT.STATISTICS_HANDLED)
                                .replace("<player>", profile.getNameWithColorNoDisguise())
                                .replace("<amount>", String.valueOf(entry.getValue())));
            }
        }
        if (stringBuilder.length() == 0) {
            stringBuilder.append(LocaleUtil.tr(sender, MessageConfig.REPORT.STATISTICS_NOBODY));
        }
        for (String text : LocaleUtil.tr(sender, MessageConfig.REPORT.STATISTICS)) {
            sender.sendMessage(
                    Color.translate(
                            text.replace("<days>", String.valueOf(Math.max(1, value)))
                                    .replace("<total>", String.valueOf(reportStatistics.getTotal()))
                                    .replace("<open>", String.valueOf(reportStatistics.getOpen()))
                                    .replace(
                                            "<inProgress>",
                                            String.valueOf(reportStatistics.getInProgress()))
                                    .replace(
                                            "<resolved>",
                                            String.valueOf(reportStatistics.getResolved()))
                                    .replace(
                                            "<punished>",
                                            String.valueOf(reportStatistics.getPunished()))
                                    .replace(
                                            "<outstanding>",
                                            String.valueOf(reportStatistics.getOutstanding()))
                                    .replace(
                                            "<response>",
                                            this.formatString(
                                                    sender, reportStatistics.getAverageResponse()))
                                    .replace(
                                            "<resolution>",
                                            this.formatString(
                                                    sender,
                                                    reportStatistics.getAverageResolution()))
                                    .replace("<handled>", stringBuilder.toString())));
        }
    }

    private String formatString(CommandSender sender, long value) {
        return value < 0L
                ? LocaleUtil.tr(sender, MessageConfig.REPORT.STATISTICS_NEVER)
                : DateUtils.formatTimeMillis(value);
    }

    @Command(name = "categories create")
    @Requires("core.command.reports.categories")
    public void categoriesCreate(
            @Sender CommandSender sender,
            @Named("id") String text,
            IPunishmentLadder punishmentLadder,
            Material material,
            @Named("displayName") @Text String text2) {
        if (this.plugin.getReportCategoryHandler().getRepository().getFromCache(text).isPresent()) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.REPORT.CATEGORY.ALREADY_EXISTS));
        }
        this.plugin
                .getReportCategoryHandler()
                .createCategory(text, text2, punishmentLadder.getId(), material.name());
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.REPORT.CATEGORY.CREATED)
                                .replace("<category>", text)));
    }

    @Command(name = "categories delete")
    @Requires("core.command.reports.categories")
    public void categoriesDelete(@Sender CommandSender sender, IReportCategory reportCategory) {
        this.plugin.getReportCategoryHandler().deleteCategory(reportCategory);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.REPORT.CATEGORY.DELETED)
                                .replace("<category>", reportCategory.getId())));
    }

    @Command(name = "categories displayName")
    @Requires("core.command.reports.categories")
    public void categoriesDisplayName(
            @Sender CommandSender sender,
            IReportCategory reportCategory,
            @Named("displayName") @Text String text) {
        reportCategory.setDisplayName(text);
        this.plugin.getReportCategoryHandler().updateCategory(reportCategory);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.REPORT.CATEGORY.UPDATED)
                                .replace("<category>", reportCategory.getId())
                                .replace("<type>", "displayname")));
    }

    @Command(name = "categories material")
    @Requires("core.command.reports.categories")
    public void categoriesMaterial(
            @Sender CommandSender sender, IReportCategory reportCategory, Material material) {
        reportCategory.setMaterialName(material.name());
        this.plugin.getReportCategoryHandler().updateCategory(reportCategory);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.REPORT.CATEGORY.UPDATED)
                                .replace("<category>", reportCategory.getId())
                                .replace("<type>", "material")));
    }

    @Command(name = "categories punishmentladder")
    @Requires("core.command.reports.categories")
    public void categoriesPunishmentladder(
            @Sender CommandSender sender,
            IReportCategory reportCategory,
            IPunishmentLadder punishmentLadder) {
        reportCategory.setPunishmentLadderId(punishmentLadder.getId());
        this.plugin.getReportCategoryHandler().updateCategory(reportCategory);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.REPORT.CATEGORY.UPDATED)
                                .replace("<category>", reportCategory.getId())
                                .replace("<type>", "punishment ladder")));
    }

    @Command(name = "categories description")
    @Requires("core.command.reports.categories")
    public void categoriesDescription(@Sender Player player, final IReportCategory reportCategory) {
        Phoenix.getInstance()
                .getMenuHandler()
                .openMenu(
                        new TextEditorMenu(new LinkedList<>(reportCategory.getDescription())) {

                            @Override
                            public void onSave(Player player, List<String> values) {
                                reportCategory.setDescription(values);
                                Phoenix.getInstance()
                                        .getReportCategoryHandler()
                                        .updateCategory(reportCategory);
                                player.sendMessage(
                                        Color.translate(
                                                LocaleUtil.tr(
                                                                player,
                                                                MessageConfig.REPORT
                                                                        .CATEGORY
                                                                        .UPDATED)
                                                        .replace(
                                                                "<category>",
                                                                reportCategory.getId())
                                                        .replace("<type>", "description")));
                            }
                        },
                        player);
    }

    public ReportsCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
