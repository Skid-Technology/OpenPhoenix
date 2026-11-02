package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.disguise.DisguiseData;
import xyz.refinedev.phoenix.profile.disguise.ISkin;
import xyz.refinedev.phoenix.profile.disguise.Skin;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.chat.ConversationUtil;
import xyz.refinedev.phoenix.utils.general.Tasks;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DisguiseSkinMenu extends PaginatedMenu {

    private final String text;

    private final IRank rank;

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.DISGUISE_SKIN_MENU.TITLE);
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        List<ISkin> cache = super.plugin.getDisguiseHandler().getSkinRepository().getCache();
        cache.forEach(
                result -> entries.put(entries.size(), new DisguiseSkinMenu.SkinButton(result)));
        return entries;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries;
        (entries = new HashMap()).put(48, new DisguiseSkinMenu.FlyntCoalButton());
        entries.put(49, new DisguiseSkinMenu.CustomSkinButton());
        entries.put(50, new DisguiseSkinMenu.EntryButton());
        return entries;
    }

    public DisguiseSkinMenu(String text, IRank rank2) {
        this.text = text;
        this.rank = rank2;
    }

    class CustomSkinButton extends Button {

        private static final String text = "<skin>";

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.OAK_SIGN.get()))
                    .setName(LocaleUtil.tr(player, MenuConfig.DISGUISE_SKIN_MENU.CUSTOM_NAME));
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            Phoenix phoenix = DisguiseSkinMenu.super.plugin;
            String text2 = LocaleUtil.tr(player, MessageConfig.DISGUISE.CHOOSE_SKIN);
            ConversationUtil.ask(
                    phoenix,
                    player,
                    text2,
                    result -> {
                        if (!BukkitAPI.isDisguiseValidName(result)) {
                            player.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(player, MessageConfig.DISGUISE.VALID)));
                        } else {
                            player.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(
                                                    player,
                                                    MessageConfig.DISGUISE
                                                            .DISGUISING_IN_PROGRESS)));
                            CompletableFuture<Skin> skinByName = Skin.getSkinByName(result);
                            skinByName.whenCompleteAsync(
                                    (result2, result3) -> {
                                        if (result2 == null) {
                                            player.sendMessage(
                                                    Color.translate(
                                                            LocaleUtil.tr(
                                                                            player,
                                                                            MessageConfig.DISGUISE
                                                                                    .SKIN_FALLBACK)
                                                                    .replace(
                                                                            text,
                                                                            Color.escape(result))));
                                        }
                                        DisguiseData disguiseData;
                                        (disguiseData = new DisguiseData())
                                                .setDisguiseSkin(
                                                        result2 == null
                                                                ? Skin.DEFAULT_SKIN
                                                                : result2);
                                        disguiseData.setRankId(
                                                DisguiseSkinMenu.this.rank.getUuid());
                                        disguiseData.setDisguiseName(DisguiseSkinMenu.this.text);
                                        IProfile cachedProfile =
                                                DisguiseSkinMenu.super
                                                        .api
                                                        .getProfileHandler()
                                                        .getCachedProfile(player.getUniqueId());
                                        DisguiseData disguiseData2 = disguiseData;
                                        Tasks.run(
                                                () ->
                                                        DisguiseSkinMenu.super
                                                                .plugin
                                                                .getDisguiseHandler()
                                                                .disguise(
                                                                        cachedProfile,
                                                                        disguiseData2,
                                                                        false));
                                    });
                        }
                    });
        }

        public CustomSkinButton() {}
    }

    class EntryButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.PLAYER_HEAD.get(), 1, (short) 3))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.DISGUISE_SKIN_MENU.DISGUISE_AS_NAME)
                                    .replace("<name>", DisguiseSkinMenu.this.text));
            try {
                itemBuilder.setSkullOwner(DisguiseSkinMenu.this.text);
            } catch (Throwable throwable) {
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            CompletableFuture<ISkin> skinByName =
                    DisguiseSkinMenu.super
                            .plugin
                            .getDisguiseHandler()
                            .getSkinByName(DisguiseSkinMenu.this.text);
            skinByName.whenCompleteAsync(
                    (result, result2) -> {
                        if (result == null) {
                            player.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(
                                                            player,
                                                            MessageConfig.DISGUISE.SKIN_FALLBACK)
                                                    .replace(
                                                            "<skin>",
                                                            Color.escape(
                                                                    DisguiseSkinMenu.this.text))));
                        }
                        DisguiseData disguiseData;
                        (disguiseData = new DisguiseData())
                                .setDisguiseSkin(result == null ? Skin.DEFAULT_SKIN : result);
                        disguiseData.setDisguiseName(DisguiseSkinMenu.this.text);
                        disguiseData.setRankId(DisguiseSkinMenu.this.rank.getUuid());
                        IProfile cachedProfile =
                                DisguiseSkinMenu.super
                                        .api
                                        .getProfileHandler()
                                        .getCachedProfile(player.getUniqueId());
                        DisguiseData disguiseData2 = disguiseData;
                        Tasks.run(
                                () ->
                                        DisguiseSkinMenu.super
                                                .plugin
                                                .getDisguiseHandler()
                                                .disguise(cachedProfile, disguiseData2, false));
                    });
        }

        public EntryButton() {}
    }

    class FlyntCoalButton extends Button {

        private static final String FLYNT_COAL_TITLE = "FlyntCoal";

        private FlyntCoalButton() {}

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    XMaterial.PLAYER_HEAD.get(),
                                    1,
                                    (short) SkullType.PLAYER.ordinal()))
                    .setSkullOwner(FLYNT_COAL_TITLE);
            itemBuilder.setName(LocaleUtil.tr(player, MenuConfig.DISGUISE_SKIN_MENU.RANDOM_NAME));
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            ISkin randomSkin = DisguiseSkinMenu.super.plugin.getDisguiseHandler().getRandomSkin();
            DisguiseData disguiseData;
            (disguiseData = new DisguiseData()).setDisguiseSkin(randomSkin);
            disguiseData.setRankId(DisguiseSkinMenu.this.rank.getUuid());
            disguiseData.setDisguiseName(DisguiseSkinMenu.this.text);
            IProfile cachedProfile =
                    DisguiseSkinMenu.super
                            .api
                            .getProfileHandler()
                            .getCachedProfile(player.getUniqueId());
            DisguiseSkinMenu.super
                    .plugin
                    .getDisguiseHandler()
                    .disguise(cachedProfile, disguiseData, false);
        }
    }

    class SkinButton extends Button {

        private final ISkin skin;

        private static final String text = "<skin>";

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    XMaterial.PLAYER_HEAD.get(),
                                    1,
                                    (short) SkullType.PLAYER.ordinal()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.DISGUISE_SKIN_MENU.SKIN_NAME)
                                    .replace(text, this.skin.getName()));
            return DisguiseSkinMenu.super
                    .plugin
                    .getNmsHandler()
                    .getAPI()
                    .itemWithBase64(itemBuilder.toItemStack(), this.skin.getValue());
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            DisguiseData disguiseData;
            (disguiseData = new DisguiseData()).setDisguiseSkin(this.skin);
            disguiseData.setRankId(DisguiseSkinMenu.this.rank.getUuid());
            disguiseData.setDisguiseName(DisguiseSkinMenu.this.text);
            IProfile cachedProfile =
                    DisguiseSkinMenu.super
                            .api
                            .getProfileHandler()
                            .getCachedProfile(player.getUniqueId());
            DisguiseSkinMenu.super
                    .plugin
                    .getDisguiseHandler()
                    .disguise(cachedProfile, disguiseData, false);
        }

        public SkinButton(ISkin skin2) {
            this.skin = skin2;
        }
    }
}
