import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Scanner;

public class Client {

    private static final int PORT = 12345;
    private static final String[][] data = new String[4][4];
    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = getUserName(console);
        long currentTimeMillis = System.currentTimeMillis();
        String userId = username + "_" + currentTimeMillis;

        String serverAddress = getServerIP(console); 
        System.out.println("Connecting to server with IP " + serverAddress);
        
        try {
        // Initialize connection and handle incoming/outgoing messages

            Socket socket = new Socket(serverAddress, PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);
            // Handle incoming messages in a separate thread
            new Thread(() -> handleIncomingMessages(socket, userId , out)).start();

            // Main thread handles outgoing messages
            System.out.println("Enter your messages (type 'exit' to quit):");
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("exit")) {
                    break;
                }
                sendMessage(input, userId, out);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static String getUserName(Scanner scanner) {
        String input = scanner.nextLine();
        if (input.isEmpty()) {
            return "user";
        }
        return input;
    }

    private static String getServerIP(Scanner console) {
        try {
            String jsonData = Request.fetchBins();
            jsonData = jsonData.substring(1, jsonData.length() - 1);
            String[] items = jsonData.split("\\},\\{");
            String binID = "";

            for (String item : items) {
                String[] parts = item.split("\"record\":\"");
                binID = parts[1].split("\"")[0]; 
            }
            String response = Request.get(binID);

            int ipStartIndex = response.indexOf("\"ip\":\"") + "\"ip\":\"".length();
            int ipEndIndex = response.indexOf("\"", ipStartIndex);
            String ipAddress = response.substring(ipStartIndex, ipEndIndex);
            System.out.println("Success 🥳🥳🎉");

            return ipAddress;

        } catch (Exception e) {
            System.out.print("Enter IP addres manually : "  );
            return console.nextLine();
        }
    }

    private static void handleIncomingMessages(Socket socket, String userId , PrintWriter out) {
        try (Scanner in = new Scanner(socket.getInputStream())) {
            while (in.hasNextLine()) {
                String message = in.nextLine();
                HashMap<String, String> parsedMessage = ParseMap.parse(message);
               
                String messageType = parsedMessage.get("type");
                switch (messageType) {
                    case "game":
                        handleGameMessage(out, parsedMessage , userId);
                        break;
                    case "game_data":
                        handleGameDataMessage( data , parsedMessage, userId);
                        break;
                    default:
                        handleDefaultMessage(parsedMessage, userId);
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error while reading from the socket.");
            e.printStackTrace();
        }
    }

    private static void handleGameMessage(PrintWriter out , HashMap<String, String> parsedMessage , String userId) {
        if ("typeracer".equals(parsedMessage.get("name"))) {
            new Thread(() -> Games.typeracer(out, userId)).start();
        } else {
            System.out.println("Game message received: " + parsedMessage);
        }
    }

    private static void handleGameDataMessage( String[][] data, HashMap<String, String> parsedMessage, String userId) {
        String id = parsedMessage.get("id");
        if (id != null && "typeracer".equals(parsedMessage.get("game"))) {
            Games.typeracer_handleData(data, parsedMessage, userId);
        } else {
            System.out.println("Game not typeracer or ID is null " + parsedMessage.get("data") + " from " + parsedMessage.get("id"));
        }
    }

    
    private static void handleDefaultMessage(HashMap<String, String> parsedMessage, String userId) {
        if (parsedMessage.containsKey("payload")) {
            System.out.println(parsedMessage.get("payload"));
        } else {
            System.out.println("Something might have gone wrong. Data: " + parsedMessage);
        }
    }
    private static void sendMessage(String message, String userId, PrintWriter out) {
        HashMap<String, String> messageMap = new HashMap<>();
        messageMap.put("id", userId);
        messageMap.put("payload", message);
        String serializedMessage = ParseMap.unparse(messageMap);
        out.println(serializedMessage);
    }
    
}
