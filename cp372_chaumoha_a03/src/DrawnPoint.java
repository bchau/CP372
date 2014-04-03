import java.awt.Point;
import java.io.Serializable;

public class DrawnPoint extends Point implements Serializable{
	

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