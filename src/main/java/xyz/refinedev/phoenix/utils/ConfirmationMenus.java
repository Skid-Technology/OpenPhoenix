package xyz.refinedev.phoenix.utils;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.dialog.api.PhoenixDialog;
import xyz.refinedev.phoenix.dialog.impl.ConfirmDialog;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.TypeCallback;
import xyz.refinedev.phoenix.menu.api.menus.ConfirmMenu;

public final class ConfirmationMenus {

    private static final String text = "confirm";

    private ConfirmationMenus() {}

    public static void applyPhoenix(
            @NotNull Phoenix phoenix,
            @NotNull Player player,
            @NotNull String text,
            boolean matches,
            @NotNull TypeCallback<Boolean> typeCallback,
            Button... button) {
        PhoenixDialog phoenixDialog;
        if (!SettingsConfig.MODERN.DIALOGS.CONFIRM
                || button != null && button.length != 0
                || !((phoenixDialog = phoenix.getDialogHandler().findPhoenixDialog("confirm"))
                        instanceof ConfirmDialog)
                || !((ConfirmDialog) phoenixDialog).matchesEnabled(player, text, typeCallback)) {
            phoenix.getMenuHandler()
                    .openMenu(new ConfirmMenu(text, typeCallback, matches, button), player);
        }
    }
}
