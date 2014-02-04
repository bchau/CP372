import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server {
	
	public static void main(String[] args) throws IOException{
		ServerSocket serverSocket = null;
		
		
		try{
			serverSocket = new ServerSocket(4444);
		}
		catch(IOException e){
			System.err.println("Could not listen on port 4444.");
			System.exit(1);
		}
		
		Socket clientSocket = null;
		try{
			clientSocket = serverSocket.accept();
			System.out.println("Accepted");
		}
		catch (IOException e){
			System.err.println("Accept failed.");
			System.exit(1);
		}
		
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
				new InputStreamReader(
				clientSocket.getInputStream()));
        String inputLine, outputLine;
        Protocol protocol = new Protocol();
        outputLine = protocol.processInput(Protocol.NEW, null);
		
        int code = -1;
        while ((inputLine = in.readLine()) != null || code != -1){	
        	if (inputLine.toLowerCase().startsWith("submit")){
        		code = Protocol.SUBMIT;
        	}
        	else if (inputLine.toLowerCase().startsWith("get")){
        		code = Protocol.GET;
        	}
        	else if (inputLine.toLowerCase().startsWith("remove")){
        		code = Protocol.REMOVE;
        	}
        	else{
        		code = Protocol.FAULT;
        	}
        	
        	String temp;
        	while (in.ready() && !Protocol.isKeyword(temp = in.readLine())){  
        		inputLine += " "+temp;
        	}

        	outputLine = protocol.processInput(code, inputLine);
        	out.println(outputLine);
            if (outputLine.equals("Bye."))
            	break;
       

        }
	}
}
