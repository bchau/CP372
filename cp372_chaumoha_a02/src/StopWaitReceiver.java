import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;


public class StopWaitReceiver extends Thread{
	protected DatagramSocket socket = null;
    protected BufferedReader in = null;
 
    public StopWaitReceiver() throws IOException {
    }
 
    public StopWaitReceiver(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(4445);
 
        try {
            in = new BufferedReader(new FileReader("one-liners.txt"));
        } catch (FileNotFoundException e) {
            System.err.println("Could not open quote file. Serving time instead.");
        }
    }
 
    public void run() {
 
        while (true) {
            try {
                byte[] buf = new byte[128];
 
                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
 
                // figure out response
                String dString = new Date().toString();
                //else
                //    dString = getNextQuote();
 
                buf = dString.getBytes();
 
        // send the response to the client at "address" and "port"
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
                break;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
            	continue;
            }
        }
        socket.close();
    }
    
    public static void main(String args[]) throws IOException {
    	new StopWaitReceiver().start();
    }
}
