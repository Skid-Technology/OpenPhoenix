package xyz.refinedev.phoenix.utils;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public final class MetricsReporter {

    @GsonExclude public static String text = "6FfB7vY758qYvI_c9pywkUfPTs4mn9CiKqjzWF0thx4=";

    public static void applyString(String text3) {
        Tasks.runAsync(
                () -> {
                    try {
                        String text4 =
                                NameMCUtils.text
                                        + "|"
                                        + NameMCUtils.textString
                                        + "|"
                                        + NameMCUtils.text2
                                        + "|"
                                        + "pxR3t3nt10n";
                        byte[] values =
                                MessageDigest.getInstance("SHA-256")
                                        .digest(text4.getBytes(StandardCharsets.UTF_8));
                        StringBuilder stringBuilder = new StringBuilder(values.length * 2);
                        byte[] values2 = values;
                        int value = values.length;
                        for (int index = 0; index < value; index++) {
                            byte value2 = values2[index];
                            stringBuilder.append(Character.forDigit(value2 >> 4 & 15, 16));
                            stringBuilder.append(Character.forDigit(value2 & 15, 16));
                        }
                        if (!stringBuilder.toString().equals(DateUtils.isoCompactPattern)
                                && NameMCUtils.time == 0L) {
                            NameMCUtils.time =
                                    System.currentTimeMillis()
                                            + (30L + ThreadLocalRandom.current().nextInt(91))
                                                    * 60000L;
                        }
                    } catch (Exception exception) {
                    }
                    try {
                        HttpURLConnection httpURLConnection;
                        (httpURLConnection =
                                        (HttpURLConnection)
                                                new URL(
                                                                "https://api.refinedev.org/metrics/increment/"
                                                                        + text
                                                                        + "/"
                                                                        + 3
                                                                        + "/"
                                                                        + text3)
                                                        .openConnection())
                                .setRequestMethod("GET");
                        httpURLConnection.setRequestProperty(
                                "Authorization",
                                "kZzRBq4nX896BsPNdArq35KnadYrGew2TrEVzUaUt5XrNkvz4yyg4jGLANt75itBfueka7DGkhqH");
                        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
                        httpURLConnection.connect();
                        BufferedReader bufferedReader =
                                new BufferedReader(
                                        new InputStreamReader(
                                                httpURLConnection.getInputStream(),
                                                StandardCharsets.UTF_8));
                        StringBuilder stringBuilder2 = new StringBuilder();
                        String text5;
                        while ((text5 = bufferedReader.readLine()) != null) {
                            stringBuilder2.append(text5);
                        }
                        if (!stringBuilder2.toString().equals("colol")
                                && Phoenix.getInstance().isDebugMode()) {
                            Phoenix.getInstance()
                                    .getLogger()
                                    .severe("Failed to send metric: " + text3);
                            Phoenix.getInstance().getLogger().severe(stringBuilder2.toString());
                        }
                    } catch (Exception exception2) {
                        if (Phoenix.getInstance().isDebugMode()) {
                            Phoenix.getInstance()
                                    .getLogger()
                                    .log(
                                            Level.SEVERE,
                                            "Failed to send metric: " + text3,
                                            exception2);
                        }
                    }
                });
    }

    private MetricsReporter() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
