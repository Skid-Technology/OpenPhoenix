package xyz.refinedev.phoenix.menu.api.menus;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;
import xyz.refinedev.phoenix.menu.api.TypeCallback;
import xyz.refinedev.phoenix.menu.api.button.ConfirmationButton;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConfirmMenu extends Menu {

    private String title;

    private TypeCallback<Boolean> response;

    private boolean closeAfterResponse;

    private Button[] centerButtons;

    public ConfirmMenu(
            String title,
            TypeCallback<Boolean> typeCallback,
            boolean closeAfterResponse,
            Button... button) {
        this.title = title;
        this.response = typeCallback;
        this.closeAfterResponse = closeAfterResponse;
        this.centerButtons = button;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        if (this.centerButtons != null) {
            for (int index = 0; index < this.centerButtons.length; index++) {
                if (this.centerButtons[index] != null) {
                    entries.put(this.getSlot(4, index), this.centerButtons[index]);
                }
            }
        }
        for (int index2 = 0; index2 < 45; index2++) {
            if (!entries.containsKey(index2)) {
                if (Arrays.asList(10, 11, 12, 19, 20, 21, 28, 29, 30).contains(index2)) {
                    entries.put(
                            index2,
                            new ConfirmationButton(
                                    false, this.response, this.closeAfterResponse, this));
                } else if (Arrays.asList(14, 15, 16, 23, 24, 25, 32, 33, 34).contains(index2)) {
                    entries.put(
                            index2,
                            new ConfirmationButton(
                                    true, this.response, this.closeAfterResponse, this));
                } else {
                    entries.put(index2, Button.placeholder());
                }
            }
        }
        return entries;
    }

    @Override
    public String getTitle(Player player) {
        return this.title;
    }
}
