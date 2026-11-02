package xyz.refinedev.phoenix.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.utils.item.ItemBuilder;
import xyz.refinedev.phoenix.utils.item.ItemUtils;

import java.util.ArrayList;
import java.util.List;

public class ModModeItem {

    private String name = "";

    private String item = "STONE";

    private int durability;

    private int slot;

    private ModModeItem.Action action = new ModModeItem.Action(false, "");

    private ModModeItem.Command command = new ModModeItem.Command(false, "");

    private List<String> lore = new ArrayList<>();

    public ItemStack build() {
        String text = this.name;
        Material material;
        try {
            material = ItemUtils.parseFromX(this.item).parseMaterial();
        } catch (Exception exception) {
            material = xyz.refinedev.lib.xseries.XMaterial.REDSTONE.parseMaterial();
            text = Color.translate("&cItem Error");
        }
        ItemBuilder itemBuilder;
        (itemBuilder = new ItemBuilder(material)).setName(text);
        itemBuilder.setDurability(this.durability);
        itemBuilder.setLore(this.lore);
        return itemBuilder.toItemStack();
    }

    public int getIndex() {
        return this.slot - 1;
    }

    public static ModModeItem.Action action(String text) {
        return new ModModeItem.Action(true, text);
    }

    public static ModModeItem.Command noCommand() {
        return new ModModeItem.Command(false, "");
    }

    public ModModeItem() {}

    public ModModeItem(
            String name,
            String item,
            int durability,
            int slot,
            ModModeItem.Action action2,
            ModModeItem.Command command2,
            List<String> lore) {
        this.name = name;
        this.item = item;
        this.durability = durability;
        this.slot = slot;
        this.action = action2;
        this.command = command2;
        this.lore = lore;
    }

    public String getName() {
        return this.name;
    }

    public String getItem() {
        return this.item;
    }

    public int getDurability() {
        return this.durability;
    }

    public int getSlot() {
        return this.slot;
    }

    public ModModeItem.Action getAction() {
        return this.action;
    }

    public ModModeItem.Command getCommand() {
        return this.command;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setAction(ModModeItem.Action action2) {
        this.action = action2;
    }

    public void setCommand(ModModeItem.Command command2) {
        this.command = command2;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public static class Action {

        private final boolean enabled;

        private final String action;

        public Action(boolean enabled, String action) {
            this.enabled = enabled;
            this.action = action;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public String getAction() {
            return this.action;
        }
    }

    public static class Command {

        private final boolean enabled;

        private final String command;

        public Command(boolean enabled, String command) {
            this.enabled = enabled;
            this.command = command;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public String getCommand() {
            return this.command;
        }
    }
}
