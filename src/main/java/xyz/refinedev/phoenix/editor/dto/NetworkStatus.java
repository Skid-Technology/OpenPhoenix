package xyz.refinedev.phoenix.editor.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Set;

public class NetworkStatus {

    @SerializedName("answeredBy")
    private final String answeredBy;

    @SerializedName("players")
    private final int players;

    @SerializedName("servers")
    private final List<NetworkStatus.Entry> servers;

    @SerializedName("proxies")
    private final List<NetworkStatus.Entry> proxies;

    public String getAnsweredBy() {
        return this.answeredBy;
    }

    public int getPlayers() {
        return this.players;
    }

    public List<NetworkStatus.Entry> getServers() {
        return this.servers;
    }

    public List<NetworkStatus.Entry> getProxies() {
        return this.proxies;
    }

    public NetworkStatus(
            String answeredBy,
            int players,
            List<NetworkStatus.Entry> servers,
            List<NetworkStatus.Entry> proxies) {
        this.answeredBy = answeredBy;
        this.players = players;
        this.servers = servers;
        this.proxies = proxies;
    }

    public static class Entry {

        @SerializedName("name")
        private final String name;

        @SerializedName("displayName")
        private final String displayName;

        @SerializedName("online")
        private final boolean online;

        @SerializedName("players")
        private final int players;

        @SerializedName("maxPlayers")
        private final int maxPlayers;

        @SerializedName("tps")
        private final double tps;

        @SerializedName("version")
        private final String version;

        @SerializedName("region")
        private final String region;

        @SerializedName("groups")
        private final Set<String> groups;

        @SerializedName("whitelisted")
        private final boolean whitelisted;

        @SerializedName("startedAt")
        private final long startedAt;

        public String getName() {
            return this.name;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public boolean isOnline() {
            return this.online;
        }

        public int getPlayers() {
            return this.players;
        }

        public int getMaxPlayers() {
            return this.maxPlayers;
        }

        public double getTps() {
            return this.tps;
        }

        public String getVersion() {
            return this.version;
        }

        public String getRegion() {
            return this.region;
        }

        public Set<String> getGroups() {
            return this.groups;
        }

        public boolean isWhitelisted() {
            return this.whitelisted;
        }

        public long getStartedAt() {
            return this.startedAt;
        }

        public Entry(
                String name,
                String displayName,
                boolean online,
                int players,
                int maxPlayers,
                double tps,
                String version,
                String region,
                Set<String> groups,
                boolean whitelisted,
                long startedAt) {
            this.name = name;
            this.displayName = displayName;
            this.online = online;
            this.players = players;
            this.maxPlayers = maxPlayers;
            this.tps = tps;
            this.version = version;
            this.region = region;
            this.groups = groups;
            this.whitelisted = whitelisted;
            this.startedAt = startedAt;
        }
    }
}
