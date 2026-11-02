package xyz.refinedev.phoenix.hook;

import xyz.refinedev.phoenix.profile.IProfile;

import java.util.UUID;

public interface ILevelHook {

    void addExperience(IProfile profile, int value, String text);

    void removeExperience(IProfile profile, int value);

    void setExperience(IProfile profile, int value);

    void openLevelMenu(UUID uUID, IProfile profile);

    String getLevelNameWithColor(IProfile profile);

    String getLevelNameWithColor(int value);

    String getLevelDisplayName(IProfile profile);

    String getLevelDisplayName(int value);

    int getExperience(IProfile profile);
}
