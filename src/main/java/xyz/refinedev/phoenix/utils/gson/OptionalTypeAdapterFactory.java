package xyz.refinedev.phoenix.utils.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import xyz.refinedev.phoenix.utils.Processable;

import java.io.IOException;

public class OptionalTypeAdapterFactory implements TypeAdapterFactory {

    public <T> TypeAdapter<T> create(Gson result, TypeToken<T> typeToken) {
        final TypeAdapter<T> delegateAdapter = result.getDelegateAdapter(this, typeToken);
        return new TypeAdapter<T>() {

            public void write(JsonWriter result, T t) throws IOException {
                delegateAdapter.write(result, t);
            }

            public T read(JsonReader result) throws IOException {
                T result2;
                if ((result2 = delegateAdapter.read(result)) instanceof Processable) {
                    ((Processable) result2).postProcess();
                }
                return (T) result2;
            }
        };
    }
}
