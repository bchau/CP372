import java.awt.Point;
import java.io.Serializable;

public class DrawnPoint extends Point implements Serializable{
	boolean isFirstPoint;

	public DrawnPoint(int x, int y, boolean isFirstPoint){
		super(x,y);
		this.isFirstPoint = isFirstPoint;
	}

	public DrawnPoint(Point p, boolean isFirstPoint){
		super(p);
		this.isFirstPoint = isFirstPoint;
	}
}