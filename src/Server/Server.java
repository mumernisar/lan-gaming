package Server;
import Utils.broadcastIP;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


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
    // broadcasting ip in another thread so as to nor hamper the socket of main thread
    public static void uploadIP() {
        new Thread(() -> {
            broadcastIP.startBroadcasting(200);
        }).start();
    }

    public static void main(String[] args) throws IOException {

        System.out.println("Server has started!");
        ServerSocket serverSocket = new ServerSocket(12345);
        Server server = new Server(serverSocket);
        server.start();

    }
}