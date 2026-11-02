package xyz.refinedev.phoenix.hook.impl;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.serverrule.ServerRuleModule;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.hook.api.IHook;
import xyz.refinedev.phoenix.listener.PlayerListener;

public class ApolloBukkitHook extends IHook {

    @Override
    public String getRequiredPlugin() {
        return "Apollo-Bukkit";
    }

    @Override
    public void register(Phoenix phoenix) {
        phoenix.getServer().getPluginManager().registerEvents(new PlayerListener(), phoenix);
        ((ServerRuleModule) Apollo.getModuleManager().getModule(ServerRuleModule.class))
                .getOptions()
                .set(
                        ServerRuleModule.COMPETITIVE_GAME,
                        SettingsConfig.SERVER.LUNAR.COMPETITIVE_GAME);
        phoenix.log("Registered LunarClient Expansion for Phoenix.");
    }
}
