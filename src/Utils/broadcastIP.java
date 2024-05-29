package Utils;

import java.net.*;

public class broadcastIP {

	/**
	 * Starts broadcasting the server's IP address using UDP broadcast.
	 *
	 * @param delayInEachPulse the delay in milliseconds between each broadcast pulse
	 * @throws SocketException if an error occurs while creating or accessing a socket
	 * @throws UnknownHostException if the IP address of the broadcast destination is invalid
	 * @throws IOException if an I/O error occurs while sending the broadcast packet
	 * @throws InterruptedException if the current thread is interrupted while sleeping
 	*/
	public static void startBroadcasting(int delayInEachPulse) {

		try {
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);

			// Get the server's IP address
			String ipAddress = InetAddress.getLocalHost().getHostAddress();
			byte[] sendData = ipAddress.getBytes();
			System.out.println("Server is running at: " + ipAddress);

			// Broadcast the server's IP address
			DatagramPacket packet = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
			while (true) {
				socket.send(packet);
				Thread.sleep(delayInEachPulse);
			}
		} catch (Exception e) {
			System.out.println("IP broadcasting was shut down due to unknown error");
		}
	}
}