package xyz.refinedev.phoenix.utils.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import xyz.refinedev.phoenix.profile.tag.ICustomTag;

import java.io.IOException;

public class EnumTypeAdapterFactory<I, T extends I> implements TypeAdapterFactory {

    private final Class<I> type;

    private final Class<T> typeClass;

    private EnumTypeAdapterFactory(Class<I> classValue, Class<T> classValue2) {
        this.type = classValue;
        this.typeClass = classValue2;
    }

    public static <I, T extends I> EnumTypeAdapterFactory<I, T> of(
            Class<I> classValue, Class<T> classValue2) {
        return new EnumTypeAdapterFactory<>(classValue, classValue2);
    }

    public <U> TypeAdapter<U> create(Gson result, TypeToken<U> typeToken) {
        if (!this.type.isAssignableFrom(typeToken.getRawType())) {
            return null;
        }
        if (ICustomTag.class.isAssignableFrom(typeToken.getRawType())
                && !this.type.equals(ICustomTag.class)) {
            return null;
        }
        final TypeAdapter delegateAdapter =
                result.getDelegateAdapter(this, TypeToken.get(this.typeClass));
        return (TypeAdapter<U>)
                (new TypeAdapter<I>() {

                    public void write(JsonWriter result, I i) throws IOException {
                        delegateAdapter.write(
                                result, EnumTypeAdapterFactory.this.typeClass.cast(i));
                    }

                    public I read(JsonReader result) throws IOException {
                        return (I) delegateAdapter.read(result);
                    }
                });
    }
}
