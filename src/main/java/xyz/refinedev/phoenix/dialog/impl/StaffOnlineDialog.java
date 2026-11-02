package xyz.refinedev.phoenix.dialog.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.CommonDialogData;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.Dialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.DialogAction;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.MultiActionDialog;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.dialog.api.DialogResponse;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.Utilities;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class StaffOnlineDialog extends StaffDialog {

    private static final String text = "player";

    private final Phoenix plugin;

    public StaffOnlineDialog(Phoenix phoenix) {
        super("staff-online");
        this.plugin = phoenix;
    }

    public boolean getDialogHandler(Player player) {
        return this.plugin.getDialogHandler().matchesEnabled(player, this, null);
    }

    @Override
    public Dialog build(Player player) {
        ArrayList<Player> values = new ArrayList<>();
        for (Player player2 : Utilities.getOnlinePlayers()) {
            IProfile profile;
            if ((profile = this.plugin.getProfileHandler().getProfile(player2.getUniqueId()))
                            != null
                    && profile.isStaff()) {
                values.add(player2);
            }
        }
        if (values.isEmpty()) {
            return null;
        }
        ArrayList<
                        xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button
                                .ActionButton>
                values2 = new ArrayList<>();
        for (Player player3 : this.page(player, values)) {
            IProfile profile2;
            if ((profile2 = this.plugin.getProfileHandler().getProfile(player3.getUniqueId()))
                    != null) {
                List<xyz.refinedev.phoenix.profile.login.ILogin> logins =
                        this.plugin.getLoginHandler().getLogins(player3.getUniqueId());
                ArrayList<String> values3 = new ArrayList<>();
                for (String text : LocaleUtil.tr(player, MenuConfig.ONLINE_STAFF_MENU.LORE)) {
                    values3.add(
                            text.replace("<isVanished>", profile2.isVanished() ? "&aYes" : "&cNo")
                                    .replace("<rank>", profile2.getGeneralRank().getDisplayName())
                                    .replace("<playtime>", profile2.getLastSeenAgo(logins))
                                    .replace("<target>", profile2.getNameWithColor()));
                }
                values2.add(
                        this.button(
                                profile2.getNameWithColorNoDisguise(),
                                String.join("\n", values3),
                                "player",
                                player3.getUniqueId().toString()));
            }
        }
        this.navigation(values2, player, values.size());
        CommonDialogData commonDialogData =
                new CommonDialogData(
                        Color.translateComponent(
                                LocaleUtil.tr(player, MenuConfig.ONLINE_STAFF_MENU.TITLE)),
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
        if (!player.hasPermission("core.staff")) {
            this.dismiss(player);
        } else if (!this.closed(player, dialogResponse)) {
            if (this.turned(player, dialogResponse)) {
                this.getDialogHandler(player);
            } else {
                String text = dialogResponse.formatString("player", null, 64);
                this.dismiss(player);
                if (text != null) {
                    UUID uUID;
                    try {
                        uUID = UUID.fromString(text);
                    } catch (IllegalArgumentException illegalArgumentException) {
                        return;
                    }
                    Player player2;
                    if ((player2 = Bukkit.getPlayer(uUID)) != null) {
                        IProfile profile;
                        if ((profile = this.plugin.getProfileHandler().getProfile(uUID)) != null
                                && profile.isStaff()) {
                            Player player3 = player2;
                            Tasks.run(
                                    () ->
                                            this.plugin
                                                    .getNmsHandler()
                                                    .getTeleportAPI()
                                                    .teleport(player, player3.getLocation()));
                        }
                    }
                }
            }
        }
    }
}
