package xyz.refinedev.phoenix.packet.impl.profile;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.StringUtils;
import xyz.refinedev.phoenix.utils.events.grant.ProfileGrantEvent;
import xyz.refinedev.phoenix.utils.events.grant.ProfileGrantRevokeEvent;

import java.util.List;

public class BukkitGrantMessagePacket extends GrantMessagePacket {

    public BukkitGrantMessagePacket(IGrant grant, boolean matches) {
        super(grant, matches);
    }

    @Override
    public void onReceive() {
        IProfile profile;
        if ((profile =
                        Phoenix.getInstance()
                                .getProfileHandler()
                                .getProfile(super.grant.getTarget()))
                != null) {
            IRank rank;
            if ((rank = super.grant.getRank()) != null) {
                if (super.remove) {
                    Player player;
                    if ((player = Bukkit.getPlayer(super.grant.getTarget())) != null) {
                        for (String text :
                                LocaleUtil.tr(player, MessageConfig.GRANT.GRANT_REMOVED_TARGET)) {
                            player.sendMessage(
                                    Color.translate(text.replace("<rank>", rank.getDisplayName())));
                        }
                    }
                    if (super.grant.getRemovedBy().equals(IProfile.getConsoleUuid())
                            && super.grant.getRemovedOn().equals(GlobalConfig.SERVER.NAME)) {
                        for (String text2 : MessageConfig.GRANT.GRANT_REMOVED_SELF) {
                            Bukkit.getConsoleSender()
                                    .sendMessage(
                                            Color.translate(
                                                    text2.replace(
                                                                    "<player>",
                                                                    super.grant.getNiceTarget())
                                                            .replace(
                                                                    "<rank>",
                                                                    rank.getDisplayName())));
                        }
                        Phoenix.getInstance()
                                .getDiscordHandler()
                                .applyGrantByWEBHOOK_URL(super.grant);
                        BukkitAPI.callEvent(
                                new ProfileGrantRevokeEvent(
                                        super.grant, profile, Bukkit.getConsoleSender()));
                    } else {
                        Player player2;
                        if ((player2 = Bukkit.getPlayer(super.grant.getRemovedBy())) != null) {
                            for (String text3 :
                                    LocaleUtil.tr(
                                            player2, MessageConfig.GRANT.GRANT_REMOVED_SELF)) {
                                player2.sendMessage(
                                        Color.translate(
                                                text3.replace(
                                                                "<player>",
                                                                super.grant.getNiceTarget())
                                                        .replace("<rank>", rank.getDisplayName())));
                            }
                            Phoenix.getInstance()
                                    .getDiscordHandler()
                                    .applyGrantByWEBHOOK_URL(super.grant);
                            BukkitAPI.callEvent(
                                    new ProfileGrantRevokeEvent(super.grant, profile, player2));
                        }
                    }
                } else {
                    List<String> values =
                            super.grant.isPermanent()
                                    ? MessageConfig.GRANT.GRANTED_PERM_SELF
                                    : MessageConfig.GRANT.GRANTED_TEMP_SELF;
                    List<String> values2 =
                            super.grant.isPermanent()
                                    ? MessageConfig.GRANT.GRANTED_PERM_TARGET
                                    : MessageConfig.GRANT.GRANTED_TEMP_TARGET;
                    Player player3;
                    if ((player3 = Bukkit.getPlayer(super.grant.getTarget())) != null) {
                        for (String text4 : values2) {
                            player3.sendMessage(
                                    Color.translate(
                                            text4.replace("<rank>", rank.getDisplayName())
                                                    .replace(
                                                            "<time>", super.grant.getNiceDuration())
                                                    .replace(
                                                            "<scopes>",
                                                            StringUtils.join(
                                                                    super.grant
                                                                            .getNiceActiveScopes()))
                                                    .replace(
                                                            "<expire>",
                                                            super.grant.getNiceExpire()
                                                                    + " ("
                                                                    + DateUtils.getDate(
                                                                            System
                                                                                            .currentTimeMillis()
                                                                                    + super.grant
                                                                                            .getDuration())
                                                                    + ")")
                                                    .replace("<reason>", super.grant.getReason())));
                        }
                    }
                    if (super.grant.getIssuedBy().equals(IProfile.getConsoleUuid())
                            && super.grant.getIssuedOn().equals(GlobalConfig.SERVER.NAME)) {
                        for (String text5 : values) {
                            Bukkit.getConsoleSender()
                                    .sendMessage(
                                            Color.translate(
                                                    text5.replace("<rank>", rank.getDisplayName())
                                                            .replace(
                                                                    "<time>",
                                                                    super.grant.getNiceDuration())
                                                            .replace(
                                                                    "<scopes>",
                                                                    StringUtils.join(
                                                                            super.grant
                                                                                    .getNiceActiveScopes()))
                                                            .replace(
                                                                    "<expire>",
                                                                    super.grant.getNiceExpire()
                                                                            + " ("
                                                                            + DateUtils.getDate(
                                                                                    System
                                                                                                    .currentTimeMillis()
                                                                                            + super
                                                                                                    .grant
                                                                                                    .getDuration())
                                                                            + ")")
                                                            .replace(
                                                                    "<reason>",
                                                                    super.grant.getReason())
                                                            .replace(
                                                                    "<user>",
                                                                    super.grant.getNiceTarget())));
                        }
                        Phoenix.getInstance().getDiscordHandler().applyGrant(super.grant);
                        BukkitAPI.callEvent(
                                new ProfileGrantEvent(
                                        super.grant, profile, Bukkit.getConsoleSender()));
                    } else {
                        Player player4;
                        if ((player4 = Bukkit.getPlayer(super.grant.getIssuedBy())) != null) {
                            for (String text6 : values) {
                                player4.sendMessage(
                                        Color.translate(
                                                text6.replace("<rank>", rank.getDisplayName())
                                                        .replace(
                                                                "<time>",
                                                                super.grant.getNiceDuration())
                                                        .replace(
                                                                "<scopes>",
                                                                StringUtils.join(
                                                                        super.grant
                                                                                .getNiceActiveScopes()))
                                                        .replace(
                                                                "<expire>",
                                                                super.grant.getNiceExpire()
                                                                        + " ("
                                                                        + DateUtils.getDate(
                                                                                System
                                                                                                .currentTimeMillis()
                                                                                        + super
                                                                                                .grant
                                                                                                .getDuration())
                                                                        + ")")
                                                        .replace(
                                                                "<reason>", super.grant.getReason())
                                                        .replace(
                                                                "<user>",
                                                                super.grant.getNiceTarget())));
                            }
                            Phoenix.getInstance().getDiscordHandler().applyGrant(super.grant);
                            BukkitAPI.callEvent(
                                    new ProfileGrantEvent(super.grant, profile, player4));
                        }
                    }
                }
            }
        }
    }
}
