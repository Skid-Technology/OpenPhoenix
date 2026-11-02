package xyz.refinedev.phoenix.editor.op;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.editor.dto.NetworkStatus;
import xyz.refinedev.phoenix.server.IServerData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class StatusOperations {

    private final CommonPlatform platform;

    public StatusOperations(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }

    public EditorOpHandler getEditorOpHandler() {
        return (result, result2) -> {
            List<NetworkStatus.Entry> values =
                    this.findList(this.platform.getNetworkHandler().getServers());
            List<NetworkStatus.Entry> values2 =
                    this.findList(this.platform.getNetworkHandler().getProxies());
            int value = 0;
            for (NetworkStatus.Entry entry : values) {
                value += entry.getPlayers();
            }
            return this.platform
                    .getGson()
                    .toJsonTree(
                            new NetworkStatus(GlobalConfig.SERVER.NAME, value, values, values2));
        };
    }

    private List<NetworkStatus.Entry> findList(Collection<IServerData> values) {
        ArrayList<NetworkStatus.Entry> values2 = new ArrayList<>();
        Iterator<IServerData> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            IServerData serverData;
            boolean online = (serverData = (IServerData) iterator2.next()).isOnline();
            values2.add(
                    new NetworkStatus.Entry(
                            serverData.getServerName(),
                            serverData.getDisplayName(),
                            online,
                            online ? serverData.getOnlinePlayers().size() : 0,
                            serverData.getMaxPlayers(),
                            serverData.getTps(),
                            serverData.getVersion(),
                            serverData.getRegion(),
                            serverData.getServerGroups(),
                            serverData.isWhitelisted(),
                            serverData.getStartedAt()));
        }
        values2.sort(Comparator.comparing(NetworkStatus.Entry::getName));
        return values2;
    }
}
