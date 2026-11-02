package xyz.refinedev.phoenix.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public final class JsonParser {

    public static JsonElement findJsonElement(String text) throws JsonSyntaxException {
        return findJsonElement(new StringReader(text));
    }

    public static JsonElement findJsonElement(Reader reader)
            throws JsonIOException, JsonSyntaxException {
        try {
            JsonReader jsonReader;
            JsonElement jsonElement;
            if (!(jsonElement = findJsonElement(jsonReader = new JsonReader(reader))).isJsonNull()
                    && jsonReader.peek() != JsonToken.END_DOCUMENT) {
                throw new JsonSyntaxException("Did not consume the entire document.");
            } else {
                return jsonElement;
            }
        } catch (MalformedJsonException malformedJsonException) {
            throw new JsonSyntaxException(malformedJsonException);
        } catch (IOException oException) {
            throw new JsonIOException(oException);
        } catch (NumberFormatException numberFormatException) {
            throw new JsonSyntaxException(numberFormatException);
        }
    }

    public static JsonElement findJsonElement(JsonReader jsonReader)
            throws JsonIOException, JsonSyntaxException {
        boolean lenient = jsonReader.isLenient();
        jsonReader.setLenient(true);
        JsonElement jsonElement;
        try {
            jsonElement = Streams.parse(jsonReader);
        } catch (StackOverflowError stackOverflowError) {
            throw new JsonParseException(
                    "Failed parsing JSON source: " + jsonReader + " to Json", stackOverflowError);
        } catch (OutOfMemoryError outOfMemoryError) {
            throw new JsonParseException(
                    "Failed parsing JSON source: " + jsonReader + " to Json", outOfMemoryError);
        } finally {
            jsonReader.setLenient(lenient);
        }
        return jsonElement;
    }

    private JsonParser() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
