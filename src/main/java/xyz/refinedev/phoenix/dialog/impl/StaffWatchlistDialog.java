package xyz.refinedev.phoenix.dialog.impl;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.CommonDialogData;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.Dialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.DialogAction;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.MultiActionDialog;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.dialog.api.DialogResponse;
import xyz.refinedev.phoenix.handler.IDatabaseHandler;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StaffWatchlistDialog extends StaffDialog {

    private static final String text = "player";

    private final Phoenix plugin;

    private final Map<UUID, Map<IProfile, Integer>> integersById = new ConcurrentHashMap<>();

    public StaffWatchlistDialog(Phoenix phoenix) {
        super("staff-watchlist");
        this.plugin = phoenix;
    }

    public boolean matchesEnabled(Player player, Map<IProfile, Integer> entries) {
        this.integersById.put(player.getUniqueId(), entries);
        if (this.plugin.getDialogHandler().matchesEnabled(player, this, null)) {
            return true;
        }
        this.integersById.remove(player.getUniqueId());
        return false;
    }

    @Override
    public void forget(UUID uUID) {
        this.integersById.remove(uUID);
        this.forgetPage(uUID);
    }

    @Override
    public Dialog build(Player player) {
        Map<IProfile, Integer> entries;
        if ((entries = this.integersById.get(player.getUniqueId())) != null && !entries.isEmpty()) {
            ArrayList values = new ArrayList(entries.entrySet());
            ArrayList<
                            xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog
                                    .button.ActionButton>
                    values2 = new ArrayList<>();
            Iterator iterator2 = this.page(player, values).iterator();
            while (iterator2.hasNext()) {
                Entry entry;
                IProfile profile = (IProfile) (entry = (Entry) iterator2.next()).getKey();
                ArrayList<String> values3 = new ArrayList<>();
                for (String text : LocaleUtil.tr(player, MenuConfig.WATCHLIST_MENU.LORE)) {
                    values3.add(
                            text.replace("<reports>", String.valueOf(entry.getValue()))
                                    .replace(
                                            "<status>",
                                            this.plugin
                                                            .getNetworkHandler()
                                                            .isOnline(profile.getUuid())
                                                    ? "Online"
                                                    : "Offline"));
                }
                values2.add(
                        this.button(
                                LocaleUtil.tr(player, MenuConfig.WATCHLIST_MENU.NAME)
                                        .replace("<player>", profile.getNameWithColorNoDisguise()),
                                String.join("\n", values3),
                                "player",
                                profile.getUniqueId().toString()));
            }
            this.navigation(values2, player, values.size());
            CommonDialogData commonDialogData =
                    new CommonDialogData(
                            Color.translateComponent(
                                    LocaleUtil.tr(player, MenuConfig.WATCHLIST_MENU.TITLE)),
                            null,
                            true,
                            false,
                            DialogAction.NONE,
                            this.body(this.showing(player, values.size())),
                            Collections.emptyList());
            return new MultiActionDialog(commonDialogData, values2, this.close(player), 1);
        } else {
            return null;
        }
    }

    @Override
    public void handle(Player player, DialogResponse dialogResponse) {
        Map<IProfile, Integer> entries;
        if ((entries = this.integersById.get(player.getUniqueId())) == null) {
            this.dismiss(player);
        } else if (!this.closed(player, dialogResponse)) {
            if (this.turned(player, dialogResponse)) {
                this.plugin.getDialogHandler().matchesEnabled(player, this, null);
            } else {
                String text;
                if ((text = dialogResponse.formatString("player", null, 64)) == null) {
                    this.dismiss(player);
                } else {
                    Iterator iterator2 = entries.keySet().iterator();
                    while (iterator2.hasNext()) {
                        IProfile profile;
                        if ((profile = (IProfile) iterator2.next())
                                .getUniqueId()
                                .toString()
                                .equals(text)) {
                            IDatabaseHandler databaseHandler = this.plugin.getDatabaseHandler();
                            IProfile profile2 = profile;
                            databaseHandler.runRedisCommand(
                                    result ->
                                            result.del(
                                                    new String[] {
                                                        "watchlist:" + profile2.getUniqueId()
                                                    }));
                            entries.remove(profile);
                            break;
                        }
                    }
                    if (entries.isEmpty()) {
                        this.integersById.remove(player.getUniqueId());
                        this.dismiss(player);
                    } else {
                        this.plugin.getDialogHandler().matchesEnabled(player, this, null);
                    }
                }
            }
        }
    }
}
