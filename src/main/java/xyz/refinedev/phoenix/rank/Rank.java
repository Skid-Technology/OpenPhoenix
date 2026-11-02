package xyz.refinedev.phoenix.rank;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.rank.permission.IPermission;
import xyz.refinedev.phoenix.rank.scopeable.Scopeable;
import xyz.refinedev.phoenix.scope.Scope;
import xyz.refinedev.phoenix.utils.Processable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class Rank extends Scopeable implements IRank, Processable {

    @SerializedName("_id")
    private final UUID uuid;

    @SerializedName("name")
    private String name = "Unknown";

    @SerializedName("defaultRank")
    private boolean defaultRank = false;

    @SerializedName("priority")
    private int priority = 1;

    @SerializedName("price")
    private int price = 0;

    @SerializedName("permissions")
    private List<IPermission> permissions = new ArrayList<>();

    @SerializedName("inheritance")
    private List<UUID> inheritance = new ArrayList<>();

    @SerializedName("prefix")
    private String prefixLegacy = "";

    @SerializedName("prefixModern")
    private String prefixModern = "";

    @SerializedName("displayName")
    private String displayNameLegacy = "SetDisplayName";

    @SerializedName("displayNameModern")
    private String displayNameModern = "SetDisplayName";

    @SerializedName("suffix")
    private String suffixLegacy = "";

    @SerializedName("suffixModern")
    private String suffixModern = "";

    @SerializedName("color")
    private String colorLegacy = "&a";

    @SerializedName("colorModern")
    private String colorModern = "&a";

    @SerializedName("playerListPrefix")
    private String playerListPrefixLegacy = "";

    @SerializedName("playerListPrefixModern")
    private String playerListPrefixModern = "";

    @SerializedName("visible")
    private boolean visible = true;

    @SerializedName("staff")
    private boolean staff = false;

    @SerializedName("subscription")
    private boolean subscription = false;

    @SerializedName("grantable")
    private boolean grantable = true;

    @SerializedName("purchasable")
    private boolean purchasable = false;

    @SerializedName("metadata")
    private Map<String, String> metadata = new HashMap<>();

    public Rank(String text) {
        this.uuid = UUID.randomUUID();
        this.name = text;
        this.displayNameLegacy = text;
        this.setScopes(Collections.singletonList(Scope.scopeScope));
    }

    @Override
    public String getColor() {
        return PlatformGetter.getInstance().isUseModernPrefixes()
                ? this.colorModern
                : this.colorLegacy;
    }

    @Override
    public String getPrefix() {
        return PlatformGetter.getInstance().isUseModernPrefixes()
                ? this.prefixModern
                : this.prefixLegacy;
    }

    @Override
    public String getSuffix() {
        return PlatformGetter.getInstance().isUseModernPrefixes()
                ? this.suffixModern
                : this.suffixLegacy;
    }

    @Override
    public String getDisplayName() {
        return PlatformGetter.getInstance().isUseModernPrefixes()
                ? this.displayNameModern
                : this.displayNameLegacy;
    }

    @Override
    public String getPlayerListPrefix() {
        return PlatformGetter.getInstance().isUseModernPrefixes()
                ? this.playerListPrefixModern
                : this.playerListPrefixLegacy;
    }

    @Override
    public String getChatColor() {
        return PlatformGetter.getInstance().isUseModernPrefixes()
                ? this.metadata.getOrDefault("chatcolor-modern", "")
                : this.metadata.getOrDefault("chatcolor", "");
    }

    @Override
    public String getColorLegacy() {
        return this.colorLegacy;
    }

    @Override
    public String getChatColorLegacy() {
        return this.metadata.getOrDefault("chatcolor", "");
    }

    @Override
    public String getPrefixLegacy() {
        return this.prefixLegacy;
    }

    @Override
    public String getSuffixLegacy() {
        return this.suffixLegacy;
    }

    @Override
    public String getDisplayNameLegacy() {
        return this.displayNameLegacy;
    }

    @Override
    public String getPlayerListPrefixLegacy() {
        return this.playerListPrefixLegacy;
    }

    @Override
    public String getChatColorModern() {
        return this.metadata.getOrDefault("chatcolor-modern", "");
    }

    @Override
    public void setChatColor(String text) {
        this.metadata.put("chatcolor", text);
    }

    @Override
    public void setChatColorModern(String text) {
        this.metadata.put("chatcolor-modern", text);
    }

    @Override
    public List<String> getAvailablePermissions() {
        ArrayList<String> values = new ArrayList<>();
        Iterator<IPermission> iterator2 = this.permissions.iterator();
        while (iterator2.hasNext()) {
            IPermission permission;
            if ((permission = (IPermission) iterator2.next()).isScope()) {
                values.add(permission.getPermission().toLowerCase());
            }
        }
        return values;
    }

    @Override
    public boolean hasPermission(String text) {
        Iterator<IPermission> iterator2 = this.permissions.iterator();
        while (iterator2.hasNext()) {
            if (((IPermission) iterator2.next()).getPermission().equalsIgnoreCase(text)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void removePermission(String text) {
        IPermission permission;
        if ((permission = this.getPermission(text)) != null) {
            this.permissions.remove(permission);
        }
    }

    @Override
    public IPermission getPermission(String text) {
        Iterator<IPermission> iterator2 = this.permissions.iterator();
        while (iterator2.hasNext()) {
            IPermission permission;
            if ((permission = (IPermission) iterator2.next())
                    .getPermission()
                    .equalsIgnoreCase(text)) {
                return permission;
            }
        }
        return null;
    }

    @Override
    public boolean hasInheritance(IRank rank) {
        return this.hasInheritance(rank.getUuid());
    }

    @Override
    public boolean hasInheritance(UUID uUID) {
        for (UUID uUID2 : this.inheritance) {
            if (uUID.equals(uUID2)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addInheritance(IRank rank) {
        this.inheritance.add(rank.getUuid());
    }

    @Override
    public void removeInheritance(IRank rank) {
        this.inheritance.remove(rank.getUuid());
    }

    @Nullable
    @Override
    public IRank findDependencyLock(IRank rank) {
        if (this == rank) {
            return rank;
        }
        if (rank.getInheritance().contains(this.uuid)) {
            return rank;
        }
        if (!GlobalConfig.SERVER.PASSTHROUGH_CHILDREN) {
            return null;
        }
        for (UUID uUID : this.inheritance) {
            IRank rank2;
            if ((rank2 = PlatformGetter.getInstance().getRankHandler().getRank(uUID)) != null) {
                if (rank2.getInheritance().contains(rank.getUuid())) {
                    return rank;
                }
                rank2.findDependencyLock(rank);
            }
        }
        for (UUID uUID2 : rank.getInheritance()) {
            IRank rank3;
            if ((rank3 = PlatformGetter.getInstance().getRankHandler().getRank(uUID2)) != null) {
                if (rank3.getInheritance().contains(this.uuid)) {
                    return rank;
                }
                rank3.findDependencyLock(rank);
            }
        }
        return null;
    }

    @Override
    public void setMetadata(String text, String text2) {
        this.metadata.put(text.toLowerCase(), text2);
    }

    @Override
    public void removeMetadata(String text) {
        this.metadata.remove(text.toLowerCase());
    }

    @Override
    public boolean hasMetadata(String text) {
        return this.metadata.containsKey(text.toLowerCase());
    }

    @Override
    public String getMetadata(String text) {
        return this.metadata.get(text.toLowerCase());
    }

    @Override
    public boolean getMetadataAsBoolean(String text) {
        return Boolean.parseBoolean(this.getMetadata(text));
    }

    @Override
    public int getMetadataAsInt(String text) {
        return Integer.parseInt(this.getMetadata(text));
    }

    @Override
    public <T> T getMetadataAsObject(String text, Function<String, T> function) {
        return (T) function.apply(this.getMetadata(text));
    }

    @Override
    public void postProcess() {
        if (this.prefixModern == null) {
            this.prefixModern = this.prefixLegacy;
        }
        if (this.displayNameModern == null) {
            this.displayNameModern = this.displayNameLegacy;
        }
        if (this.suffixModern == null) {
            this.suffixModern = this.suffixLegacy;
        }
        if (this.colorModern == null) {
            this.colorModern = this.colorLegacy;
        }
        if (this.playerListPrefixModern == null) {
            this.playerListPrefixModern = this.playerListPrefixLegacy;
        }
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }

    @Override
    public boolean equals(Object result) {
        if (this == result) {
            return true;
        } else if (result != null && this.getClass() == result.getClass()) {
            Rank rank = (Rank) result;
            return this.uuid.equals(rank.uuid);
        } else {
            return false;
        }
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isDefaultRank() {
        return this.defaultRank;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public int getPrice() {
        return this.price;
    }

    @Override
    public List<IPermission> getPermissions() {
        return this.permissions;
    }

    @Override
    public List<UUID> getInheritance() {
        return this.inheritance;
    }

    @Override
    public String getPrefixModern() {
        return this.prefixModern;
    }

    @Override
    public String getDisplayNameModern() {
        return this.displayNameModern;
    }

    @Override
    public String getSuffixModern() {
        return this.suffixModern;
    }

    @Override
    public String getColorModern() {
        return this.colorModern;
    }

    @Override
    public String getPlayerListPrefixModern() {
        return this.playerListPrefixModern;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public boolean isStaff() {
        return this.staff;
    }

    @Override
    public boolean isSubscription() {
        return this.subscription;
    }

    @Override
    public boolean isGrantable() {
        return this.grantable;
    }

    @Override
    public boolean isPurchasable() {
        return this.purchasable;
    }

    @Override
    public Map<String, String> getMetadata() {
        return this.metadata;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setDefaultRank(boolean defaultRank) {
        this.defaultRank = defaultRank;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public void setPermissions(List<IPermission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public void setInheritance(List<UUID> inheritance) {
        this.inheritance = inheritance;
    }

    @Override
    public void setPrefix(String prefixLegacy) {
        this.prefixLegacy = prefixLegacy;
    }

    @Override
    public void setPrefixModern(String prefixModern) {
        this.prefixModern = prefixModern;
    }

    @Override
    public void setDisplayName(String displayNameLegacy) {
        this.displayNameLegacy = displayNameLegacy;
    }

    @Override
    public void setDisplayNameModern(String displayNameModern) {
        this.displayNameModern = displayNameModern;
    }

    @Override
    public void setSuffix(String suffixLegacy) {
        this.suffixLegacy = suffixLegacy;
    }

    @Override
    public void setSuffixModern(String suffixModern) {
        this.suffixModern = suffixModern;
    }

    @Override
    public void setColor(String colorLegacy) {
        this.colorLegacy = colorLegacy;
    }

    @Override
    public void setColorModern(String colorModern) {
        this.colorModern = colorModern;
    }

    @Override
    public void setPlayerListPrefix(String playerListPrefixLegacy) {
        this.playerListPrefixLegacy = playerListPrefixLegacy;
    }

    @Override
    public void setPlayerListPrefixModern(String playerListPrefixModern) {
        this.playerListPrefixModern = playerListPrefixModern;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void setStaff(boolean staff) {
        this.staff = staff;
    }

    @Override
    public void setSubscription(boolean subscription) {
        this.subscription = subscription;
    }

    @Override
    public void setGrantable(boolean grantable) {
        this.grantable = grantable;
    }

    @Override
    public void setPurchasable(boolean purchasable) {
        this.purchasable = purchasable;
    }

    @Override
    public void setMetadata(Map<String, String> entries) {
        this.metadata = entries;
    }

    public Rank(UUID uuid) {
        this.uuid = uuid;
    }
}
