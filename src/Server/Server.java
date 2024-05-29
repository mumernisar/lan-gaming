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

    /**
     * Starts the server and listens for incoming connections.
     *
     * @throws RuntimeException if an IOException occurs during the process
     */
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

    /**
     * Closes the server socket if it is not null.
     *
     * @throws RuntimeException if an IOException occurs while closing the server socket
     */
    public void closeAll() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Adds a PrintWriter to the list of client writers.
     *
     * @param writer the PrintWriter to be added
     * @throws NullPointerException if the specified writer is null
     */
    public static void addWriter(PrintWriter writer) {
        ClientWriters.add(writer);
    }


    /**
     * Removes the specified PrintWriter from the list of client writers.
     *
     * @param writer the PrintWriter to be removed
     * @throws NullPointerException if the specified writer is null
     */
    public static void removeWriter(PrintWriter writer) {
        ClientWriters.remove(writer);
    }


    /**
     * Returns a Set of PrintWriter objects representing the client writers.
     *
     * @return a Set of PrintWriter objects
     * @throws NullPointerException if the ClientWriters Set is null
     */
    public static Set<PrintWriter> getWriters() {
        return ClientWriters;
    }


    // broadcasting ip in another thread so as to not hamper the socket of main thread
    /**
     * This method uploads the IP address by starting a new thread to broadcast the IP.
     * It uses an anonymous inner class to implement the Runnable interface and start the broadcasting process.
     *
     * @throws IllegalStateException if the broadcasting process is already running
     */
    public static void uploadIP() {
        new Thread(new Runnable() { // Anonymous Inner class
            @Override
            /**
             * Runs the broadcastIP.startBroadcasting method with a specified timeout.
             *
             * @throws IllegalArgumentException if the timeout is less than or equal to 0
             * @throws SomeOtherException if there is an issue with starting the broadcasting
             */
            public void run() {
                broadcastIP.startBroadcasting(200);
            }
        }).start();
    }
    

    /**
     * Starts the server on port 12345.
     *
     * @throws IOException if an I/O error occurs when creating the server socket.
     */
    public static void startServer() throws IOException {

        System.out.println("Server has started!");
        ServerSocket serverSocket = new ServerSocket(12345);
        Server server = new Server(serverSocket);
        server.start();

    }
}