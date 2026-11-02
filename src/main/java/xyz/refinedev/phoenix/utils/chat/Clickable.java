package xyz.refinedev.phoenix.utils.chat;

import org.bukkit.command.CommandSender;

import xyz.refinedev.lib.kyori.adventure.text.Component;
import xyz.refinedev.lib.kyori.adventure.text.event.ClickEvent;
import xyz.refinedev.lib.kyori.adventure.text.event.HoverEvent;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.utils.Color;

public class Clickable {

    private Component component = Component.empty();

    private String activeTrailingColor = "";

    public Clickable(String text) {
        this.add(text);
    }

    public Clickable(String text, String text2, String text3) {
        this.add(text, text2, text3);
    }

    public Clickable(String text, String text2, String text3, String text4) {
        this.add(text, text2, text3, text4);
    }

    public void add(String text) {
        this.component = this.component.append(this.toChainedComponent(text));
    }

    public void add(Component component2) {
        this.component = this.component.append(component2);
        this.activeTrailingColor = "";
    }

    public void addUrl(String text, String text2, String text3) {
        Component component2 = this.toChainedComponent(text);
        if (text2 != null && !text2.isEmpty()) {
            component2 = component2.hoverEvent(HoverEvent.showText(this.toComponent(text2)));
        }
        if (text3 != null && !text3.isEmpty()) {
            component2 = component2.clickEvent(ClickEvent.openUrl(text3));
        }
        this.component = this.component.append(component2);
    }

    public void add(String text, String text2, String text3) {
        this.add(text, text2, text3, null);
    }

    public void add(String text, String text2) {
        this.add(text, text2, null, null);
    }

    public void add(String text, String text2, String text3, String text4) {
        Component component2 = this.toChainedComponent(text);
        if (text2 != null && !text2.isEmpty()) {
            component2 = component2.hoverEvent(HoverEvent.showText(this.toComponent(text2)));
        }
        if (text3 != null && !text3.isEmpty()) {
            component2 = component2.clickEvent(ClickEvent.runCommand(text3));
        }
        if (text4 != null && !text4.isEmpty()) {
            component2 = component2.clickEvent(ClickEvent.suggestCommand(text4));
        }
        this.component = this.component.append(component2);
    }

    public void addComponent(Component component2) {
        this.component = this.component.append(component2);
        this.activeTrailingColor = "";
    }

    public void sendToPlayer(CommandSender sender2) {
        Phoenix.getInstance().getAudience().sender(sender2).sendMessage(this.component);
    }

    private Component toComponent(String text) {
        return Color.translateComponent(text);
    }

    private Component toChainedComponent(String text) {
        String text2 = Color.carryTrailingColor(this.activeTrailingColor, text);
        this.activeTrailingColor = Color.trailingLegacyColor(text2);
        return Color.translateComponent(text2);
    }

    public Clickable() {}

    public Component getComponent() {
        return this.component;
    }

    public String getActiveTrailingColor() {
        return this.activeTrailingColor;
    }
}
