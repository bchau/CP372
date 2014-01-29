import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;


public class ClientGUI extends JFrame{
	public ClientGUI () {
		super();
	}
	public ClientGUI (String title, int width, int height) {
		super(title);
		this.setContentPane(new ClientGUIPanel());
		this.setSize (new Dimension (width, height));
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
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
