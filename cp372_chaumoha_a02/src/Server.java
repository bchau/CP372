import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server {

	public static void main(String[] args) throws Exception{
		int clientPortNum;
		int serverPortNum;
		String senderhost;
		String fileName; // file to write received data
		
		if (args.length != 4) {
			System.err
					.println("To run the program please enter valid parameters as:");
			System.err
					.println("prog <Host Address> <Client Port> <Server Port> <File Name>");
			System.exit(1); // exit out
		}
		try { // try to parse command line args
			senderhost = args[0];
			clientPortNum = Integer.parseInt(args[1]); 
			serverPortNum = Integer.parseInt(args[2]);
			fileName = args[3];
													
		} catch (Exception e) {
			throw new Exception("Could not parse commandline args.");
		}
		try {
			new ServerThread(serverPortNum,fileName).start();
		} catch (Exception e) { // if there is some other error exit
			System.err.println("An error has occured. Exiting.");
		}
	}

	private static class ServerThread extends Thread{
		private DatagramSocket datagramSocket;
		private String outputLine;
		private String fileName;
		private boolean reachedEOF = false;

		public ServerThread(int serverPortNum,String fileName) {
			try {
				this.datagramSocket = new DatagramSocket(serverPortNum);
				this.fileName = fileName;
				System.out.println("UDP socket created.");
			} catch (SocketException e) {
				System.err.println("Could not create socket: "+e);
			} catch (Exception e){
				System.err.println("Unknown error occured: "+e);
			}
			
		}

		public byte[] parsePacket(byte[] p){
			byte[] data = new byte[124];
			for (int i = 0; i < data.length; i++){
				data[i] = p[i+4];
			}
			return data;
		}
		
		public String removeWhiteSpace(String s){
			int i;
			for (i = 0; i < s.length()-1;i++){
				if (s.substring(i, i+1).equals("\0")){
					reachedEOF = true;
					return s.substring(0, i);
				}
			}
			return s;
		}
		
		public void run() {
				while (true){
					byte[] receivedData = new byte[128];
					byte[] sendData = new byte[128];
					
					try {
						if (reachedEOF){
							System.out.println("Reached EOF, terminating.");
							break;
						}
						DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
						
						datagramSocket.receive(receivePacket);
						System.out.println("accepted packet");
						
						
						byte[] data = parsePacket(receivePacket.getData());
						String s = new String(data);
						s = removeWhiteSpace(s);
						outputLine = Protocol.saveInput(fileName, s);
						System.out.println(outputLine);
						
						InetAddress address = receivePacket.getAddress();
						int port = receivePacket.getPort();
						
						//sendData = createACK(receivedData);
						for (int i = 0; i < 4; i++){
							sendData[i] = receivedData[i];
						}
						byte[] ack = "ACK".getBytes();
						for (int i = 0; i < ack.length;i++){
							sendData[i+4] = ack[i];
						}
						DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length, address, port);
						datagramSocket.send(sendPacket);
					} catch (IOException e) {
						System.err.println("Could not send packet: "+e);
						break;
					} catch (Exception e){
						System.err.println("Unknown error occured: "+e);
						break;
					}
				}
				datagramSocket.close();
			}
		
	}
}
