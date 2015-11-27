package fr.upem.mdigangi.dreseau.users;

import android.util.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mattia on 22/11/15.
 */
public class JsonProfileParser {

    public static class Pair{
        String key;
        String value;

        Pair(String key, String value){
            this.key = key;
            this.value = value;
        }
    }

    /**
     * Parse a Json document containing a profile and returns it like a List of Pairs
     * @param jsonProfile
     * @return
     */
    public List<Pair> readProfile(String jsonProfile) throws IOException {
        JsonReader reader = new JsonReader(new StringReader(jsonProfile));
        try{
            return readPairs(reader);
        } finally {
            reader.close();
        }
    }

    private List<Pair> readPairs(JsonReader reader) throws IOException {
        List<Pair> pairs = new ArrayList<>();
        reader.beginObject();
        while (reader.hasNext()){
            String key = reader.nextName();
            String value = reader.nextString();
            pairs.add(new Pair(key, value));
        }
        reader.endObject();
        return pairs;
    }
}
