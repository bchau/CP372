import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import javax.net.SocketFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class WhiteBoard {

	/**
	 * Reference for BufferedImage Drawing:
	 * http://stackoverflow.com/questions/12683533
	 * /drawing-a-rectangle-that-wont-disappear-in-next-paint/12683632#12683632
	 */
	// Fields
	private JTextField inputArea, ipField, portField;
	private JEditorPane outputArea;
	private JToggleButton connectToggle;

	// Input Fields
	private JLabel ipLabel, portLabel;

	// Connection
	private Socket socket = null;
	private SocketFactory socketfactory = null;
	private Client client = null;
	private boolean isConnected = true;

	// ImageBuffering
	private BufferedImage canvasImage;
	private Raster tempImageDetails;
	private JLabel imageLabel;
	private JPanel gui;

	// Window Preferences
	private int drawAreaWidth = 640;
	private int drawAreaHeight = 640;

	// WhiteBoard Preferences
	private Color textColour = Color.BLACK;
	private final boolean ENABLED_TEXT_COLOUR_SELECTION = true;
	private BufferedImage colourSample = new BufferedImage(16, 16,
			BufferedImage.TYPE_INT_RGB);
	private JLabel output = new JLabel("White Board");
	private ArrayList<DrawnPoint> points = new ArrayList<DrawnPoint>();
	private ArrayList<DrawnPoint> temp;
	private ArrayList<DrawnPoint> pointsSent;
	private boolean clickHeld = false;
	private boolean clickCanceled = false;
	private int penSize = 3;
	private Stroke stroke = new BasicStroke(penSize, BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_ROUND, 1.7f);

	/**
	 * Creates and populates the graphic user interface.
	 * 
	 * @return a JComponent containing the gui
	 */
	public JComponent getGui() {
		if (gui == null) {
			gui = new JPanel(new BorderLayout());

			// labels
			ipLabel = new JLabel("IP:");
			portLabel = new JLabel("Port:");

			// fields
			ipField = new JTextField("127.0.0.1", 15);
			portField = new JTextField("4444", 4);

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

			// Tool Bar
			JToolBar connectionPane = new JToolBar();
			connectionPane.setLayout(new FlowLayout());
			connectionPane.add(ipLabel);
			connectionPane.add(ipField);
			connectionPane.add(portLabel);
			connectionPane.add(portField);
			connectionPane.add(connectToggle);
			connectionPane.setFloatable(false);
			gui.add(connectionPane, BorderLayout.NORTH);

			// WhiteBoard gui
			BufferedImage defaultImage = new BufferedImage(drawAreaWidth,
					drawAreaHeight, BufferedImage.TYPE_INT_RGB);
			setImage(defaultImage);

			JPanel drawPanel = new JPanel();
			imageLabel = new JLabel(new ImageIcon(canvasImage));
			imageLabel.setPreferredSize(new Dimension(this.drawAreaWidth,
					this.drawAreaHeight));
			imageLabel.addMouseMotionListener(new MouseMotionListener() {
				@Override
				public void mouseDragged(MouseEvent arg0) {
					if (SwingUtilities.isLeftMouseButton(arg0)
							&& clickHeld == true && isConnected) {
						points.add(new DrawnPoint(arg0.getPoint()));
						pointsSent.add(new DrawnPoint(arg0.getPoint()));

						if (points.size() > 1) {
							Point initialPoint = points.get(points.size() - 1);
							Point finalPoint = points.get(points.size() - 2);
							draw(initialPoint, finalPoint, textColour, stroke);
						}
						updateHelpText(arg0.getPoint());
					}
				}

				@Override
				public void mouseMoved(MouseEvent arg0) {
					updateHelpText(arg0.getPoint());
				}
			});
			imageLabel.addMouseListener(new MouseListener() {
				@Override
				public void mousePressed(MouseEvent arg0) {
					if (SwingUtilities.isLeftMouseButton(arg0) && isConnected) {
						clickCanceled = false;
						temp = new ArrayList<DrawnPoint>();
						pointsSent = new ArrayList<DrawnPoint>();
						temp.addAll(points);
						tempImageDetails = canvasImage.getData();
						points.add(new DrawnPoint(arg0.getPoint()));
						pointsSent.add(new DrawnPoint(arg0.getPoint()));

						updateHelpText(arg0.getPoint());
						draw(arg0.getPoint(), null, textColour, stroke);
						synchronized (this) {
							clickHeld = true;
						}
					} else if (SwingUtilities.isRightMouseButton(arg0)
							&& isConnected) {
						if (clickHeld) {
							canvasImage.setData(tempImageDetails);
							imageLabel.repaint();
							points = temp;
							updateHelpText(arg0.getPoint());
							synchronized (this) {
								clickHeld = false;
							}
							clickCanceled = true;
						}
					}
				}

				@Override
				public void mouseClicked(MouseEvent arg0) {
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
					if (SwingUtilities.isLeftMouseButton(arg0) && isConnected) {
						synchronized (this) {
							clickHeld = false;
						}

						String s = new Line(pointsSent, penSize, textColour)
								.toString();

						if (!clickCanceled) {
							try {
								client.sendData(s);
							} catch (Exception e) {
								appendOutputArea("Could not send Data.\n");
							}
						}
					}
				}
			});
			clear(canvasImage, Color.white);

			JScrollPane imageScroll = new JScrollPane(drawPanel);
			drawPanel.add(imageLabel);
			gui.add(imageScroll, BorderLayout.CENTER);

			final SpinnerNumberModel strokeModel = new SpinnerNumberModel(
					penSize, 1, 20, 1);
			JSpinner strokeSize = new JSpinner(strokeModel);
			ChangeListener strokeListener = new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent arg0) {
					Object o = strokeModel.getValue();

					penSize = (Integer) o;
					stroke = new BasicStroke(penSize, BasicStroke.CAP_ROUND,
							BasicStroke.JOIN_ROUND, 1.7f);
				}
			};
			strokeSize.addChangeListener(strokeListener);
			connectionPane.add(strokeSize);

			JButton colourButton = new JButton("Colour");
			colourButton.setToolTipText("Choose a Color");
			if (this.ENABLED_TEXT_COLOUR_SELECTION) {
				ActionListener textColourListener = new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						Color c = JColorChooser.showDialog(gui,
								"Choose a color", textColour);
						if (c != null) {
							textColour = c;
							clear(colourSample, c);
						}
					}
				};
				colourButton.addActionListener(textColourListener);
			}
			colourButton.setIcon(new ImageIcon(colourSample));
			connectionPane.add(colourButton);
			clear(colourSample, textColour);

			JButton clearButton = new JButton("Clear");
			clearButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					clear(canvasImage, Color.WHITE);
					if (client != null)
						client.sendData("CLEAR;ENDCLEAR");
				}

			});
			connectionPane.add(clearButton);

			gui.add(drawPanel);
			gui.add(output, BorderLayout.SOUTH);

			// Text area and input
			JPanel messageBox = new JPanel(new BorderLayout());
			outputArea = new JEditorPane();
			// outputArea.setLineWrap(true);
			outputArea.setEditable(false);
			outputArea.setBackground(Color.LIGHT_GRAY);
			messageBox.add(new JScrollPane(outputArea), BorderLayout.CENTER);

			JPanel inputBox = new JPanel(new FlowLayout());
			inputArea = new JTextField("", 15);
			inputArea.setBackground(Color.LIGHT_GRAY);

			inputBox.add(new JScrollPane(inputArea));
			JButton sendButton = new JButton("SEND");
			sendButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (!inputArea.getText().trim().equals("")) {
						appendOutputArea("You: " + inputArea.getText() + "\n");
						if (client != null) {
							client.sendData("MESSAGE;" + inputArea.getText()
									+ ";ENDMESSAGE");
						}
					}
					inputArea.setText("");
				}

			});
			inputBox.add(sendButton);
			messageBox.add(inputBox, BorderLayout.SOUTH);
			gui.add(messageBox, BorderLayout.EAST);
		}

		return gui;
	}

	/**
	 * Sets the canvas to the contents of image.
	 * 
	 * @param image
	 */
	private void setImage(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		canvasImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = this.canvasImage.createGraphics();
		g.drawImage(image, 0, 0, gui);
		g.dispose();
	}

	/**
	 * Clears the area of the buffered image to the specified colour.
	 * 
	 * @param bi
	 * @param colour
	 */
	private void clear(BufferedImage bi, Color colour) {
		Graphics2D g = bi.createGraphics();
		g.setColor(colour);
		g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
		g.dispose();
		this.imageLabel.repaint();
	}

	public void clear() {
		clear(canvasImage, Color.WHITE);
	}

	/**
	 * Draws a line of 'textColour' between the two points
	 * 
	 * @param initialPoint
	 * @param finalPoint
	 */
	private void draw(Point initialPoint, Point finalPoint, Color c, Stroke s) {
		Graphics2D g = this.canvasImage.createGraphics();
		// g.setRenderingHints(renderingHints);
		g.setColor(c);
		g.setStroke(s);
		if (finalPoint != null)
			g.drawLine(initialPoint.x, initialPoint.y, finalPoint.x,
					finalPoint.y);
		else
			g.drawLine(initialPoint.x, initialPoint.y, initialPoint.x,
					initialPoint.y);
		g.dispose();
		this.imageLabel.repaint();
	}

	/**
	 * Updates the help text, which contains x&y coordinates and information
	 * about the point buffer.
	 * 
	 * @param point
	 */
	private void updateHelpText(Point point) {
		output.setText("X,Y: " + (point.x + 1) + "," + (point.y + 1)
				+ " Points array size: " + points.size());
	}

	/**
	 * Determine what must be done to the Toggle Button, setting state and
	 * managing connections
	 */
	private void connectDisconnect() {

		if (socket == null && client == null) { // if there is no connection,
												// create one.
			
			appendOutputArea("Connecting...\n");
			connectToggle.setText("Connecting");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try { // try to determine the optimal connection, on error
							// show a nice dialog
						socket = new Socket(ipField.getText(), new Integer(
								portField.getText()));
						if (socket != null)
							client = new Client(socket, outputArea,
									WhiteBoard.this);
						else
							throw new Exception(
									"Could not create connection, error with host");
						connectToggle.setText("Disconnect");
						connectToggle.setSelected(false);
						appendOutputArea("Connected.\n\n");
						appendOutputArea("Connected.\n\n");
						isConnected = true;
						client.sendData(client.SEND_PASSWORD);
						// connectToggle.setSelected(true);
					} catch (UnknownHostException e) {
						connectToggle.setText("Connect");
						connectToggle.setSelected(false);
						appendOutputArea("Could not find host.\n\n");
					} catch (NumberFormatException e) {
						connectToggle.setText("Connect");
						connectToggle.setSelected(false);
						appendOutputArea("Please ensure port number is correct.\n");
					} catch (IOException e) {
						connectToggle.setText("Connect");
						connectToggle.setSelected(false);
						appendOutputArea("Could not connect.\n\n");
					} catch (Exception e) {
						connectToggle.setText("Connect");
						connectToggle.setSelected(false);
						appendOutputArea(e.getMessage() + "\n\n");
					}
				}
			});
			
		} else {
			appendOutputArea("Disconnecting...\n");
			connectToggle.setText("Disconnecting");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						client.tStop();
						client = null;
						socket.close();
						socket = null;
						connectToggle.setText("Connect");
						connectToggle.setSelected(false);
						appendOutputArea("Disconnected.\n\n");
					} catch (Exception e) {
						connectToggle.setText("Disconnect");
						connectToggle.setSelected(true);
					}
				}
			});
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				WhiteBoard whiteBoard = new WhiteBoard();

				JFrame f = new JFrame("White Board");
				f.setContentPane(whiteBoard.getGui());

				f.pack();
				f.setMinimumSize(f.getSize());
				f.setResizable(false);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setLocationRelativeTo(null);
				f.setVisible(true);
			}
		});

	}

	public void drawLine(final Line line) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				for (int i = 1; i < line.points.size(); i++)
					draw(line.points.get(i - 1), line.points.get(i),
							line.colourRGB, new BasicStroke(line.strokeSize,
									BasicStroke.CAP_ROUND,
									BasicStroke.JOIN_ROUND, 1.7f));
			}
		});
	}

	private void appendOutputArea(String string) {
		outputArea.setText(outputArea.getText() + string);
	}
}
