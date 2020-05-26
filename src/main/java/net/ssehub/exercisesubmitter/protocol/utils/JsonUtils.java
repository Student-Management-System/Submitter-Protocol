package net.ssehub.exercisesubmitter.protocol.utils;

import com.google.gson.Gson;

import io.gsonfire.GsonFireBuilder;
import net.ssehub.studentmgmt.backend_api.JSON;

/**
 * Provides a JSON-based (de-)serializer.
 * @author El-Sharkawy
 *
 */
public class JsonUtils {
    
    /**
     * Avoids initialization.
     */
    private JsonUtils() {}
    
    /**
     * Creates a JSON-based (de-)serializer that uses pretty printing (attributes in new lines and indentation).
     * Will serialize all attributes, except for:
     * <ul>
     *   <li><tt>static</tt></li>
     *   <li><tt>transient</tt></li>
     * </ul>
     * See for more details: <a href="https://sites.google.com/site/gson/gson-user-guide
     *#TOC-Excluding-Fields-From-Serialization-and-Deserialization">
     * https://sites.google.com/site/gson/gson-user-guide
     *#TOC-Excluding-Fields-From-Serialization-and-Deserialization</a>.
     * 
     * @return Googles JSON-based (de-)serializer.
     */
    public static JSON createParser() {
        JSON jsonParser = new JSON();
        Gson gson = new GsonFireBuilder().createGsonBuilder()
                .setPrettyPrinting()
                .create();
        jsonParser.setGson(gson);
        
        return jsonParser;
    }

}
