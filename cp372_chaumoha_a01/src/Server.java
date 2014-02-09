import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) throws IOException {
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
			System.out.println("Running on port " + serverSocket.getLocalPort());
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
		while (true) {
			try {
				try { // accept new connections every time and handle them synchronously.
					new ClientConnection(serverSocket.accept()).run();
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

	private static class ClientConnection implements Runnable {
		private Socket clientSocket;
		private PrintWriter out;
		private String inputLine, outputLine;
		private Protocol protocol;
		private BufferedReader in;

		public ClientConnection(Socket s) {
			this.clientSocket = s;
			try {
				this.out = new PrintWriter(clientSocket.getOutputStream(), true);
				this.in = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));
				this.protocol = new Protocol();
			} catch (IOException e) {
				System.err.println("Error creating connection to client.");
			}
		}

		public void run() {
			int code = -1;
			try {
				while ((inputLine = in.readLine()) != null || code != -1) {
					try {
						if (inputLine.startsWith("SUBMIT")) {
							code = Protocol.SUBMIT;
						} else if (inputLine.startsWith("GET")) {
							code = Protocol.GET;
						} else if (inputLine.startsWith("REMOVE")) {
							code = Protocol.REMOVE;
						} else {
							code = Protocol.FAULT;
						}

						String temp;
						while (in.ready()
								&& !Protocol.isKeyword(temp = in.readLine())) {
							inputLine += " " + temp;
						}

						outputLine = protocol.processInput(code, inputLine);
						out.println(outputLine);

					} catch (NullPointerException e) { // on error end run
						System.err.println("Client was disconnected.");
						clientSocket.close();
						break;
					}
				}
			} catch (IOException e) {
				System.err.println("Client was disconnected.");
			}
		}
	}
}
