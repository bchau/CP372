import java.awt.Point;
import java.io.Serializable;
/**
 * Assignment 03 - White Board
 * CP372 2014
 * @author Bryan Chau & Mohamed Mohamedtaki
 *
 */
public class DrawnPoint extends Point implements Serializable{
	private static final long serialVersionUID = 1L;

	public DrawnPoint(int x, int y){
		super(x,y);
	}

	public DrawnPoint(Point p){
		super(p);
	}
	
	public String toString(){
		return ""+x+","+y;
	}
}