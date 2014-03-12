import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;

public class Server {

	//Reference: http://docs.oracle.com/javase/tutorial/networking/datagrams/clientServer.html
	public static void main(String[] args) throws Exception{
		ServerSocket serverSocket = null;
		int clientPortNum;
		int serverPortNum;
		String senderhost;
		String fileName; // file to write received data
		try { // try to parse command line args
			senderhost = args[0];
			clientPortNum = Integer.parseInt(args[1]); 
			serverPortNum = Integer.parseInt(args[2]);
			fileName = args[3];
													
		} catch (Exception e) {
			throw new Exception("Could not parse commandline args.");
		}
		try {
			serverSocket = new ServerSocket(clientPortNum);
			System.out.println("Running on port " + serverSocket.getLocalPort());
		} catch (IOException e) {
			System.err.println("Could not listen on port " + serverPortNum + ".");
			try { // try again on error
				serverSocket = new ServerSocket(0);
				System.out.println("Listening on port "
						+ serverSocket.getLocalPort() + ".");
			} catch (IOException ioe) { // exit if we are unable to allocate a
										// port
				System.err.println("Could not listen on port. Exiting...");
				System.exit(1);
			}
		}
		
		while (true) {
			try {
				new ServerThread(clientPortNum,fileName).start();
				System.out.println("Accepted new Client");
			} catch (Exception e) { // if there is some other error exit
				serverSocket.close();
				System.err.println("An error has occured. Exiting.");
				break;
			}
		}
		System.exit(0);
	}

	private static class ServerThread extends Thread{
		private DatagramSocket datagramSocket;
		private PrintWriter out;
		private String inputLine, outputLine;
		private BufferedReader in;
		private String fileName;

		public ServerThread(int clientPortNum,String fileName) {
			try {
				this.datagramSocket = new DatagramSocket(clientPortNum);
				//this.out = new PrintWriter(datagramSocket.getOutputStream(), true);
				this.in = new BufferedReader(new FileReader(fileName));
			} catch (IOException e) {
				System.err.println("Error creating connection to client.");
			}
		}

		public void run() {
			try {
				while ((inputLine = in.readLine()) != null) {
					try {
						byte[] buf = new byte[256];
						DatagramPacket packet = new DatagramPacket(buf, buf.length);
						datagramSocket.receive(packet);
						outputLine = Protocol.saveInput(fileName, inputLine);
						out.println(outputLine);
						
						InetAddress address = packet.getAddress();
						int port = packet.getPort();
						packet = new DatagramPacket(buf, buf.length, address, port);
						datagramSocket.send(packet);

					} catch (NullPointerException e) { // on error end run
						System.err.println("Client was disconnected.");
						datagramSocket.close();
						break;
					}
				}
			} catch (IOException e) {
				System.err.println("Client was disconnected.");
			}
		}
	}
}
