import java.awt.Dimension;

import javax.swing.JFrame;


public class A01Main {
	public static void main(String args[]) {
		JFrame frame = new JFrame("CP372 A01 Client - chau3120 moha7220");
		frame.setContentPane(new ClientGUIPanel());
		frame.setSize (new Dimension (400, 550));
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
