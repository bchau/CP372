import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
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

		// init i/o area
		inputArea = new JTextArea(10, 50);
		inputArea.setLineWrap(true);
		outputArea = new JTextArea(10, 50);
		outputArea.setLineWrap(true);
		outputArea.setEditable(false);

		// init buttons and events
		sendButton = new JButton("Send");
		sendButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				
			}
		});
		connectToggle = new JToggleButton("Connect");
		connectToggle.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				connectDisconnect();
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				
			}
		});

		// create layout
		JPanel connectionPane = new JPanel();
		connectionPane.setLayout(new FlowLayout());
		connectionPane.add(ipLabel);
		connectionPane.add(ipField);
		connectionPane.add(portLabel);
		connectionPane.add(portField);
		connectionPane.add(connectToggle);
		this.add(connectionPane, BorderLayout.NORTH);
		JPanel interactionPane = new JPanel();
		interactionPane.setLayout(new BoxLayout(interactionPane,
				BoxLayout.Y_AXIS));
		JPanel inputLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		inputLabelPanel.add(inputLabel);
		interactionPane.add(inputLabelPanel);
		interactionPane.add(new JScrollPane(inputArea));
		JPanel sendBPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		sendBPanel.add(sendButton);
		interactionPane.add(sendBPanel);
		JPanel resultLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		resultLabelPanel.add(resultLabel);
		interactionPane.add(resultLabelPanel);
		interactionPane.add(new JScrollPane(outputArea));
		this.add(interactionPane, BorderLayout.CENTER);
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
	
}