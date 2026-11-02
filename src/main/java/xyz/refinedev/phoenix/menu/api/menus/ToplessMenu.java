package xyz.refinedev.phoenix.menu.api.menus;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class ToplessMenu extends Menu {

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        Iterator<Entry<Integer, Button>> iterator2 =
                this.getAllButtons(player).entrySet().iterator();
        while (iterator2.hasNext()) {
            Entry entry;
            int value;
            if ((value = (Integer) (entry = (Entry) iterator2.next()).getKey() + 8) > 8) {
                entries.put(value, (Button) entry.getValue());
            }
        }
        Map<Integer, Button> entries2;
        if ((entries2 = this.getTopLevelButtons(player)) != null) {
            Iterator iterator3 = entries2.entrySet().iterator();
            while (iterator3.hasNext()) {
                int value2;
                Entry entry2;
                if ((value2 = (Integer) (entry2 = (Entry) iterator3.next()).getKey()) <= 8) {
                    entries.put(value2, (Button) entry2.getValue());
                }
            }
        }
        return entries;
    }

    public Map<Integer, Button> getTopLevelButtons(Player player) {
        return null;
    }

    public abstract Map<Integer, Button> getAllButtons(Player player);
}
