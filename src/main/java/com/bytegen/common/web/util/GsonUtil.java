package com.bytegen.common.web.util;

import com.bytegen.common.web.formatter.DeserializationExStrategy;
import com.bytegen.common.web.formatter.SerializationExStrategy;
import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 */
public class GsonUtil {
    private static final Gson gsonIns = new GsonBuilder()
            .disableHtmlEscaping()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .addSerializationExclusionStrategy(new SerializationExStrategy())
            .addDeserializationExclusionStrategy(new DeserializationExStrategy())
            .registerTypeAdapter(DateTime.class,
                    (JsonSerializer<DateTime>) (dateTime, typeOfSrc, context)
                            -> new JsonPrimitive(dateTime.getMillis() / 1000))
            .registerTypeAdapter(DateTime.class,
                    (JsonDeserializer<DateTime>) (jsonElement, type, jdc)
                            -> jsonElement.getAsLong() == 0 ? null : new DateTime(jsonElement.getAsLong() * 1000))
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context)
                            -> new JsonPrimitive(src.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000))
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonDeserializer<LocalDateTime>) (jsonElement, type, jdc)
                            -> jsonElement.getAsLong() == 0 ? null : LocalDateTime.ofInstant(Instant.ofEpochMilli(jsonElement.getAsLong() * 1000), ZoneId.systemDefault()))
            .create();

    public static Gson getGson() {
        return gsonIns;
    }

    //  set value start
    private static void putValueAt0(final JsonObject cursor, JsonElement value, String[] keys, int offset) {
        if (null == cursor) {
            throw new IllegalArgumentException("json object cursor is null");
        }
        if (null == keys || offset >= keys.length || null == value) {
            String s = String.format("value(%s) should not be null, and keys(%s) length should be greater than offset(%s).",
                    value, Arrays.toString(keys), offset);
            throw new IllegalArgumentException(s);
        }

        if (offset + 1 == keys.length) {
            cursor.add(keys[offset], value);
        } else {
            String key0 = keys[offset];
            JsonElement value0 = cursor.get(key0);
            if (null == value0) {
                JsonObject tmp = new JsonObject();
                cursor.add(key0, tmp);
                putValueAt0(tmp, value, keys, offset + 1);
            } else if (!value0.isJsonObject()) {
                throw new RuntimeException(String.format("the point %s in %s is not an object.",
                        Arrays.toString(keys), cursor.toString()));
            } else {
                JsonObject tmp = value0.getAsJsonObject();
                putValueAt0(tmp, value, keys, offset + 1);
            }
        }
    }

    public static JsonObject putValueAt(final JsonObject obj, String value, String... keys) {
        putValueAt0(obj, new JsonPrimitive(value), keys, 0);
        return obj;
    }

    public static JsonObject putValueAt(final JsonObject obj, int value, String... keys) {
        putValueAt0(obj, new JsonPrimitive(value), keys, 0);
        return obj;
    }

    public static JsonObject putValueAt(final JsonObject obj, boolean value, String... keys) {
        putValueAt0(obj, new JsonPrimitive(value), keys, 0);
        return obj;
    }

    public static JsonObject putValueAt(final JsonObject obj, char value, String... keys) {
        putValueAt0(obj, new JsonPrimitive(value), keys, 0);
        return obj;
    }

    public static JsonObject putValueAt(final JsonObject obj, JsonElement value, String... keys) {
        putValueAt0(obj, value, keys, 0);
        return obj;
    }
    //  set value end

    //  get value start
    private static Optional<JsonElement> valueAt0(final JsonObject cursor, String[] keys, int offset) {
        if (null == cursor) {
            throw new IllegalArgumentException("json object cursor is null");
        }
        if (null == keys || offset >= keys.length) {
            String s = String.format("keys(%s) length should be greater than offset(%s).",
                    Arrays.toString(keys), offset);
            throw new IllegalArgumentException(s);
        }

        if (offset + 1 == keys.length) {
            return Optional.ofNullable(cursor.get(keys[offset]));
        } else {
            String key0 = keys[offset];
            JsonElement value0 = cursor.get(key0);
            if (null == value0) {
                return Optional.empty();
            } else if (!value0.isJsonObject()) {
                return Optional.empty();
            } else {
                JsonObject tmp = value0.getAsJsonObject();
                return valueAt0(tmp, keys, offset + 1);
            }
        }
    }

    public static Optional<JsonElement> valueAt(final JsonObject obj, String... keys) {
        return valueAt0(obj, keys, 0);
    }

    public static String stringAt(final JsonObject obj, String... keys) {
        Optional<JsonElement> je = valueAt(obj, keys);
        if (!je.isPresent() || !je.get().isJsonPrimitive() || !je.get().getAsJsonPrimitive().isString()) {
            return null;
        }

        return je.get().getAsJsonPrimitive().getAsString();
    }

    public static Integer integerAt(final JsonObject obj, String... keys) {
        Optional<JsonElement> je = valueAt(obj, keys);
        if (!je.isPresent() || !je.get().isJsonPrimitive() || !je.get().getAsJsonPrimitive().isNumber()) {
            return null;
        }

        return je.get().getAsJsonPrimitive().getAsInt();
    }

    public static Long longAt(final JsonObject obj, String... keys) {
        Optional<JsonElement> je = valueAt(obj, keys);
        if (!je.isPresent() || !je.get().isJsonPrimitive() || !je.get().getAsJsonPrimitive().isNumber()) {
            return null;
        }

        return je.get().getAsJsonPrimitive().getAsLong();
    }

    public static Boolean boolAt(final JsonObject obj, String... keys) {
        Optional<JsonElement> je = valueAt(obj, keys);
        if (!je.isPresent() || !je.get().isJsonPrimitive() || !je.get().getAsJsonPrimitive().isBoolean()) {
            return null;
        }

        return je.get().getAsJsonPrimitive().getAsBoolean();
    }

    public static JsonObject objAt(final JsonObject obj, String... keys) {
        Optional<JsonElement> je = valueAt(obj, keys);
        if (!je.isPresent() || !je.get().isJsonObject()) {
            return null;
        }

        return je.get().getAsJsonObject();
    }
    //  get value end


    //  remove value start
    private static void removeAt0(JsonObject cursor, String[] keys, int offset) {
        if (null == cursor) {
            throw new IllegalArgumentException("json object cursor is null");
        }
        if (null == keys || offset >= keys.length) {
            String s = String.format("keys(%s) length should be greater than offset(%s).",
                    Arrays.toString(keys), offset);
            throw new IllegalArgumentException(s);
        }

        if (offset + 1 == keys.length) {
            cursor.remove(keys[offset]);
        } else {
            String key0 = keys[offset];
            JsonElement value0 = cursor.get(key0);
            if (null == value0) {
                // omit
            } else if (!value0.isJsonObject()) {
                // omit
            } else {
                JsonObject tmp = value0.getAsJsonObject();
                removeAt0(tmp, keys, offset + 1);
            }
        }
    }

    public static JsonObject removeAt(final JsonObject obj, String... keys) {
        removeAt0(obj, keys, 0);
        return obj;
    }
    //  remove value end

    //  deep copy start
    public static JsonObject deepCopy(final JsonObject obj) {
        if (null == obj) {
            return null;
        } else {
            return getGson().fromJson(obj.toString(), JsonObject.class);
        }
    }
    //  deep copy end

    // merge start
    public JsonObject mergeTo(final JsonObject from, JsonObject to) {
        if (null == from) {
            return to;
        }
        if (null == to) {
            return deepCopy(from);
        }
        for (Map.Entry<String, JsonElement> entry : from.entrySet()) {
            to.add(entry.getKey(), entry.getValue());
        }
        return to;
    }
    // merge end


    private static boolean isEqual(String s1, String s2) {
        if (null == s1 && null == s2) {
            return true;
        }
        if (null == s1 || null == s2) {
            return false;
        }

        return s1.equals(s2);
    }

    private static boolean isEqualIgnoreCase(String s1, String s2) {
        if (null == s1 && null == s2) {
            return true;
        }
        if (null == s1 || null == s2) {
            return false;
        }

        return s1.equalsIgnoreCase(s2);
    }

    public static <T> List<T> readAsList(String serializedObj, Class<T[]> clazz) {
        if (StringUtils.isBlank(serializedObj)) {
            return new ArrayList<>();
        }

        return Arrays.asList(gsonIns.fromJson(serializedObj, clazz));
    }
}
