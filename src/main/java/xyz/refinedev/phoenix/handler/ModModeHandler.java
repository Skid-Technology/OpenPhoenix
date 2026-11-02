package xyz.refinedev.phoenix.handler;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.ModModeConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.ModModeItem;
import xyz.refinedev.phoenix.utils.PlayerStateSnapshot;
import xyz.refinedev.phoenix.utils.events.player.PlayerModModeUpdateEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public class ModModeHandler {

    private final Phoenix plugin;

    private final List<ModModeItem> modModeItems = new ArrayList<>();

    private final Map<UUID, PlayerStateSnapshot> playerStateSnapshotsById = new HashMap<>();

    public ModModeHandler(Phoenix phoenix) {
        this.plugin = phoenix;
        this.initialize();
    }

    public ModModeItem findModModeItem(ItemStack itemStack) {
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            String displayName = itemStack.getItemMeta().getDisplayName();
            Stream<ModModeItem> stream2 = this.modModeItems.stream();
            stream2 = stream2.filter(result -> matchesEnabled(result.getName(), displayName));
            return stream2.filter(result -> result.build().getType() == itemStack.getType())
                    .findFirst()
                    .orElse(null);
        } else {
            return null;
        }
    }

    static boolean matchesEnabled(String text, String text2) {
        return Color.stripColor(Color.translate(text)).equals(Color.stripColor(text2));
    }

    public ModModeItem getModModeItem() {
        return this.modModeItems.stream()
                .filter(result -> result.getAction().isEnabled())
                .filter(result -> result.getAction().getAction().equalsIgnoreCase("disable-vanish"))
                .findFirst()
                .orElse(null);
    }

    public ModModeItem getModModeItemByModModeItems() {
        return this.modModeItems.stream()
                .filter(result -> result.getAction().isEnabled())
                .filter(result -> result.getAction().getAction().equalsIgnoreCase("enable-vanish"))
                .findFirst()
                .orElse(null);
    }

    public void initialize() {
        this.modModeItems.clear();
        this.modModeItems.addAll(ModModeConfig.ITEMS.values());
    }

    public void sendMessage(Player player) {
        player.setMetadata("ModMode", new FixedMetadataValue(this.plugin, true));
        BukkitAPI.callEvent(new PlayerModModeUpdateEvent(player, true));
        this.playerStateSnapshotsById.remove(player.getUniqueId());
        PlayerStateSnapshot playerStateSnapshot =
                new PlayerStateSnapshot(
                        player.getInventory().getContents(),
                        player.getInventory().getArmorContents(),
                        player.getActivePotionEffects(),
                        player.getExp(),
                        player.getGameMode());
        this.playerStateSnapshotsById.put(player.getUniqueId(), playerStateSnapshot);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.CREATIVE);
        if (SettingsConfig.VANISH.ON_MODMODE) {
            player.sendMessage(
                    Color.translate(LocaleUtil.tr(player, MessageConfig.VANISH.AUTOMATIC)));
            this.plugin.getVanishHandler().vanishPlayer(player);
        }
        this.modModeItems.forEach(
                result -> player.getInventory().setItem(result.getIndex(), result.build()));
        ModModeItem modModeItemByModModeItems = this.getModModeItemByModModeItems();
        ModModeItem modModeItem = this.getModModeItem();
        IProfile profile = this.plugin.getProfileHandler().getProfile(player.getUniqueId());
        if ((SettingsConfig.VANISH.ON_MODMODE || profile.isVanished()) && modModeItem != null) {
            player.getInventory().setItem(modModeItem.getIndex(), modModeItem.build());
        } else if (!profile.isVanished() && modModeItemByModModeItems != null) {
            player.getInventory()
                    .setItem(
                            modModeItemByModModeItems.getIndex(),
                            modModeItemByModModeItems.build());
        }
        player.updateInventory();
    }

    public void sendMessageByFORCE_MODMODE(Player player) {
        if (player.hasMetadata("ModMode")) {
            if (SettingsConfig.VANISH.FORCE_MODMODE
                    && this.plugin.getVanishHandler().isVanished(player)) {
                player.sendMessage(
                        Color.translate(LocaleUtil.tr(player, MessageConfig.VANISH.MUST_UNVANISH)));
            } else {
                player.removeMetadata("ModMode", this.plugin);
                BukkitAPI.callEvent(new PlayerModModeUpdateEvent(player, false));
                if (SettingsConfig.VANISH.ON_MODMODE) {
                    player.sendMessage(
                            Color.translate(LocaleUtil.tr(player, MessageConfig.VANISH.DISABLED)));
                    this.plugin.getVanishHandler().unvanishPlayer(player);
                }
                PlayerStateSnapshot playerStateSnapshot;
                if ((playerStateSnapshot =
                                this.playerStateSnapshotsById.remove(player.getUniqueId()))
                        == null) {
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);
                    player.updateInventory();
                } else {
                    player.getInventory().setContents(playerStateSnapshot.getItemStacks());
                    player.getInventory()
                            .setArmorContents(playerStateSnapshot.getItemStacksItemStackArray());
                    player.setExp(playerStateSnapshot.getFloatValue());
                    player.setGameMode(playerStateSnapshot.getGameMode());
                    player.updateInventory();
                }
            }
        }
    }
}
