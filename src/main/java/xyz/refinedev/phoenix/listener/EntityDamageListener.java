package xyz.refinedev.phoenix.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {

    private static final String GOD_MODE_TEXT = "GodMode";

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent entityDamageEvent) {
        if (entityDamageEvent.getEntity() instanceof Player) {
            if (((Player) entityDamageEvent.getEntity()).hasMetadata(GOD_MODE_TEXT)) {
                entityDamageEvent.setCancelled(true);
            }
        }
    }
}
