import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Server {
    private static final int PORT = 12345;
    private static Set<PrintWriter> writers = new HashSet<>();

    public static void main(String[] args) throws Exception {

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

        InetAddress myIP=InetAddress.getLocalHost();
        final String curIP = String.format("{\"ip\":\"%s\"}", myIP.getHostAddress());

        //////////////////////////////////////////////////////////////////////////////////////////////////


        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                String jsonData = Request.fetchBins(collectionID, myMasterKey, null, "ascending");
                jsonData = jsonData.substring(1, jsonData.length() - 1);
                String[] items = jsonData.split("\\},\\{");
                for (String item : items) {
                    String[] parts = item.split("\"record\":\"");
                    String binID = parts[1].split("\"")[0]; 
                    Request.delete(binID, myMasterKey, myToken);
                }
            } catch (Exception e) {
                System.out.println("Could not form connection to the database. Share IP address manually. IP address is: " + curIP );
            }
        });

        try {
            future.get(); 
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("An error occurred: " + e.getMessage());
            System.out.println("Could not form connection to the database. Share IP address manually 2. IP address is : " + curIP );

        }
        try {
            Request.post(curIP, myMasterKey ,collectionID, myToken);
        } catch (Exception e) {
            System.err.println(e + " conn error ------------");
        }

        System.out.println("The game server is running... on ip server : " + curIP);
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
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
    
                Scanner in = new Scanner(socket.getInputStream());
    
                while (in.hasNextLine()) {
                    String input = in.nextLine();
                    System.out.println("raw input " + input);
                    for (PrintWriter writer : writers) {
                        HashMap<String, String> rmap = MapHash.parse(input);
                        if ((rmap.get("payload" ) != null) && rmap.get("payload").contains("typeracer")) {
                            rmap.remove("payload");
                            rmap.put("type", "game");
                            rmap.put("name", "typeracer");
                            String smap =  MapHash.unparse(rmap);
                            System.out.println("Sending SIMLE: " + smap);
                            writer.println(smap);
                        } else {
                            String smap =  MapHash.unparse(rmap);
                            System.out.println("before" + rmap + "after " + smap);
                            System.out.println("Sending SIMLE: " + smap);
                            writer.println(smap);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("IOException in handler: " + e.getMessage());
                e.printStackTrace(); 
            } finally {
                if (out != null) {
                    writers.remove(out);
                    out.close();
                }
                try {
                    socket.close(); 
                    System.out.println("Socket closed for client: " + socket.getRemoteSocketAddress());
                } catch (IOException e) {
                    System.out.println("IOException while closing socket: " + e.getMessage());
                    e.printStackTrace(); 
                }
            }
        }
    }
}

