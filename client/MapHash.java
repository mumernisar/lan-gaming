import java.util.HashMap;

public class MapHash {

    public static String unparse(HashMap<String, String> map) {
        StringBuilder builder = new StringBuilder();
        for (HashMap.Entry<String, String> entry : map.entrySet()) {
            builder.append(entry.getKey()).append(":").append(entry.getValue());
            builder.append(",");
        }
        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1);
        }
        return builder.toString();
    }

    public static HashMap<String, String> parse(String string) {
        HashMap<String, String> map = new HashMap<>();
        String[] entries = string.split(",");
        for (String entry : entries) {
            String[] keyValue = entry.split(":");
            if (keyValue.length == 2) {
                map.put(keyValue[0], keyValue[1]);
            }
        }
        return map;
    }
}