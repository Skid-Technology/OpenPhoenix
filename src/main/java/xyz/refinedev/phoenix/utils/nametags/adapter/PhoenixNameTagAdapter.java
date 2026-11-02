package xyz.refinedev.phoenix.utils.nametags.adapter;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.NameTagVisibility;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.nametag.NameTagService;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.nametags.PlayerTagCache;
import xyz.refinedev.phoenix.utils.nametags.providers.PlayerTagProvider;
import xyz.refinedev.phoenix.utils.nametags.tag.PlayerTag;
import xyz.refinedev.phoenix.utils.nametags.util.ColorUtil;

import java.util.Optional;

public class PhoenixNameTagAdapter implements PlayerTagProvider {

    private final CommonPlatform api;

    private final IRank defaultRank;

    private PlayerTag defaultTag;

    public PlayerTag getDefaultNameTag() {
        if (this.defaultTag == null) {
            this.defaultTag =
                    PlayerTagCache.of(
                            this.defaultRank.getPlayerListPrefix().isEmpty()
                                    ? Color.translate(this.defaultRank.getColor())
                                    : this.defaultRank.getPlayerListPrefix(),
                            "",
                            ColorUtil.getLastColors(this.defaultRank.getColor()),
                            this.defaultRank.getPriority());
        }
        return this.defaultTag;
    }

    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public Optional<PlayerTag> buildTag(Player player, Player player2) {
        if (player2 != null && player != null) {
            IProfile profile;
            if ((profile = this.api.getProfileHandler().getCachedProfile(player2.getUniqueId()))
                    == null) {
                return Optional.of(this.getDefaultNameTag());
            }
            IRank rank;
            ChatColor lastColors =
                    ColorUtil.getLastColors((rank = profile.getHighestRank()).getColor());
            NameTagVisibility nameTagVisibility =
                    this.hiddenFor(player) ? NameTagVisibility.NEVER : NameTagVisibility.ALWAYS;
            if (profile.isVanished()) {
                String text =
                        SettingsConfig.FORMATS
                                .STAFF_NAMETAGS
                                .VANISHED_PREFIX
                                .replace("<color>", rank.getColor())
                                .replace("<prefix>", rank.getPlayerListPrefix());
                String text2 =
                        SettingsConfig.FORMATS
                                .STAFF_NAMETAGS
                                .VANISHED_SUFFIX
                                .replace("<color>", rank.getColor())
                                .replace("<prefix>", rank.getPlayerListPrefix());
                return Optional.of(
                        PlayerTagCache.of(
                                text, text2, lastColors, rank.getPriority(), nameTagVisibility));
            }
            if (player2.hasMetadata("ModMode")) {
                String text3 =
                        SettingsConfig.FORMATS
                                .STAFF_NAMETAGS
                                .MODMODE_PREFIX
                                .replace("<color>", rank.getColor())
                                .replace("<prefix>", rank.getPlayerListPrefix());
                String text4 =
                        SettingsConfig.FORMATS
                                .STAFF_NAMETAGS
                                .MODMODE_SUFFIX
                                .replace("<color>", rank.getColor())
                                .replace("<prefix>", rank.getPlayerListPrefix());
                return Optional.of(
                        PlayerTagCache.of(
                                text3, text4, lastColors, rank.getPriority(), nameTagVisibility));
            }
            String text5 =
                    rank.getPlayerListPrefix().isEmpty()
                            ? Color.translate(rank.getColor())
                            : rank.getPlayerListPrefix();
            if (player2.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                nameTagVisibility = NameTagVisibility.NEVER;
            }
            return Optional.of(
                    PlayerTagCache.of(
                            text5,
                            rank.getSuffix(),
                            lastColors,
                            rank.getPriority(),
                            nameTagVisibility));
        } else {
            return Optional.of(this.getDefaultNameTag());
        }
    }

    private boolean hiddenFor(Player player) {
        NameTagService nameTagService;
        return (nameTagService = Phoenix.getInstance().getNameTagService()) != null
                && nameTagService.getNameTagBackendFactory().matchesEnabled(player);
    }

    public PhoenixNameTagAdapter(CommonPlatform commonPlatform, IRank rank) {
        this.api = commonPlatform;
        this.defaultRank = rank;
    }
}
