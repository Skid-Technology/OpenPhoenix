package xyz.refinedev.phoenix.menu.api.button;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class IntegerTraitButton<T> extends Button {

    private final T target;

    private final String trait;

    private final BiConsumer<T, Integer> writeFunction;

    private final Function<T, Integer> readFunction;

    private final Runnable saveFunction;

    private final Material material;

    private final List<String> description;

    public IntegerTraitButton(
            T t, String text, BiConsumer<T, Integer> biConsumer, Function<T, Integer> function) {
        this((T) t, text, biConsumer, function, () -> {});
    }

    public IntegerTraitButton(
            T t,
            String trait,
            BiConsumer<T, Integer> biConsumer,
            Function<T, Integer> function,
            Runnable runnable) {
        this.target = (T) t;
        this.trait = trait;
        this.writeFunction = biConsumer;
        this.readFunction = function;
        this.saveFunction = runnable;
        this.material = Material.GHAST_TEAR;
        this.description = Collections.emptyList();
    }

    public IntegerTraitButton(
            T t,
            String trait,
            List<String> description,
            Material material2,
            BiConsumer<T, Integer> biConsumer,
            Function<T, Integer> function,
            Runnable runnable) {
        this.target = (T) t;
        this.trait = trait;
        this.writeFunction = biConsumer;
        this.readFunction = function;
        this.saveFunction = runnable;
        this.material = material2;
        this.description = description;
    }

    @Override
    public ItemStack getItem(Player player) {
        ItemBuilder itemBuilder = new ItemBuilder(this.material);
        String text = String.valueOf(this.readFunction.apply(this.target));
        itemBuilder.setName(
                LocaleUtil.tr(player, MenuConfig.SETTINGS_MENU.NUMBER_SETTING.NAME)
                        .replace("<name>", this.trait));
        Iterator<String> iterator2 =
                LocaleUtil.tr(player, MenuConfig.SETTINGS_MENU.NUMBER_SETTING.LORE).iterator();
        while (iterator2.hasNext()) {
            String text2;
            if ((text2 = (String) iterator2.next()).contains("<description>")) {
                for (String text3 : this.description) {
                    itemBuilder.addLoreLine("&7" + text3);
                }
            } else {
                itemBuilder.addLoreLine(text2.replace("<number>", text));
            }
        }
        return itemBuilder.toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        int value = this.readFunction.apply(this.target);
        int value2 = clickType.isShiftClick() ? 10 : 1;
        if (clickType.isRightClick()) {
            value2 = -value2;
        }
        this.writeFunction.accept(this.target, value + value2);
        this.saveFunction.run();
    }

    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return true;
    }
}
