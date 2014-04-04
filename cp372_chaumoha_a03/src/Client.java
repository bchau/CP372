import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;

class Client extends Thread {
	private final static int STATE_RUN = 0, STATE_PAUSE = 2, STATE_STOP = 3;
	private int _state;
	private Socket socket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private WhiteBoard wb = null;
	private JEditorPane outText;
	private String fromServer = "";
	private String password = "PASSWORD;ENDPASSWORD";
	private boolean retry = true;

	/**
	 * Create a new Client, which by defninition interacts with a server
	 * 
	 * @param s
	 *            - a socket on which to communicate
	 * @param i
	 *            - Input jtextarea
	 * @param outputArea
	 *            - output jtextarea
	 * @throws IOException
	 *             - thrown if we can not create a buffered reader or print
	 *             writer
	 */
	public Client(Socket s, JEditorPane outputArea, WhiteBoard wb)
			throws IOException {
		super();
		this.wb = wb;
		socket = s;
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		outText = outputArea;
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
					if (fromServer.startsWith("LINE")) {
						wb.drawLine(Line.parseLine(fromServer));
					} else if (fromServer.startsWith("MESSAGE")) {
						wb.printMessage(fromServer);
					} else if (fromServer.startsWith("CLEAR")) {
						wb.clear();
					} else if (fromServer.startsWith("OK")) {
						retry = false;
						wb.setTextColour(fromServer);
					} else if (fromServer.startsWith("PASSWORDREQUEST")) {
						if (this.password.split(";").length != 3 || retry) {
							JPasswordField pf = new JPasswordField();
							int okCxl = JOptionPane.showConfirmDialog(null, pf,
									"White Board Password",
									JOptionPane.OK_CANCEL_OPTION,
									JOptionPane.PLAIN_MESSAGE);
							if (okCxl == JOptionPane.OK_OPTION) {
								this.password = new String(pf.getPassword())
										.trim().replace(';', '\\');
								this.password = "PASSWORD;" + this.password
										+ ";ENDPASSWORD";
								sendData(password);
							} else {
								synchronized (this) {
									_state = STATE_STOP;
								}
								wb.clientDisconnected();
							}
						}
					} else {
						wb.systemAppendOutputArea(fromServer + "\n");
					}
					outText.setCaretPosition(outText.getDocument().getLength());
					if (fromServer.equals("Bye."))
						synchronized (this) {
							_state = STATE_STOP;
						}
					break;
				case STATE_PAUSE:
					yield();
					break;
				}
				synchronized (this) {
					stateTemp = _state;
				}
			}
		} catch (IOException e) {
			//wb.systemAppendOutputArea("Disconnected.\n");
			wb.clientDisconnected();
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
	 * This takes text and sends it to the server
	 */
	public synchronized void sendData(final String request) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				out.println(request);
			}
		});
	}

	public synchronized void sendPassword() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				out.println(password);
			}
		});
	}

}