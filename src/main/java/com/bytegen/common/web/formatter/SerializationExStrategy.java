package com.bytegen.common.web.formatter;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.annotations.Expose;

/**
 * Serialization exclusion rule for {@link Expose}
 * to make sure field can be skipped when deserialize of
 * {@link Expose} is false
 */
public class SerializationExStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        final Expose expose = fieldAttributes.getAnnotation(Expose.class);
        return expose != null && !expose.serialize();
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }

}
