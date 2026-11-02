package xyz.refinedev.phoenix.dialog.impl;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.CommonDialogData;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.Dialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.DialogAction;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.MultiActionDialog;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.dialog.api.DialogResponse;
import xyz.refinedev.phoenix.profile.punishment.IProof;
import xyz.refinedev.phoenix.report.IReport;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.chat.Clickable;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StaffReportProofDialog extends StaffDialog {

    private static final String text = "proof";

    private final Phoenix plugin;

    private final Map<UUID, IReport> reportsById = new ConcurrentHashMap<>();

    public StaffReportProofDialog(Phoenix phoenix) {
        super("staff-report-proof");
        this.plugin = phoenix;
    }

    public boolean matchesEnabled(Player player, IReport report) {
        if (report.getProof().isEmpty()) {
            return false;
        }
        this.reportsById.put(player.getUniqueId(), report);
        if (this.plugin.getDialogHandler().matchesEnabled(player, this, report.getTarget())) {
            return true;
        }
        this.reportsById.remove(player.getUniqueId());
        return false;
    }

    @Override
    public void forget(UUID uUID) {
        this.reportsById.remove(uUID);
        this.forgetPage(uUID);
    }

    @Override
    public Dialog build(Player player) {
        IReport report;
        if ((report = this.reportsById.get(player.getUniqueId())) == null) {
            return null;
        }
        List<IProof> values;
        if ((values = report.getProof()).isEmpty()) {
            return null;
        }
        ArrayList<
                        xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button
                                .ActionButton>
                values2 = new ArrayList<>();
        for (IProof proof : this.page(player, values)) {
            ArrayList<String> values3 = new ArrayList<>();
            for (String text : LocaleUtil.tr(player, MenuConfig.PROOF_MENU.ITEM_LORE)) {
                values3.add(
                        text.replace("<proof>", proof.getProof())
                                .replace("<addedBy>", proof.getNiceAddedBy()));
            }
            values2.add(
                    this.button(
                            LocaleUtil.tr(player, MenuConfig.PROOF_MENU.ITEM_NAME)
                                    .replace("<type>", proof.getType().getNiceName()),
                            String.join("\n", values3),
                            "proof",
                            proof.getProof()));
        }
        this.navigation(values2, player, values.size());
        CommonDialogData commonDialogData =
                new CommonDialogData(
                        Color.translateComponent(
                                LocaleUtil.tr(player, MenuConfig.PROOF_MENU.TITLE)),
                        null,
                        true,
                        false,
                        DialogAction.NONE,
                        this.body(this.showing(player, values.size())),
                        Collections.emptyList());
        return new MultiActionDialog(commonDialogData, values2, this.close(player), 1);
    }

    @Override
    public void handle(Player player, DialogResponse dialogResponse) {
        IReport report;
        if ((report = this.reportsById.get(player.getUniqueId())) == null) {
            this.dismiss(player);
        } else if (!this.closed(player, dialogResponse)) {
            if (this.turned(player, dialogResponse)) {
                this.plugin.getDialogHandler().matchesEnabled(player, this, report.getTarget());
            } else {
                String text;
                if ((text = dialogResponse.formatString("proof", null, 256)) == null) {
                    this.dismiss(player);
                } else {
                    this.reportsById.remove(player.getUniqueId());
                    this.dismiss(player);
                    Iterator<IProof> iterator2 = report.getProof().iterator();
                    while (iterator2.hasNext()) {
                        IProof proof;
                        if ((proof = (IProof) iterator2.next()).getProof().equals(text)) {
                            this.applyPlayer(player, proof);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void applyPlayer(Player player, IProof proof) {
        switch (proof.getType()) {
            case CHAT_SNAPSHOT:
                Tasks.run(() -> player.performCommand("chatsnapshot view " + proof.getProof()));
                return;
            case LINK:
                Clickable clickable;
                (clickable = new Clickable())
                        .add("&a" + proof.getProof(), "", "", proof.getProof());
                clickable.sendToPlayer(player);
        }
    }
}
