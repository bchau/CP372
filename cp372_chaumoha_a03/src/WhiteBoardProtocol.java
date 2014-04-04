import java.util.ArrayList;


public class WhiteBoardProtocol {
	
	//requests
	public static final int LINE = 0;
	public static final int WBPASSWORD = 1;
	public static final int FAULT = 3;
	
	//keywords
	public static final String[] keywords = {"SUBMIT"};
	
	//lines
	private ArrayList<Server.ClientConnection> clients = new ArrayList<Server.ClientConnection>();
	
	//responses
	public static final String LINE_SUCCESS = "Line submitted successfully.\n";
	public static final String PARSE_FAIL = "Unable to parse request.\n";
	
	/**
	 * 
	 * @param command SUBMIT, GET, or REMOVE a book. Anything else is a parse fail.
	 * @param input The book information preceding the command.
	 * @return Response based on command and input
	 */
	public String processInput(int command, String input){
		String result = "";
		switch(command){
			case LINE:
				//parseLine(input);
				break;
			case FAULT:
				result = PARSE_FAIL;
				break;
			default:
				break;
		}
		return result;
	}
	
	public void addClient(Server.ClientConnection c) {
		clients.add(c);
	}
	
	public synchronized void notifyClients(String message, Server.ClientConnection sender) {
		for (Server.ClientConnection c : clients) {
			try {
				if (!sender.equals(c)) {
					c.send(message);
				}
			} catch(Exception e) {
				continue;
			}
		}
	}
}
	
	
