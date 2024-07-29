package net.snackbag.shit.config;

import com.google.gson.JsonArray;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface Configuration {
    void save();

    void reload();

    boolean has(String key);

    void put(String key, @Nullable String value);

    void put(String key, @Nullable Object[] value);

    void put(String key, @Nullable Boolean value);

    default void putIfEmpty(String key, @NotNull String value) {
        Objects.requireNonNull(value);

        if (!has(key)) {
            put(key, value);
        }
    }

    default void putIfEmpty(String key, @NotNull Object[] value) {
        Objects.requireNonNull(value);

        if (!has(key)) {
            put(key, value);
        }
    }

    default void putIfEmpty(String key, @NotNull Boolean value) {
        Objects.requireNonNull(value);

        if (!has(key)) {
            put(key, value);
        }
    }

    String getAsString(String key);

    Boolean getAsBoolean(String key);

    default String getAsStringOrDefault(String key, String def) {
        if (!has(key)) {
            return def;
        }

        return getAsString(key);
    }

    default Boolean getAsBooleanOrDefault(String key, Boolean def) {
        if (!has(key)) {
            return def;
        }

        return getAsBoolean(key);
    }

    String[] keys(String key);

    default String[] keys() {
        return keys("");
    }

    default String[] splitKey(String key) {
        return key.split("\\.");
    }

    String getFileName();

    String getAbsolutePath();
}
