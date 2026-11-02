package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.alt.IAltLink;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.antivpn.IAntiVPNData;
import xyz.refinedev.phoenix.profile.login.ILogin;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.utils.AltLinkFormatter;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PunishmentUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AltsMenu extends PaginatedMenu {

    private final IProfile profile;

    private final Set<IProfile> profiles;

    private final List<ILogin> logins;

    private final Map<UUID, List<ILogin>> loginsById;

    private final Map<UUID, IAntiVPNData> antiVPNDatasById;

    private final Map<UUID, IAltLink> altLinksById;

    private static final String text = "<player>";

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.ALTS_MENU.TITLE)
                .replace(text, this.profile.getName());
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        ArrayList<IProfile> values;
        ArrayList values2 = values = new ArrayList<>(this.profiles);
        values2.sort(
                Comparator.<IProfile>comparingInt(
                                result -> {
                                    IAltLink altLink;
                                    return (altLink = this.altLinksById.get(result.getUniqueId()))
                                                    == null
                                            ? -1
                                            : altLink.getScore();
                                })
                        .reversed());
        for (IProfile profile : values) {
            entries.put(entries.size(), new AltsMenu.IpBanButton(profile));
        }
        return entries;
    }

    public AltsMenu(
            IProfile profile2,
            Set<IProfile> profiles,
            List<ILogin> logins,
            Map<UUID, List<ILogin>> entries,
            Map<UUID, IAntiVPNData> entries2,
            Map<UUID, IAltLink> entries3) {
        this.profile = profile2;
        this.profiles = profiles;
        this.logins = logins;
        this.loginsById = entries;
        this.antiVPNDatasById = entries2;
        this.altLinksById = entries3;
    }

    public class IpBanButton extends Button {

        private final IProfile profile;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    XMaterial.PLAYER_HEAD.get(),
                                    1,
                                    (short) SkullType.PLAYER.ordinal()))
                    .setSkullOwner(this.profile.getName());
            itemBuilder.setName(
                    LocaleUtil.tr(player, MenuConfig.ALTS_MENU.NAME)
                            .replace(
                                    "<sibling>",
                                    AltLinkFormatter.getStatus(
                                                    AltsMenu.this.altLinksById.get(
                                                            this.profile.getUniqueId()))
                                            ? MessageConfig.ALTS.TRUSTED_TAG
                                            : "")
                            .replace("<player>", this.profile.getNameWithColorNoDisguise())
                            .replace(
                                    "<status>",
                                    PunishmentUtil.getAltStatus(
                                            AltsMenu.this.profile, this.profile)));
            List<ILogin> values;
            if ((values = AltsMenu.this.loginsById.get(this.profile.getUniqueId())) != null
                    && !values.isEmpty()) {
                AltsMenu.IpBanButton ipBanButton = this;
                ArrayList values2;
                if ((values2 =
                                FastStreams.filterArrayList(
                                        values, ipBanButton::matchesEnabledByProfile))
                        .isEmpty()) {
                    itemBuilder.addLoreLine(
                            this.profile.getNameWithColorNoDisguise()
                                    + "&c has no current ip logins.");
                    return itemBuilder.toItemStack();
                }
                IAntiVPNData antiVPNData =
                        AltsMenu.this.antiVPNDatasById.get(this.profile.getUniqueId());
                Iterator<String> iterator2 =
                        LocaleUtil.tr(player, MenuConfig.ALTS_MENU.LORE).iterator();
                while (iterator2.hasNext()) {
                    String text;
                    if ((text = (String) iterator2.next()).contains("<score>")) {
                        for (String text2 :
                                AltLinkFormatter.findList(
                                        AltsMenu.this.altLinksById.get(
                                                this.profile.getUniqueId()))) {
                            itemBuilder.addLoreLine(text2);
                        }
                    } else if (text.contains("<matching>")) {
                        String text3 =
                                this.profile
                                                .getCurrentAddress()
                                                .equals(AltsMenu.this.profile.getCurrentAddress())
                                        ? LocaleUtil.tr(player, MenuConfig.ALTS_MENU.IP_MATCHING)
                                        : LocaleUtil.tr(
                                                player, MenuConfig.ALTS_MENU.IP_NOT_MATCHING);
                        itemBuilder.addLoreLine(
                                text3.replace(
                                        "<player>",
                                        AltsMenu.this.profile.getNameWithColorNoDisguise()));
                    } else if (text.contains("<subnetMatch>")) {
                        if (!this.profile
                                        .getCurrentAddress()
                                        .equals(AltsMenu.this.profile.getCurrentAddress())
                                && this.profile
                                        .getCurrentSubnet()
                                        .equals(AltsMenu.this.profile.getCurrentSubnet())) {
                            List<String> values3 =
                                    LocaleUtil.tr(player, MenuConfig.ALTS_MENU.SUBNET_ONLY_MATCH);
                            ItemBuilder itemBuilder2 = itemBuilder;
                            values3.forEach(itemBuilder2::addLoreLine);
                        }
                    } else if (text.contains("<punishments>")) {
                        IPunishment activeBlacklist = this.profile.getActiveBlacklist();
                        IPunishment activeBan = this.profile.getActiveBan();
                        IPunishment activeMute = this.profile.getActiveMute();
                        if (activeBlacklist != null) {
                            String text4 =
                                    activeBlacklist
                                                    .getAddress()
                                                    .equals(
                                                            AltsMenu.this.profile
                                                                    .getCurrentAddress())
                                            ? LocaleUtil.tr(
                                                    player,
                                                    MenuConfig.ALTS_MENU.PUNISHMENT_MATCHING)
                                            : LocaleUtil.tr(
                                                    player,
                                                    MenuConfig.ALTS_MENU.PUNISHMENT_NOT_MATCHING);
                            itemBuilder.addLoreLine(
                                    text4.replace(
                                                    "<type>",
                                                    MessageConfig.ALTS.STATUS.BLACKLIST.NAME)
                                            .replace(
                                                    "<player>",
                                                    AltsMenu.this.profile
                                                            .getNameWithColorNoDisguise()));
                        }
                        if (activeBan != null) {
                            String text5 =
                                    activeBan
                                                    .getAddress()
                                                    .equals(
                                                            AltsMenu.this.profile
                                                                    .getCurrentAddress())
                                            ? LocaleUtil.tr(
                                                    player,
                                                    MenuConfig.ALTS_MENU.PUNISHMENT_MATCHING)
                                            : LocaleUtil.tr(
                                                    player,
                                                    MenuConfig.ALTS_MENU.PUNISHMENT_NOT_MATCHING);
                            itemBuilder.addLoreLine(
                                    text5.replace(
                                                    "<type>",
                                                    MessageConfig.ALTS.STATUS.BANNED.COLOR
                                                            + (activeBan.isIp() ? "IP Ban" : "Ban"))
                                            .replace(
                                                    "<player>",
                                                    AltsMenu.this.profile
                                                            .getNameWithColorNoDisguise()));
                        }
                        if (activeMute != null) {
                            String text6 =
                                    activeMute
                                                    .getAddress()
                                                    .equals(
                                                            AltsMenu.this.profile
                                                                    .getCurrentAddress())
                                            ? LocaleUtil.tr(
                                                    player,
                                                    MenuConfig.ALTS_MENU.PUNISHMENT_MATCHING)
                                            : LocaleUtil.tr(
                                                    player,
                                                    MenuConfig.ALTS_MENU.PUNISHMENT_NOT_MATCHING);
                            itemBuilder.addLoreLine(
                                    text6.replace(
                                                    "<type>",
                                                    MessageConfig.ALTS.STATUS.MUTED_ONLINE.COLOR
                                                            + (activeMute.isIp()
                                                                    ? "IP Mute"
                                                                    : "Mute"))
                                            .replace(
                                                    "<player>",
                                                    AltsMenu.this.profile
                                                            .getNameWithColorNoDisguise()));
                        }
                    } else if (text.contains("<matchingIPInfo>")) {
                        if (AltsMenu.this.logins.isEmpty()) {
                            itemBuilder.addLoreLine(
                                    Color.translate(
                                            AltsMenu.this.profile.getNameWithColorNoDisguise()
                                                    + "&c has no registered logins."));
                        } else {
                            List<ILogin> values4 = AltsMenu.this.logins;
                            AltsMenu.IpBanButton ipBanButton2 = this;
                            ArrayList values5;
                            if ((values5 =
                                            FastStreams.filterArrayList(
                                                    values4, ipBanButton2::matchesEnabled))
                                    .isEmpty()) {
                                itemBuilder.addLoreLine(Color.translate("&cNo Matching IP Info."));
                            } else {
                                long size2 =
                                        FastStreams.mapArrayListToSet(values5, ILogin::getIp)
                                                .size();
                                Iterator<String> iterator3 =
                                        LocaleUtil.tr(player, MenuConfig.ALTS_MENU.MATCHING_IP_INFO)
                                                .iterator();
                                while (iterator3.hasNext()) {
                                    String text7 =
                                            ((String) iterator3.next())
                                                    .replace(
                                                            "<name>",
                                                            AltsMenu.this.profile
                                                                    .getNameWithColorNoDisguise())
                                                    .replace(
                                                            "<country>",
                                                            antiVPNData != null
                                                                            && antiVPNData
                                                                                            .getCountry()
                                                                                    != null
                                                                    ? antiVPNData.getCountry()
                                                                    : "N/A")
                                                    .replace(
                                                            "<isp>",
                                                            antiVPNData != null
                                                                            && antiVPNData.getIsp()
                                                                                    != null
                                                                    ? antiVPNData.getIsp()
                                                                    : "N/A")
                                                    .replace("<amount>", String.valueOf(size2))
                                                    .replace(
                                                            "<logins>",
                                                            String.valueOf(values5.size()))
                                                    .replace(
                                                            "<firstLogin>",
                                                            DateUtils.getDate(
                                                                    ((ILogin) values5.get(0))
                                                                            .getLogin()))
                                                    .replace(
                                                            "<lastLogin>",
                                                            DateUtils.getDate(
                                                                    ((ILogin)
                                                                                    values5.get(
                                                                                            values5
                                                                                                            .size()
                                                                                                    - 1))
                                                                            .getLogin()));
                                    itemBuilder.addLoreLine(text7);
                                }
                            }
                        }
                    } else {
                        text =
                                text.replace("<name>", this.profile.getNameWithColorNoDisguise())
                                        .replace(
                                                "<country>",
                                                antiVPNData != null
                                                                && antiVPNData.getCountry() != null
                                                        ? antiVPNData.getCountry()
                                                        : "N/A")
                                        .replace(
                                                "<isp>",
                                                antiVPNData != null && antiVPNData.getIsp() != null
                                                        ? antiVPNData.getIsp()
                                                        : "N/A")
                                        .replace("<lastSeen>", this.profile.getLastSeenAgo(values))
                                        .replace("<logins>", String.valueOf(values2.size()))
                                        .replace(
                                                "<firstLogin>",
                                                DateUtils.getDate(
                                                        ((ILogin) values2.get(0)).getLogin()))
                                        .replace(
                                                "<lastLogin>",
                                                DateUtils.getDate(
                                                        ((ILogin) values2.get(values2.size() - 1))
                                                                .getLogin()));
                        itemBuilder.addLoreLine(text);
                    }
                }
                return itemBuilder.toItemStack();
            } else {
                itemBuilder.addLoreLine(
                        this.profile.getNameWithColorNoDisguise() + "&c has no registered logins.");
                return itemBuilder.toItemStack();
            }
        }

        public IpBanButton(IProfile profile2) {
            this.profile = profile2;
        }

        private boolean matchesEnabled(ILogin login) {
            return this.profile.getKnownAddresses().contains(login.getIp());
        }

        private boolean matchesEnabledByProfile(ILogin login) {
            return login.getIp().equals(this.profile.getCurrentAddress());
        }
    }
}
