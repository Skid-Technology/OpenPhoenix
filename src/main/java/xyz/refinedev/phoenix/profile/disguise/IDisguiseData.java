package xyz.refinedev.phoenix.profile.disguise;

import java.util.Map;
import java.util.UUID;

public interface IDisguiseData {

    boolean isRankDisguised();

    boolean isDisguised();

    String getRealName();

    String getDisguiseName();

    UUID getRankId();

    ISkin getRealSkin();

    ISkin getDisguiseSkin();

    UUID getDisguiseUuid();

    void setRealName(String text);

    void setDisguiseName(String text);

    void setRankId(UUID uUID);

    void setRealSkin(ISkin skin);

    void setDisguiseSkin(ISkin skin);

    void setDisguiseUuid(UUID uUID);

    Map<String, String> getRollbackData();

    void setRollbackData(Map<String, String> entries);
}
