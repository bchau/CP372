import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class WhiteBoardProtocol {

	// requests
	public static final int PASSWORD = 0;
	public static final int LINE = 1;
	public static final int CLEAR = 2;
	public static final int MESSAGE = 3;
	public static final int FAULT = 4;

	// keywords
	public static final String[] keywordsStart = { "PASSWORD", "LINE", "CLEAR",
			"MESSAGE" };
	public static final String[] keywordsEnd = { "ENDPASSWORD", "ENDLINE",
			"ENDCLEAR", "ENDMESSAGE" };

	// connections
	private ArrayList<Server.ClientConnection> clients = new ArrayList<Server.ClientConnection>();
	private ArrayList<Line> lines = new ArrayList<Line>();

	// responses
	public static final String LINE_SUCCESS = "Line submitted successfully.\n";
	public static final String PARSE_FAIL = "Unable to parse request.\n";

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
		synchronized (this) {
			switch (command) {
			case PASSWORD:
				if (!password.equals(getPassword(input)))
					return false;
				break;
			case LINE:
				Line l = Line.parseLine(input);
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
			default:
				break;
			}
		}
		if (result != "")
			notifyClients(result, c);
		return true;
	}

	public void addClient(Server.ClientConnection c) {
		clients.add(c);
	}

	public synchronized void notifyClients(String message,
			Server.ClientConnection sender) {
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

	public synchronized void addLine(Line l) {
		lines.add(l);
	}

	public void sendImage(final Server.ClientConnection c) {
		new Runnable() {

			@Override
			public void run() {
				int i = 0;
				int aSize = 0;
				boolean go = true;
				synchronized (this) {
					aSize = lines.size();
				}
				while (i < aSize && go) {
					synchronized (this) {
						c.send(lines.get(i).toString());
						aSize = lines.size();
						i++;
					}
				}

			}
		};
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
}
