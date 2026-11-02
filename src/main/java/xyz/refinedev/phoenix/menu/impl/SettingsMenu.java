package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.filter.FilterablePaginatedMenu;
import xyz.refinedev.phoenix.menu.api.pagination.filter.PageFilter;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.settings.api.PlayerSettings;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SettingsMenu extends FilterablePaginatedMenu<PlayerSettings> {

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.SETTINGS_MENU.TITLE);
    }

    @Override
    public void onClose(Player player) {
        if (!this.isClosedByMenu()) {
            IProfile profile;
            if ((profile =
                            Phoenix.getInstance()
                                    .getProfileHandler()
                                    .getCachedProfile(player.getUniqueId()))
                    == null) {
                return;
            }
            profile.saveAndUpdate(false);
        }
    }

    @Override
    public List<PageFilter<PlayerSettings>> generateFilters(Player player) {
        ArrayList<PageFilter<PlayerSettings>> values = new ArrayList<>();
        Stream<PlayerSettings> stream2 =
                super.plugin.getPlayerSettings().stream()
                        .sorted(Comparator.comparingInt(PlayerSettings::getPriority));
        for (String text :
                stream2.filter(result -> result.hasPermission(player))
                        .map(PlayerSettings::name)
                        .distinct()
                        .collect(Collectors.toList())) {
            values.add(new PageFilter<>(text, result -> result.name().equalsIgnoreCase(text)));
        }
        return values;
    }

    @Override
    public Map<Integer, Button> getFilteredButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        List<PlayerSettings> values2 =
                super.plugin.getPlayerSettings().stream()
                        .sorted(Comparator.comparingInt(PlayerSettings::getPriority))
                        .collect(Collectors.toList());
        int value = 0;
        for (PlayerSettings playerSettings : values2) {
            if (this.passesFilter(player, playerSettings) && playerSettings.hasPermission(player)) {
                for (Button button : playerSettings.getButtons(player).values()) {
                    entries.put(value, button);
                    value++;
                }
            }
        }
        return entries;
    }
}
