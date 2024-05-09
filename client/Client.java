import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class Client {
    public static void main(String[] args) throws Exception {

        Scanner console = new Scanner(System.in);
        long currentTimeMillis = System.currentTimeMillis();
        System.out.print("Enter you username : ");
        String name = console.nextLine();
        String id =  name + "_" + String.valueOf(currentTimeMillis)  ;
        
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
        

        final AtomicReference<String> serverAddress = new AtomicReference<>("localhost");
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
        try {
            String jsonData = Request.fetchBins(collectionID, myMasterKey , null, "ascending");
            jsonData = jsonData.substring(1, jsonData.length() - 1);
            String[] items = jsonData.split("\\},\\{");
            String binID = "";

            for (String item : items) {
                String[] parts = item.split("\"record\":\"");
                binID = parts[1].split("\"")[0]; 
            }
            String response = Request.get(binID , myMasterKey ,myToken);

            int ipStartIndex = response.indexOf("\"ip\":\"") + "\"ip\":\"".length();
            int ipEndIndex = response.indexOf("\"", ipStartIndex);
            String ipAddress = response.substring(ipStartIndex, ipEndIndex);

            serverAddress.set(ipAddress);
            System.out.println("Success 🥳🥳🎉");

        } catch (Exception e) {
            System.out.print("Enter IP addres manually : "  );
            serverAddress.set(console.nextLine());
        }
    });


    try {
        future.get(); // Block and wait for the future to complete
    } catch (InterruptedException | ExecutionException e) {
        // Handle InterruptedException or ExecutionException
        System.out.println("An error occurred: " + e.getMessage());
    }

    System.out.println("Connecting to server with ip "+ serverAddress.get());
    Socket socket = new Socket(serverAddress.get(), 12345);


        new Thread(new Runnable() {
            public void run() {
                try {
                    Scanner in = new Scanner(socket.getInputStream());
                    while (in.hasNextLine()) {
                        String message = in.nextLine();
                        System.out.println(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your messages (exit to quit):");

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) break;
            out.println(input + " from " + id); // Send to the server
        }
        socket.close();
    }
}
