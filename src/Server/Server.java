package Server;

import Utils.broadcastIP;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    private final ServerSocket serverSocket;
    private static final Set<PrintWriter> ClientWriters = Collections.synchronizedSet(new HashSet<>());

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void start() {

        try {
            while (!serverSocket.isClosed()) {
                uploadIP();
                Socket socket = serverSocket.accept();
                System.out.println("A new Client has entered!");
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandler.init();
                Thread t = new Thread(clientHandler);
                t.start();
            }
        } catch (IOException e) {
            closeAll();
            throw new RuntimeException(e);
        }
    }

    public void closeAll() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void addWriter(PrintWriter writer) {
        ClientWriters.add(writer);
    }


    public static void removeWriter(PrintWriter writer) {
        ClientWriters.remove(writer);
    }


    public static Set<PrintWriter> getWriters() {
        return ClientWriters;
    }


    // broadcasting ip in another thread so as to not hamper the socket of main thread
    public static void uploadIP() {
        new Thread(new Runnable() { // Anonymous Inner class
            @Override
            public void run() {
                broadcastIP.startBroadcasting(200);
            }
        }).start();
    }
    

    public static void startServer() throws IOException {

        System.out.println("Server has started!");
        ServerSocket serverSocket = new ServerSocket(12345);
        Server server = new Server(serverSocket);
        server.start();

    }
}