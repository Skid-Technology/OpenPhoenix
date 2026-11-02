package xyz.refinedev.phoenix.dialog.impl;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.CommonDialogData;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.Dialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.DialogAction;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.MultiActionDialog;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.dialog.api.DialogResponse;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.report.IReportCategory;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StaffReportDialog extends StaffDialog {

    private static final String text = "category";

    private static final String textString = "custom:reason";

    private final Phoenix plugin;

    private final Map<UUID, IProfile> profilesById = new ConcurrentHashMap<>();

    public StaffReportDialog(Phoenix phoenix) {
        super("staff-report");
        this.plugin = phoenix;
    }

    public boolean matchesEnabled(Player player, IProfile profile) {
        this.profilesById.put(player.getUniqueId(), profile);
        if (this.plugin.getDialogHandler().matchesEnabled(player, this, profile.getUniqueId())) {
            return true;
        }
        this.profilesById.remove(player.getUniqueId());
        return false;
    }

    @Override
    public void forget(UUID uUID) {
        this.profilesById.remove(uUID);
        this.forgetPage(uUID);
    }

    @Override
    public Dialog build(Player player) {
        IProfile profile;
        if ((profile = this.profilesById.get(player.getUniqueId())) == null) {
            return null;
        }
        ArrayList<IReportCategory> values = new ArrayList<>();
        Iterator<IReportCategory> iterator2 =
                Phoenix.getInstance()
                        .getReportCategoryHandler()
                        .getRepository()
                        .getCache()
                        .iterator();
        while (iterator2.hasNext()) {
            IReportCategory reportCategory;
            if ((reportCategory = (IReportCategory) iterator2.next()).getPunishmentLadder()
                    != null) {
                values.add(reportCategory);
            }
        }
        if (values.isEmpty()) {
            return null;
        }
        ArrayList<
                        xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button
                                .ActionButton>
                values2 = new ArrayList<>();
        for (IReportCategory reportCategory2 : this.page(player, values)) {
            values2.add(
                    this.button(
                            LocaleUtil.tr(player, MenuConfig.REPORT_MENU.ITEM_NAME)
                                    .replace("<category>", reportCategory2.getDisplayName()),
                            String.join("\n", reportCategory2.getDescription()),
                            "category",
                            reportCategory2.getId()));
        }
        if (SettingsConfig.REPORTS.CUSTOM_REASON.ENABLED) {
            values2.add(
                    this.button(
                            LocaleUtil.tr(player, MenuConfig.REPORT_MENU.CUSTOM_ITEM_NAME),
                            String.join(
                                            "\n",
                                            LocaleUtil.tr(
                                                    player,
                                                    MenuConfig.REPORT_MENU.CUSTOM_ITEM_LORE))
                                    .replace("<target>", profile.getNameWithColorNoDisguise()),
                            "category",
                            "custom:reason"));
        }
        this.navigation(values2, player, values.size());
        ArrayList values3;
        (values3 = new ArrayList())
                .add(
                        LocaleUtil.tr(player, MenuConfig.REPORT_MENU.FORM_SUBTITLE)
                                .replace("<target>", profile.getNameWithColorNoDisguise()));
        values3.addAll(this.showing(player, values.size()));
        CommonDialogData commonDialogData =
                new CommonDialogData(
                        Color.translateComponent(
                                LocaleUtil.tr(player, MenuConfig.REPORT_MENU.TITLE)
                                        .replace("<target>", profile.getNameWithColorNoDisguise())),
                        null,
                        true,
                        false,
                        DialogAction.NONE,
                        this.body(values3),
                        Collections.emptyList());
        return new MultiActionDialog(commonDialogData, values2, this.close(player), 1);
    }

    @Override
    public void handle(Player player, DialogResponse dialogResponse) {
        IProfile profile;
        if ((profile = this.profilesById.get(player.getUniqueId())) == null) {
            this.dismiss(player);
        } else if (!this.closed(player, dialogResponse)) {
            if (this.turned(player, dialogResponse)) {
                this.plugin.getDialogHandler().matchesEnabled(player, this, profile.getUniqueId());
            } else {
                this.profilesById.remove(player.getUniqueId());
                this.dismiss(player);
                String text;
                if ((text = dialogResponse.formatString("category", null, 64)) != null) {
                    if ("custom:reason".equals(text)) {
                        if (SettingsConfig.REPORTS.CUSTOM_REASON.ENABLED) {
                            Phoenix.getInstance().getReportHandler().sendMessage(player, profile);
                        }
                    } else {
                        IReportCategory reportCategory;
                        if ((reportCategory =
                                                Phoenix.getInstance()
                                                        .getReportCategoryHandler()
                                                        .getRepository()
                                                        .getFromCache(text)
                                                        .orElse(null))
                                        != null
                                && reportCategory.getPunishmentLadder() != null) {
                            Phoenix.getInstance()
                                    .getReportHandler()
                                    .applyPlayer(player, profile, reportCategory);
                        }
                    }
                }
            }
        }
    }
}
