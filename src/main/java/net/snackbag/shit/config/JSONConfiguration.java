package net.snackbag.shit.config;

import com.google.gson.*;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class JSONConfiguration implements Configuration {
    private final Path path;

    private JsonObject json;
    private final Gson gson;

    public JSONConfiguration(String path) {
        this.path = Path.of(path);
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        // load file into configuration
        reload();
    }

    public void save() {
        try {
            File file = new File(getAbsolutePath());
            file.getParentFile().mkdirs();
            file.createNewFile();

            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(json));
            writer.close();

            reload();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save JSONConfiguration '" + getFileName() + "'");
        }
    }

    public void reload() {
        try {
            File file = new File(getAbsolutePath());
            file.getParentFile().mkdirs();

            // if the file didn't exist before
            if (file.createNewFile()) {
                FileWriter writer = new FileWriter(file);
                writer.write("{}");
                writer.close();
            }

            BufferedReader reader = new BufferedReader(new FileReader(getAbsolutePath() + getFileName()));
            json = gson.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to reload JSONConfiguration '" + getFileName() + "'");
        }
    }

    public boolean has(String key) {
        // Create a deep copy of the json object
        JsonElement element = gson.fromJson(json.toString(), JsonElement.class);

        for (String part : splitKey(key)) {
            if (element == null || !element.getAsJsonObject().has(part)) {
                return false;
            }
            element = element.getAsJsonObject().get(part);
        }
        return element != null;
    }

    private @Nullable Object[] preparePut(String key, @Nullable Object value) {
        String[] parts = splitKey(key);
        JsonObject current = json;

        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];

            if (!current.has(part) || !current.get(part).isJsonObject()) {
                current.add(part, new JsonObject());
            }

            current = current.getAsJsonObject(part);
        }

        if (value == null) {
            current.remove(parts[parts.length - 1]);
            return null;
        }

        return new Object[]{current, parts[parts.length - 1]};
    }

    public void put(String key, @Nullable String value) {
        Object[] values = preparePut(key, value);
        if (values == null) return;

        JsonObject current = (JsonObject) values[0];
        current.addProperty((String) values[1], value);
    }

    public void put(String key, @Nullable Object[] value) {
        Object[] values = preparePut(key, value);
        if (values == null) return;

        JsonObject current = (JsonObject) values[0];

        JsonArray array = new JsonArray();
        for (Object object : value) {
            if (object instanceof String) {
                array.add((String) object);
            } else if (object instanceof Boolean) {
                array.add((Boolean) object);
            } else if (object instanceof Number) {
                array.add((Number) object);
            } else {
                throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
            }
        }

        current.add((String) values[1], array);
    }

    public void put(String key, @Nullable Boolean value) {
        Object[] values = preparePut(key, value);
        if (values == null) return;

        JsonObject current = (JsonObject) values[0];
        current.addProperty((String) values[1], value);
    }

    public String getAsString(String key) {
        JsonElement element = json;

        for (String part : splitKey(key)) {
            if (element == null || !element.getAsJsonObject().has(part)) {
                return null;
            }
            element = element.getAsJsonObject().get(part);
        }

        return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()
                ? element.getAsString()
                : null;
    }

    public Boolean getAsBoolean(String key) {
        JsonElement element = json;

        for (String part : splitKey(key)) {
            if (element == null || !element.getAsJsonObject().has(part)) {
                return null;
            }
            element = element.getAsJsonObject().get(part);
        }

        return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()
                ? element.getAsBoolean()
                : null;
    }

    public JsonArray getAsArray(String key) {
        JsonElement element = json;

        for (String part : splitKey(key)) {
            if (element == null || !element.getAsJsonObject().has(part)) {
                return null;
            }
            element = element.getAsJsonObject().get(part);
        }

        return element != null && element.isJsonArray()
                ? element.getAsJsonArray()
                : null;
    }

    public String getFileName() {
        return path.getFileName().toString();
    }

    public String[] keys(String key) {
        // If the key is empty, return all string keys in the root JSON object
        if (key.isEmpty()) {
            Set<String> keys = new HashSet<>();
            collectStringKeys(json, "", keys);
            return keys.toArray(new String[0]);
        }

        // Traverse the JSON structure to the specified key
        JsonElement element = json;
        for (String part : splitKey(key)) {
            if (element == null || !element.getAsJsonObject().has(part)) {
                return new String[0];
            }
            element = element.getAsJsonObject().get(part);
        }

        // If the element is a JSON object, collect its string keys
        if (element != null && element.isJsonObject()) {
            Set<String> keys = new HashSet<>();
            collectStringKeys(element.getAsJsonObject(), "", keys);
            return keys.toArray(new String[0]);
        } else {
            return new String[0];
        }
    }

    public String[] keys() {
        return keys("");
    }

    public String[] splitKey(String key) {
        return key.split("\\.");
    }

    public String getAbsolutePath() {
        return path.toString();
    }

    private void collectStringKeys(JsonObject jsonObject, String prefix, Set<String> keys) {
        for (String key : jsonObject.keySet()) {
            JsonElement element = jsonObject.get(key);
            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;

            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                keys.add(fullKey);
            } else if (element.isJsonObject()) {
                collectStringKeys(element.getAsJsonObject(), fullKey, keys);
            }
        }
    }
}