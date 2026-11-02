package xyz.refinedev.phoenix.rank;

import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.rank.permission.IPermission;
import xyz.refinedev.phoenix.rank.scopeable.IScopeable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public interface IRank extends IScopeable {

    List<String> getAvailablePermissions();

    boolean hasPermission(String text);

    void removePermission(String text);

    IPermission getPermission(String text);

    boolean hasInheritance(IRank rank);

    boolean hasInheritance(UUID uUID);

    void addInheritance(IRank rank);

    void removeInheritance(IRank rank);

    @Nullable
    IRank findDependencyLock(IRank rank);

    void setMetadata(String text, String text2);

    void removeMetadata(String text);

    boolean hasMetadata(String text);

    String getMetadata(String text);

    boolean getMetadataAsBoolean(String text);

    int getMetadataAsInt(String text);

    <T> T getMetadataAsObject(String text, Function<String, T> function);

    @Override
    int hashCode();

    @Override
    boolean equals(Object result);

    UUID getUuid();

    String getName();

    boolean isDefaultRank();

    int getPriority();

    int getPrice();

    List<IPermission> getPermissions();

    List<UUID> getInheritance();

    String getColor();

    String getChatColor();

    String getPrefix();

    String getSuffix();

    String getDisplayName();

    String getPlayerListPrefix();

    String getColorLegacy();

    String getChatColorLegacy();

    String getPrefixLegacy();

    String getSuffixLegacy();

    String getDisplayNameLegacy();

    String getPlayerListPrefixLegacy();

    String getPrefixModern();

    String getDisplayNameModern();

    String getSuffixModern();

    String getColorModern();

    String getChatColorModern();

    String getPlayerListPrefixModern();

    boolean isVisible();

    boolean isStaff();

    boolean isSubscription();

    boolean isGrantable();

    boolean isPurchasable();

    Map<String, String> getMetadata();

    void setName(String text);

    void setDefaultRank(boolean matches);

    void setPriority(int value);

    void setPrice(int value);

    void setPermissions(List<IPermission> values);

    void setInheritance(List<UUID> values);

    void setPrefix(String text);

    void setPrefixModern(String text);

    void setDisplayName(String text);

    void setDisplayNameModern(String text);

    void setSuffix(String text);

    void setSuffixModern(String text);

    void setColor(String text);

    void setChatColor(String text);

    void setChatColorModern(String text);

    void setColorModern(String text);

    void setPlayerListPrefix(String text);

    void setPlayerListPrefixModern(String text);

    void setVisible(boolean matches);

    void setStaff(boolean matches);

    void setSubscription(boolean matches);

    void setGrantable(boolean matches);

    void setPurchasable(boolean matches);

    void setMetadata(Map<String, String> entries);
}
