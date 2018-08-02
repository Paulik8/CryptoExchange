package deserializers;

import com.google.gson.*;
import models.PairBITFINEX;
import models.PairListBITFINEX;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class DeserializerBITFINEX implements JsonDeserializer<PairListBITFINEX>{

    public PairListBITFINEX deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        HashMap<String, PairBITFINEX> listPair = new HashMap<String, PairBITFINEX>();
        for (int i = 0; i < jsonArray.size(); ++i) {
            PairBITFINEX pairBITFINEX = jsonDeserializationContext.deserialize(jsonArray.get(i), PairBITFINEX.class);
            listPair.put(pairBITFINEX.getPair(), pairBITFINEX);
        }
        return new PairListBITFINEX(listPair);
    }
}
