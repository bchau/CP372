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
	private static char[] passphrase = "wbp123".toCharArray();

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err
					.println("To run the program please enter valid parameters as:");
			System.err.println("prog <PORT>");
			System.exit(1); // exit out
		}
		
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
			ctx.init(null, tmf.getTrustManagers(), new java.security.SecureRandom());
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
				ClientConnection c = new ClientConnection(
						(SSLSocket) sslserversocket.accept(), p);
				connections.add(c);
				c.start();
				System.out.println("Accepted new WB Client");
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
			System.out.println("Client IP: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
		}

		public void run() {
			int code = 0;
			boolean validated = false;
			try {
				while (true) {
					try {
						inputLine = in.readLine();
						System.out.println(inputLine);
						out.println("Got Stuff :D");
						if (false) break;
					} catch (Exception e) { // on error end run
						//System.err.println("Client was disconnected.");
						
					}
				}
				clientSocket.close();
			} catch (IOException e) {
				System.err.println("Client was disconnected.");
			}
			System.out.println("Client was disconnected.");
		}
	}
}
