import java.io.FileWriter;
import java.io.IOException;


public class Protocol {
	
	public static String SAVE_SUCCESS = "SAVE_SUCCESS";
	public static String SAVE_FAIL = "SAVE_FAIL";
	
	public static String saveInput(String fileName, String input){
		String result = "";
		try {
			FileWriter writer = new FileWriter(fileName,true);
			writer.append(input);
			writer.close();
		} catch (IOException e) {
			result = SAVE_FAIL;
		}
		return result;
	}
}
