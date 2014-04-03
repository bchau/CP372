import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JTextArea;

class Client extends Thread{
    	private final static int STATE_RUN = 0, STATE_PAUSE = 2, STATE_STOP = 3;
    	private int _state;
    	private Socket socket = null;
    	private PrintWriter out = null;
    	private BufferedReader in = null;
    	private JTextArea inText, outText;
    	private String fromServer = "", fromUser = "";
    	/**
    	 * Create a new Client, which by defninition interacts with a server
    	 * @param s - a socket on which to communicate
    	 * @param i - Input jtextarea
    	 * @param o - output jtextarea
    	 * @throws IOException - thrown if we can not create a buffered reader or print writer
    	 */
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
    	public synchronized void sendData(String request) {
    		out.println(request);
    	}
    	/**
    	 * Main client method, create a GUI JFrame here and use GUIPanel as the pane
    	 * @param args from the command line, unused here.
    	 */
    	
    }