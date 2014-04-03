import java.util.ArrayList;


public class WhiteBoardProtocol {
	
	//requests
	public static final int SUBMIT = 0;
	public static final int FAULT = 1;
	
	//keywords
	public static final String[] keywords = {"SUBMIT"};
	
	//lines
	private ArrayList<Line> lines = new ArrayList<Line>();
	
	//responses
	public static final String SUBMIT_SUCCESS = "Book submitted successfully.\n";
	public static final String PARSE_FAIL = "Unable to parse request.\n";
	public static final String GET_FAIL = "Book(s) cannot be found.\n";
	public static final String REMOVE_SUCCESS = "Book(s) removed.\n";
	public static final String REMOVE_FAIL = "Book(s) not found.\n";
	
	/**
	 * 
	 * @param command SUBMIT, GET, or REMOVE a book. Anything else is a parse fail.
	 * @param input The book information preceding the command.
	 * @return Response based on command and input
	 */
	public String processInput(int command, String input){
		String result = "";
		switch(command){
			case SUBMIT:
				//update catalogue
				result = submit(input);
				break;
			case FAULT:
				result = PARSE_FAIL;
				break;
			default:
				break;
		}
		return result;
	}
	
	/**
	 * 
	 * @param input Expecting the TITLE followed by the actualy title, AUTHOR followed by author, and LOCATION followed by location.
	 * @return Response based on SUBMIT command
	 */
	private String submit(String input){
		
		Line line = parseInput(input);
		if (line == null){
			return PARSE_FAIL;
		}
		else{
			lines.add(line);
			return SUBMIT_SUCCESS;
		}	
	}
	
	/**
	 * 
	 * @param input The string that is checked to be SUBMIT, GET or REMOVE
	 * @return true if it is one of the three, false if not.
	 */
	public static boolean isKeyword(String input){
		for (int i = 0; i < keywords.length; i++){
			if (input.equals(keywords[i]))
				return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param input Text to be converted into a book if proper format.
	 * @return a Book, or null if not proper format.
	 */
	private Line parseInput(String input){
		return Line.deserialize(input);
	}
}
