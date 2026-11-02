package xyz.refinedev.phoenix.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class FeatureFlagClient {

    private final String text;

    private final String textString;

    private final Gson gson = new GsonBuilder().serializeNulls().create();

    private JsonObject jsonObject;

    public FeatureFlagClient(String text, String textString) {
        this.text = text;
        this.textString = textString;
    }

    private String formatString(String text2, String text3) throws Exception {
        HttpURLConnection httpURLConnection;
        (httpURLConnection = (HttpURLConnection) new URL(this.text + text2).openConnection())
                .setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Authorization", this.textString);
        httpURLConnection.setDoOutput(true);
        OutputStream outputStream = httpURLConnection.getOutputStream();
        try {
            outputStream.write(text3.getBytes(StandardCharsets.UTF_8));
        } catch (Throwable throwable) {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
            }
            throw throwable;
        }
        if (outputStream != null) {
            outputStream.close();
        }
        int value;
        InputStream inputStream =
                (value = httpURLConnection.getResponseCode()) >= 200 && value < 300
                        ? httpURLConnection.getInputStream()
                        : httpURLConnection.getErrorStream();
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();
        String text4;
        while ((text4 = bufferedReader.readLine()) != null) {
            stringBuilder.append(text4);
        }
        bufferedReader.close();
        httpURLConnection.disconnect();
        return stringBuilder.toString();
    }

    public void setJsonObject(JsonObject jsonObject2) throws Exception {
        String text;
        JsonObject jsonObject3;
        if ((jsonObject3 =
                                JsonParser.findJsonElement(
                                                text =
                                                        this.formatString(
                                                                "/api/public/sdk/client/latest-all",
                                                                this.gson.toJson(jsonObject2)))
                                        .getAsJsonObject())
                        .has("messageType")
                && "data-sync".equals(jsonObject3.get("messageType").getAsString())) {
            this.jsonObject = jsonObject3.getAsJsonObject("data");
        } else {
            throw new RuntimeException("Unexpected response: " + text);
        }
    }

    public JsonElement findJsonElement(String text) {
        if (this.jsonObject != null && this.jsonObject.has("featureFlags")) {
            Iterator<JsonElement> iterator2 =
                    this.jsonObject.getAsJsonArray("featureFlags").iterator();
            while (iterator2.hasNext()) {
                JsonObject asJsonObject = ((JsonElement) iterator2.next()).getAsJsonObject();
                if (text.equals(asJsonObject.get("id").getAsString())) {
                    return asJsonObject.get("variation");
                }
            }
            return null;
        } else {
            return null;
        }
    }

    public boolean matchesEnabled(String text, boolean matches) {
        JsonElement jsonElement;
        return (jsonElement = this.findJsonElement(text)) != null && jsonElement.isJsonPrimitive()
                ? jsonElement.getAsBoolean()
                : matches;
    }

    public String formatStringByString(String text, String text2) {
        JsonElement jsonElement;
        return (jsonElement = this.findJsonElement(text)) != null && jsonElement.isJsonPrimitive()
                ? jsonElement.getAsString()
                : text2;
    }

    public double calculateValue(String text, double value) {
        JsonElement jsonElement;
        return (jsonElement = this.findJsonElement(text)) != null && jsonElement.isJsonPrimitive()
                ? jsonElement.getAsDouble()
                : value;
    }

    public void applyString(String text, JsonObject jsonObject, Object result) throws Exception {
        JsonObject jsonObject2;
        (jsonObject2 = new JsonObject()).addProperty("eventName", text);
        jsonObject2.addProperty("timestamp", System.currentTimeMillis());
        jsonObject2.add("user", jsonObject);
        JsonObject jsonObject3 = new JsonObject();
        if (result instanceof Integer) {
            jsonObject3.addProperty("value", (Integer) result);
        } else if (result instanceof Double) {
            jsonObject3.addProperty("value", (Double) result);
        } else {
            jsonObject3.addProperty("value", result.toString());
        }
        jsonObject2.add("data", jsonObject3);
        this.formatString("/api/public/sdk/client/metric", this.gson.toJson(jsonObject2));
    }

    public static class User {

        private final JsonObject jsonObject = new JsonObject();

        public User(String text, String text2) {
            this.jsonObject.addProperty("keyId", text);
            this.jsonObject.addProperty("name", text2);
        }

        public FeatureFlagClient.User findUser(String text, String text2) {
            JsonArray jsonArray;
            if (this.jsonObject.has("customizedProperties")) {
                jsonArray = this.jsonObject.getAsJsonArray("customizedProperties");
            } else {
                jsonArray = new JsonArray();
                this.jsonObject.add("customizedProperties", jsonArray);
            }
            JsonObject jsonObject2;
            (jsonObject2 = new JsonObject()).addProperty("name", text);
            jsonObject2.addProperty("value", text2);
            jsonArray.add(jsonObject2);
            return this;
        }

        public FeatureFlagClient.User findUser(Map<String, String> entries) {
            for (Entry<String, String> entry : entries.entrySet()) {
                this.findUser((String) entry.getKey(), (String) entry.getValue());
            }
            return this;
        }

        public JsonObject getJsonObject() {
            return this.jsonObject;
        }
    }
}
