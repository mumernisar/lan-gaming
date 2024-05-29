package Utils;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class fetchIP {

	/**
	 * Receives the IP address from a server or manually from user input.
	 *
	 * @return the received IP address
	 * @throws IOException if an I/O error occurs while receiving the IP address
 	*/
	public static String recieveIP() {
		
		try {
			DatagramSocket socket = new DatagramSocket(8888);
			byte[] receiveData = new byte[1024];

			// Listen for broadcast messages from the server
			DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
			socket.receive(packet);

			// Recieve and returnIP
			String ipAddress = new String(packet.getData()).trim();
			socket.close();
			System.out.println(ipAddress);
			return ipAddress;

		} catch (IOException e) {
			// Enter manually if connection fails
			System.out.println("Connection failed, please enter IP manually: ");
			Scanner scan = new Scanner(System.in);
			String ip = scan.nextLine();
			scan.close();
			return ip;
		}
	}

}