import java.io.*;
import java.net.*;

public class Client {
	public static void main(String[] args) throws IOException {

        Socket socket = null;
        PrintWriter out = null; // do we need this?
        BufferedReader in = null; // do we need this?
        
        try{
        	socket = new Socket("192.168.5.6",80);
        	//TODO: do we need a buffer?
        }
        catch(UnknownHostException e){
        	//TODO: display error on GUI about unknown host
        	System.exit(1);
        }
        catch(IOException e){
        	//TODO: display error on GUI about I/O
        	System.exit(1);
        }
        
        
        //TODO: From Server
        //TODO: From User        
        socket.close();
	}
}
