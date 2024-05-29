package Server;

import Utils.ParseMap;
import java.io.*;
import java.net.Socket;
import java.util.*;

public final class ClientHandler implements Runnable {

	private static final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String clientUsername;
    private PrintWriter writer;
	Set <PrintWriter> writers = Server.getWriters();


	public ClientHandler(Socket socket) {

		try {
			this.socket = socket;
			// saved for future possible use
			this.writer = new PrintWriter(socket.getOutputStream(), true);
			//
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.clientUsername = bufferedReader.readLine();

		} catch (IOException e) {
			closeAll(socket, bufferedReader, bufferedWriter);
		}


	}
	public void init() {
        clientHandlers.add(this);
		Server.addWriter(writer);
    }

	@Override
	public void run() {

		String messageFromClients;

		while (socket.isConnected()) {
			try {
				messageFromClients = bufferedReader.readLine();

				//checks for capital guessing game
				checkMessageForCapitalGame(messageFromClients);

				//normal message
				broadcastMessage(messageFromClients);
			} catch (IOException e){
				System.out.println("Client disconnected " + clientUsername);
				closeAll(socket, bufferedReader, bufferedWriter);
				break;
			}
		}
	}

	
    private void broadcastMessage(String input) {
        for (PrintWriter client : writers) {
            String message = processInput(input);
            System.out.println("Broadcasting: " + message);
            client.println(message);
        }
    }


	private void checkMessageForCapitalGame(String message) throws IOException {
		
		// saves data for capital guess and also fetches the results
		ArrayList<String> results = SaveData.saveDataCapitals(message);
		for (ClientHandler client : clientHandlers) {
			if (client == this) {
				for (String line : results) {
					client.bufferedWriter.write(line);
					client.bufferedWriter.newLine();
					client.bufferedWriter.flush();
				}
			}
		}
	}


	private String processInput(String input) {
		HashMap<String, String> rmap = ParseMap.parse(input);
		String payload = rmap.get("payload");
		/// means we dealing with a game
		if (payload != null && payload.contains("play")) {
			String game = payload.split(" ", -1)[1];
			rmap.put("type", game);
			rmap.put("payload", "run");
		}

		// means we wanna save to server (leaderboard)
		if (rmap.get("saveData") != null && "true".equals(rmap.get("saveData"))) {

			if (rmap.get("type").equals("typeracer")) {
				int u = SaveData.save(rmap);
				rmap.put("payload", "leaderboard");
				rmap.put("index", u+"");
				rmap.put("leaderboard", SaveData.getLeaderboardString(u));
			}
		}
		
		return ParseMap.unparse(rmap);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	public void broadcastMessageToClients(String messageToSend) {
		for (ClientHandler client : clientHandlers) {
			try {
				if (client != this) {
					client.bufferedWriter.write(messageToSend);
					client.bufferedWriter.newLine();
					client.bufferedWriter.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public void removeClientHandler() {

		clientHandlers.remove(this);
		Server.removeWriter(writer);
		broadcastMessageToClients("SERVER: " + clientUsername + " has left the chat!");
		
	}

	public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedwriter) {
		removeClientHandler();
		try {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (bufferedwriter != null) {
				bufferedwriter.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
		}
	}
}
