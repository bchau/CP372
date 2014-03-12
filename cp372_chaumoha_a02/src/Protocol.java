import java.io.FileWriter;
import java.io.IOException;


public class Protocol {
	
	public static String SAVE_SUCCESS = "SAVE_SUCCESS";
	public static String SAVE_FAIL = "SAVE_FAIL";
	
	public static String saveInput(String fileName, byte[] input){
		String result = new String(input, 0, input.length);
		try {
			FileWriter writer = new FileWriter(fileName,true);
			writer.append(result);
			writer.close();
		} catch (IOException e) {
			result = SAVE_FAIL;
		}
		return result;
	}
}
