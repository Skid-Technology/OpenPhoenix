package xyz.refinedev.phoenix.menu.button;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.lib.xseries.XEnchantment;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.tag.CustomTagStatus;
import xyz.refinedev.phoenix.profile.tag.ICustomTag;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

public class SelectableTagButton extends Button {

    private final ICustomTag customTag;

    private final CommonPlatform platform;

    @Override
    public ItemStack getItem(Player player) {
        IProfile cachedProfile =
                this.platform.getProfileHandler().getCachedProfile(player.getUniqueId());
        ItemBuilder itemBuilder =
                new ItemBuilder(
                        this.customTag.isAccepted()
                                ? XMaterial.NAME_TAG.get()
                                : (this.customTag.isDenied()
                                        ? XMaterial.REDSTONE.get()
                                        : XMaterial.PAPER.get()));
        String text =
                this.customTag.getStatus() == CustomTagStatus.ACCEPTED
                        ? LocaleUtil.tr(player, MenuConfig.CUSTOM_TAGS.STATUS_ACCEPTED)
                        : (this.customTag.getStatus() == CustomTagStatus.DENIED
                                ? LocaleUtil.tr(player, MenuConfig.CUSTOM_TAGS.STATUS_DENIED)
                                : LocaleUtil.tr(player, MenuConfig.CUSTOM_TAGS.STATUS_UNDECIDED));
        itemBuilder.setName(
                LocaleUtil.tr(player, MenuConfig.CUSTOM_TAGS.DELETE_NAME)
                        .replace("<createdOn>", DateUtils.getDate(this.customTag.getCreatedOn()))
                        .replace("<tag>", this.customTag.getColor() + this.customTag.getPrefix())
                        .replace("<status>", text)
                        .replace(
                                "<selected>",
                                cachedProfile.getTag() == this.customTag
                                        ? LocaleUtil.tr(player, MenuConfig.TAGS_MENU.SELECTED_NAME)
                                        : ""));
        for (String text2 : LocaleUtil.tr(player, MenuConfig.CUSTOM_TAGS.DELETE_LORE)) {
            itemBuilder.addLoreLine(
                    text2.replace("<createdOn>", DateUtils.getDate(this.customTag.getCreatedOn()))
                            .replace(
                                    "<tag>", this.customTag.getColor() + this.customTag.getPrefix())
                            .replace("<status>", this.customTag.getStatus().getDisplayName()));
        }
        if (cachedProfile.getTag() == this.customTag) {
            itemBuilder.addUnsafeEnchantment(XEnchantment.UNBREAKING.getEnchant(), 10);
        }
        return itemBuilder.toItemStack();
    }

    public SelectableTagButton(ICustomTag customTag2, CommonPlatform commonPlatform) {
        this.customTag = customTag2;
        this.platform = commonPlatform;
    }
}
