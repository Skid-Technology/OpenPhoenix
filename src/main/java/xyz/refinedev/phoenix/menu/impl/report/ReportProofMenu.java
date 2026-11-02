package xyz.refinedev.phoenix.menu.impl.report;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.lib.xseries.XMaterial;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.button.BackButton;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.menu.impl.ReportsMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IProof;
import xyz.refinedev.phoenix.report.IReport;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.chat.Clickable;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportProofMenu extends PaginatedMenu {

    private final IReport report;

    private final IProfile profile;

    private final List<IReport> reports;

    private final boolean enabled;

    private static final String title = "Proof";

    @Override
    public String getTitle(Player player) {
        return title;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        if (this.enabled) {
            entries.put(4, new BackButton(new ReportsMenu(this.reports)));
        } else {
            entries.put(4, new BackButton(new ProfileReportsMenu(this.profile, this.reports)));
        }
        return entries;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (IProof proof : this.report.getProof()) {
            entries.put(entries.size(), new ReportProofMenu.EntryButton(proof));
        }
        return entries;
    }

    public ReportProofMenu(
            IReport report2, IProfile profile2, List<IReport> reports, boolean enabled) {
        this.report = report2;
        this.profile = profile2;
        this.reports = reports;
        this.enabled = enabled;
    }

    class EntryButton extends Button {

        private final IProof proof;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.PAPER.parseMaterial()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.PROOF_MENU.ITEM_NAME)
                                    .replace("<type>", this.proof.getType().getNiceName()));
            for (String text : LocaleUtil.tr(player, MenuConfig.PROOF_MENU.ITEM_LORE)) {
                itemBuilder.addLoreLine(
                        text.replace("<proof>", this.proof.getProof())
                                .replace("<addedBy>", this.proof.getNiceAddedBy()));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            switch (this.proof.getType()) {
                case CHAT_SNAPSHOT:
                    player.performCommand("chatsnapshot view " + this.proof.getProof());
                    break;
                case LINK:
                    Clickable clickable;
                    (clickable = new Clickable())
                            .add("&a" + this.proof.getProof(), "", "", this.proof.getProof());
                    clickable.sendToPlayer(player);
            }
        }

        public EntryButton(IProof proof2) {
            this.proof = proof2;
        }
    }
}
