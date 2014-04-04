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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.net.SocketFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class WhiteBoard {

	/**
	 * Reference for BufferedImage Drawing:
	 * http://stackoverflow.com/questions/12683533
	 * /drawing-a-rectangle-that-wont-disappear-in-next-paint/12683632#12683632
	 */

	//Fields
	private JTextField inputArea,ipField, portField;
	private JTextPane outputArea;
	protected JToggleButton connectToggle;

	// Input Fields
	private JLabel ipLabel, portLabel;

	// Connection
	private Socket socket = null;
	private SocketFactory socketfactory = null;
	private Client client = null;
	protected boolean isConnected = false;

	
	//ImageBuffering
    private BufferedImage canvasImage;
    private Raster tempImageDetails;
    private JLabel imageLabel;
    private JPanel gui;
    
    //Window Preferences
    private int drawAreaWidth = 640;
    private int drawAreaHeight = 640;
    
    //WhiteBoard Preferences
    private Color textColour = Color.BLACK;
    private BufferedImage colourSample = new BufferedImage(
            16,16,BufferedImage.TYPE_INT_RGB);
    private JLabel output = new JLabel("White Board");
    private ArrayList<DrawnPoint> points = new ArrayList<DrawnPoint>();
    private ArrayList<DrawnPoint> temp;
    private ArrayList<DrawnPoint> pointsSent;
    private boolean clickHeld = false;
    private boolean clickCanceled = false;
    private int penSize = 3;
    private Stroke stroke = new BasicStroke(penSize,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,1.7f);
    private JButton colourButton;
    //ChatBox Preferences
    private StyledDocument doc;
    private Style style;
    private String name = "User";
    
    /**
     * Creates and populates the graphic user interface.
     * @return a JComponent containing the gui
     */
    public JComponent getGui(){
    	if (gui == null){
    		gui = new JPanel(new BorderLayout());
    		
    		//labels
    		ipLabel = new JLabel("IP:");
    		portLabel = new JLabel("Port:");

    		//fields
    		ipField = new JTextField("127.0.0.1", 15);
    		ipField.addMouseListener(new MouseListener(){

				@Override
				public void mouseClicked(MouseEvent arg0) {
					if (ipField.getText().trim().equals("127.0.0.1")){
						ipField.setText("");
					}
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
    			
    		});
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
    		
    		//Tool Bar
    		JToolBar connectionPane = new JToolBar();
    		connectionPane.setLayout(new FlowLayout());
    		connectionPane.add(ipLabel);
    		connectionPane.add(ipField);
    		connectionPane.add(portLabel);
    		connectionPane.add(portField);
    		connectionPane.add(connectToggle);
    		connectionPane.setFloatable(false);
    		gui.add(connectionPane, BorderLayout.NORTH);
    		
    		//WhiteBoard gui
    		BufferedImage defaultImage = new BufferedImage(drawAreaWidth,drawAreaHeight,BufferedImage.TYPE_INT_RGB);
    		setImage(defaultImage);
    		
    		JPanel drawPanel = new JPanel();
    		imageLabel = new JLabel(new ImageIcon(canvasImage));
            imageLabel.setPreferredSize(new Dimension(this.drawAreaWidth,this.drawAreaHeight));
            imageLabel.addMouseMotionListener(new MouseMotionListener(){
            	@Override
            	public void mouseDragged(MouseEvent arg0) {
            		if (SwingUtilities.isLeftMouseButton(arg0)&& clickHeld == true && isConnected){
            			points.add(new DrawnPoint(arg0.getPoint()));
            			pointsSent.add(new DrawnPoint(arg0.getPoint()));

            			if (points.size() > 1 ){
            				Point initialPoint = points.get(points.size()-1);
            				Point finalPoint = points.get(points.size()-2);
            				draw(initialPoint,finalPoint,textColour,stroke);
            			}
            			updateHelpText(arg0.getPoint());
            		}
                }

                @Override
                public void mouseMoved(MouseEvent arg0) {
                	updateHelpText(arg0.getPoint());
                }
            });
            imageLabel.addMouseListener(new MouseListener(){
            	@Override
                public void mousePressed(MouseEvent arg0) {
            		if (SwingUtilities.isLeftMouseButton(arg0)&& isConnected){
            			clickCanceled = false;
            			temp = new ArrayList<DrawnPoint>();
            			pointsSent = new ArrayList<DrawnPoint>();
            			temp.addAll(points);
            			tempImageDetails = canvasImage.getData();
            			points.add(new DrawnPoint(arg0.getPoint()));
            			pointsSent.add(new DrawnPoint(arg0.getPoint()));

            			updateHelpText(arg0.getPoint());
            			draw(arg0.getPoint(),null,textColour,stroke);
            			synchronized(this){ clickHeld = true; }
            		}
                    else if (SwingUtilities.isRightMouseButton(arg0)&& isConnected){
                    	if (clickHeld){
                    		canvasImage.setData(tempImageDetails);
                    		imageLabel.repaint();
                    		points = temp;
                    		updateHelpText(arg0.getPoint());
                    		synchronized(this){
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

			colourButton = new JButton("Colour");
			colourButton.setToolTipText("Choose a Color");
			colourButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (client != null)
						client.sendPassword();
				}
				
			});
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
    		gui.add(output,BorderLayout.SOUTH);
    		
    		//Text area and input
    		JPanel messageBox = new JPanel(new BorderLayout());
    		final JTextField nameArea = new JTextField("Enter your name: ");
    		nameArea.addMouseListener(new MouseListener(){

				@Override
				public void mouseClicked(MouseEvent arg0) {
					if (nameArea.getText().equals("Enter your name: ")){
						nameArea.setText("");
					}
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
    			
    		});
    		nameArea.addKeyListener(new KeyListener(){

				@Override
				public void keyPressed(KeyEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void keyReleased(KeyEvent arg0) {
					if (arg0.getKeyChar() == KeyEvent.VK_ENTER && !nameArea.getText().trim().equals("")){
						client.sendData("MESSAGE,"+nameArea.getText()+","+Line.getColourHex(textColour)+";;ENDMESSAGE");
					}
				}

				@Override
				public void keyTyped(KeyEvent arg0) {
					// TODO Auto-generated method stub
					
				}
    			
    		});
    		nameArea.addFocusListener(new FocusListener(){

				@Override
				public void focusGained(FocusEvent arg0) {
				}

				@Override
				public void focusLost(FocusEvent arg0) {
					if (!nameArea.getText().trim().equals(""))
						client.sendData("MESSAGE,"+nameArea.getText()+","+Line.getColourHex(textColour)+";;ENDMESSAGE");
					else
						nameArea.setText("Enter your name: ");
				}
    			
    		});
    		messageBox.add(nameArea,BorderLayout.NORTH);
    		
    		outputArea = new JTextPane();
    		outputArea.setEditorKit(new WrapEditorKit());
    		outputArea.setEditable(false);
    		outputArea.setBackground(Color.LIGHT_GRAY);
    		messageBox.add(new JScrollPane(outputArea),BorderLayout.CENTER);
    		doc = outputArea.getStyledDocument();

            style = outputArea.addStyle("I'm a Style", null);
            
    		
    		JPanel inputBox = new JPanel(new FlowLayout());
    		inputArea = new JTextField("",15);
    		inputArea.setBackground(Color.LIGHT_GRAY);
    		inputArea.addKeyListener(new KeyListener(){

				@Override
				public void keyPressed(KeyEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void keyReleased(KeyEvent arg0) {
					if (arg0.getKeyChar() == KeyEvent.VK_ENTER && !inputArea.getText().trim().equals("")){
						
						appendOutputArea("You: " + inputArea.getText() + "\n");
						if (client != null) {
							client.sendData("MESSAGE,"+name+","+Line.getColourHex(textColour)+",0;" + inputArea.getText()
									+ ";ENDMESSAGE");
						}
						inputArea.setText("");
					}
				}

				@Override
				public void keyTyped(KeyEvent arg0) {
					// TODO Auto-generated method stub
					
				}
    			
    		});
    		inputBox.add(new JScrollPane(inputArea));
    		JButton sendButton = new JButton("SEND");
    		sendButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (!inputArea.getText().trim().equals("")) {
						appendOutputArea("You: " + inputArea.getText() + "\n");
						if (client != null) {
							client.sendData("MESSAGE,"+name+","+Line.getColourHex(textColour)+",0;" + inputArea.getText()
									+ ";ENDMESSAGE");
						}
					}
					inputArea.setText("");
				}

    			
    		});
    		inputBox.add(sendButton);
    		messageBox.add(inputBox,BorderLayout.SOUTH);
    		gui.add(messageBox,BorderLayout.EAST);
            
    	}
    	
    	return gui;
    }
    
    /**
     * Sets the canvas to the contents of image.
     * @param image
     */
    private void setImage(BufferedImage image){
        int w = image.getWidth();
        int h = image.getHeight();
        canvasImage = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = this.canvasImage.createGraphics();
        g.drawImage(image, 0, 0, gui);
        g.dispose();
    }
    
    /**
     * Clears the area of the buffered image to the specified colour.
     * @param bi
     * @param colour
     */
    private void clear(BufferedImage bi,Color colour) {
        Graphics2D g = bi.createGraphics();
        g.setColor(colour);
        g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        g.dispose();
        this.imageLabel.repaint();
    }
    public void clear(){
    	clear(canvasImage,Color.WHITE);
    }
    /**
     * Draws a line of 'textColour' between the two points
     * @param initialPoint
     * @param finalPoint
     */
    private void draw(Point initialPoint, Point finalPoint,Color c, Stroke s){
    	Graphics2D g = this.canvasImage.createGraphics();
        g.setColor(c);
        g.setStroke(s);
        if (finalPoint != null)
        	g.drawLine(initialPoint.x, initialPoint.y, finalPoint.x, finalPoint.y);
        else
        	g.drawLine(initialPoint.x, initialPoint.y, initialPoint.x,initialPoint.y);
        g.dispose();
        this.imageLabel.repaint();
    }
    
    /**
     * Updates the help text, which contains x&y coordinates and information about the point buffer.
     * @param point
     */
    private void updateHelpText(Point point){
    	output.setText("X,Y: " + (point.x+1) + "," + (point.y+1) +" Points array size: "+ points.size());
    }

    /**
	 * Determine what must be done to the Toggle Button, setting state and managing connections
	 */
	private void connectDisconnect() {

		if (socket == null && client == null) { // if there is no connection,
												// create one.
			
			systemAppendOutputArea("Connecting...\n");
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
						systemAppendOutputArea("Connected.\n");;
						isConnected = true;
						client.sendPassword();
						// connectToggle.setSelected(true);
					} catch (UnknownHostException e) {
						connectToggle.setText("Connect");
						connectToggle.setSelected(false);
						systemAppendOutputArea("Could not find host.\n");
					} catch (NumberFormatException e) {
						connectToggle.setText("Connect");
						connectToggle.setSelected(false);
						systemAppendOutputArea("Please ensure port number is correct.\n");
					} catch (IOException e) {
						connectToggle.setText("Connect");
						connectToggle.setSelected(false);
						systemAppendOutputArea("Could not connect.\n");
					} catch (Exception e) {
						connectToggle.setText("Connect");
						connectToggle.setSelected(false);
						systemAppendOutputArea(e.getMessage() + "\n");
					}
				}
			});
			
		} else {
			isConnected = false;
			systemAppendOutputArea("Disconnecting...\n");
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
						systemAppendOutputArea("Disconnected.\n");
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

	private void appendOutputArea(String string, Color c) {
        StyleConstants.setForeground(style, c);
        try {
			doc.insertString(doc.getLength(), string, style);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}
	
	protected void appendOutputArea(String string){
		appendOutputArea(string, textColour);
	}
	
	protected void systemAppendOutputArea(String string){
		appendOutputArea(string, Color.BLACK);
	}
	
	protected void setTextColour(String string){
		String[] temp = string.split(";");
		textColour = Line.getColourFromHex(temp[1]);
		clear(colourSample,textColour);
		colourButton.updateUI();
	}
	
	protected void printMessage(String string){
		String[] temp = string.split(";");
		String[] temp2 = temp[0].split(",");
		String name = temp2[1];
		Color c = Line.getColourFromHex(temp2[2]);
		appendOutputArea(name+" :"+temp[1],c);
	}
	
	/**
	 * Online solution for JTextPane word wrapping
	 * @author https://community.oracle.com/message/10692405
	 */
	class WrapEditorKit extends StyledEditorKit {
        ViewFactory defaultFactory=new WrapColumnFactory();
        public ViewFactory getViewFactory() {
            return defaultFactory;
        }

    }

	/**
	 * Online solution for JTextPane word wrapping
	 * @author https://community.oracle.com/message/10692405
	 */
    class WrapColumnFactory implements ViewFactory {
        public View create(Element elem) {
            String kind = elem.getName();
            if (kind != null) {
                if (kind.equals(AbstractDocument.ContentElementName)) {
                    return new WrapLabelView(elem);
                } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                    return new ParagraphView(elem);
                } else if (kind.equals(AbstractDocument.SectionElementName)) {
                    return new BoxView(elem, View.Y_AXIS);
                } else if (kind.equals(StyleConstants.ComponentElementName)) {
                    return new ComponentView(elem);
                } else if (kind.equals(StyleConstants.IconElementName)) {
                    return new IconView(elem);
                }
            }

            // default to text display
            return new LabelView(elem);
        }
    }

    /**
	 * Online solution for JTextPane word wrapping
	 * @author https://community.oracle.com/message/10692405
	 */
    class WrapLabelView extends LabelView {
        public WrapLabelView(Element elem) {
            super(elem);
        }

        public float getMinimumSpan(int axis) {
            switch (axis) {
                case View.X_AXIS:
                    return 0;
                case View.Y_AXIS:
                    return super.getMinimumSpan(axis);
                default:
                    throw new IllegalArgumentException("Invalid axis: " + axis);
            }
        }

    }

}
