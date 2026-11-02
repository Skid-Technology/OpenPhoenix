package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.Bukkit;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.chat.ConversationUtil;
import xyz.refinedev.phoenix.utils.general.Tasks;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;
import xyz.refinedev.phoenix.utils.reflection.Reflection;

import java.util.HashMap;
import java.util.Map;

public class SocialMediaMenu extends Menu {

    private final IProfile profile;

    private final boolean enabled;

    private boolean enabledEnabled = false;

    @Override
    public String getTitle(Player player) {
        return this.enabled
                ? LocaleUtil.tr(player, MenuConfig.SOCIAL_MEDIA.SELF_TITLE)
                        .replace("<profile>", this.profile.getNameWithColor())
                : LocaleUtil.tr(player, MenuConfig.SOCIAL_MEDIA.TITLE)
                        .replace("<profile>", this.profile.getNameWithColor());
    }

    @Override
    public void onClose(Player player) {
        if (this.enabledEnabled) {
            this.profile.saveAndUpdate(false);
        }
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        this.backButton(entries, 4);
        entries.put(10, new SocialMediaMenu.NoneButton(SocialMediaType.TWITTER));
        if (Bukkit.getServer().getPluginManager().getPlugin("pxDiscord") != null) {
            entries.put(12, new SocialMediaMenu.DiscordNameButton(SocialMediaType.DISCORD));
        } else {
            entries.put(12, new SocialMediaMenu.NoneButton(SocialMediaType.DISCORD));
        }
        entries.put(14, new SocialMediaMenu.NoneButton(SocialMediaType.YOUTUBE));
        entries.put(16, new SocialMediaMenu.NoneButton(SocialMediaType.INSTAGRAM));
        for (int index = 0; index < 27; index++) {
            entries.computeIfAbsent(index, result -> Button.placeholder());
        }
        return entries;
    }

    public SocialMediaMenu(IProfile profile2, boolean enabled) {
        this.profile = profile2;
        this.enabled = enabled;
    }

    class DiscordNameButton extends Button {

        private final SocialMediaType socialMediaType;

        @Override
        public ItemStack getItem(Player player) {
            String text;
            String text2 =
                    (text = SocialMediaMenu.this.profile.getSetting("discordName", null)) != null
                            ? text
                            : SocialMediaMenu.this.profile.getSetting(
                                    this.socialMediaType.name(), "None");
            String setting =
                    SocialMediaMenu.this.profile.getSetting(
                            this.socialMediaType.name() + "-privacy", "None");
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    XMaterial.PLAYER_HEAD.get(),
                                    1,
                                    (short) SkullType.PLAYER.ordinal()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.SOCIAL_MEDIA.NAME)
                                    .replace("<name>", this.socialMediaType.getName())
                                    .replace(
                                            "<color>",
                                            String.valueOf(this.socialMediaType.getColor())));
            if (SocialMediaMenu.this.enabled) {
                for (String text3 : LocaleUtil.tr(player, MenuConfig.SOCIAL_MEDIA.SELF_LORE)) {
                    itemBuilder.addLoreLine(
                            text3.replace("<current>", text2).replace("<privacy>", setting));
                }
            } else {
                for (String text4 : LocaleUtil.tr(player, MenuConfig.SOCIAL_MEDIA.LORE)) {
                    itemBuilder.addLoreLine(
                            text4.replace(
                                    "<current>",
                                    setting.equalsIgnoreCase("None") ? "None" : text2));
                }
            }
            return Reflection.itemWithBase64(
                    itemBuilder.toItemStack(), this.socialMediaType.getTexture());
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (SocialMediaMenu.this.enabled) {
                if (clickType == ClickType.MIDDLE) {
                    if (SocialMediaMenu.this
                            .profile
                            .getSetting(this.socialMediaType.name() + "-privacy", "none")
                            .equalsIgnoreCase("Everyone")) {
                        SocialMediaMenu.this.profile.updateMetadata(
                                this.socialMediaType.name() + "-privacy", "None", false);
                    } else {
                        SocialMediaMenu.this.profile.updateMetadata(
                                this.socialMediaType.name() + "-privacy", "Everyone", false);
                    }
                    SocialMediaMenu.this.enabledEnabled = true;
                } else {
                    player.closeInventory();
                    player.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(player, MessageConfig.SOCIALS.SYNC_DISCORD)));
                }
            }
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return !clickType.isLeftClick();
        }

        public DiscordNameButton(SocialMediaType socialMediaType2) {
            this.socialMediaType = socialMediaType2;
        }
    }

    class NoneButton extends Button {

        private final SocialMediaType socialMediaType;

        @Override
        public ItemStack getItem(Player player) {
            String setting =
                    SocialMediaMenu.this.profile.getSetting(this.socialMediaType.name(), "None");
            String setting2 =
                    SocialMediaMenu.this.profile.getSetting(
                            this.socialMediaType.name() + "-privacy", "None");
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    XMaterial.PLAYER_HEAD.get(),
                                    1,
                                    (short) SkullType.PLAYER.ordinal()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.SOCIAL_MEDIA.NAME)
                                    .replace("<name>", this.socialMediaType.getName())
                                    .replace(
                                            "<color>",
                                            String.valueOf(this.socialMediaType.getColor())));
            if (SocialMediaMenu.this.enabled) {
                for (String text : LocaleUtil.tr(player, MenuConfig.SOCIAL_MEDIA.SELF_LORE)) {
                    itemBuilder.addLoreLine(
                            text.replace("<current>", setting).replace("<privacy>", setting2));
                }
            } else {
                for (String text2 : LocaleUtil.tr(player, MenuConfig.SOCIAL_MEDIA.LORE)) {
                    itemBuilder.addLoreLine(
                            text2.replace(
                                    "<current>",
                                    setting2.equalsIgnoreCase("None") ? "None" : setting));
                }
            }
            return Reflection.itemWithBase64(
                    itemBuilder.toItemStack(), this.socialMediaType.getTexture());
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (SocialMediaMenu.this.enabled) {
                if (clickType.isRightClick()) {
                    if (!SocialMediaMenu.this
                            .profile
                            .getSetting(this.socialMediaType.name(), "None")
                            .equalsIgnoreCase("None")) {
                        SocialMediaMenu.this.profile.updateMetadata(
                                this.socialMediaType.name() + "-privacy", "None", false);
                        SocialMediaMenu.this.profile.updateMetadata(
                                this.socialMediaType.name(), "None", true);
                        SocialMediaMenu.this.enabledEnabled = true;
                    }
                } else if (clickType == ClickType.MIDDLE) {
                    if (SocialMediaMenu.this
                            .profile
                            .getSetting(this.socialMediaType.name() + "-privacy", "none")
                            .equalsIgnoreCase("Everyone")) {
                        SocialMediaMenu.this.profile.updateMetadata(
                                this.socialMediaType.name() + "-privacy", "None", false);
                    } else {
                        SocialMediaMenu.this.profile.updateMetadata(
                                this.socialMediaType.name() + "-privacy", "Everyone", false);
                    }
                    SocialMediaMenu.this.enabledEnabled = true;
                } else {
                    player.closeInventory();
                    Phoenix phoenix = SocialMediaMenu.super.plugin;
                    String text =
                            LocaleUtil.tr(player, MessageConfig.SOCIALS.ENTER_URL)
                                    .replace(
                                            "<social>",
                                            this.socialMediaType.getColor()
                                                    + this.socialMediaType.getName());
                    ConversationUtil.ask(
                            phoenix,
                            player,
                            text,
                            result -> {
                                if (!result.matches(this.socialMediaType.getRegex())) {
                                    player.sendMessage(
                                            Color.translate(
                                                    LocaleUtil.tr(
                                                            player,
                                                            MessageConfig.SOCIALS.INVALID_URL)));
                                } else {
                                    Tasks.run(
                                            () -> {
                                                player.sendMessage(
                                                        Color.translate(
                                                                LocaleUtil.tr(
                                                                                player,
                                                                                MessageConfig
                                                                                        .SOCIALS
                                                                                        .UPDATED)
                                                                        .replace(
                                                                                "<social>",
                                                                                this.socialMediaType
                                                                                                .getColor()
                                                                                        + this
                                                                                                .socialMediaType
                                                                                                .getName())
                                                                        .replace("<url>", result)));
                                                SocialMediaMenu.this.profile.updateMetadata(
                                                        this.socialMediaType.name() + "-privacy",
                                                        "Everyone",
                                                        false);
                                                SocialMediaMenu.this.profile.updateMetadata(
                                                        this.socialMediaType.name(), result, true);
                                            });
                                }
                            });
                }
            }
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return !clickType.isLeftClick();
        }

        public NoneButton(SocialMediaType socialMediaType2) {
            this.socialMediaType = socialMediaType2;
        }
    }
}
