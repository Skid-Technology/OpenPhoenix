package xyz.refinedev.phoenix.hook.impl;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.hook.ICosmeticsHook;

import java.util.UUID;

public class CosmeticsHook implements ICosmeticsHook {

    @Override
    public int getCoins(UUID uUID) {
        return 0;
    }

    @Override
    public String getColor(UUID uUID) {
        return PlatformGetter.getInstance()
                .getProfileHandler()
                .getProfile(uUID)
                .getHighestRank()
                .getColor();
    }

    @Override
    public String getChatColor(UUID uUID) {
        return "";
    }
}
