// src/main/java/chess/ChessPositionAdapter.java
package chess;

import com.google.gson.*;
import java.lang.reflect.Type;

public class ChessPositionAdapter implements JsonSerializer<ChessPosition>,
        JsonDeserializer<ChessPosition> {
    @Override
    public JsonElement serialize(ChessPosition src, Type typeOfSrc, JsonSerializationContext ctx) {
        JsonObject o = new JsonObject();
        o.addProperty("row", src.getRow());
        o.addProperty("col", src.getColumn());
        return o;
    }

    @Override
    public ChessPosition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx)
            throws JsonParseException {
        JsonObject o = json.getAsJsonObject();
        int row = o.get("row").getAsInt();
        int col = o.get("col").getAsInt();
        return new ChessPosition(row, col);
    }
}
