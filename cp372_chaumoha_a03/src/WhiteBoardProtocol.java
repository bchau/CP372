import java.util.ArrayList;


public class WhiteBoardProtocol {
	
	//requests
	public static final int LINE = 0;
	public static final int WBPASSWORD = 1;
	public static final int FAULT = 3;
	
	//keywords
	public static final String[] keywords = {"SUBMIT"};
	
	//lines
	private ArrayList<Line> lines = new ArrayList<Line>();
	
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
				parseLine(input);
				break;
			case FAULT:
				result = PARSE_FAIL;
				break;
			default:
				break;
		}
		return result;
	}
	
	public String parseLine(String input){
		try{
			Line line = Line.parseLine(input);
			lines.add(line);
			return LINE_SUCCESS;
		}
		catch(Exception e){
			return PARSE_FAIL;
		}
	}
	
}
	
	
