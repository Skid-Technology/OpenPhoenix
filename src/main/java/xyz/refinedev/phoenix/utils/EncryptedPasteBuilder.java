package xyz.refinedev.phoenix.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import xyz.refinedev.phoenix.Phoenix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Level;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptedPasteBuilder {

    private final String text;

    private int count = 0;

    private String textString = "";

    private String text2 = "";

    private String text3 = "";

    private JsonObject jsonObject = null;

    public EncryptedPasteBuilder() {
        this.text = "https://bin.refinedev.org";
    }

    public EncryptedPasteBuilder(String text) {
        this.text = text;
    }

    public EncryptedPasteBuilder findEncryptedPasteBuilder(String text2) {
        this.text2 = text2;
        return this;
    }

    public EncryptedPasteBuilder findEncryptedPasteBuilder(boolean matches) {
        this.count = matches ? 1 : 0;
        return this;
    }

    public EncryptedPasteBuilder findEncryptedPasteBuilderByTextString(String textString) {
        this.textString = textString;
        return this;
    }

    public EncryptedPasteBuilder findEncryptedPasteBuilderByEncryptedPasteBuilder(String text) {
        Path path = Paths.get(text);
        try {
            byte[] values = Files.readAllBytes(path);
            this.textString = new String(values);
        } catch (IOException oException) {
            Phoenix.getInstance()
                    .getLogger()
                    .log(
                            Level.SEVERE,
                            "Failed to read paste message from file: " + text,
                            oException);
        }
        return this;
    }

    public EncryptedPasteBuilder replaceAll() {
        this.textString =
                this.textString.replaceAll(
                        "(?i)\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b", "IP CENSORED");
        return this;
    }

    public EncryptedPasteBuilder getEncryptedPasteBuilder() {
        try {
            this.jsonObject = new JsonObject();
            KeyGenerator keyGenerator;
            (keyGenerator = KeyGenerator.getInstance("AES")).init(192);
            byte[] encoded = keyGenerator.generateKey().getEncoded();
            String text = Base64.getEncoder().encodeToString(encoded);
            this.text3 = text;
            String text4 = text + this.text2;
            byte[] values = new byte[16];
            SecureRandom secureRandom;
            (secureRandom = SecureRandom.getInstanceStrong()).nextBytes(values);
            String text5 = Base64.getEncoder().encodeToString(values);
            byte[] values2 = new byte[8];
            secureRandom.nextBytes(values2);
            String text6 = Base64.getEncoder().encodeToString(values2);
            JsonObject jsonObject2;
            (jsonObject2 = new JsonObject()).addProperty("paste", this.textString);
            byte[] bytes = jsonObject2.toString().getBytes();
            SecretKeyFactory instance = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            PBEKeySpec pBEKeySpec = new PBEKeySpec(text4.toCharArray(), values2, 100000, 256);
            SecretKeySpec secretKeySpec =
                    new SecretKeySpec(instance.generateSecret(pBEKeySpec).getEncoded(), "AES");
            JsonArray jsonArray = new JsonArray();
            JsonArray jsonArray2;
            (jsonArray2 = new JsonArray()).add(new JsonPrimitive(text5));
            jsonArray2.add(new JsonPrimitive(text6));
            jsonArray2.add(new JsonPrimitive(100000));
            jsonArray2.add(new JsonPrimitive(256));
            jsonArray2.add(new JsonPrimitive(128));
            jsonArray2.add(new JsonPrimitive("aes"));
            jsonArray2.add(new JsonPrimitive("gcm"));
            jsonArray2.add(new JsonPrimitive("none"));
            jsonArray.add(jsonArray2);
            jsonArray.add(new JsonPrimitive("plaintext"));
            jsonArray.add(new JsonPrimitive(0));
            jsonArray.add(new JsonPrimitive(this.count));
            this.jsonObject.add("adata", jsonArray);
            byte[] bytes2 = jsonArray.toString().getBytes();
            Cipher instance2 = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gCMParameterSpec = new GCMParameterSpec(128, values);
            instance2.init(1, secretKeySpec, gCMParameterSpec);
            instance2.updateAAD(bytes2);
            byte[] values3 = instance2.doFinal(bytes);
            String text7 = Base64.getEncoder().encodeToString(values3);
            this.jsonObject.addProperty("v", 2);
            this.jsonObject.addProperty("ct", text7);
            JsonObject jsonObject3;
            (jsonObject3 = new JsonObject()).addProperty("expire", "1week");
            this.jsonObject.add("meta", jsonObject3);
            return this;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public String getString() throws IOException {
        if (this.jsonObject != null && !this.text3.isEmpty()) {
            byte[] bytes = this.jsonObject.toString().getBytes();
            URLConnection uRLConnection;
            (uRLConnection = new URL(this.text).openConnection()).setDoOutput(true);
            uRLConnection.setRequestProperty("Content-Type", "application/json");
            uRLConnection.setRequestProperty("X-Requested-With", "JSONHttpRequest");
            uRLConnection.setRequestProperty("Content-Length", Integer.toString(bytes.length));
            try {
                OutputStream outputStream = uRLConnection.getOutputStream();
                try {
                    outputStream.write(bytes);
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
            } catch (IOException oException) {
                String text2 =
                        String.format(
                                "Failure in generating the output stream and sending the payload: %s  %s ",
                                oException.getMessage(),
                                oException.getCause() == null
                                        ? ""
                                        : oException.getCause().getMessage());
                throw new IOException(text2);
            }
            StringBuilder stringBuilder = new StringBuilder();
            try {
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(uRLConnection.getInputStream()));
                String text4;
                try {
                    while ((text4 = bufferedReader.readLine()) != null) {
                        stringBuilder.append(text4);
                    }
                } catch (Throwable throwable3) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable throwable4) {
                        throwable3.addSuppressed(throwable4);
                    }
                    throw throwable3;
                }
                bufferedReader.close();
            } catch (IOException oException2) {
                String text5 =
                        String.format(
                                "Failure in reading the response: %s  %s ",
                                oException2.getMessage(),
                                oException2.getCause() == null
                                        ? ""
                                        : oException2.getCause().getMessage());
                throw new RuntimeException(text5);
            }
            String text6;
            if (!(text6 = stringBuilder.toString()).isEmpty()) {
                JsonObject jsonObject2;
                if (!(jsonObject2 = JsonParser.findJsonElement(text6).getAsJsonObject())
                        .has("url")) {
                    throw new RuntimeException("Failed to send paste: " + jsonObject2);
                } else {
                    String text7;
                    if ((text7 = jsonObject2.get("url").getAsString()) == null) {
                        throw new RuntimeException(
                                "Failed to get paste ID from response: " + text6);
                    } else {
                        return this.text + text7 + "#" + Base58.encode(this.text3.getBytes());
                    }
                }
            } else {
                throw new RuntimeException("Failed to send paste: No response received.");
            }
        } else {
            String text8 =
                    "Invalid payload: "
                            + (this.jsonObject == null ? "Paste is empty; nothing to send. " : "")
                            + (this.text3.isEmpty()
                                    ? "You must encrypt the paste before sending it."
                                    : "");
            throw new RuntimeException(text8);
        }
    }
}
