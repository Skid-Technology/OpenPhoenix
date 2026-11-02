package xyz.refinedev.phoenix.dialog.impl;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.CommonDialogData;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.Dialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.DialogAction;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.MultiActionDialog;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.dialog.api.DialogResponse;
import xyz.refinedev.phoenix.utils.BedrockUtils;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StaffPanicDialog extends StaffDialog {

    private final Phoenix plugin;

    private final Set<UUID> uUIDs = ConcurrentHashMap.newKeySet();

    private static final String STAFF_PANIC = "staff-panic";

    public StaffPanicDialog(Phoenix phoenix) {
        super(STAFF_PANIC);
        this.plugin = phoenix;
    }

    public boolean matchesEnabled(Player player) {
        if (BedrockUtils.getUniqueId(player)) {
            return false;
        }
        if (this.uUIDs.contains(player.getUniqueId())) {
            return true;
        }
        if (!this.plugin.getDialogHandler().matchesEnabled(player, this, player.getUniqueId())) {
            return false;
        }
        this.uUIDs.add(player.getUniqueId());
        return true;
    }

    public void applyPlayer(Player player) {
        if (this.uUIDs.remove(player.getUniqueId())) {
            this.plugin.getDialogHandler().applyPlayer(player);
        }
    }

    @Override
    public void forget(UUID uUID) {
        this.uUIDs.remove(uUID);
    }

    @Override
    public Dialog build(Player player) {
        CommonDialogData commonDialogData =
                new CommonDialogData(
                        Color.translateComponent(
                                LocaleUtil.tr(player, MenuConfig.PANIC_SCREEN.NAME)),
                        null,
                        false,
                        true,
                        DialogAction.NONE,
                        this.body(LocaleUtil.tr(player, MessageConfig.PANIC.MESSAGE)),
                        Collections.emptyList());
        return new MultiActionDialog(commonDialogData, Collections.emptyList(), null, 1);
    }

    @Override
    public void handle(Player player, DialogResponse dialogResponse) {}
}
