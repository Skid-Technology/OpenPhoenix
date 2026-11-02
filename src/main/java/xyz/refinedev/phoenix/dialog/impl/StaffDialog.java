package xyz.refinedev.phoenix.dialog.impl;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button.ActionButton;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.dialog.api.DialogResponse;
import xyz.refinedev.phoenix.dialog.api.PhoenixDialog;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class StaffDialog extends PhoenixDialog {

    protected static final int PER_PAGE = 20;

    protected static final String KEY_PAGE = "page";

    private final Map<UUID, Integer> pages = new ConcurrentHashMap<>();

    public StaffDialog(String text) {
        super(text);
    }

    protected <T> List<T> page(Player player, List<T> values) {
        if (values.size() <= 20) {
            this.pages.remove(player.getUniqueId());
            return values;
        } else {
            int value = (values.size() - 1) / 20;
            int value2 =
                    Math.min(Math.max(this.pages.getOrDefault(player.getUniqueId(), 0), 0), value);
            this.pages.put(player.getUniqueId(), value2);
            int value3 = value2 * 20;
            return values.subList(value3, Math.min(value3 + 20, values.size()));
        }
    }

    protected void navigation(List<ActionButton> values, Player player, int value) {
        if (value > 20) {
            int value2 = (value - 1) / 20;
            int value3;
            if ((value3 =
                            Math.min(
                                    Math.max(this.pages.getOrDefault(player.getUniqueId(), 0), 0),
                                    value2))
                    > 0) {
                values.add(
                        this.button(
                                LocaleUtil.tr(player, MenuConfig.GENERAL.FORM.PREVIOUS)
                                        .replace("<page>", String.valueOf(value3))
                                        .replace("<pages>", String.valueOf(value2 + 1)),
                                null,
                                "page",
                                String.valueOf(value3 - 1)));
            }
            if (value3 < value2) {
                values.add(
                        this.button(
                                LocaleUtil.tr(player, MenuConfig.GENERAL.FORM.NEXT)
                                        .replace("<page>", String.valueOf(value3 + 2))
                                        .replace("<pages>", String.valueOf(value2 + 1)),
                                null,
                                "page",
                                String.valueOf(value3 + 1)));
            }
        }
    }

    protected List<String> showing(Player player, int value) {
        if (value <= 20) {
            return Collections.emptyList();
        }
        int value2 = (value - 1) / 20;
        int value3 =
                Math.min(Math.max(this.pages.getOrDefault(player.getUniqueId(), 0), 0), value2)
                        * 20;
        return Collections.singletonList(
                LocaleUtil.tr(player, MenuConfig.GENERAL.FORM.SHOWING)
                        .replace("<from>", String.valueOf(value3 + 1))
                        .replace("<to>", String.valueOf(Math.min(value3 + 20, value)))
                        .replace("<total>", String.valueOf(value)));
    }

    protected boolean turned(Player player, DialogResponse dialogResponse) {
        long value;
        if ((value = dialogResponse.calculateTime("page", -1L, 0L, 2147483647L)) < 0L) {
            return false;
        }
        this.pages.put(player.getUniqueId(), (int) value);
        return true;
    }

    protected void forgetPage(UUID uUID) {
        this.pages.remove(uUID);
    }
}
