package xyz.refinedev.phoenix.hook.impl;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.hook.api.IHook;
import xyz.refinedev.phoenix.placeholder.PhoenixPlaceholderAdapter;
import xyz.refinedev.phoenix.placeholder.PhoenixPlaceholderExpansion;

public class PlaceholderAPIHook extends IHook {

    @Override
    public String getRequiredPlugin() {
        return "PlaceholderAPI";
    }

    @Override
    public void register(Phoenix phoenix) {
        phoenix.getPlaceholderHandler().addAdapter(new PhoenixPlaceholderAdapter());
        new PhoenixPlaceholderExpansion(phoenix).register();
        phoenix.log("Registered PlaceholderAPI Expansion for Phoenix.");
    }
}
