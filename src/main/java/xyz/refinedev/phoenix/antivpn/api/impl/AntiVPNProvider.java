package xyz.refinedev.phoenix.antivpn.api.impl;

import com.google.gson.JsonObject;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.antivpn.api.IAntiVPNProvider;
import xyz.refinedev.phoenix.profile.antivpn.AntiVPNData;
import xyz.refinedev.phoenix.profile.antivpn.IAntiVPNData;
import xyz.refinedev.phoenix.utils.HttpUtils;
import xyz.refinedev.phoenix.utils.JsonParser;

import java.util.UUID;

public class AntiVPNProvider implements IAntiVPNProvider {

    private final CommonPlatform platform;

    private final String text;

    @Override
    public IAntiVPNData check(UUID uUID, String text2) {
        String text3 = this.platform.getHashHandler().hash(text2);
        String text4;
        if ((text4 =
                        HttpUtils.get(
                                "https://proxycheck.io/v2/"
                                        + text2
                                        + "?key="
                                        + this.text
                                        + "&vpn=1&asn=1"))
                == null) {
            return null;
        }
        JsonObject jsonObject;
        boolean matches =
                (jsonObject =
                                        JsonParser.findJsonElement(text4)
                                                .getAsJsonObject()
                                                .getAsJsonObject(text2))
                                .get("proxy")
                                .getAsString()
                                .equalsIgnoreCase("yes")
                        || jsonObject.get("type").getAsString().equalsIgnoreCase("VPN");
        AntiVPNData antiVPNData;
        (antiVPNData = new AntiVPNData(uUID, text3)).setCity(jsonObject.get("city").getAsString());
        antiVPNData.setIsp(jsonObject.get("provider").getAsString());
        antiVPNData.setCountry(jsonObject.get("country").getAsString());
        antiVPNData.setRegion(jsonObject.get("continent").getAsString());
        antiVPNData.setVpn(matches);
        antiVPNData.setMobile(
                jsonObject.has("type")
                        && jsonObject.get("type").getAsString().equalsIgnoreCase("Mobile"));
        return antiVPNData;
    }

    public AntiVPNProvider(CommonPlatform commonPlatform, String text) {
        this.platform = commonPlatform;
        this.text = text;
    }
}
