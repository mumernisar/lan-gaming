import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class Request {
            
        ////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////

        // SET THESE FROM JSONBIN.IO BEFORE EXECUTION
        // String myToken = "ACCESS_TOKEN";
        // String myMasterKey = "MASTER_TOKEN";
        // String collectionID = "COLLECTION_ID";


    private static final String API_URL = "https://api.jsonbin.io/v3/b";

    public static String post(String dataJson ) {
        return sendRequest("POST", dataJson, myMasterKey, null, collectionID , myToken);
    }

    public static String get(String binId ) {
        return sendRequest("GET", null, myMasterKey, binId, null , myToken);
    }

    public static String delete(String binId) {
        return sendRequest("DELETE", null, myMasterKey, binId, null , myToken );
    }
    public static String fetchBins() {
        HttpURLConnection connection = null;
        try {
            String urlStr = "https://api.jsonbin.io/v3" + "/c/" + collectionID + "/bins/";
            URI uri = new URI(urlStr);
            URL url = uri.toURL();
            
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Master-Key", myMasterKey);
    
            connection.setRequestProperty("X-Sort-Order", "ascending");
    
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
            connection.setRequestProperty("X-Access-Key", accessToken); 
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

    public static String getApiUrl() {
        return API_URL;
    }
}

