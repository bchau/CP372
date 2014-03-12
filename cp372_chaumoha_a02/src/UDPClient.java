import java.io.*;
import java.net.*;
 
public class UDPClient {
 
	DatagramSocket theSocket = null;
	int serverPort = 9999;
 
	public UDPClient()
	{
		try {
			theSocket = new DatagramSocket();
 
			// but if you want to connect to your remote server, then alter the theServer address below
			InetAddress theServer = InetAddress.getLocalHost();
			theSocket.connect(theServer,serverPort);
 
			System.out.println("Client socket created");
		}catch (SocketException ExceSocket)
		{
			System.out.println("Socket creation error  : "+ExceSocket.getMessage());
		} 
		catch (UnknownHostException ExceHost)
		{
			System.out.println("Socket host unknown : "+ExceHost.getMessage());
		}
	}
 
	public void connectToServer()
	{
		DatagramPacket theSendPacket;
		DatagramPacket theReceivedPacket;
		InetAddress theServerAddress;
		byte[] outBuffer;
		byte[] inBuffer;
 
		// the place to store the sending and receiving data
		inBuffer = new byte[500];
		outBuffer = new byte[50];
		try {
			String message = "genux";
			outBuffer = message.getBytes();
 
			System.out.println("Message sending is : " + message);
 
			// the server details
			theServerAddress = theSocket.getLocalAddress();
 
			// build up a packet to send to the server
			theSendPacket = new DatagramPacket(outBuffer, outBuffer.length, theServerAddress, serverPort);
			// send the data
			theSocket.send(theSendPacket);
 
			// get the servers response within this packet
			theReceivedPacket = new DatagramPacket(inBuffer, inBuffer.length);
			theSocket.receive(theReceivedPacket);
 
			// the server response is...
			System.out.println("Client - server response : "+new String(theReceivedPacket.getData(), 0, theReceivedPacket.getLength()));
			theSocket.close();
		} catch (IOException ExceIO)
		{
			System.out.println("Client getting data error : "+ExceIO.getMessage());
		}
	}
 
	public static void main(String[] args)
	{
		UDPClient theClient = new UDPClient();
		theClient.connectToServer();
	}
}