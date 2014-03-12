import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JTextArea;
/**
 * Client thread to continuous interaction with the server
 * @author Bryan Chau & Mohamed Mohamedtaki
 *
 */
public class Client extends Thread {
	private final static int STATE_RUN = 0, STATE_PAUSE = 2, STATE_STOP = 3;
	private int _state;
	private static Socket socket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;

	private String fromServer = "", fromUser = "";
	private static Client client;
	
	/**
	 * Create a new Client, which by defninition interacts with a server
	 * @param s - a socket on which to communicate
	 * @param i - Input jtextarea
	 * @param o - output jtextarea
	 * @throws IOException - thrown if we can not create a buffered reader or print writer
	 */
	public Client(Socket s) throws IOException {
		super();
		socket = s;
		out = new PrintWriter(System.out, true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		_state = STATE_RUN;
		this.start();
		System.out.println("Client: Connected");
	}
	/**
	 * On every pass get a message from the server
	 */
	public void run() {
		int stateTemp;
		// check state at every pass
		synchronized (this) {
			stateTemp = _state;
		}
		try {
			while (stateTemp != STATE_STOP) {
				switch (stateTemp) {
				case STATE_RUN:
					fromServer = in.readLine();
					if (fromServer == null)
						break;
					// add to the output text area
					System.out.println(fromServer + "\n");
					//outText.setCaretPosition(outText.getDocument().getLength());
					if (fromServer.equals("Bye."))
						break;
					break;
				case STATE_PAUSE:
					yield();
					break;
				}
				synchronized (this) {
					stateTemp = _state;
				}
			}
			System.out.println(fromServer + "\n");
		} catch (IOException e) {
		}

	}
	/*
	 * Because the stop method is static final we use this to set state to stop
	 */
	public synchronized void tStop() {
		_state = STATE_STOP;
		System.out.println("Client: Disconnected");
		// may need to call interrupt() if the processing calls blocking
		// methods.
	}
	/**
	 * Sets this thread's state to pause
	 */
	public synchronized void pause() {
		_state = STATE_PAUSE;
		// may need to call interrupt() if the processing calls blocking
		// methods.
		// perhaps set priority very low with setPriority(MIN_PRIORITY);
	}
	/**
	 * Unpauses this thread by setting state to run
	 */
	public synchronized void unpause() {
		_state = STATE_RUN;
		// perhaps restore priority with setPriority(somePriority);
		// may need to re-establish any blocked calls interrupted by pause()
	}
	/**
	 * Main client method, create a GUI JFrame here and use GUIPanel as the pane
	 * @param args from the command line, unused here.
	 * @throws Exception 
	 */
	public static void main(String args[]) throws Exception {
		//JFrame frame = new JFrame("CP372 A01 Client - chau3120 moha7220");
		//frame.setContentPane(new ClientGUIPanel());
		//frame.setSize (new Dimension (400, 550));
		///frame.setResizable(false);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setLocationRelativeTo(null);
		//frame.setVisible(true);
		try {
			socket = new Socket(args[0], new Integer(args[1]));
		} catch (Exception e) {
			try{
				socket = new Socket("127.0.0.1", 999);
			}
			catch(NumberFormatException| IOException er){
				System.out.println("Connection refused");
			}
		}
		if (socket != null){
			try {
				client = new Client(socket);
				System.out.println("Connected.\n\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Could not create connection, error with host");
			}
		}

	}
}