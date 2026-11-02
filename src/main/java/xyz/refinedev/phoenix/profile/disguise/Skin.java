package xyz.refinedev.phoenix.profile.disguise;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.utils.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Skin implements ISkin {

    private static final String PROFILE_URL = "https://api.mojang.com/users/profiles/minecraft/%s";

    private static final String MOJANG_URL =
            "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

    public static final Skin DEFAULT_SKIN =
            new Skin(
                    "",
                    "eyJ0aW1lc3RhbXAiOjE0MTEyNjg3OTI3NjUsInByb2ZpbGVJZCI6IjNmYmVjN2RkMGE1ZjQwYmY5ZDExODg1YTU0NTA3MTEyIiwicHJvZmlsZU5hbWUiOiJsYXN0X3VzZXJuYW1lIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg0N2I1Mjc5OTg0NjUxNTRhZDZjMjM4YTFlM2MyZGQzZTMyOTY1MzUyZTNhNjRmMzZlMTZhOTQwNWFiOCJ9fX0=",
                    "u8sG8tlbmiekrfAdQjy4nXIcCfNdnUZzXSx9BE1X5K27NiUvE1dDNIeBBSPdZzQG1kHGijuokuHPdNi/KXHZkQM7OJ4aCu5JiUoOY28uz3wZhW4D+KG3dH4ei5ww2KwvjcqVL7LFKfr/ONU5Hvi7MIIty1eKpoGDYpWj3WjnbN4ye5Zo88I2ZEkP1wBw2eDDN4P3YEDYTumQndcbXFPuRRTntoGdZq3N5EBKfDZxlw4L3pgkcSLU5rWkd5UH4ZUOHAP/VaJ04mpFLsFXzzdU4xNZ5fthCwxwVBNLtHRWO26k/qcVBzvEXtKGFJmxfLGCzXScET/OjUBak/JEkkRG2m+kpmBMgFRNtjyZgQ1w08U6HHnLTiAiio3JswPlW5v56pGWRHQT5XWSkfnrXDalxtSmPnB5LmacpIImKgL8V9wLnWvBzI7SHjlyQbbgd+kUOkLlu7+717ySDEJwsFJekfuR6N/rpcYgNZYrxDwe4w57uDPlwNL6cJPfNUHV7WEbIU1pMgxsxaXe8WSvV87qLsR7H06xocl2C0JFfe2jZR4Zh3k9xzEnfCeFKBgGb4lrOWBu1eDWYgtKV67M2Y+B3W5pjuAjwAxn0waODtEn/3jKPbc/sxbPvljUCw65X+ok0UUN1eOwXV5l2EGzn05t3Yhwq19/GxARg63ISGE8CKw=");

    @SerializedName("name")
    private String name;

    @SerializedName("value")
    private String value;

    @SerializedName("signature")
    private String signature;

    public static Skin fetchSkinUUID(String text, UUID uUID) {
        try {
            HttpURLConnection httpURLConnection;
            (httpURLConnection =
                            (HttpURLConnection)
                                    new URL(
                                                    String.format(
                                                            "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false",
                                                            uUID.toString()))
                                            .openConnection())
                    .setRequestMethod("GET");
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String text2;
            while ((text2 = bufferedReader.readLine()) != null) {
                stringBuilder.append(text2);
            }
            JsonElement jsonElement;
            if (!(jsonElement = JsonParser.findJsonElement(stringBuilder.toString()))
                    .isJsonObject()) {
                return DEFAULT_SKIN;
            }
            JsonObject jsonObject;
            String asString =
                    (jsonObject =
                                    jsonElement
                                            .getAsJsonObject()
                                            .get("properties")
                                            .getAsJsonArray()
                                            .get(0)
                                            .getAsJsonObject())
                            .get("value")
                            .getAsString();
            String asString2 = jsonObject.get("signature").getAsString();
            return new Skin(text, asString, asString2);
        } catch (Exception exception) {
            return DEFAULT_SKIN;
        }
    }

    public static CompletableFuture<Skin> getSkinByName(String text) {
        return CompletableFuture.supplyAsync(() -> getSkinByNameSync(text));
    }

    public static Skin getSkinByNameSync(String text) {
        try {
            JsonElement jsonElement;
            if ((jsonElement =
                                    get(
                                            String.format(
                                                    "https://api.mojang.com/users/profiles/minecraft/%s",
                                                    text)))
                            != null
                    && jsonElement.isJsonObject()) {
                String asString = jsonElement.getAsJsonObject().get("id").getAsString();
                JsonElement jsonElement2;
                if ((jsonElement2 =
                                        get(
                                                String.format(
                                                        "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false",
                                                        asString)))
                                != null
                        && jsonElement2.isJsonObject()) {
                    Iterator<JsonElement> iterator2 =
                            jsonElement2.getAsJsonObject().getAsJsonArray("properties").iterator();
                    while (iterator2.hasNext()) {
                        JsonObject jsonObject;
                        if ((jsonObject = ((JsonElement) iterator2.next()).getAsJsonObject())
                                .get("name")
                                .getAsString()
                                .equals("textures")) {
                            return new Skin(
                                    text,
                                    jsonObject.get("value").getAsString(),
                                    jsonObject.get("signature").getAsString());
                        }
                    }
                    return null;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception exception) {
            return null;
        }
    }

    private static JsonElement get(String text) throws Exception {
        HttpURLConnection httpURLConnection;
        (httpURLConnection = (HttpURLConnection) new URL(text).openConnection())
                .setRequestMethod("GET");
        httpURLConnection.setConnectTimeout(5000);
        httpURLConnection.setReadTimeout(5000);
        if (httpURLConnection.getResponseCode() != 200) {
            return null;
        }
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        JsonElement jsonElement;
        try {
            StringBuilder stringBuilder = new StringBuilder();
            String text2;
            while ((text2 = bufferedReader.readLine()) != null) {
                stringBuilder.append(text2);
            }
            jsonElement = JsonParser.findJsonElement(stringBuilder.toString());
        } catch (Throwable throwable) {
            try {
                bufferedReader.close();
            } catch (Throwable throwable2) {
                throwable.addSuppressed(throwable2);
            }
            throw throwable;
        }
        bufferedReader.close();
        return jsonElement;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object result) {
        if (this == result) {
            return true;
        } else if (result != null && this.getClass() == result.getClass()) {
            Skin skin = (Skin) result;
            return this.name.equals(skin.name);
        } else {
            return false;
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String getSignature() {
        return this.signature;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Skin(String name, String value, String signature) {
        this.name = name;
        this.value = value;
        this.signature = signature;
    }
}
