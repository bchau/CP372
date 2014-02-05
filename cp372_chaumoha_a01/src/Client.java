import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class Client extends Thread {
	private final static int STATE_RUN = 0, STATE_PAUSE = 2, STATE_STOP = 3;
	private int _state;
	private Socket socket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private JTextArea inText, outText;
	private String fromServer = "", fromUser = "";

	public Client(Socket s, JTextArea i, JTextArea o) throws IOException {
		super();
		socket = s;
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		inText = i;
		outText = o;
		_state = STATE_RUN;
		this.start();
		System.out.println("Client: Connected");
	}

	public void run() {
		int stateTemp;

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
					outText.append(fromServer + "\n");
					outText.setCaretPosition(outText.getDocument().getLength());
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
			outText.append(fromServer + "\n");
		} catch (IOException e) {
		}

	}

	public synchronized void tStop() {
		_state = STATE_STOP;
		System.out.println("Client: Disconnected");
		// may need to call interrupt() if the processing calls blocking
		// methods.
	}

	public synchronized void pause() {
		_state = STATE_PAUSE;
		// may need to call interrupt() if the processing calls blocking
		// methods.
		// perhaps set priority very low with setPriority(MIN_PRIORITY);
	}

	public synchronized void unpause() {
		_state = STATE_RUN;
		// perhaps restore priority with setPriority(somePriority);
		// may need to re-establish any blocked calls interrupted by pause()
	}
	
	public synchronized void sendData() {
		fromUser = inText.getText();
		inText.setText("");
		outText.append(fromUser + "\n");
		outText.setCaretPosition(outText.getDocument().getLength());
		out.println(fromUser);
	}
	
	public static void main(String args[]) {
		JFrame frame = new JFrame("CP372 A01 Client - chau3120 moha7220");
		frame.setContentPane(new ClientGUIPanel());
		frame.setSize (new Dimension (400, 550));
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
