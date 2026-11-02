package xyz.refinedev.phoenix.utils.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.general.WoolUtil;
import xyz.refinedev.phoenix.utils.version.VersionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ItemBuilder {

    private ItemStack is;

    private static final String SET_MAP_VIEW_TEXT = "setMapView";

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(XMaterial xMaterial) {
        this(xMaterial.get(), 1);
    }

    public ItemBuilder(XMaterial xMaterial, int value) {
        this(xMaterial.get(), value);
    }

    public ItemBuilder(ItemStack itemStack) {
        this.is = itemStack;
    }

    public ItemBuilder(String text) {
        this(ItemUtils.parseFromX(text).get(), 1);
        int value;
        if ((value = ItemUtils.modelDataOf(text)) >= 0) {
            this.setCustomModelData(value);
        }
        String text2;
        if ((text2 = ItemUtils.itemModelOf(text)) != null) {
            this.setItemModel(text2);
        }
        String text3;
        if ((text3 = ItemUtils.tooltipStyleOf(text)) != null) {
            this.setTooltipStyle(text3);
        }
    }

    public ItemBuilder setCustomModelData(int value) {
        if (this.isAir()) {
            return this;
        }
        ItemMeta itemMeta;
        return ItemComponentUtil.setCustomModelData(itemMeta = this.is.getItemMeta(), value)
                ? this.updatedMeta(itemMeta)
                : this;
    }

    public ItemBuilder setItemModel(String text) {
        if (this.isAir()) {
            return this;
        }
        ItemMeta itemMeta;
        return ItemComponentUtil.setItemModel(itemMeta = this.is.getItemMeta(), text)
                ? this.updatedMeta(itemMeta)
                : this;
    }

    public ItemBuilder setTooltipStyle(String text) {
        if (this.isAir()) {
            return this;
        }
        ItemMeta itemMeta;
        return ItemComponentUtil.setTooltipStyle(itemMeta = this.is.getItemMeta(), text)
                ? this.updatedMeta(itemMeta)
                : this;
    }

    public ItemBuilder(Material material, int value) {
        this.is = new ItemStack(material, value);
    }

    public ItemBuilder(Material material, int value, short value2) {
        this.is = new ItemStack(material, value, value2);
    }

    public ItemBuilder(Material material, int value, byte value2) {
        this.is = new ItemStack(material, value, value2);
    }

    public ItemBuilder clone() {
        return new ItemBuilder(this.is);
    }

    public ItemBuilder setDurability(short value) {
        this.is.setDurability(value);
        return this;
    }

    public ItemBuilder flags(ItemFlag... itemFlag) {
        if (this.isAir()) {
            return this;
        }
        ItemMeta itemMeta;
        (itemMeta = this.is.getItemMeta()).addItemFlags(itemFlag);
        return this.updatedMeta(itemMeta);
    }

    public ItemBuilder setDurability(int value) {
        if (this.isAir()) {
            return this;
        }
        if (VersionUtil.canHex()) {
            ItemStack itemStack = this.is;
            if (this.is.getType().equals(XMaterial.WHITE_WOOL.get())) {
                this.is = new ItemStack(WoolUtil.intToWool(value), itemStack.getAmount());
            } else if (this.is.getType().equals(XMaterial.WHITE_TERRACOTTA.get())) {
                this.is = new ItemStack(WoolUtil.intToTerracota(value), itemStack.getAmount());
            } else if (this.is.getType().equals(XMaterial.WHITE_STAINED_GLASS_PANE.get())) {
                this.is = new ItemStack(WoolUtil.intToPanes(value), itemStack.getAmount());
            } else if (this.is.getType().equals(XMaterial.INK_SAC.get())) {
                this.is = new ItemStack(WoolUtil.intToDye(value), itemStack.getAmount());
            } else if (this.is.getType().equals(XMaterial.WHITE_BED.get())) {
                this.is = new ItemStack(WoolUtil.intToBed(value), itemStack.getAmount());
            } else {
                this.is.setDurability((short) value);
            }
            this.is.setItemMeta(itemStack.getItemMeta());
        } else {
            this.is.setDurability((short) value);
        }
        return this;
    }

    public ItemBuilder setName(String text) {
        if (this.isAir()) {
            return this;
        }
        ItemMeta itemMeta;
        (itemMeta = this.is.getItemMeta()).setDisplayName(Color.translate(text));
        return this.updatedMeta(itemMeta);
    }

    public ItemBuilder setUnColoredName(String text) {
        if (this.isAir()) {
            return this;
        }
        ItemMeta itemMeta;
        (itemMeta = this.is.getItemMeta()).setDisplayName(text);
        return this.updatedMeta(itemMeta);
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment enchantment, int value) {
        if (value < 1) {
            return this;
        }
        this.is.addUnsafeEnchantment(enchantment, value);
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        this.is.removeEnchantment(enchantment);
        return this;
    }

    public ItemBuilder setSkullOwner(String text) {
        try {
            SkullMeta skullMeta;
            (skullMeta = (SkullMeta) this.is.getItemMeta()).setOwner(text);
            this.is.setItemMeta(skullMeta);
        } catch (ClassCastException classCastException) {
        }
        return this;
    }

    public ItemBuilder addCustomPotionEffect(PotionEffect potionEffect, boolean matches) {
        try {
            PotionMeta potionMeta;
            (potionMeta = (PotionMeta) this.is.getItemMeta()).setMainEffect(potionEffect.getType());
            potionMeta.addCustomEffect(potionEffect, matches);
            this.is.setItemMeta(potionMeta);
        } catch (ClassCastException classCastException) {
        }
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int value) {
        if (value < 1) {
            return this;
        }
        ItemMeta itemMeta;
        (itemMeta = this.is.getItemMeta()).addEnchant(enchantment, value, true);
        return this.updatedMeta(itemMeta);
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int value) {
        if (value < 1) {
            return this;
        }
        this.is.addEnchantment(enchantment, value);
        return this;
    }

    public ItemBuilder addEnchantments(Map<Enchantment, Integer> entries) {
        this.is.addEnchantments(entries);
        return this;
    }

    public ItemBuilder setInfinityDurability() {
        this.is.setDurability((short) 32767);
        return this;
    }

    public ItemBuilder removeLoreLine(String text) {
        if (this.isAir()) {
            return this;
        }
        List<String> values;
        if (!(values = this.getLore()).contains(text)) {
            return this;
        }
        values.remove(text);
        return this.setUntranslatedLore(values);
    }

    public ItemBuilder removeLoreLine(int value) {
        if (this.isAir()) {
            return this;
        } else {
            List<String> lore = this.getLore();
            if (value >= 0 && value <= lore.size()) {
                lore.remove(value);
                return this.setUntranslatedLore(lore);
            } else {
                return this;
            }
        }
    }

    public ItemBuilder addLoreLine(String text) {
        if (this.isAir()) {
            return this;
        }
        List<String> values;
        (values = this.getLore()).add(Color.translate(text));
        return this.setUntranslatedLore(values);
    }

    public ItemBuilder addUnTranslatedLoreLine(String text) {
        if (this.isAir()) {
            return this;
        }
        List<String> values;
        (values = this.getLore()).add(text);
        return this.setUntranslatedLore(values);
    }

    public ItemBuilder addLoreLine(String text, int value) {
        if (this.isAir()) {
            return this;
        }
        List<String> values;
        (values = this.getLore()).set(value, Color.translate(text));
        return this.setUntranslatedLore(values);
    }

    public ItemBuilder setLeatherArmorColor(org.bukkit.Color color) {
        try {
            LeatherArmorMeta leatherArmorMeta;
            (leatherArmorMeta = (LeatherArmorMeta) this.is.getItemMeta()).setColor(color);
            this.is.setItemMeta(leatherArmorMeta);
        } catch (ClassCastException classCastException) {
        }
        return this;
    }

    public ItemBuilder setMapView(MapView mapView) {
        try {
            MapMeta mapMeta;
            (mapMeta = (MapMeta) this.is.getItemMeta())
                    .getClass()
                    .getMethod(SET_MAP_VIEW_TEXT, MapView.class)
                    .invoke(mapMeta, mapView);
            this.is.setItemMeta(mapMeta);
        } catch (Exception exception) {
        }
        return this;
    }

    public ItemStack toItemStack() {
        if (!Phoenix.getInstance().is1point7()) {
            this.flags(
                    ItemFlag.HIDE_ENCHANTS,
                    ItemFlag.HIDE_ATTRIBUTES,
                    ItemFlag.HIDE_UNBREAKABLE,
                    ItemFlag.HIDE_ENCHANTS);
        }
        return this.is;
    }

    public List<String> getLore() {
        if (this.isAir()) {
            return Collections.emptyList();
        }
        List<String> values;
        return (values = this.is.getItemMeta().getLore()) == null ? new ArrayList<>() : values;
    }

    public ItemBuilder setLore(String... text) {
        if (this.isAir()) {
            return this;
        }
        ItemMeta itemMeta;
        (itemMeta = this.is.getItemMeta()).setLore(Color.translate(Arrays.asList(text)));
        return this.updatedMeta(itemMeta);
    }

    public ItemBuilder setLore(List<String> values) {
        if (this.isAir()) {
            return this;
        }
        ItemMeta itemMeta;
        (itemMeta = this.is.getItemMeta()).setLore(Color.translate(values));
        return this.updatedMeta(itemMeta);
    }

    public ItemBuilder setUntranslatedLore(List<String> values) {
        if (this.isAir()) {
            return this;
        }
        ItemMeta itemMeta;
        (itemMeta = this.is.getItemMeta()).setLore(values);
        return this.updatedMeta(itemMeta);
    }

    private boolean isAir() {
        return this.is.getType() == Material.AIR;
    }

    private ItemBuilder updatedMeta(ItemMeta itemMeta) {
        this.is.setItemMeta(itemMeta);
        return this;
    }
}
