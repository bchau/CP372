import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

@SuppressWarnings("serial")
public class ClientGUIPanel extends JPanel {
	private JTextField ipField, portField;
	private JTextArea inputArea, outputArea;
	private JButton sendButton;
	private JToggleButton connectToggle;
	private JLabel ipLabel, portLabel, inputLabel, resultLabel;

	public ClientGUIPanel() {
		super();
		this.init();
	}

	private void init() {
		this.setLayout(new BorderLayout());

		// init labels
		ipLabel = new JLabel("IP:");
		portLabel = new JLabel("Port:");
		inputLabel = new JLabel("Server Request:");
		resultLabel = new JLabel("Server Result:");

		// init fields
		ipField = new JTextField(15);
		portField = new JTextField(4);

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
				outputArea.append(inputArea.getText());
				inputArea.setText("");
			}
		});
		connectToggle = new JToggleButton("Connect");
		connectToggle.addMouseListener(new MouseListener() {
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
				if (connectToggle.isSelected()) {
					connectToggle.setText("Disconnect");
				} else {
					connectToggle.setText("Connect");
				}
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
}