import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;


public class BlackBoard {
	//Fields
	private JTextField ipField, portField;
	private JTextArea inputArea, outputArea;
	private JButton sendButton;
	private JToggleButton connectToggle;
	
	//Socket/Input Fields
	private JLabel ipLabel, portLabel, inputLabel, resultLabel;
	private Socket socket = null;
	
	//ImageBuffering
    private BufferedImage originalImage;
    private BufferedImage canvasImage;
    private JLabel imageLabel;
    private JPanel gui;
    private Rectangle selection;
    
    //Window Preferences
    private int windowWidth = 720;
    private int windowHeight = 640;
    
    //BlackBoard Preferences
    private Color textColour = Color.WHITE;
    private BufferedImage colourSample = new BufferedImage(
            16,16,BufferedImage.TYPE_INT_RGB);
    private JLabel output = new JLabel("Black Board");
    private ArrayList<Point> points = new ArrayList<Point>();
    private boolean clickHeld = false;
    
    public JComponent getGui() {
    	if (gui == null){
    		gui = new JPanel(new BorderLayout());
    		
    		//labels
    		ipLabel = new JLabel("IP:");
    		portLabel = new JLabel("Port:");
    		inputLabel = new JLabel("Server Request:");
    		resultLabel = new JLabel("Server Result:");

    		//fields
    		ipField = new JTextField("127.0.0.1", 15);
    		portField = new JTextField("4444", 4);
    		
    		connectToggle = new JToggleButton();
    		connectToggle.setText("connect");

    		//Tool Bars
    		//JPanel toolbar = 
    		
    		JToolBar connectionPane = new JToolBar();
    		connectionPane.setLayout(new FlowLayout());
    		connectionPane.add(ipLabel);
    		connectionPane.add(ipField);
    		connectionPane.add(portLabel);
    		connectionPane.add(portField);
    		connectionPane.add(connectToggle);
    		connectionPane.setFloatable(false);
    		gui.add(connectionPane, BorderLayout.NORTH);
    		
    		//BlackBoard gui
    		BufferedImage defaultImage = new BufferedImage(windowWidth,windowHeight,BufferedImage.TYPE_INT_RGB);
    		setImage(defaultImage);
    		JPanel drawPanel = new JPanel();
    		imageLabel = new JLabel(new ImageIcon(canvasImage));
            imageLabel.setPreferredSize(new Dimension(this.windowWidth,this.windowHeight));
    		drawPanel.add(imageLabel);
    		

            JScrollPane imageScroll = new JScrollPane(drawPanel);
            drawPanel.add(imageLabel);
            imageLabel.addMouseMotionListener(new MouseMotionListener(){
            	@Override
                public void mouseDragged(MouseEvent arg0) {
                    points.add(arg0.getPoint());

                    if (points.size() > 1 || clickHeld == true){
                    	Point initialPoint = points.get(points.size()-1);
                    	Point finalPoint = points.get(points.size()-2);
                    	draw(finalPoint,initialPoint);
                    }
                    else{
                    	draw(arg0.getPoint(),null);
                    }
                    updateHelpText(arg0.getPoint());
                }

                @Override
                public void mouseMoved(MouseEvent arg0) {
                	updateHelpText(arg0.getPoint());
                }
            });
            imageLabel.addMouseListener(new MouseListener(){
            	@Override
                public void mousePressed(MouseEvent arg0) {
            		points.add(arg0.getPoint());
                    draw(arg0.getPoint(),null);
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
				}
            });
            gui.add(imageScroll,BorderLayout.CENTER);
    		
    		
    		
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
    	}
    	
    	return gui;
    }
    
    private void setImage(BufferedImage image){
    	this.originalImage = image;
        int w = image.getWidth();
        int h = image.getHeight();
        canvasImage = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = this.canvasImage.createGraphics();
        //g.setRenderingHints(renderingHints);
        g.drawImage(image, 0, 0, gui);
        g.dispose();
        
        selection = new Rectangle(0,0,w,h); 
    }
    
    /** Clears the entire image area by painting it with the current color. */
    
    private void clear(BufferedImage bi,Color colour) {
        Graphics2D g = bi.createGraphics();
        g.setColor(colour);
        g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        g.dispose();
    }
    
    public void draw(Point point) {
        Graphics2D g = this.canvasImage.createGraphics();
        //g.setRenderingHints(renderingHints);
        g.setColor(textColour);
        g.setStroke(new BasicStroke(
                10,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,1.7f));
        int n = 0;
        g.drawLine(point.x, point.y, point.x+n, point.y+n);
        g.dispose();
        this.imageLabel.repaint();
    }
    
    public void draw(Point initialPoint, Point finalPoint){
    	Graphics2D g = this.canvasImage.createGraphics();
        //g.setRenderingHints(renderingHints);
        g.setColor(textColour);
        g.setStroke(new BasicStroke(
                3,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,1.7f));
        if (finalPoint != null)
        	g.drawLine(initialPoint.x, initialPoint.y, finalPoint.x, finalPoint.y);
        else
        	g.drawLine(initialPoint.x, initialPoint.y, initialPoint.x,initialPoint.y);
        g.dispose();
        this.imageLabel.repaint();
    }
    
    private void updateHelpText(Point point){
    	output.setText("X,Y: " + (point.x+1) + "," + (point.y+1) +" Points array size: "+ points.size());
    }


    public static void main(String[] args) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                BlackBoard blackBoard = new BlackBoard();

                JFrame f = new JFrame("Black Board");
                f.setContentPane(blackBoard.getGui());
                f.setTitle("Black Board");
                
                f.pack();
                f.setMinimumSize(f.getSize());
                f.setResizable(false);
        		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        		f.setLocationRelativeTo(null);
                f.setVisible(true);
            }
        };
        r.run();
    }
}
