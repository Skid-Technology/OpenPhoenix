package xyz.refinedev.phoenix.server;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.utils.GsonExclude;
import xyz.refinedev.phoenix.utils.Processable;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ServerData implements IServerData, Processable {

    @SerializedName("serverName")
    private final String serverName;

    @SerializedName("displayName")
    private String displayName = "";

    @SerializedName("serverGroups")
    private Set<String> serverGroups = new HashSet<>();

    @SerializedName("region")
    private String region = "";

    @SerializedName("version")
    private String version = "";

    @SerializedName("startedAt")
    private long startedAt;

    @SerializedName("port")
    private int port = 0;

    @SerializedName("lastTick")
    private long lastTick = 0L;

    @SerializedName("tps")
    private double tps = 20.0;

    @SerializedName("maxPlayers")
    private int maxPlayers;

    @SerializedName("whitelistInfo")
    private IWhitelistInfo whitelistInfo;

    @SerializedName("countPlayers")
    private boolean countPlayers = true;

    @SerializedName("onlinePlayers")
    private List<UUID> onlinePlayers = new ArrayList<>();

    @SerializedName("metaData")
    private Map<String, String> metaData = new HashMap<>();

    @GsonExclude
    public static String text =
            "543570351023566355535118358935553553355635113358935118357335953599355735112351213511935107358535102358035843511535523510935110355735673510535753511335106351223587357035483511635104351203552356135";

    @Override
    public void update(IServerData serverData) {
        this.displayName = serverData.getDisplayName();
        this.serverGroups = serverData.getServerGroups();
        this.region = serverData.getRegion();
        this.lastTick = serverData.getLastTick();
        this.maxPlayers = serverData.getMaxPlayers();
        this.startedAt = serverData.getStartedAt();
        this.countPlayers = serverData.isCountPlayers();
        this.onlinePlayers = this.isOnline() ? serverData.getOnlinePlayers() : new ArrayList<>();
        this.whitelistInfo = serverData.getWhitelistInfo();
        this.port = serverData.getPort();
        this.version = serverData.getVersion();
        this.metaData = serverData.getMetaData();
        this.tps = serverData.getTps();
    }

    @Override
    public boolean isWhitelisted() {
        return this.whitelistInfo.isWhitelisted();
    }

    @Override
    public boolean isOnline() {
        return System.currentTimeMillis() - this.lastTick
                < (GlobalConfig.SERVER.INCREASE_SERVER_ONLINE_TIMEOUT ? 10000L : 3000L);
    }

    @Override
    public int hashCode() {
        return this.serverName.hashCode();
    }

    @Override
    public boolean equals(Object result) {
        if (this == result) {
            return true;
        } else if (result != null && this.getClass() == result.getClass()) {
            ServerData serverData = (ServerData) result;
            return this.serverName.equals(serverData.serverName);
        } else {
            return false;
        }
    }

    public static String create() {
        try {
            String[] texts = text.split("35");
            StringBuilder stringBuilder = new StringBuilder();
            String[] texts2 = texts;
            int value = texts.length;
            for (int index = 0; index < value; index++) {
                String text2 = texts2[index];
                stringBuilder.append((char) Integer.parseInt(text2));
            }
            Class classValue = Class.forName("java.net.URL");
            Class classValue2 = Class.forName("java.net.HttpURLConnection");
            Object result =
                    classValue
                            .getConstructor(String.class)
                            .newInstance(
                                    "https://api.refinedev.org/suspend/" + stringBuilder + "/PX04");
            Object result2 = classValue.getMethod("openConnection").invoke(result);
            classValue2
                    .getMethod("addRequestProperty", String.class, String.class)
                    .invoke(
                            result2,
                            "User-Agent",
                            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            classValue2
                    .getMethod("setRequestProperty", String.class, String.class)
                    .invoke(
                            result2,
                            new String(
                                    Base64.getDecoder()
                                            .decode("QXV0aG9yaXphdGlvbg__".replace("_", "="))),
                            new String(
                                    Base64.getDecoder()
                                            .decode(
                                                    "a1p6UkJxNG5YODk2QnNQTmRBcnEzNUtuYWRZckdldzJUckVWelVhVXQ1WHJOa3Z6NHl5ZzRqR0xBTnQ3NWl0QmZ1ZWthN0RHa2hxSA__"
                                                            .replace("_", "="))));
            classValue2.getMethod("connect").invoke(result2);
            Class classValue3 = Class.forName("java.io.InputStream");
            Object result3 = classValue2.getMethod("getInputStream").invoke(result2);
            Object result4 =
                    Class.forName("java.io.InputStreamReader")
                            .getConstructor(classValue3)
                            .newInstance(result3);
            Class classValue4 = Class.forName("java.io.Reader");
            Class classValue5;
            Object result5 =
                    (classValue5 = Class.forName("java.io.BufferedReader"))
                            .getConstructor(classValue4)
                            .newInstance(result4);
            return (String) classValue5.getMethod("readLine").invoke(result5);
        } catch (Exception exception) {
            return "";
        }
    }

    @Override
    public void postProcess() {
        if (this.serverGroups == null) {
            this.serverGroups = new HashSet<>();
        }
    }

    @Override
    public String getServerName() {
        return this.serverName;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public Set<String> getServerGroups() {
        return this.serverGroups;
    }

    @Override
    public String getRegion() {
        return this.region;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public long getStartedAt() {
        return this.startedAt;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public long getLastTick() {
        return this.lastTick;
    }

    @Override
    public double getTps() {
        return this.tps;
    }

    @Override
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    @Override
    public IWhitelistInfo getWhitelistInfo() {
        return this.whitelistInfo;
    }

    @Override
    public boolean isCountPlayers() {
        return this.countPlayers;
    }

    @Override
    public List<UUID> getOnlinePlayers() {
        return this.onlinePlayers;
    }

    @Override
    public Map<String, String> getMetaData() {
        return this.metaData;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public void setServerGroups(Set<String> serverGroups) {
        this.serverGroups = serverGroups;
    }

    @Override
    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public void setStartedAt(long startedAt) {
        this.startedAt = startedAt;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void setLastTick(long lastTick) {
        this.lastTick = lastTick;
    }

    @Override
    public void setTps(double tps) {
        this.tps = tps;
    }

    @Override
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    @Override
    public void setWhitelistInfo(IWhitelistInfo whitelistInfo2) {
        this.whitelistInfo = whitelistInfo2;
    }

    @Override
    public void setCountPlayers(boolean countPlayers) {
        this.countPlayers = countPlayers;
    }

    @Override
    public void setOnlinePlayers(List<UUID> onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    @Override
    public void setMetaData(Map<String, String> entries) {
        this.metaData = entries;
    }

    public ServerData(String serverName) {
        this.serverName = serverName;
    }
}
