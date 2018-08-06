package deserializers;

import com.google.gson.*;
import models.PairEXMO;
import models.PairListEXMO;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class DeserializerEXMO implements JsonDeserializer<PairListEXMO> {

    public PairListEXMO deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        HashMap<String, PairEXMO> pairList = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            PairEXMO pair = jsonDeserializationContext.deserialize(entry.getValue(), PairEXMO.class);
            pairList.put(changeToStandard(entry.getKey()), pair);
        }
        return new PairListEXMO(pairList);
    }

    private String changeToStandard(String value) {
        return value.replace("_", "");
    }
}