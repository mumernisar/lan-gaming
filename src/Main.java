import Client.*;
import Server.*;
import java.io.IOException;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws Exception {

		System.out.println("Please specify your choice: ");

		System.out.println("""
                                   1. Run Server
                                   2. Run Client
                                   """);

		Scanner scanner = new Scanner(System.in);
		int choice = scanner.nextInt();

		try {
			switch (choice) {
				case 1:
					System.out.println("Running Server only!");
					Server.startServer();
					break;
				case 2:
					System.out.println("Running Client Only!");
					Client.startClient();
					break;
				case 3:
					System.out.println("Running server and client!");
					startClientAndServer();
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void startClientAndServer() throws IOException {

		Server.startServer();

		String command = "";
		
		ProcessBuilder pb = new ProcessBuilder();
		
	}
}
