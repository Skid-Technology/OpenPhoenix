package xyz.refinedev.phoenix.config.provider;

import xyz.refinedev.lib.boostedyaml.block.implementation.Section;
import xyz.refinedev.lib.config.provider.TypeProvider;
import xyz.refinedev.lib.config.provider.context.LoadContext;
import xyz.refinedev.lib.config.provider.context.SaveContext;
import xyz.refinedev.phoenix.utils.ModModeItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModModeItemProvider implements TypeProvider<ModModeItem> {

    public ModModeItem load(LoadContext loadContext) {
        Map entries;
        if ((entries = this.toMap(loadContext.getObject())) == null) {
            throw new IllegalArgumentException(
                    "The mod mode item '" + loadContext.getKey() + "' must be a section.");
        }
        ModModeItem modModeItem;
        (modModeItem = new ModModeItem()).setName(this.string(entries, "name", "&fItem"));
        modModeItem.setItem(this.string(entries, "item", "STONE"));
        modModeItem.setDurability(this.number(entries, "durability"));
        modModeItem.setSlot(this.number(entries, "slot"));
        modModeItem.setLore(this.strings(entries, "lore"));
        Map entries2 = this.section(entries, "action");
        modModeItem.setAction(
                new ModModeItem.Action(
                        this.bool(entries2, "enabled"), this.string(entries2, "action", "")));
        Map entries3 = this.section(entries, "command");
        modModeItem.setCommand(
                new ModModeItem.Command(
                        this.bool(entries3, "enabled"), this.string(entries3, "command", "")));
        return modModeItem;
    }

    public Object save(SaveContext<ModModeItem> saveContext) {
        ModModeItem modModeItem = (ModModeItem) saveContext.getObject();
        LinkedHashMap entries;
        (entries = new LinkedHashMap()).put("name", modModeItem.getName());
        entries.put("item", modModeItem.getItem());
        entries.put("durability", modModeItem.getDurability());
        entries.put("slot", modModeItem.getSlot());
        LinkedHashMap entries2;
        (entries2 = new LinkedHashMap()).put("enabled", modModeItem.getAction().isEnabled());
        entries2.put("action", modModeItem.getAction().getAction());
        entries.put("action", entries2);
        LinkedHashMap entries3;
        (entries3 = new LinkedHashMap()).put("enabled", modModeItem.getCommand().isEnabled());
        entries3.put("command", modModeItem.getCommand().getCommand());
        entries.put("command", entries3);
        entries.put("lore", modModeItem.getLore());
        return entries;
    }

    private Map<String, Object> section(Map<String, Object> entries, String text) {
        Map entries2;
        return (entries2 = this.toMap(entries.get(text))) == null
                ? new LinkedHashMap<>()
                : entries2;
    }

    private Map<String, Object> toMap(Object result) {
        if (result instanceof Section) {
            return ((Section) result).getStringRouteMappedValues(false);
        } else {
            return result instanceof Map ? (Map) result : null;
        }
    }

    private String string(Map<String, Object> entries, String text, String text2) {
        Object result;
        return (result = entries.get(text)) == null ? text2 : String.valueOf(result);
    }

    private int number(Map<String, Object> entries, String text) {
        Object result;
        return (result = entries.get(text)) instanceof Number ? ((Number) result).intValue() : 0;
    }

    private boolean bool(Map<String, Object> entries, String text) {
        Object result;
        return (result = entries.get(text)) instanceof Boolean && (Boolean) result;
    }

    private List<String> strings(Map<String, Object> entries, String text) {
        Object result;
        if (!((result = entries.get(text)) instanceof List)) {
            return new ArrayList<>();
        }
        ArrayList<String> values = new ArrayList<>();
        for (Object result2 : (List) result) {
            values.add(String.valueOf(result2));
        }
        return values;
    }
}
