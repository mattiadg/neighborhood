package fr.upem.android.usersprovider;

import android.util.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mattia on 22/11/15.
 */
public class JsonProfileParser {


    /**
     * Parse a Json document containing a profile and returns it like a List of Pairs
     * @param jsonProfile
     * @return
     */
    public Map<String, String> readProfile(String jsonProfile) throws IOException {
        JsonReader reader = new JsonReader(new StringReader(jsonProfile));
        try{
            return readPairs(reader);
        } finally {
            reader.close();
        }
    }

    private Map<String, String> readPairs(JsonReader reader) throws IOException {
        Map<String, String> map = new HashMap<>();
        reader.beginObject();
        while (reader.hasNext()){
            String key = reader.nextName();
            String value = reader.nextString();
            map.put(key, value);
        }
        reader.endObject();
        return map;
    }
}
