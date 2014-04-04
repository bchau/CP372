import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;zzz

import javax.swing.JEditorPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

class Client extends Thread{
    	private final static int STATE_RUN = 0, STATE_PAUSE = 2, STATE_STOP = 3;
    	private int _state;
    	private Socket socket = null;
    	private PrintWriter out = null;
    	private BufferedReader in = null;
    	private WhiteBoard wb = null;
    	private JEditorPane outText;
    	private JTextField inText;
    	private String fromServer = "", fromUser = "";
    	public final String SEND_PASSWORD = "PASSWORD;PASSWORD;ENDPASSWORD";
    	/**
    	 * Create a new Client, which by defninition interacts with a server
    	 * @param s - a socket on which to communicate
    	 * @param i - Input jtextarea
    	 * @param outputArea - output jtextarea
    	 * @throws IOException - thrown if we can not create a buffered reader or print writer
    	 */
    	public Client(Socket s, JEditorPane outputArea,WhiteBoard wb) throws IOException {
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
    					System.out.println(fromServer);
    					if (fromServer == null)
    						break;
    					if (fromServer.startsWith("LINE")){
    						wb.drawLine(Line.parseLine(fromServer));
    					}
    					else if(fromServer.startsWith("MESSAGE")){
    						//TODO:MESSAGE Handle from server
    					}
    					else if(fromServer.startsWith("CLEAR")){
    						wb.clear();
    					}
    					else{
    						outText.setText(outText.getText()+"\n"+fromServer);
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
    			outText.setText(outText.getText()+"\n"+fromServer  );
    		} catch (IOException e) {
    			_state = STATE_STOP;
    			outText.setText(outText.getText()+"Server disconnected."+"\n");
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
    	
    	
    	
    }