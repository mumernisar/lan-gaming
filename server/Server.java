import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static Set<PrintWriter> writers = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        try {
            String curIP = getCurrentIP();
            clearDBAsync(curIP);
            registerServerIP(curIP);
            startServer(curIP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getCurrentIP() throws UnknownHostException {
        InetAddress myIP = InetAddress.getLocalHost();
        return String.format("{\"ip\":\"%s\"}", myIP.getHostAddress());
    }

    private static void clearDBAsync(final String curIP) {
            try {
                clearDB(curIP);
            } catch (Exception e) {
                System.out.println("Could not clear the database: " + e.getMessage());
                System.out.println("IP address is: " + curIP);
            }
    }

    private static void registerServerIP(String curIP) {
        try {
            Request.post(curIP);
            System.out.println("Server IP registered: " + curIP);
        } catch (Exception e) {
            System.err.println("Error during IP registration: " + e.getMessage());
        }
    }

    private static void startServer(String curIP) {
        try (ServerSocket listener = new ServerSocket(PORT)) {
            System.out.println("The game server is running... on IP server: " + curIP);
            while (true) {
                new Handler(listener.accept()).start();
            }
        } catch (IOException e) {
            System.out.println("Could not start server: " + e.getMessage());
        }
    }

    private static void clearDB(String curIP) throws Exception {
        String jsonData = Request.fetchBins();
        jsonData = jsonData.substring(1, jsonData.length() - 1);
        String[] items = jsonData.split("\\},\\{");
        for (String item : items) {
            String binID = extractBinID(item);
            Request.delete(binID);
        }
    }

    private static String extractBinID(String item) {
        String[] parts = item.split("\"record\":\"");
        return parts[1].split("\"")[0];
    }

    private static class Handler extends Thread {
        private Socket socket;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                writers.add(out);
                handleClientCommunication();
            } catch (IOException e) {
                System.out.println("Handler IOException: " + e.getMessage());
            } finally {
                cleanup();
            }
        }

        private void handleClientCommunication() throws IOException {
            try (Scanner in = new Scanner(socket.getInputStream())) {
                while (in.hasNextLine()) {
                    String input = in.nextLine();
                    broadcastMessage(input);
                }
            }
        }

        private void broadcastMessage(String input) {
            System.out.println("Received input: " + input);
            for (PrintWriter writer : writers) {
                String message = processInput(input);
                System.out.println("Broadcasting: " + message);
                writer.println(message);
            }
        }

        private String processInput(String input) {
            HashMap<String, String> rmap = ParseMap.parse(input);
            if (isTypeRacerPayload(rmap)) {
                rmap.remove("payload");
                rmap.put("type", "game");
                rmap.put("name", "typeracer");
            }else if (rmap.get("type")  == null){
                rmap.put("type", "general");
            }
            return ParseMap.unparse(rmap);
        }

        private boolean isTypeRacerPayload(Map<String, String> rmap) {
            String payload = rmap.get("payload");
            return payload != null && payload.contains("typeracer");
        }

        private void cleanup() {
            if (out != null) {
                writers.remove(out);
                out.close();
            }
            try {
                socket.close();
                System.out.println("Closed socket for client: " + socket.getRemoteSocketAddress());
            } catch (IOException e) {
                System.out.println("Could not close client socket: " + e.getMessage());
            }
        }
    }
}
