import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;


public class Line implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int size;
	ArrayList<DrawnPoint> points;
	
	public Line(ArrayList<DrawnPoint> points, int size){
		this.size = size;
		this.points = points;
	}
	
	public static String serialize(DrawnPoint point) {
	    try {
	        ByteArrayOutputStream bo = new ByteArrayOutputStream();
	        ObjectOutputStream so = new ObjectOutputStream(bo);
	        so.writeObject(point);
	        so.flush();
	        // This encoding induces a bijection between byte[] and String (unlike UTF-8)
	        return bo.toString("ISO-8859-1");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	public static DrawnPoint deserialize(String str) {
	    // deserialize the object
	    try {
	        // This encoding induces a bijection between byte[] and String (unlike UTF-8)
	        byte b[] = str.getBytes("ISO-8859-1"); 
	        ByteArrayInputStream bi = new ByteArrayInputStream(b);
	        ObjectInputStream si = new ObjectInputStream(bi);
	        return (DrawnPoint)si.readObject();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		return null;
	}
}
