package xyz.refinedev.phoenix.server;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.whitelist.WhitelistAccess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class WhitelistInfo implements IWhitelistInfo {

    @SerializedName("whitelisted")
    private final boolean whitelisted;

    @SerializedName("accessTypes")
    private final Set<WhitelistAccess> accessTypes;

    @SerializedName("rankId")
    private final UUID rankId;

    @SerializedName("uuids")
    private final List<String> uuids;

    @Nullable
    @Override
    public IRank getRank() {
        return this.rankId == null
                ? null
                : PlatformGetter.getInstance().getRankHandler().getRank(this.rankId);
    }

    @NotNull
    @Override
    public List<String> getNiceAccessTypes() {
        ArrayList<String> values = new ArrayList<>();
        Iterator<WhitelistAccess> iterator2 = this.accessTypes.iterator();
        while (iterator2.hasNext()) {
            WhitelistAccess whitelistAccess;
            if ((whitelistAccess = (WhitelistAccess) iterator2.next()) != null) {
                values.add(whitelistAccess.getDisplayName());
            }
        }
        return values;
    }

    @Override
    public boolean isWhitelisted() {
        return this.whitelisted;
    }

    @Override
    public Set<WhitelistAccess> getAccessTypes() {
        return this.accessTypes;
    }

    @Override
    public UUID getRankId() {
        return this.rankId;
    }

    @Override
    public List<String> getUuids() {
        return this.uuids;
    }

    public WhitelistInfo(
            boolean whitelisted,
            Set<WhitelistAccess> accessTypes,
            UUID rankId,
            List<String> uuids) {
        this.whitelisted = whitelisted;
        this.accessTypes = accessTypes;
        this.rankId = rankId;
        this.uuids = uuids;
    }
}
