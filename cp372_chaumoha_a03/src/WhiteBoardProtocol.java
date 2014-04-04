import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public class WhiteBoardProtocol {

	// requests
	public static final int NEEDPASSWORD = 0;
	public static final int PASSWORD = 1;
	public static final int LINE = 2;
	public static final int CLEAR = 3;
	public static final int MESSAGE = 4;
	public static final int FAULT = 5;

	// keywords
	public static final String[] keywordsStart = { "PASSWORD", "LINE", "CLEAR",
			"MESSAGE" };
	public static final String[] keywordsEnd = { "ENDPASSWORD", "ENDLINE",
			"ENDCLEAR", "ENDMESSAGE" };

	// connections
	private ArrayList<Server.ClientConnection> clients = new ArrayList<Server.ClientConnection>();
	private ArrayList<Line> lines = new ArrayList<Line>();

	// responses
	public static final String PASSWORD_REQUEST = "PASSWORDREQUEST;Please enter the correct password.;ENDREQUESTPASSWORD";

	private String password = "";

	public WhiteBoardProtocol(String pass) {
		password = pass;
	}

	/**
	 * 
	 * @param command
	 *            SUBMIT, GET, or REMOVE a book. Anything else is a parse fail.
	 * @param input
	 *            The book information preceding the command.
	 * @return Response based on command and input
	 */
	public boolean processInput(int command, String input,
			Server.ClientConnection c) {
		String result = "";
		switch (command) {
		case PASSWORD:
			if (password.equals(getPassword(input))) {
				c.setColour(newClientColour());
				sendImage(c);
				break;
			}
		case NEEDPASSWORD:
			c.send(PASSWORD_REQUEST);
			return false;
		case LINE:
			Line l = Line.parseLine(input);
			lines.add(l);
			result = l.toString();
			break;
		case CLEAR:
			result = "CLEAR;ENDCLEAR";
			clearImage();
			break;
		case MESSAGE:
			result = input;
			break;
		case FAULT:
			result = "";
			return false;
		default:
			break;
		}

		System.out.println("Result: " + result);
		System.out.println("Input: " + input);
		if (result != "")
			notifyClients(result, c);
		return true;
	}

	public void addClient(Server.ClientConnection c) {
		clients.add(c);
	}

	public void notifyClients(final String message,
			final Server.ClientConnection sender) {
		Thread t = new Thread() {
			@Override
			public void run () {
				for (Server.ClientConnection c : clients) {
					try {
						if (!sender.equals(c)) {
							c.send(message);
						}
					} catch (Exception e) {
						continue;
					}
				}
			}
		};
		t.start();
	}

	public synchronized void addLine(Line l) {
		lines.add(l);
	}

	public void sendImage(final Server.ClientConnection c) {
		Thread t = new Thread(){
			@Override
			public void run() {
				int i = 0;
				int aSize = 0;
				synchronized (lines) {
					aSize = lines.size();
				}
				while (i < aSize) {
					synchronized (lines) {
						String line = lines.get(i).toString();
						System.out.println("Update: " + line);
						c.send(line);
						c.send(lines.get(i).toString());
						aSize = lines.size();
						i++;
					}
				}

			}
		};
		t.start();
	}

	public synchronized void clearImage() {
		lines.clear();
	}

	public String getPassword(String in) {
		String[] temp = in.split(";");
		if (temp.length == 3)
			return temp[1];
		return "";
	}

	public Color newClientColour() {
		Random rand = new Random();
		float h = rand.nextFloat(), s = (float) 0.5, b = (float) 0.5;
		return Color.getHSBColor(h, s, b);
	}
}
