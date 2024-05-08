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

            // Finding the start index of the IP address value
            int ipStartIndex = response.indexOf("\"ip\":\"") + "\"ip\":\"".length();
            // Finding the end index of the IP address value
            int ipEndIndex = response.indexOf("\"", ipStartIndex);

            String ipAddress = response.substring(ipStartIndex, ipEndIndex);

            // Extracting the IP address value using substring
            serverAddress.set(ipAddress);

            System.out.println(response);
            System.out.println("Success 🥳🥳🎉");

            System.out.println("Running on " + serverAddress);

        } catch (Exception e) {
            System.out.println("Enter IP addres manually : "  );
            // console = new Scanner(System.in);
            // serverAddress = console.nextLine();
            // serverAddress = console.nextLine();
            // console.close();
        }
    });


    try {
        future.get(); // Block and wait for the future to complete
    } catch (InterruptedException | ExecutionException e) {
        // Handle InterruptedException or ExecutionException
        System.out.println("An error occurred: " + e.getMessage());
    }

    System.out.println(serverAddress.get());
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
            // String y  = Integer.toString(id);
            // Response response = new Response();
            // String res = response.main(y);
            // res = res + input;
            out.println(input + " from " + id); // Send to the server
        }
        socket.close();
    }
}




// import java.io.*;
// import java.net.*;
// import java.util.Scanner;

// public class Client {
//     public static void main(String[] args) throws Exception {
        
//         Scanner console = new Scanner(System.in);
//         long currentTimeMillis = System.currentTimeMillis();
//         // String name = console.nextLine();
//         String id = String.valueOf(currentTimeMillis)   + "name" ; 

//         String myToken = "$2a$10$koZ/yeSqeo8WbpSBdzFgZOlaqbiyluCXc8owJHxmpWhApcg3.b8Z2";
//         String myMasterKey = "$2a$10$.dep6oDvBbWezLBN0Ec6qeaWgPgiF.K/JbxExRVsgIYyw5xHwa/W6";
//         String collectionID = "663b9390acd3cb34a844ca7b";

//         String serverAddress = (args.length == 0) ? "localhost" : args[1];
//         Socket socket = new Socket(serverAddress, 12345);


        



//         new Thread(new Runnable() {
//             public void run() {
//                 try {
//                     Scanner in = new Scanner(socket.getInputStream());
//                     while (in.hasNextLine()) {
//                         String message = in.nextLine();
//                         System.out.println(message);
//                     }
//                 } catch (Exception e) {
//                     e.printStackTrace();
//                 }
//             }
//         }).start();

//         PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//         Scanner scanner = new Scanner(System.in);
//         System.out.println("Enter your messages (exit to quit):");

//         while (scanner.hasNextLine()) {
//             String input = scanner.nextLine();
//             if (input.equalsIgnoreCase("exit")) break;
//             out.println(input); // Send to the server
//         }
//         socket.close();
//     }
// }