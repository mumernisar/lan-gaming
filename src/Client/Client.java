package Client;

import Games.CountryGuess;
import Games.Typeracer;
import Utils.ParseMap;
import Utils.fetchIP;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

// final makes unoverridable
public final class Client {

	private Socket socket;
	private BufferedReader ReadServer;
	private BufferedWriter WriteServer;
	private String username;
	private String messageToSend;

	// ANSI Colour code
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_RESET = "\u001B[0m";


	// Constructor
	public Client(Socket socket, String username) {
		try {
			this.socket = socket;
			this.ReadServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.WriteServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.username = username;
		} catch (IOException e) {
			closeAll(socket, ReadServer, WriteServer);
		}
	}

	// initial message sent(only the username is sent so that it can be shown that user has joined
	private void sendMessage(String name) {
		try {
			WriteServer.write(name);
			WriteServer.newLine();
			WriteServer.flush();
			System.out.println("Your name has been registered!");
		} catch (IOException e) {
			closeAll(socket, ReadServer, WriteServer);
			System.out.println("Your name has not been registered!");
		}
	}

	// Client handler is waiting for this message in its constructor (line 20)
	// limitation will not start game for other users if send from one....! --------------------------!!! NOTE !!!-------------------------- FIXED
	private void sendMessage() {
		try {
			Scanner scan = new Scanner(System.in);
			while (socket.isConnected()) {
				messageToSend = scan.nextLine();
				// checks if user wants to play for games
				if (messageToSend.equalsIgnoreCase("exit")) {
                    break;
                }
				HashMap<String, String> messageMap = new HashMap<>();
				messageMap.put("id", username);
				messageMap.put("type", "general");
				messageMap.put("payload", messageToSend);
				String serializedMessage = ParseMap.unparse(messageMap);
				
				/// write the message to the server
				WriteServer.write(serializedMessage);
				WriteServer.newLine();
				WriteServer.flush();
			}
		} catch (IOException e) {
			closeAll(socket, ReadServer, WriteServer);
		}
	}


	public void closeAll(Socket socket, BufferedReader ReadServer, BufferedWriter WriteServer) {
		try {
			if (ReadServer != null) {
				ReadServer.close();
			}
			if (WriteServer != null) {
				WriteServer.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// private Socket socket;
	// private BufferedReader ReadServer;
	// private BufferedWriter WriteServer;
	// private String username;
	// private String messageToSend;
	
    private  void handleIncomingMessages() {

        try {
            while (socket.isConnected()) {
                String message = ReadServer.readLine();
                HashMap<String, String> parsedMessage = ParseMap.parse(message);
               
                String messageType = parsedMessage.get("type");
                switch (messageType) {
                    case "typeracer":
                        Typeracer.handleGame(parsedMessage,username ,  WriteServer);
                        break;
                    case "capital":
						CountryGuess.handleGame(username);
                        break;
					case "tictactoe":
						// handleGameDataMessage(data , parsedMessage);
						break;

					// general type message
                    default:
                        handleDefaultMessage(parsedMessage );
                        break;
                }
            }
        } catch (IOException e) {
			e.printStackTrace();
			e.getMessage();
			closeAll(socket, ReadServer, WriteServer);

        }
    }
    
    private static void handleDefaultMessage(HashMap<String, String> parsedMessage) {
        if (parsedMessage.containsKey("payload")) {
            System.out.println(parsedMessage.get("payload"));
        } else {
            System.out.println("Something might have gone wrong. Data: " + parsedMessage);
        }
    }
	public static void main(String[] args) throws IOException {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter your username for the group chat: ");
		String username = scan.nextLine();
		long currentTimeMillis = System.currentTimeMillis();
		username = username + "_" + currentTimeMillis;

		Socket clientSocket = new Socket(fetchIP.recieveIP(), 12345);
		Client client = new Client(clientSocket, username);
		client.sendMessage(username);

		// Both these methods are blocking methods,so each method is run
		// on a separate thread so the process is concurrent (i.e. sending and
		// receiving messages)
		new Thread(() -> client.handleIncomingMessages()).start();
		// client.listenForMessage();
		client.sendMessage();
	}
}



// {
// 	type: "game/general",
// 	"id": "username",
// 	payload: "data/run",
// }