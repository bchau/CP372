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
		String result = "LINE,";
		result+=strokeSize+",";
		result+=colourRGB+";";
		for (int i = 0; i < points.size();i++){
			result+=points.get(i).toString()+";";
		}
		result+="ENDLINE";
		return result;
	}
	
	public static Line parseLine(String s){
		String[] tokens = s.split(";");
		String[] first = tokens[0].split(",");
		int strokeSize = Integer.parseInt(first[1]);
		String colourRGB = first[2];
		
		ArrayList<DrawnPoint> p = new ArrayList<DrawnPoint>();
		for (int i = 1; i < tokens.length; i++){
			String[] temp = tokens[i].split(",");
			p.add(new DrawnPoint(Integer.parseInt(temp[0]),Integer.parseInt(temp[1])));
		}
		return new Line(p,strokeSize,colourRGB);
	}
}
