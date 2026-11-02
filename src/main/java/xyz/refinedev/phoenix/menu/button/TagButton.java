package xyz.refinedev.phoenix.menu.button;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.tag.ICustomTag;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

public class TagButton extends Button {

    private final ICustomTag customTag;

    private final CommonPlatform platform;

    @Override
    public ItemStack getItem(Player player) {
        ItemBuilder itemBuilder = new ItemBuilder(XMaterial.NAME_TAG.get());
        IProfile profile =
                this.platform.getProfileHandler().getProfile(this.customTag.getCreator());
        itemBuilder.setName(
                LocaleUtil.tr(player, MenuConfig.CUSTOM_TAGS_ADMIN.CONFIRMATION_NAME)
                        .replace("<createdOn>", DateUtils.getDate(this.customTag.getCreatedOn()))
                        .replace("<tag>", this.customTag.getPrefix())
                        .replace("<creator>", profile.getNameWithColorNoDisguise()));
        for (String text : LocaleUtil.tr(player, MenuConfig.CUSTOM_TAGS_ADMIN.CONFIRMATION_LORE)) {
            itemBuilder.addLoreLine(
                    text.replace("<createdOn>", DateUtils.getDate(this.customTag.getCreatedOn()))
                            .replace("<tag>", this.customTag.getPrefix())
                            .replace("<creator>", profile.getNameWithColorNoDisguise()));
        }
        return itemBuilder.toItemStack();
    }

    public TagButton(ICustomTag customTag2, CommonPlatform commonPlatform) {
        this.customTag = customTag2;
        this.platform = commonPlatform;
    }
}
