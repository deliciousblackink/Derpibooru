package derpibooru.derpy.data;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * @author http://stackoverflow.com/a/9550086/1726690
 */
public class GsonAbstractClassAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
    public JsonElement serialize(T object, Type interfaceType, JsonSerializationContext context) {
        JsonObject wrapper = new JsonObject();
        wrapper.addProperty("type", object.getClass().getName());
        wrapper.add("data", new Gson().toJsonTree(object)); /* create a new Gson object to prevent stack overflow (see http://stackoverflow.com/a/28362433/1726690) */
        return wrapper;
    }

    public T deserialize(JsonElement element, Type interfaceType, JsonDeserializationContext context) throws JsonParseException {
        JsonObject wrapper = (JsonObject) element;
        JsonElement typeName = wrapper.get("type");
        JsonElement data = wrapper.get("data");
        Type actualType = typeForName(typeName);
        return new Gson().fromJson(data, actualType); /* prevents stack overflow (see above) */
    }

    private Type typeForName(final JsonElement typeElement) {
        try {
            return Class.forName(typeElement.getAsString());
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }
}
