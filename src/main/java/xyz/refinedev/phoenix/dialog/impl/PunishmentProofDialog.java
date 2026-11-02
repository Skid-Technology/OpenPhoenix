package xyz.refinedev.phoenix.dialog.impl;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.CommonDialogData;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.Dialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.DialogAction;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.MultiActionDialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.action.DynamicCustomAction;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button.ActionButton;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button.CommonButtonData;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.dialog.api.DialogResponse;
import xyz.refinedev.phoenix.dialog.api.PhoenixDialog;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.login.ILogin;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PunishmentUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PunishmentProofDialog extends PhoenixDialog {

    private final Phoenix plugin;

    private final Map<UUID, Deque<IPunishment>> punishmentsById = new ConcurrentHashMap<>();

    private static final String WARN_TEXT = "warn";

    public PunishmentProofDialog(Phoenix phoenix) {
        super(WARN_TEXT);
        this.plugin = phoenix;
    }

    public boolean singletonList(Player player, IPunishment punishment) {
        return this.matchesEnabled(player, Collections.singletonList(punishment));
    }

    public boolean matchesEnabled(Player player, List<IPunishment> values) {
        if (SettingsConfig.MODERN.DIALOGS.WARN && !values.isEmpty()) {
            this.punishmentsById
                    .computeIfAbsent(player.getUniqueId(), result -> new ArrayDeque<>())
                    .addAll(values);
            if (this.plugin.getDialogHandler().matchesEnabled(player, this, player.getUniqueId())) {
                return true;
            }
            this.punishmentsById.remove(player.getUniqueId());
            return false;
        } else {
            return false;
        }
    }

    @Override
    public void forget(UUID uUID) {
        this.punishmentsById.remove(uUID);
    }

    public List<IPunishment> findList(IProfile profile, List<ILogin> values) {
        long value = 0L;
        Iterator<ILogin> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            ILogin login;
            if ((login = (ILogin) iterator2.next()).hasLoggedOut()) {
                value = Math.max(value, login.getLogout());
            }
        }
        ArrayList<IPunishment> values2 = new ArrayList<>();
        Iterator<IPunishment> iterator3 = profile.getPunishments().iterator();
        while (iterator3.hasNext()) {
            IPunishment punishment;
            if ((punishment = (IPunishment) iterator3.next()).getPunishmentType()
                            == PunishmentType.WARN
                    && punishment.isActive()
                    && punishment.getIssuedAt() > value) {
                values2.add(punishment);
            }
        }
        values2.sort(
                (result, result2) -> Long.compare(result.getIssuedAt(), result2.getIssuedAt()));
        return values2;
    }

    @Override
    public Dialog build(Player player) {
        Deque values;
        if ((values = this.punishmentsById.get(player.getUniqueId())) != null
                && !values.isEmpty()) {
            IPunishment punishment = (IPunishment) values.peek();
            ArrayList<String> values2 = new ArrayList<>();
            for (String text : LocaleUtil.tr(player, MessageConfig.PUNISHMENT.WARN.MESSAGE)) {
                values2.add(PunishmentUtil.replaceKickMessage(text, punishment));
            }
            List values3 = this.body(values2, 320);
            CommonDialogData commonDialogData =
                    new CommonDialogData(
                            Color.translateComponent(
                                    LocaleUtil.tr(player, MenuConfig.WARN_SCREEN.NAME)),
                            null,
                            false,
                            true,
                            DialogAction.CLOSE,
                            values3,
                            Collections.emptyList());
            ActionButton actionButton =
                    new ActionButton(
                            new CommonButtonData(
                                    Color.translateComponent(
                                            LocaleUtil.tr(player, MenuConfig.WARN_SCREEN.ACCEPT)),
                                    null,
                                    200),
                            new DynamicCustomAction(this.getActionId(), null));
            return new MultiActionDialog(
                    commonDialogData, Collections.singletonList(actionButton), null, 1);
        } else {
            return null;
        }
    }

    @Override
    public void handle(Player player, DialogResponse dialogResponse) {
        Deque values;
        if ((values = this.punishmentsById.get(player.getUniqueId())) != null) {
            values.poll();
            if (values.isEmpty()) {
                this.punishmentsById.remove(player.getUniqueId());
            } else {
                this.plugin.getDialogHandler().matchesEnabled(player, this, player.getUniqueId());
            }
        }
    }
}
