import java.awt.Dimension;

import javax.swing.JFrame;


public class ClientGUI extends JFrame{
	public ClientGUI () {
		super();
	}
	public ClientGUI (String title, int width, int height) {
		super(title);
		this.setSize (new Dimension (width, height));
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	public static void main(String args[]) {
		new ClientGUI("CP372 A01 Client - chau3120 moha7220", 500, 700);
	}
}
