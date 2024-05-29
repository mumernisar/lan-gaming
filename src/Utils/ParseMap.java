package Utils;

import java.util.HashMap;

public class ParseMap {


	/**
	 * Generates a string representation of the given HashMap.
	 *
	 * @param map the HashMap to be converted to a string
	 * @return a string representation of the given HashMap
 	*/
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

	/**
	 * Parses the input string to create a HashMap of key-value pairs.
	 *
	 * @param string the input string containing key-value pairs separated by commas
	 * @return a HashMap containing the parsed key-value pairs
	 * @throws NullPointerException if the input string is null
 	*/
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
	// public void sendHashMap(HashMap<String, String> data , ObjectOutputStream out) throws IOException {
    //         out.writeObject(data);
        
    // }

    // @SuppressWarnings("unchecked")
	// public HashMap<String, String> receiveHashMap(ObjectInputStream in) throws IOException, ClassNotFoundException {
    //         return (HashMap<String, String>) in.readObject();
    // }

}
