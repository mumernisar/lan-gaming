import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class Request {

    private static final String API_URL = "https://api.jsonbin.io/v3/b";

    public static String post(String dataJson, String masterKey, String collectionId ,String accessToken ) {
        return sendRequest("POST", dataJson, masterKey, null, collectionId , accessToken);
    }

    public static String get(String binId, String masterKey ,String accessToken ) {
        return sendRequest("GET", null, masterKey, binId, null , accessToken);
    }

    public static String delete(String binId, String masterKey ,String accessToken) {
        return sendRequest("DELETE", null, masterKey, binId, null , accessToken );
    }
    public static String fetchBins(String collectionId, String masterKey, String lastBinId, String sortOrder) {
        HttpURLConnection connection = null;
        try {
            // Construct the URL based on whether the lastBinId is provided
            String urlStr = "https://api.jsonbin.io/v3" + "/c/" + collectionId + "/bins" + (lastBinId != null && !lastBinId.isEmpty() ? "/" + lastBinId : "");
            URI uri = new URI(urlStr);
            URL url = uri.toURL();
            
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Master-Key", masterKey);
    
            // Optional: Sort Order
            if (sortOrder != null && !sortOrder.isEmpty()) {
                connection.setRequestProperty("X-Sort-Order", sortOrder);
            }
    
            int responseCode = connection.getResponseCode();
            System.out.println("GET Response Code: " + responseCode);
    
            InputStream inputStream = responseCode < HttpURLConnection.HTTP_BAD_REQUEST ?
                connection.getInputStream() : connection.getErrorStream();
    
            try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
                String responseBody = scanner.useDelimiter("\\A").next();
                System.out.println("GET Response Body: " + responseBody);
                return responseBody;
            }
    
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not form connection to the database. Share ip address manually . Ip addres is : " + "123.123.123.123" );
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String sendRequest(String method, String dataJson, String masterKey, String binId, String collectionId , String accessToken) {
        HttpURLConnection connection = null;
        try {
            String urlStr = API_URL + (binId != null ? "/" + binId : "");
            URI uri = new URI(urlStr);
            URL url = uri.toURL();

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("X-Master-Key", masterKey);
            connection.setRequestProperty("X-Access-Key", accessToken); // Access Key Header
            connection.setDoOutput(true);

            if (collectionId != null && !collectionId.isEmpty()) {
                connection.setRequestProperty("X-Collection-Id", collectionId);
            }

            if (method.equals("POST")) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = dataJson.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            int responseCode = connection.getResponseCode();
            System.out.println(method + " Response Code: " + responseCode);

            InputStream inputStream = responseCode < HttpURLConnection.HTTP_BAD_REQUEST ?
                connection.getInputStream() : connection.getErrorStream();

            try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
                String responseBody = scanner.useDelimiter("\\A").next();
                System.out.println(method + " Response Body: " + responseBody);
                return responseBody;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not form connection to the database. Share ip address manually . Ip addres is : " + dataJson );

            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}

