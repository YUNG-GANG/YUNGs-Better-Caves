package com.yungnickyoung.minecraft.bettercaves.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class ResourceLocationAdapter extends TypeAdapter<ResourceLocation> {
    public ResourceLocationAdapter() {
    }

    public ResourceLocation read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        } else {
            return ResourceLocation.parse(reader.nextString());
        }
    }

    public void write(JsonWriter writer, ResourceLocation resourceLocation) throws IOException {
        if (resourceLocation == null) {
            writer.nullValue();
        } else {
            writer.value(resourceLocation.toString());
        }
    }
}
