import java.io.*;
import java.net.*;
 
public class UDPServer {
 
	DatagramSocket theSocket = null;
	int serverPort = 9999;
 
	public UDPServer()
	{
		try {
			// create the server UDP end point
			theSocket = new DatagramSocket(serverPort);
 
			System.out.println("UDP Socket (end point) created");
		} catch (SocketException ExceSocket)
		{
			System.out.println("Socket creation error : "+ ExceSocket.getMessage());
		}
	}
 
	public void clientRequest()
	{
		DatagramPacket theRecievedPacket;
		DatagramPacket theSendPacket;
		InetAddress clientAddress;
		int clientPort;
		byte[] outBuffer;
		byte[] inBuffer;
 
		// create some space for the text to send and recieve data 
		outBuffer = new byte[500];
		inBuffer = new byte[50];
 
		try {
			// create a place for the client to send data too
			theRecievedPacket = new DatagramPacket(inBuffer, inBuffer.length);
			// wait for a client to request a connection
			theSocket.receive(theRecievedPacket);
			System.out.println("Client connected");
 
			// get the client details
			clientAddress = theRecievedPacket.getAddress();
			clientPort = theRecievedPacket.getPort();
 
			String message = "Server - client sent : " + new String(theRecievedPacket.getData(),0, theRecievedPacket.getLength());
			outBuffer = message.getBytes();
 
			System.out.println("Client data sent ("+message+")");
			// send some data to the client
			theSendPacket = new DatagramPacket(outBuffer, outBuffer.length, clientAddress, clientPort);
			theSocket.send(theSendPacket);
 
		} catch (IOException ExceIO)
		{
			System.out.println("Error with client request : "+ExceIO.getMessage());
		}
		// close the server socket
		theSocket.close();
	}
 
	public static void main(String[] args)
	{
		UDPServer theServer = new UDPServer();
		theServer.clientRequest();
	}
}