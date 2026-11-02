package xyz.refinedev.phoenix.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class PhoenixExclusionStrategy implements ExclusionStrategy {

    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        return !fieldAttributes.hasModifier(8)
                || fieldAttributes.getAnnotation(Exclude.class) != null;
    }

    public boolean shouldSkipClass(Class<?> classValue) {
        return classValue.getAnnotation(Exclude.class) != null;
    }
}
