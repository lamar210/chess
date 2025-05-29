//package chess;
//
//import com.google.gson.*;
//import java.lang.reflect.Type;
//import java.util.*;
//
//public class ChessBoardAdapter implements JsonSerializer<Map<ChessPosition, ChessPiece>>,
//        JsonDeserializer<Map<ChessPosition, ChessPiece>> {
//
//    @Override
//    public JsonElement serialize(Map<ChessPosition, ChessPiece> src, Type typeOfSrc, JsonSerializationContext context) {
//        JsonObject obj = new JsonObject();
//        for (var entry : src.entrySet()) {
//            String key = entry.getKey().getRow() + "," + entry.getKey().getColumn();
//            obj.add(key, context.serialize(entry.getValue()));
//        }
//        return obj;
//    }
//
//    @Override
//    public Map<ChessPosition, ChessPiece> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//        Map<ChessPosition, ChessPiece> map = new HashMap<>();
//        JsonObject obj = json.getAsJsonObject();
//        for (var entry : obj.entrySet()) {
//            String[] parts = entry.getKey().split(",");
//            int row = Integer.parseInt(parts[0]);
//            int col = Integer.parseInt(parts[1]);
//            ChessPosition pos = new ChessPosition(row, col);
//            ChessPiece piece = context.deserialize(entry.getValue(), ChessPiece.class);
//            map.put(pos, piece);
//        }
//        return map;
//    }
//}
