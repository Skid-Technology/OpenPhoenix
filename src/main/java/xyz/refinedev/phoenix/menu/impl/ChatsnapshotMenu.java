package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.menu.button.ChatSnapshotButton;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatSnapshot;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatsnapshotMenu extends PaginatedMenu {

    private final List<IChatSnapshot> chatSnapshots;

    private final boolean enabled;

    @Override
    public String getTitle(Player player) {
        return this.enabled
                ? LocaleUtil.tr(player, MenuConfig.CHATSNAPSHOT_MENU.OWN_MENU_TITLE)
                : LocaleUtil.tr(player, MenuConfig.CHATSNAPSHOT_MENU.MENU_TITLE);
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        this.backButton(entries, 4);
        return entries;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (IChatSnapshot chatSnapshot : this.chatSnapshots) {
            entries.put(entries.size(), new ChatSnapshotButton(chatSnapshot, this.enabled));
        }
        return entries;
    }

    public ChatsnapshotMenu(List<IChatSnapshot> chatSnapshots, boolean enabled) {
        this.chatSnapshots = chatSnapshots;
        this.enabled = enabled;
    }
}
