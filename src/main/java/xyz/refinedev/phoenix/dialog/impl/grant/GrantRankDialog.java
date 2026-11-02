package xyz.refinedev.phoenix.dialog.impl.grant;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.Dialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.MultiActionDialog;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.dialog.api.DialogResponse;
import xyz.refinedev.phoenix.menu.impl.grant.GrantScopeMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GrantRankDialog extends GrantStepDialog {

    private static final String RANK_TEXT = "rank";

    private final Map<UUID, IProfile> profilesById = new ConcurrentHashMap<>();

    public GrantRankDialog(Phoenix phoenix) {
        super(phoenix, "grant");
    }

    public boolean matchesEnabled(Player player, IProfile profile) {
        this.profilesById.put(player.getUniqueId(), profile);
        if (super.plugin.getDialogHandler().matchesEnabled(player, this, profile.getUniqueId())) {
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
        IProfile profile2;
        if ((profile2 = super.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                == null) {
            return null;
        }
        ArrayList<IRank> values = new ArrayList<>();
        Iterator<IRank> iterator2 =
                super.plugin.getRankHandler().getSortedRanksReversed().iterator();
        while (iterator2.hasNext()) {
            IRank rank;
            if (!(rank = (IRank) iterator2.next()).isDefaultRank()
                    && (MenuConfig.GRANT_MENU.SHOW_UNGRANTABLE_RANKS
                            || super.plugin.getRankHandler().canGrant(profile2, rank))) {
                values.add(rank);
            }
        }
        if (values.isEmpty()) {
            return null;
        }
        ArrayList<
                        xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button
                                .ActionButton>
                values2 = new ArrayList<>();
        for (IRank rank2 : this.page(player, values)) {
            values2.add(
                    this.button(
                            rank2.getDisplayName(),
                            this.formatString(
                                    rank2, super.plugin.getRankHandler().canGrant(profile2, rank2)),
                            "rank",
                            rank2.getName()));
        }
        this.navigation(values2, player, values.size());
        List<String> values3;
        (values3 = this.summary(profile, null, null, null, null))
                .addAll(this.showing(player, values.size()));
        String text =
                LocaleUtil.tr(player, MenuConfig.GRANT_MENU.RANK_TITLE)
                        .replace("<player>", profile.getNameWithColorNoDisguise());
        return new MultiActionDialog(
                this.common(text, values3),
                values2,
                this.close(player),
                values2.size() > 15 ? 3 : 2);
    }

    @Override
    public void handle(Player player, DialogResponse dialogResponse) {
        IProfile profile;
        if ((profile = this.profilesById.get(player.getUniqueId())) == null) {
            this.dismiss(player);
        } else if (!this.closed(player, dialogResponse)) {
            if (this.turned(player, dialogResponse)) {
                this.matchesEnabled(player, profile);
            } else {
                IRank rank;
                if ((rank = this.findRank(dialogResponse.formatString("rank", null, 128))) != null
                        && this.allowed(player, profile, rank)) {
                    player.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(
                                                    player,
                                                    MessageConfig.GRANT.GRANT_PROCEDURE_RANK_SET)
                                            .replace(
                                                    "<rank>",
                                                    Color.translate(rank.getDisplayName()))));
                    GrantScopeDialog grantScopeDialog;
                    if ((grantScopeDialog =
                                            super.plugin
                                                    .getDialogHandler()
                                                    .findPhoenixDialog(GrantScopeDialog.class))
                                    == null
                            || !grantScopeDialog.matchesEnabled(player, profile, rank)) {
                        super.plugin
                                .getMenuHandler()
                                .openMenu(new GrantScopeMenu(profile, rank), player);
                    }
                } else {
                    this.dismiss(player);
                }
            }
        }
    }

    private String formatString(IRank rank, boolean matches) {
        ArrayList<String> values;
        (values = new ArrayList()).add("&7Priority: &f" + rank.getPriority());
        values.add("&7Prefix: &f" + Color.translate(rank.getPrefix()));
        values.add("&7Suffix: &f" + Color.translate(rank.getSuffix()));
        if (!matches) {
            values.add("&cYou do not have access to this rank.");
        }
        return this.tooltip(values);
    }

    private IRank findRank(String text) {
        if (text == null) {
            return null;
        }
        Iterator<IRank> iterator2 =
                super.plugin.getRankHandler().getSortedRanksReversed().iterator();
        while (iterator2.hasNext()) {
            IRank rank;
            if ((rank = (IRank) iterator2.next()).getName().equalsIgnoreCase(text)) {
                return rank;
            }
        }
        return null;
    }
}
