package xyz.refinedev.phoenix.utils;

import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;

public class PlayerStateSnapshot {

    private ItemStack[] itemStacks;

    private ItemStack[] itemStacksItemStackArray;

    private Collection<PotionEffect> potionEffects;

    private float floatValue;

    private GameMode gameMode;

    public PlayerStateSnapshot(
            ItemStack[] itemStacks2,
            ItemStack[] itemStacks3,
            Collection<PotionEffect> potionEffects,
            float floatValue,
            GameMode gameMode2) {
        this.itemStacks = new ItemStack[36];
        this.itemStacksItemStackArray = new ItemStack[4];
        this.potionEffects = new ArrayList<>();
        this.itemStacks = itemStacks2;
        this.itemStacksItemStackArray = itemStacks3;
        this.potionEffects = potionEffects;
        this.floatValue = floatValue;
        this.gameMode = gameMode2;
    }

    public ItemStack[] getItemStacks() {
        return this.itemStacks;
    }

    public ItemStack[] getItemStacksItemStackArray() {
        return this.itemStacksItemStackArray;
    }

    public Collection<PotionEffect> getPotionEffects() {
        return this.potionEffects;
    }

    public float getFloatValue() {
        return this.floatValue;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public void setItemStacks(ItemStack[] itemStacks2) {
        this.itemStacks = itemStacks2;
    }

    public void setItemStacksItemStackArray(ItemStack[] itemStacks) {
        this.itemStacksItemStackArray = itemStacks;
    }

    public void setPotionEffects(Collection<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;
    }

    public void setFloatValue(float floatValue) {
        this.floatValue = floatValue;
    }

    public void setGameMode(GameMode gameMode2) {
        this.gameMode = gameMode2;
    }
}
