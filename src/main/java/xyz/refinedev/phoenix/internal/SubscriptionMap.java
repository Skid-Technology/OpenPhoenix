package xyz.refinedev.phoenix.internal;

import lombok.NonNull;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import xyz.refinedev.phoenix.Phoenix;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;

public final class SubscriptionMap implements Map<String, Map<Permissible, Boolean>> {

    public final Phoenix plugin;

    private final Map<Permissible, Set<String>> stringsByPermissible =
            Collections.synchronizedMap(new WeakHashMap<>());

    private static final String text = "Failed to setup PhoenixSubscriptionMap:";

    public SubscriptionMap(Phoenix phoenix, Map<String, Map<Permissible, Boolean>> entries) {
        this.plugin = phoenix;
        try {
            Iterator<Entry<String, Map<Permissible, Boolean>>> iterator2 =
                    entries.entrySet().iterator();
            while (iterator2.hasNext()) {
                Entry entry;
                ((Map<Permissible, Boolean>) (entry = (Entry) iterator2.next()).getValue())
                        .keySet()
                        .forEach(result -> this.applyPermissible(result, (String) entry.getKey()));
            }
        } catch (Exception exception) {
            phoenix.getLogger().log(Level.SEVERE, text, exception);
        }
    }

    public Map<Permissible, Boolean> get(Object result) {
        return new SubscriptionMap.SubscriptionValues((String) result);
    }

    public void applyPermissible(Permissible permissible, String text) {
        if (!(permissible instanceof Player)) {
            this.stringsByPermissible
                    .computeIfAbsent(
                            permissible, result -> Collections.synchronizedSet(new HashSet<>()))
                    .add(text);
        }
    }

    public boolean remove(Permissible permissible, String text) {
        if (permissible instanceof Player) {
            return false;
        }
        Set<String> values;
        return (values = this.stringsByPermissible.get(permissible)) == null
                ? false
                : values.remove(text);
    }

    @NonNull
    public Set<Permissible> findSet(String text) {
        Collection onlinePlayers = this.plugin.getServer().getOnlinePlayers();
        HashSet<Permissible> values =
                new HashSet<>(onlinePlayers.size() + this.stringsByPermissible.size());
        this.stringsByPermissible.forEach(
                (result, result2) -> {
                    if (result2.contains(text)) {
                        values.add(result);
                    }
                });
        Iterator iterator2 = onlinePlayers.iterator();
        while (iterator2.hasNext()) {
            Player player;
            if ((player = (Player) iterator2.next()).hasPermission(text)
                    || player.isPermissionSet(text)) {
                values.add(player);
            }
        }
        return values;
    }

    public Map<String, Map<Permissible, Boolean>> forEach() {
        HashMap<String, Map<Permissible, Boolean>> entries = new HashMap<>();
        this.stringsByPermissible.forEach(
                (result, result2) -> {
                    for (String text : (Iterable<String>) ((Iterable) result2)) {
                        entries.computeIfAbsent(text, result3 -> new WeakHashMap<>())
                                .put(result, true);
                    }
                });
        return entries;
    }

    public Map<Permissible, Boolean> put(String text, Map<Permissible, Boolean> entries) {
        throw new UnsupportedOperationException();
    }

    public Map<Permissible, Boolean> remove(Object result) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ? extends Map<Permissible, Boolean>> entries) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Map<Permissible, Boolean>> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<String, Map<Permissible, Boolean>>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object result) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsValue(Object result) {
        throw new UnsupportedOperationException();
    }

    public final class SubscriptionValues implements Map<Permissible, Boolean> {

        private final String text;

        public SubscriptionValues(String text) {
            this.text = text;
        }

        public Boolean put(Permissible permissible, Boolean matches) {
            SubscriptionMap.this.applyPermissible(permissible, this.text);
            return null;
        }

        public Boolean remove(Object result) {
            Permissible permissible = (Permissible) result;
            return SubscriptionMap.this.remove(permissible, this.text) ? true : null;
        }

        @NonNull
        @Override
        public Set<Permissible> keySet() {
            return SubscriptionMap.this.findSet(this.text);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public void putAll(Map<? extends Permissible, ? extends Boolean> entries) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<Boolean> values() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<Entry<Permissible, Boolean>> entrySet() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey(Object result) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsValue(Object result) {
            throw new UnsupportedOperationException();
        }

        public Boolean get(Object result) {
            throw new UnsupportedOperationException();
        }
    }
}
