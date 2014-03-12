import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server {

	public static void main(String[] args) throws Exception{
		DatagramSocket serverSocket = null;
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
			serverSocket = new DatagramSocket(serverPortNum);//,InetAddress.getByName(senderhost));
			System.out.println("Running on port " + serverSocket.getLocalPort());
		} catch (IOException e) {
			System.err.println("Could not listen on port " + serverPortNum + ".");
			try { // try again on error
				serverSocket = new DatagramSocket(0);
				System.out.println("Listening on port "
						+ serverSocket.getLocalPort() + ".");
			} catch (IOException ioe) { // exit if we are unable to allocate a
										// port
				System.err.println("Could not listen on port. Exiting...");
				System.exit(1);
			}
		}
		
		
		try {
			new ServerThread(clientPortNum,fileName).start();
		} catch (Exception e) { // if there is some other error exit
			serverSocket.close();
			System.err.println("An error has occured. Exiting.");
		}
	}

	private static class ServerThread extends Thread{
		private DatagramSocket datagramSocket;
		private String outputLine;
		private String fileName;

		public ServerThread(int clientPortNum,String fileName) {
			try {
				this.datagramSocket = new DatagramSocket(clientPortNum);
				this.fileName = fileName;
				System.out.println("UDP socket created.");
			} catch (SocketException e) {
				System.err.println("Could not create socket: "+e);
			} catch (Exception e){
				System.err.println("Unknown error occured: "+e);
			}
			
		}

		public void run() {
				while (true){
					byte[] receivedData = new byte[128];
					byte[] sendData = new byte[128];
					
					try {
						DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
						datagramSocket.receive(receivePacket);
						System.out.println("accepted packet");
						
						String s = receivePacket.getData().toString();
						outputLine = Protocol.saveInput(fileName, s);
						System.out.println(outputLine);
						
						InetAddress address = receivePacket.getAddress();
						int port = receivePacket.getPort();
						
						for (int i = 0; i < 4; i++){
							sendData[i] = receivedData[i];
						}
						byte[] ack = "ACK".getBytes();
						for (int i = 0; i < ack.length;i++){
							sendData[i+4] = ack[i];
						}
						DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length, address, port);
						datagramSocket.send(sendPacket);
						datagramSocket.close();
					} catch (IOException e) {
						System.err.println("Could not send packet: "+e);
						datagramSocket.close();
						break;
					} catch (Exception e){
						System.err.println("Unknown error occured: "+e);
						datagramSocket.close();
						break;
					}
				}
			}
		
	}
}
