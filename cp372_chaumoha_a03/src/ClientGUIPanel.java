import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;
/**
 * Client GUI to interact visually with the server
 * Features two JTextAreas and two JInputfields for ip and port number
 * @author Bryan Chau & Mohamed Mohamedtaki
 *
 */
@SuppressWarnings("serial")
public class ClientGUIPanel extends JPanel {
	private JTextField ipField, portField;
	private JTextArea inputArea, outputArea;
	private JButton sendButton;
	private JToggleButton connectToggle;
	private JLabel ipLabel, portLabel, inputLabel, resultLabel;
	private Socket socket = null;
	/**
	 * Call super to make the JPanel abilities available here
	 */
	public ClientGUIPanel() {
		super();
		this.init();
	}
	/**
	 * Initialize the Panel and it's children
	 */
	private void init() {
		this.setLayout(new BorderLayout());

		// init labels
		ipLabel = new JLabel("IP:");
		portLabel = new JLabel("Port:");
		inputLabel = new JLabel("Server Request:");
		resultLabel = new JLabel("Server Result:");

		// init fields
		ipField = new JTextField("127.0.0.1", 15);
		portField = new JTextField("4444", 4);
		
		connectToggle = new JToggleButton();
		connectToggle.setText("connect");

		// create layout
		JPanel connectionPane = new JPanel();
		connectionPane.setLayout(new FlowLayout());
		connectionPane.add(ipLabel);
		connectionPane.add(ipField);
		connectionPane.add(portLabel);
		connectionPane.add(portField);
		connectionPane.add(connectToggle);
		this.add(connectionPane, BorderLayout.NORTH);
		DrawPanel drawPanel = new DrawPanel();

        drawPanel.setBackground(new java.awt.Color(255, 255, 255));
        drawPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        this.add(drawPanel, BorderLayout.CENTER);
        this.setVisible(true);
		
		 
	}
	
	class DrawPanel extends JPanel implements MouseListener{

        DrawPanel() {
            // set a preferred size for the custom panel.
            setPreferredSize(new Dimension(420,420));
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawString("Our Whiteboard", 20, 20);
        }

		@Override
		public void mouseClicked(MouseEvent arg0) {
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
    }
	/**
	 * Determine what must be done to the Toggle Button, setting state and managing connections
	 */
	private void connectDisconnect() {
		if (socket == null) { // if there is no connection, create one.
			String err = null;
			try { // try to determine the optimal connection, on error show a nice dialog
				outputArea.append("Connecting...\n");
				connectToggle.setText("Connecting");
				socket = new Socket(ipField.getText(), new Integer(portField.getText()));
				if (socket != null){}
				else
					throw new Exception("Could not create connection, error with host");
				connectToggle.setText("Disconnect");
				connectToggle.setSelected(false);
				outputArea.append("Connected.\n\n");
				//connectToggle.setSelected(true);
			} catch (UnknownHostException e) {
				connectToggle.setText("Connect");
				connectToggle.setSelected(false);
				err = "Could not find host.";
			} catch (NumberFormatException e) {
				connectToggle.setText("Connect");
				connectToggle.setSelected(false);
				err = "Please ensure port number is correct.";
			} catch (IOException e) {
				connectToggle.setText("Connect");
				connectToggle.setSelected(false);
				err = "Could not connect.";
			} catch (Exception e) {
				connectToggle.setText("Connect");
				connectToggle.setSelected(false);
				err = e.getMessage();
			}
			if (err != null)
				JOptionPane.showMessageDialog(this,
					    err,
					    "Error Connecting",
					    JOptionPane.ERROR_MESSAGE);
		} else {
			try {
				outputArea.append("Disconnecting...\n");
				connectToggle.setText("Disconnecting");
				socket.close();
				socket = null;
				connectToggle.setText("Connect");
				connectToggle.setSelected(false);
				outputArea.append("Disconnected.\n\n");
			} catch (Exception e) {
				connectToggle.setText("Disconnect");
				connectToggle.setSelected(true);
			}

		}
	}

	public static void main(String[] args){
		JFrame frame = new JFrame();
		frame.setContentPane(new ClientGUIPanel());
		frame.setSize (new Dimension (1024, 720));
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
}