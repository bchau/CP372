
public class Protocol {
	public static final int NEW = 0;
	public static final int SUBMIT = 1;
	public static final int GET = 2;
	public static final int REMOVE = 3;
	public static final String[] keywords = {"submit","get","remove"};
	
	public String processInput(int command, String input){
		String result = "";
		switch(command){
			case NEW:
				result = "Server has been connected.";
				break;
			case SUBMIT:
				//update catalogue
				result = submit(input);
				break;
			case GET:
				//send book list to client where is available
				result = get(input);
				break;
			case REMOVE:
				//remove
				result = remove(input);
				break;
			default:
				break;
		}
		return result;
	}
	
	private String submit(String input){
		return "submit";
	}
	
	private String get(String input){
		return "get";
	}
	
	private String remove(String input){
		return "remove";
	}
	
	public static boolean isKeyword(String input){
		for (int i = 0; i < keywords.length; i++){
			if (input.toLowerCase().equalsIgnoreCase(keywords[i]))
				return true;
		}
		return false;
	}
}
