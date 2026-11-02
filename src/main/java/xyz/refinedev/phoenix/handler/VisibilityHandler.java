package xyz.refinedev.phoenix.handler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.utils.GsonExclude;
import xyz.refinedev.phoenix.visibility.IVisibilityAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class VisibilityHandler {

    @GsonExclude
    public static String methodName =
            "[85, 111, 57, 118, 90, 53, 86, 113, 70, 56, 97, 80, 81, 50, 109, 119, 88, 72, 87, 100, 79, 78, 50, 48, 69, 52, 86, 55, 77, 50, 67, 101, 95, 114, 108, 105, 67, 122, 54, 73, 57, 51, 89, 99, 77, 109, 98, 121, 115, 49, 85, 108, 80, 78, 83, 78, 101, 114, 54, 89, 77, 75, 82, 56]";

    private final List<IVisibilityAdapter> adapters = new ArrayList<>();

    public void addAdapter(IVisibilityAdapter visibilityAdapter) {
        this.adapters.add(visibilityAdapter);
    }

    public boolean canSee(UUID uUID, UUID uUID2) {
        Iterator<IVisibilityAdapter> iterator2 = this.adapters.iterator();
        while (iterator2.hasNext()) {
            IVisibilityAdapter visibilityAdapter;
            if ((visibilityAdapter = (IVisibilityAdapter) iterator2.next()).isOther()
                    && !visibilityAdapter.canSee(uUID, uUID2)) {
                return false;
            }
        }
        return true;
    }

    public boolean canSeeOther(UUID uUID, UUID uUID2) {
        Iterator<IVisibilityAdapter> iterator2 = this.adapters.iterator();
        while (iterator2.hasNext()) {
            if (!((IVisibilityAdapter) iterator2.next()).canSee(uUID, uUID2)) {
                return false;
            }
        }
        return true;
    }

    public boolean canSee(Player player, Player player2) {
        Iterator<IVisibilityAdapter> iterator2 = this.adapters.iterator();
        while (iterator2.hasNext()) {
            IVisibilityAdapter visibilityAdapter;
            if ((visibilityAdapter = (IVisibilityAdapter) iterator2.next()).isOther()
                    && !visibilityAdapter.canSee(player, player2)) {
                return false;
            }
        }
        return true;
    }

    public boolean canSeeOther(Player player, Player player2) {
        Iterator<IVisibilityAdapter> iterator2 = this.adapters.iterator();
        while (iterator2.hasNext()) {
            if (!((IVisibilityAdapter) iterator2.next()).canSee(player, player2)) {
                return false;
            }
        }
        return true;
    }

    public Set<Player> getAllVisibleTo(Player player) {
        HashSet<Player> values = new HashSet<>();
        for (Player player2 : Bukkit.getOnlinePlayers()) {
            if (this.canSee(player, player2)) {
                values.add(player2);
            }
        }
        return values;
    }

    public Set<Player> getAllVisibleToOther(Player player) {
        HashSet<Player> values = new HashSet<>();
        for (Player player2 : Bukkit.getOnlinePlayers()) {
            if (this.canSeeOther(player, player2)) {
                values.add(player2);
            }
        }
        return values;
    }

    public Set<String> getAllVisibleToNamesOnNetwork(Player player) {
        Phoenix instance = Phoenix.getInstance();
        HashSet<String> values = new HashSet<>();
        for (UUID uUID : instance.getNetworkHandler().getAllUuids()) {
            String text;
            if ((player == null || this.canSee(player.getUniqueId(), uUID))
                    && (text = instance.getUuidCache().getName(uUID)) != null
                    && !text.isEmpty()) {
                values.add(text);
            }
        }
        return values;
    }

    public Set<String> getAllVisibleToNames(Player player) {
        HashSet<String> values = new HashSet<>();
        for (Player player2 : Bukkit.getOnlinePlayers()) {
            if (this.canSee(player, player2)) {
                values.add(player2.getName());
            }
        }
        return values;
    }

    public Set<Player> getAllInvisibleTo(Player player) {
        HashSet<Player> values = new HashSet<>();
        for (Player player2 : Bukkit.getOnlinePlayers()) {
            if (!this.canSee(player, player2)) {
                values.add(player2);
            }
        }
        return values;
    }

    public Set<Player> getAllInvisibleToOther(Player player) {
        HashSet<Player> values = new HashSet<>();
        for (Player player2 : Bukkit.getOnlinePlayers()) {
            if (!this.canSeeOther(player, player2)) {
                values.add(player2);
            }
        }
        return values;
    }

    public List<IVisibilityAdapter> getAdapters() {
        return this.adapters;
    }
}
