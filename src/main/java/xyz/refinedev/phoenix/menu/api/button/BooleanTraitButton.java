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

public final class BooleanTraitButton<T> extends Button {

    private final T target;

    private final String trait;

    private final BiConsumer<T, Boolean> writeFunction;

    private final Function<T, Boolean> readFunction;

    private final Runnable saveFunction;

    private final Material material;

    private final List<String> description;

    public BooleanTraitButton(
            T t, String text, BiConsumer<T, Boolean> biConsumer, Function<T, Boolean> function) {
        this((T) t, text, biConsumer, function, () -> {});
    }

    public BooleanTraitButton(
            T t,
            String trait,
            BiConsumer<T, Boolean> biConsumer,
            Function<T, Boolean> function,
            Runnable runnable) {
        this.target = (T) t;
        this.trait = trait;
        this.writeFunction = biConsumer;
        this.readFunction = function;
        this.saveFunction = runnable;
        this.material = Material.LEVER;
        this.description = Collections.emptyList();
    }

    public BooleanTraitButton(
            T t,
            String trait,
            List<String> description,
            Material material2,
            BiConsumer<T, Boolean> biConsumer,
            Function<T, Boolean> function,
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
        String text =
                this.readFunction.apply(this.target)
                        ? LocaleUtil.tr(
                                player, MenuConfig.SETTINGS_MENU.BOOLEAN_SETTING.ENABLED_NAME)
                        : LocaleUtil.tr(
                                player, MenuConfig.SETTINGS_MENU.BOOLEAN_SETTING.DISABLED_NAME);
        List<String> values =
                this.readFunction.apply(this.target)
                        ? LocaleUtil.tr(
                                player, MenuConfig.SETTINGS_MENU.BOOLEAN_SETTING.ENABLED_LORE)
                        : LocaleUtil.tr(
                                player, MenuConfig.SETTINGS_MENU.BOOLEAN_SETTING.DISABLED_LORE);
        itemBuilder.setName(text.replace("<name>", this.trait));
        Iterator iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            String text2;
            if ((text2 = (String) iterator2.next()).contains("<description>")) {
                for (String text3 : this.description) {
                    itemBuilder.addLoreLine("&7" + text3);
                }
            } else {
                itemBuilder.addLoreLine(text2.replace("<name>", this.trait));
            }
        }
        return itemBuilder.toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        boolean matches = this.readFunction.apply(this.target);
        this.writeFunction.accept(this.target, !matches);
        this.saveFunction.run();
    }

    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return true;
    }
}
