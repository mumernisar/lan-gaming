// import java.io.*;
// import java.net.*;
// import java.util.HashMap;
// import java.util.Scanner;
// import java.util.concurrent.Executors;

// public class Client {
//     public static void main(String[] args) throws Exception {
//         Scanner console = new Scanner(System.in);
//         long currentTimeMillis = System.currentTimeMillis();
//         System.out.print("Enter you username : ");
//         String name = console.nextLine();
//         if (name == "" ){
//             name = "user";
//         }
//         String id =  name + "_" + String.valueOf(currentTimeMillis);

//         final String serverAddress = "localhost";
//         getServer(serverAddress, console);
//         System.out.println("Connecting to server with ip "+ serverAddress);
//         Socket socket = new Socket(serverAddress, 12345);

//         PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

//         Scanner scanner = new Scanner(System.in);
//         System.out.println("Enter your messages (exit to quit):");
//         new Thread(new Runnable() {
//             public void run() {
//                 handleIncoming(socket, id, out);
//             }
//             }).start();

//             /// handle outgoing
//         while (scanner.hasNextLine()) {
//             String input = scanner.nextLine();
//             if (input.equalsIgnoreCase("exit")) break;
//             HashMap<String, String> ms = new HashMap<>();
//             ms.put("id", id);
//             ms.put("payload", input);
//             String ds = ParseMap.unparse(ms);
//             out.println(ds); 
//         }
//         socket.close();
//     }
//     private static void handleIncoming(Socket socket , String id , PrintWriter out) {

//                 try {
//                     Scanner in = new Scanner(socket.getInputStream());
//                     while (in.hasNextLine()) {
//                         String message = in.nextLine();
//                         HashMap<String,String> rmap = ParseMap.parse(message);

//                         if (rmap.containsKey("type") && rmap.get("type").equals("game")) {
//                             Executors.newSingleThreadExecutor().execute(new Runnable() {
//                                 public void run() {
//                                     if (rmap.get("name").equals("typeracer")) {
//                                         System.out.println("Running typeracer");
//                                         Games.typeracer(out , id);
//                                     }
//                                     System.out.println("Game ended");
//                                 }
//                             }).start()
//                             System.out.println(message + "Done with if");
//                         }else if (rmap.containsKey("type") && rmap.get("type").equals("game_data")) {
//                             System.out.println("Runnign elif with data" + rmap);
//                             if(!rmap.get("type").equals(id)) {
//                                 System.out.println(rmap.get("data") + " from " + rmap.get("id"));
//                             }else {
//                                 System.out.println(rmap.get("data") + "Shouldnt get this" );
//                             }
//                             System.err.println("end of line");
//                         } else if (rmap.containsKey("payload")) {
//                             System.out.println(rmap.get("payload"));
//                         }else {
//                             System.out.println("Something mustve gone wrong . DAta :" + rmap);

//                         }
//                     }
//                 } catch (Exception e) {
//                     e.printStackTrace();
//                 }
//     }
    // private static void getServer(String serverAddress ,Scanner console) {
    //     try {
    //         String jsonData = Request.fetchBins();
    //         jsonData = jsonData.substring(1, jsonData.length() - 1);
    //         String[] items = jsonData.split("\\},\\{");
    //         String binID = "";

    //         for (String item : items) {
    //             String[] parts = item.split("\"record\":\"");
    //             binID = parts[1].split("\"")[0]; 
    //         }
    //         String response = Request.get(binID);

    //         int ipStartIndex = response.indexOf("\"ip\":\"") + "\"ip\":\"".length();
    //         int ipEndIndex = response.indexOf("\"", ipStartIndex);
    //         String ipAddress = response.substring(ipStartIndex, ipEndIndex);

    //         serverAddress = ipAddress;
    //         System.out.println("Success 🥳🥳🎉");

    //     } catch (Exception e) {
    //         System.out.print("Enter IP addres manually : "  );
    //         serverAddress = console.nextLine();
    //     }
    // }

// }



import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Scanner;

public class Client {

    private static final int PORT = 12345;
    
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
                        handleGameDataMessage(parsedMessage, userId);
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
            System.out.println("Running typeracer");
            Games.typeracer(out, userId);
            System.out.println("Game ended");
        } else {
            System.out.println("Game message received: " + parsedMessage);
        }
    }
    
    private static void handleGameDataMessage(HashMap<String, String> parsedMessage, String userId) {
        System.out.println("Running else-if with data " + parsedMessage);
        if (!parsedMessage.get("id").equals(userId)) {
            System.out.println(parsedMessage.get("data") + " from " + parsedMessage.get("id"));
        } else {
            System.out.println(parsedMessage.get("data") + "ERROR### Shouldn't have receive own data.");
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
