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




        // CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
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
        // });

        // try {
        //     future.get(); // Block and wait for the future to complete
        // } catch (InterruptedException | ExecutionException e) {
        //     // Handle InterruptedException or ExecutionException
        //     System.out.println("An error occurred: " + e.getMessage());
        // }

        System.out.println("The game server is running... on ip server : " + curIP);

        ServerSocket listener = new ServerSocket(PORT);



        String response = Request.post(curIP, myMasterKey ,collectionID, myToken);
        System.out.println(response);

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
                    for (PrintWriter writer : writers) {
                        writer.println(input); // Echo the message out to all writers
                        System.out.println("Received: " + input); // Log received message
                    }
                }
            } catch (IOException e) {
                System.out.println("IOException in handler: " + e.getMessage());
                e.printStackTrace(); // Log stack trace for IOException
            } finally {
                if (out != null) {
                    writers.remove(out);
                    out.close(); // Close the PrintWriter when done
                }
                try {
                    socket.close(); // Close the socket when done
                    System.out.println("Socket closed for client: " + socket.getRemoteSocketAddress());
                } catch (IOException e) {
                    System.out.println("IOException while closing socket: " + e.getMessage());
                    e.printStackTrace(); // Log stack trace for IOException when closing socket
                }
            }
        }
    }

    // private static class Handler extends Thread {
    //     private Socket socket;
    //     private PrintWriter out;

    //     public Handler(Socket socket) {
    //         this.socket = socket;
    //     }

    //     public void run() {
    //         try {
    //             out = new PrintWriter(socket.getOutputStream(), true);
    //             writers.add(out);

    //             Scanner in = new Scanner(socket.getInputStream());

    //             while (in.hasNextLine()) {
    //                 String input = in.nextLine();
    //                 for (PrintWriter writer : writers) {
    //                     writer.println(input);
    //                     System.out.println(input);
    //                 }
    //             }
    //             in.close();
    //         } catch (IOException e) {
    //             System.out.println(e.getMessage());
    //             System.out.println("error occured");
    //         } finally {
    //             if (out != null) {
    //                 writers.remove(out);
    //             }
    //             try {
    //                 socket.close();
    //                 System.out.println("Closing socket");
    //             } catch (IOException e) {
    //                 // Ignore
    //                 System.out.println("Closing socket 2");

    //             }
    //         }
    //     }
    // }
}

