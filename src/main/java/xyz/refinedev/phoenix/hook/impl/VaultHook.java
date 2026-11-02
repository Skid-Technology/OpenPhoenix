package xyz.refinedev.phoenix.hook.impl;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.hook.api.IHook;
import xyz.refinedev.phoenix.hook.vault.PhoenixVaultChat;
import xyz.refinedev.phoenix.hook.vault.PhoenixVaultPermission;

public class VaultHook extends IHook {

    @Override
    public String getRequiredPlugin() {
        return "Vault";
    }

    @Override
    public void register(Phoenix phoenix) {
        PhoenixVaultPermission phoenixVaultPermission = new PhoenixVaultPermission(phoenix);
        Bukkit.getServicesManager()
                .register(Permission.class, phoenixVaultPermission, phoenix, ServicePriority.High);
        Bukkit.getServicesManager()
                .register(
                        Chat.class,
                        new PhoenixVaultChat(phoenixVaultPermission, phoenix),
                        phoenix,
                        ServicePriority.High);
        phoenix.log("Registered Vault Expansion for Phoenix.");
    }
}
