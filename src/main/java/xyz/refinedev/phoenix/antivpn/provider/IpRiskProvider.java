package xyz.refinedev.phoenix.antivpn.provider;

import com.google.gson.JsonObject;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.antivpn.api.IAntiVPNProvider;
import xyz.refinedev.phoenix.profile.antivpn.AntiVPNData;
import xyz.refinedev.phoenix.profile.antivpn.IAntiVPNData;
import xyz.refinedev.phoenix.utils.HttpUtils;
import xyz.refinedev.phoenix.utils.JsonParser;

import java.util.UUID;
import java.util.logging.Level;

public class IpRiskProvider implements IAntiVPNProvider {

    private final CommonPlatform platform;

    @Override
    public IAntiVPNData check(UUID uUID, String text) {
        String text2 = this.platform.getHashHandler().hash(text);
        String text3;
        if ((text3 = HttpUtils.get("https://beta.iprisk.info/v1/" + text)) == null) {
            return null;
        }
        try {
            JsonObject jsonObject;
            boolean matches =
                    (jsonObject = JsonParser.findJsonElement(text3).getAsJsonObject())
                                    .get("data_center")
                                    .getAsBoolean()
                            || jsonObject.get("public_proxy").getAsBoolean()
                            || jsonObject.get("tor_exit_relay").getAsBoolean();
            AntiVPNData antiVPNData;
            (antiVPNData = new AntiVPNData(uUID, text2))
                    .setCity(
                            jsonObject.get("city_name") != null
                                    ? jsonObject.get("city_name").getAsString()
                                    : "N/A");
            antiVPNData.setIsp(
                    jsonObject.get("autonomous_system_organization") != null
                            ? jsonObject.get("autonomous_system_organization").getAsString()
                            : "N/A");
            antiVPNData.setCountry(
                    jsonObject.get("country_name") != null
                            ? jsonObject.get("country_name").getAsString()
                            : "N/A");
            antiVPNData.setRegion(
                    jsonObject.get("region_name") != null
                            ? jsonObject.get("region_name").getAsString()
                            : "N/A");
            antiVPNData.setVpn(matches);
            return antiVPNData;
        } catch (Exception exception) {
            this.platform
                    .getLogger()
                    .log(
                            Level.SEVERE,
                            "There was an error while performing the AntiVPN Check: ",
                            exception);
            return null;
        }
    }

    public IpRiskProvider(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
