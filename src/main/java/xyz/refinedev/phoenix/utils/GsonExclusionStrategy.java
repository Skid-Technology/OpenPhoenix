package xyz.refinedev.phoenix.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class GsonExclusionStrategy implements ExclusionStrategy {

    public static boolean skips(boolean matches, boolean matches2, boolean matches3) {
        return matches || matches2 || matches3;
    }

    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        return skips(
                fieldAttributes.hasModifier(128),
                fieldAttributes.hasModifier(8),
                fieldAttributes.getAnnotation(Exclude.class) != null);
    }

    public boolean shouldSkipClass(Class<?> classValue) {
        return classValue.getAnnotation(Exclude.class) != null;
    }
}
