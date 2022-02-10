package com.snowflake.redteam.uarlos;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * MultiStringAdaptor provides support for IAM policy documents which allow
 * a given field to either be a single entity, or a list of entities.
 */
class MultiStringAdaptor extends TypeAdapter<String[]> {

  @Override
  public String[] read(JsonReader reader) throws IOException {
    List<String> result = new ArrayList<>();
    if (reader.peek() == JsonToken.STRING) {
      result.add(reader.nextString());
    } else if (reader.peek() == JsonToken.NULL) {
      // we have seen some interestingly broken policy documents...
      reader.nextNull();
    } else {
      reader.beginArray();
      while (reader.peek() != JsonToken.END_ARRAY) {
        result.add(reader.nextString());
      }
      reader.endArray();
    }
    return result.toArray(new String[result.size()]);
  }

  @Override
  public void write(JsonWriter writer, String[] values) throws IOException {
    writer.beginArray();
    for (String value : values) {
      writer.value(value);
    }
    writer.endArray();
  }
}