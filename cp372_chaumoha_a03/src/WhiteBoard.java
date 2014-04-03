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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class WhiteBoard {
	
	/** Reference for BufferedImage Drawing:
	 * 	http://stackoverflow.com/questions/12683533/drawing-a-rectangle-that-wont-disappear-in-next-paint/12683632#12683632
	 */
	//Fields
	private JTextField ipField, portField;
	private JTextArea inputArea, outputArea;
	private JToggleButton connectToggle;
	
	//Input Fields
	private JLabel ipLabel, portLabel;
	
	//Connection
	private Socket socket = null;
	private Client client = null;
	
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
    private Stroke stroke = new BasicStroke(
            3,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,1.7f);
    
    /**
     * Creates and populates the graphic user interface.
     * @return a JComponent containing the gui
     */
    public JComponent getGui() {
    	if (gui == null){
    		gui = new JPanel(new BorderLayout());
    		
    		//labels
    		ipLabel = new JLabel("IP:");
    		portLabel = new JLabel("Port:");

    		//fields
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
            		if (SwingUtilities.isLeftMouseButton(arg0)&& clickHeld == true){
            			points.add(new DrawnPoint(arg0.getPoint(),false));

            			if (points.size() > 1 ){
            				Point initialPoint = points.get(points.size()-1);
            				Point finalPoint = points.get(points.size()-2);
            				draw(initialPoint,finalPoint);
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
            		if (SwingUtilities.isLeftMouseButton(arg0)){
            			temp = new ArrayList<DrawnPoint>();
            			pointsSent = new ArrayList<DrawnPoint>();
            			temp.addAll(points);
            			tempImageDetails = canvasImage.getData();
            			points.add(new DrawnPoint(arg0.getPoint(),true));
            			pointsSent.add(new DrawnPoint(arg0.getPoint(),true));
            			updateHelpText(arg0.getPoint());
            			draw(arg0.getPoint(),null);
            			synchronized(this){ clickHeld = true; }
            		}
                    else if (SwingUtilities.isRightMouseButton(arg0)){
                    	if (clickHeld){
                    		canvasImage.setData(tempImageDetails);
                    		imageLabel.repaint();
                    		points = temp;
                    		updateHelpText(arg0.getPoint());
                    		synchronized(this){
        						clickHeld = false;
        					}
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
					synchronized(this){
						clickHeld = false;
					}
					
					String s = Line.serialize(pointsSent.get(0));
					DrawnPoint newLine = Line.deserialize(s);
					
					
						outputArea.append("x: "+newLine.x);
						outputArea.append(" y: "+newLine.y+"\n");
					
					
					try{
						client.sendData(s);
					}
					catch(Exception e){
						outputArea.append("Could not send Data.\n");
					}
				}
            });
            drawPanel.add(imageLabel);
    		clear(canvasImage,Color.white);
    		
            JScrollPane imageScroll = new JScrollPane(drawPanel);
            drawPanel.add(imageLabel);
            gui.add(imageScroll,BorderLayout.CENTER);
    		
            final SpinnerNumberModel strokeModel = 
                    new SpinnerNumberModel(3,1,16,1);
            JSpinner strokeSize = new JSpinner(strokeModel);
            ChangeListener strokeListener = new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent arg0) {
                    Object o = strokeModel.getValue();
                    Integer i = (Integer)o; 
                    stroke = new BasicStroke(
                            i.intValue(),
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND,
                            1.7f);
                }
            };
            strokeSize.addChangeListener(strokeListener);
            connectionPane.add(strokeSize);
            
            JButton colourButton = new JButton("Colour");
            colourButton.setToolTipText("Choose a Color");
            ActionListener textColourListener = new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    Color c = JColorChooser.showDialog(
                            gui, "Choose a color", textColour);
                    if (c!=null) {
                    	textColour = c;
                    	clear(colourSample,c);
                    }
                }
            };
    		colourButton.addActionListener(textColourListener);
    		colourButton.setIcon(new ImageIcon(colourSample));
    		connectionPane.add(colourButton);
    		clear(colourSample,textColour);
    		
    		gui.add(drawPanel);
    		gui.add(output,BorderLayout.SOUTH);
    		
    		outputArea = new JTextArea(10, 20);
    		outputArea.setLineWrap(true);
    		outputArea.setEditable(false);
    		gui.add(new JScrollPane(outputArea),BorderLayout.EAST);
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
    }
    
    /**
     * Draws a line of 'textColour' between the two points
     * @param initialPoint
     * @param finalPoint
     */
    private void draw(Point initialPoint, Point finalPoint){
    	Graphics2D g = this.canvasImage.createGraphics();
        //g.setRenderingHints(renderingHints);
        g.setColor(textColour);
        g.setStroke(stroke);
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
		
		if (socket == null && client == null) { // if there is no connection, create one.
			outputArea.append("Connecting...\n");
			connectToggle.setText("Connecting");
			SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
			    	try { // try to determine the optimal connection, on error show a nice dialog
						socket = new Socket(ipField.getText(), new Integer(portField.getText()));
						if (socket != null)
							client = new Client(socket, inputArea, outputArea);
						else
							throw new Exception("Could not create connection, error with host");
						connectToggle.setText("Disconnect");
						connectToggle.setSelected(false);
						outputArea.append("Connected.\n\n");
						//connectToggle.setSelected(true);
					} catch (UnknownHostException e) {
						connectToggle.setText("Connect");
						connectToggle.setSelected(false);
						outputArea.append("Could not find host.\n\n");
					} catch (NumberFormatException e) {
						connectToggle.setText("Connect");
						connectToggle.setSelected(false);
						outputArea.append("Please ensure port number is correct.\n");
					} catch (IOException e) {
						connectToggle.setText("Connect");
						connectToggle.setSelected(false);
						outputArea.append("Could not connect.\n\n");
					} catch (Exception e) {
						connectToggle.setText("Connect");
						connectToggle.setSelected(false);
						outputArea.append(e.getMessage()+"\n\n");
					}
			    }
			});
		} else {
			outputArea.append("Disconnecting...\n");
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
						outputArea.append("Disconnected.\n\n");
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

    
}
