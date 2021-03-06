import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * Assignment 03 - White Board
 * CP372 2014
 * @author Bryan Chau & Mohamed Mohamedtaki
 *
 */
public class Server {

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err
					.println("To run the program please enter valid parameters as:");
			System.err.println("prog <PORT>");
			System.exit(1); // exit out
		}
		ServerSocket serverSocket = null;
		int portnum;
		try { // try to convert to a port
			portnum = Integer.parseInt(args[0]); // portnumber entered by
													// commandline if any
		} catch (Exception e) {
			System.err
					.println("Invalid port entered, attempting default port 4444.");
			portnum = 4444;
		}
		try {
			serverSocket = new ServerSocket(portnum);
			System.out
					.println("Running on port " + serverSocket.getLocalPort());
		} catch (IOException e) {
			System.err.println("Could not listen on port " + portnum + ".");
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
		String password = "";
		while(password.length() < 4) {
			System.out.println("Please enter a password for the white booard (4 character minimum)");
			password = new BufferedReader(new InputStreamReader(System.in)).readLine().replace(';', '\\');
		}
		WhiteBoardProtocol p = new WhiteBoardProtocol(password);
		System.out.println("Good to go, waiting on new clients.");
		while (true) {
			try {
				try { // accept new connections every time and handle them
						// synchronously.
					ClientConnection c = new ClientConnection(
							serverSocket.accept(), p);
					c.start();
					p.addClient(c);
					System.out.println("Accepted new Client");
				} catch (IOException e) {
					System.err.println("Accept failed, trying again.");
				}
			} catch (Exception e) { // if there is some other error exit
				serverSocket.close();
				System.err.println("An error has occured. Exiting.");
				break;
			}
		}
		System.exit(0);
	}

	public static class ClientConnection extends Thread {
		private Socket clientSocket;
		private PrintWriter out;
		private String inputLine;
		private WhiteBoardProtocol protocol;
		private BufferedReader in;
		private Color colour = null;
		private boolean valid = false;

		public ClientConnection(Socket s, WhiteBoardProtocol p) {
			this.clientSocket = s;
			this.protocol = p;
			try {
				this.out = new PrintWriter(clientSocket.getOutputStream(), true);
				this.in = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));
			} catch (IOException e) {
				System.err.println("Error creating connection to client.");
			}
			System.out.println("Client IP: " + clientSocket.getInetAddress()
					+ ":" + clientSocket.getPort());
		}

		public void run() {
			int code = -1;
			try {
				while ((inputLine = in.readLine()) != null || code != -1) {
					try {
						if (valid == false) {
							if (inputLine.startsWith("PASSWORD")
									&& inputLine.endsWith("ENDPASSWORD")) {
								code = WhiteBoardProtocol.PASSWORD;
							} else {
								code = WhiteBoardProtocol.NEEDPASSWORD;
							}
						} else if (inputLine.startsWith("PASSWORD")
								&& inputLine.endsWith("ENDPASSWORD")) {
							code = WhiteBoardProtocol.PASSWORD;
						}else if (inputLine.startsWith("LINE")
								&& inputLine.endsWith("ENDLINE")) {
							code = WhiteBoardProtocol.LINE;
						} else if (inputLine.startsWith("CLEAR")
								&& inputLine.endsWith("ENDCLEAR")) {
							code = WhiteBoardProtocol.CLEAR;
						} else if (inputLine.startsWith("MESSAGE")
								&& inputLine.endsWith("ENDMESSAGE")) {
							code = WhiteBoardProtocol.MESSAGE;
						} else {
							code = WhiteBoardProtocol.FAULT;
						}
						boolean res = this.protocol.processInput(code,
								inputLine, this);
						if (valid == false)
							valid = res;
					} catch (NullPointerException e) { // on error end run
						System.err.println("Client was disconnected.");
						clientSocket.close();
						break;
					}
				}
			} catch (IOException e) {
				System.err.println("Client was disconnected.");
			}
			this.protocol.clientDisconnected(this);
		}

		public boolean equals(ClientConnection o) {
			return this.clientSocket.equals(o.clientSocket);
		}

		public void send(String message) {
			//System.out.println("Message to [" + this.clientSocket.getInetAddress() + "]: " + message);
			out.println(message);
		}

		public void setColour(Color c) {
				this.colour = c;
				this.send("OK;" + Line.getColourHex(this.colour) + ";ENDOK");
		}
		public boolean isVaidated() {
			return this.valid;
		}
	}
}
