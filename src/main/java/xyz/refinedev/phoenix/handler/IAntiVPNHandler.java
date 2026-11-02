package xyz.refinedev.phoenix.handler;

import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.antivpn.IAntiVPNData;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface IAntiVPNHandler {

    void fetchAntiVpnBypass();

    void whitelist(UUID uUID, boolean matches);

    void unwhitelist(UUID uUID, boolean matches);

    void cacheVpnDataRedis(IAntiVPNData antiVPNData);

    Map<UUID, IAntiVPNData> fetchLatestVpnDataRedis(Set<IProfile> values);

    IAntiVPNData fetchLatestVpnDataRedis(UUID uUID, String text);

    List<IAntiVPNData> fetchVpnDataRedis(UUID uUID);

    void removeVpnDataRedis(IAntiVPNData antiVPNData);

    boolean isEnabled();

    boolean hasBypass(UUID uUID);

    boolean check(UUID uUID, String text);

    IAntiVPNData getAntiVPNData(UUID uUID);

    IAntiVPNData getAntiVPNData(String text);

    Set<UUID> getAntiVpnBypass();
}
