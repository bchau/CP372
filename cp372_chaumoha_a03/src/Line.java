import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;


public class Line implements Serializable{

	private static final long serialVersionUID = 1L;
	ArrayList<DrawnPoint> points;
	int strokeSize;
	String colourRGB;
	
	public Line(ArrayList<DrawnPoint> points, int s, String colourRGB){
		this.points = points;
		this.strokeSize = s;
		this.colourRGB = colourRGB;
	}
	
	public String toString(){
		String result = "LINE;";
		result+=strokeSize+";";
		result+=colourRGB+";";
		for (int i = 0; i < points.size();i++){
			result+=points.get(i).toString()+";";
		}
		result+="ENDLINE";
		return result;
	}
}
