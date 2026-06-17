package com.yungnickyoung.minecraft.bettercaves.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.resources.Identifier;

import java.io.IOException;

public class IdentifierAdapter extends TypeAdapter<Identifier> {
    public IdentifierAdapter() {
    }

    public Identifier read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        } else {
            return Identifier.parse(reader.nextString());
        }
    }

    public void write(JsonWriter writer, Identifier resourceLocation) throws IOException {
        if (resourceLocation == null) {
            writer.nullValue();
        } else {
            writer.value(resourceLocation.toString());
        }
    }
}
