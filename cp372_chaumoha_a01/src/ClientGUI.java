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
		new ClientGUI("CP372 A01 Client - chau3120 moha7220", 500, 700);
	}
	
	private class ClientGUIPanel extends JPanel {
		private JTextField ipField, portField;
		private JTextArea inputArea, outputArea;
		private JButton sendButton;
		private JToggleButton connectToggle;
		private JLabel ipLabel, portLabel, inputLabel, resultLabel;
		
		public ClientGUIPanel () {
			super();
			ipLabel = new JLabel("IP:");
			portLabel = new JLabel("Port:");
			inputLabel = new JLabel("Server Request:");
			resultLabel = new JLabel("Server Result:");
			
			ipField = new JTextField(15);
			portField = new JTextField(3);
			
			inputArea = new JTextArea();
			outputArea = new JTextArea();
			
			sendButton = new JButton("Send");
			connectToggle = new JToggleButton("Connect");
			
			init();
		}
		
		private void init() {
			this.setLayout(new BorderLayout());
			
			JPanel connectionPane = new JPanel();
			connectionPane.setLayout(new FlowLayout());
			connectionPane.add(ipLabel);
			connectionPane.add(ipField);
			connectionPane.add(portLabel);
			connectionPane.add(portField);
			connectionPane.add(connectToggle);
			this.add(connectionPane, BorderLayout.NORTH);
			
			JPanel interactionPane = new JPanel();
			interactionPane.setLayout(new BoxLayout(interactionPane, BoxLayout.Y_AXIS));
			interactionPane.add(inputLabel);
			interactionPane.add(inputArea);
			JPanel sendBPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			sendBPanel.add(sendButton);
			interactionPane.add(sendBPanel);
			interactionPane.add(resultLabel);
			interactionPane.add(resultLabel);
			interactionPane.add(outputArea);
			this.add(interactionPane, BorderLayout.CENTER);
			//this.add(ipField, BorderLayout.NORTH);
		}
	}
}
