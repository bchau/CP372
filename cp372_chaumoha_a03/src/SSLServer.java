import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class SSLServer {
	private static int defaultPort = 4444;
	private static String certPath = "WBPkeystore";

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err
					.println("To run the program please enter valid parameters as:");
			System.err.println("prog <PORT>");
			System.exit(1); // exit out
		}
		char[] passphrase = "wbp123".toCharArray();
		KeyStore keyStore = null;
		TrustManagerFactory tmf = null;
		SSLContext ctx = null;
		SSLServerSocketFactory sslserversocketfactory = null;
		try {
			keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(new FileInputStream(certPath), passphrase);

			tmf = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(keyStore);
			ctx = SSLContext.getInstance("SSL");
			ctx.init(null, tmf.getTrustManagers(), null);
			sslserversocketfactory = ctx.getServerSocketFactory();
		} catch (IOException e) {
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SSLServerSocket sslserversocket = null;
		int portnum;
		ArrayList<ClientConnection> connections = new ArrayList<ClientConnection>();

		try { // try to convert to a port
			portnum = Integer.parseInt(args[0]); // portnumber entered by
													// commandline if any
		} catch (Exception e) {
			System.err.println("Invalid port entered, attempting default port "
					+ defaultPort);
			portnum = defaultPort;
		}
		try {
			sslserversocket = (SSLServerSocket) sslserversocketfactory
					.createServerSocket(defaultPort);
			System.out.println("Running on port "
					+ sslserversocket.getLocalPort());
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
		} catch (Exception e) {
			System.err.println("Error creating socket, exiting...");
			System.exit(1);
		}
		WhiteBoardProtocol p = new WhiteBoardProtocol();
		while (true) {
			try {
				connections.add(new ClientConnection(
						(SSLSocket) sslserversocket.accept(), p));
			} catch (IOException e) {
				System.err.println("An error has occured. Exiting.");
				try {
					sslserversocket.close();
				} catch (IOException ioe) {
					System.exit(1);
				}
				break;
			}
		}
		System.exit(0);
	}

	private static class ClientConnection extends Thread {
		private SSLSocket clientSocket;
		private String inputLine, outputLine;
		private WhiteBoardProtocol protocol;
		private BufferedReader in;
		private PrintWriter out;

		public ClientConnection(SSLSocket s, WhiteBoardProtocol p) {
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
			boolean validated = false;
			try {
				while ((inputLine = in.readLine()) != null || code != -1) {
					try {
						if (inputLine.startsWith("SUBMIT")) {
							code = WhiteBoardProtocol.SUBMIT;
						} else if (inputLine.startsWith("GET")) {
							//code = WhiteBoardProtocol.GET;
						} else if (inputLine.startsWith("REMOVE")) {
							//code = WhiteBoardProtocol.REMOVE;
						} else {
							code = WhiteBoardProtocol.FAULT;
						}

						String temp;
						while (in.ready()
								&& !WhiteBoardProtocol.isKeyword(temp = in
										.readLine())) {
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
