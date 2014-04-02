import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
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

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;


public class WhiteBoard {
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
    
    //WhiteBoard Preferences
    private Color textColour = Color.BLACK;
    private Color backColour = Color.WHITE;
    private BufferedImage colourSample = new BufferedImage(
            16,16,BufferedImage.TYPE_INT_RGB);
    
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
    		connectionPane.setFloatable(true);
    		gui.add(connectionPane, BorderLayout.NORTH);
    		
    		//Whiteboard gui
    		JPanel drawPanel = new JPanel();
    		imageLabel = new JLabel(new ImageIcon(canvasImage));
            imageLabel.setPreferredSize(new Dimension(480,320));
    		drawPanel.add(imageLabel);
    		
    		BufferedImage defaultImage = new BufferedImage(windowWidth,windowHeight,BufferedImage.TYPE_INT_RGB);
    		setImage(defaultImage);
    		
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
    		
    		
    		//gui.add(tb, BorderLayout.NORTH);
    		
    		gui.add(drawPanel);
    		
    		/*
            DrawPanel drawPanel = new DrawPanel();
            drawPanel.setBackground(new java.awt.Color(255, 255, 255));
            drawPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            gui.add(drawPanel, BorderLayout.CENTER);
            */
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
        //g.setStroke(stroke);
        int n = 0;
        g.drawLine(point.x, point.y, point.x+n, point.y+n);
        g.dispose();
        this.imageLabel.repaint();
    }
    
    class DrawPanel extends JPanel implements MouseMotionListener,MouseListener{

        DrawPanel() {
            // set a preferred size for the custom panel.
            setPreferredSize(new Dimension(windowWidth,windowHeight));
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

		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
    }

    public static void main(String[] args) {
        Runnable r = new Runnable() {
            @Override
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
        };
        r.run();
    }
}
