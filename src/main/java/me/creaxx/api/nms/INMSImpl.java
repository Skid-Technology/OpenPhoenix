package me.creaxx.api.nms;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.disguise.ISkin;

public interface INMSImpl {

    void disguise(IProfile profile, boolean matches);

    void undisguise(IProfile profile);

    void updateSkin(Player player, ISkin skin, boolean matches);

    void updateHeaderFooter(Player player, String text, String text2);

    ISkin getPlayerSkin(Player player);

    ItemStack itemWithBase64(ItemStack itemStack, String text);

    void sitPlayer(Player player);

    void unsitPlayer(Player player);

    void removePlayerFromTeam(String text, Team team);
}
