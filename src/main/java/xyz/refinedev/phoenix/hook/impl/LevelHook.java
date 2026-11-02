package xyz.refinedev.phoenix.hook.impl;

import xyz.refinedev.phoenix.hook.ILevelHook;
import xyz.refinedev.phoenix.profile.IProfile;

import java.util.UUID;

public class LevelHook implements ILevelHook {

    @Override
    public void addExperience(IProfile profile, int value, String text) {}

    @Override
    public void removeExperience(IProfile profile, int value) {}

    @Override
    public void setExperience(IProfile profile, int value) {}

    @Override
    public void openLevelMenu(UUID uUID, IProfile profile) {}

    @Override
    public String getLevelNameWithColor(IProfile profile) {
        return "";
    }

    @Override
    public String getLevelNameWithColor(int value) {
        return "";
    }

    @Override
    public String getLevelDisplayName(IProfile profile) {
        return "";
    }

    @Override
    public String getLevelDisplayName(int value) {
        return "";
    }

    @Override
    public int getExperience(IProfile profile) {
        return 0;
    }
}
