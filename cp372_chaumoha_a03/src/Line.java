import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
/**
 * Assignment 03 - White Board
 * CP372 2014
 * @author Bryan Chau & Mohamed Mohamedtaki
 *
 */
public class Line implements Serializable{

	private static final long serialVersionUID = 1L;
	ArrayList<DrawnPoint> points;
	int strokeSize;
	Color colourRGB;
	
	public Line(ArrayList<DrawnPoint> points, int s, Color colourRGB){
		this.points = points;
		this.strokeSize = s;
		this.colourRGB = colourRGB;
	}
	
	public String toString(){
		String result = "LINE,";
		result+=strokeSize+",";
		result+=getColourHex(colourRGB)+";";
		for (int i = 0; i < points.size();i++){
			result+=points.get(i).toString()+";";
		}
		result+="ENDLINE";
		return result;
	}
	public static String getColourHex(Color c){
		return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
	}
	
	public static Color getColourFromHex(String colorStr){
		return new Color(
	            Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
	            Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
	            Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
	}
	public static Line parseLine(String s){
		String[] tokens = s.split(";");
		String[] first = tokens[0].split(",");
		int strokeSize = Integer.parseInt(first[1]);
		String colourRGB = first[2];
		
		ArrayList<DrawnPoint> p = new ArrayList<DrawnPoint>();
		for (int i = 1; i < tokens.length-1; i++){
			String[] temp = tokens[i].split(",");
			p.add(new DrawnPoint(Integer.parseInt(temp[0]),Integer.parseInt(temp[1])));
		}
		return new Line(p,strokeSize,getColourFromHex(colourRGB));
	}
}
