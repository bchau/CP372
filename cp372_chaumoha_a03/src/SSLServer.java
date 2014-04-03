import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class SSLServer {
	private static int defaultPort = 4444;
	public static void main(String[] args) {
		try {
			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
					.getDefault();
			SSLServerSocket sslserversocket = null;
			int portnum;
			ArrayList<ClientConnection> connections = new ArrayList<ClientConnection>();
			
			try { // try to convert to a port
				portnum = Integer.parseInt(args[0]); // portnumber entered by
														// commandline if any
			} catch (Exception e) {
				System.err
						.println("Invalid port entered, attempting default port " + defaultPort);
				portnum = defaultPort;
			}
			try {
				sslserversocket = (SSLServerSocket) sslserversocketfactory
						.createServerSocket(defaultPort);
				System.out.println("Running on port " + sslserversocket.getLocalPort());
			} catch (IOException e) {
				System.err.println("Could not listen on port " + portnum + ".");
				try { // try again on error
					sslserversocket = (SSLServerSocket) sslserversocketfactory
							.createServerSocket(0);
					System.out.println("Listening on port "
							+ sslserversocket.getLocalPort() + ".");
				} catch (IOException ioe) { // exit if we are unable to allocate a
											// port
					System.err.println("Could not listen on port. Exiting...");
					System.exit(1);
				}
			}
			SSLSocket sslsocket = (SSLSocket) sslserversocket.accept();

			InputStream inputstream = sslsocket.getInputStream();
			InputStreamReader inputstreamreader = new InputStreamReader(
					inputstream);
			BufferedReader bufferedreader = new BufferedReader(
					inputstreamreader);

			String string = null;
			while ((string = bufferedreader.readLine()) != null) {
				System.out.println(string);
				System.out.flush();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	private static class ClientConnection extends Thread{
		private Socket clientSocket;
		private PrintWriter out;
		private String inputLine, outputLine;
		private WhiteBoardProtocol protocol;
		private BufferedReader in;

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
		}

		public void run() {
			int code = -1;
			try {
				while ((inputLine = in.readLine()) != null || code != -1) {
					try {
						if (inputLine.startsWith("SUBMIT")) {
							code = WhiteBoardProtocol.SUBMIT;
						} else if (inputLine.startsWith("GET")) {
							code = WhiteBoardProtocol.GET;
						} else if (inputLine.startsWith("REMOVE")) {
							code = WhiteBoardProtocol.REMOVE;
						} else {
							code = WhiteBoardProtocol.FAULT;
						}

						String temp;
						while (in.ready()
								&& !WhiteBoardProtocol.isKeyword(temp = in.readLine())) {
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
