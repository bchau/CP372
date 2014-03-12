import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class StopWaitSender extends Thread {
	private InetAddress rAddress;
	private int rPort, sPort, rN, fPointer = 0, cPacket = 0;
	private final int MAX_BYTES = 124, PACKET_SIZE=128;
	private File f;
	private DatagramSocket socket = null;
	private DatagramPacket packet = null;
	private byte[] fBytes, dBytes;
	private boolean eof = false, skipPrep = false;

	public StopWaitSender(InetAddress address, int rp, int sp,
			File fn, int rn) {
		this.rAddress = address;
		this.rPort = rp;
		this.sPort = sp;
		this.f = fn;
		this.rN = rn;
		try {
			readFile(this.f);
			this.dBytes = new byte[this.PACKET_SIZE];
		} catch(IOException e) {
			e.printStackTrace();
			System.err.println("File is invalid");
		}
	}

	public void run() {
		// create the new socket then send the file as packets
		try {
			this.socket = new DatagramSocket(this.sPort);
		} catch (IOException e) {
			System.err.println("Unable to create socket on port " + sPort);
			System.exit(1);
		}
		while (true) {
			// set data as either 0 or 1 sequence
			if (!this.eof && !this.skipPrep)
				prepareDByte();
			else if(this.skipPrep)
				this.skipPrep = false;
			// determine if we should drop packet
			// send packet
			this.packet = new DatagramPacket(this.dBytes, this.dBytes.length, this.rAddress, this.rPort);
			try {
				System.out.println(new String(packet.getData(), 0, packet.getLength()));
				this.socket.send(this.packet);
			} catch (IOException e) {
				System.err.println("Unable to send datagram, retrying...");
				skipPrep = true;
			}
			// get ack from server
			packet = new DatagramPacket(this.dBytes, this.dBytes.length);
	        try {
				socket.receive(packet);
				System.out.println(new String(packet.getData(), 0, packet.getLength()));
			} catch (IOException e) {
				System.err.println("Error getting ACK, retrying...");
				skipPrep = true;
			}
			// send next packet, or previous packet
			break;
		}
		this.socket.close();
		System.out.println("Exiting...");
		System.exit(0);
	}
	
	private void prepareDByte(){
		int i;
		for(i = 0; i < (this.PACKET_SIZE - this.MAX_BYTES); i++) {
			this.dBytes[i] = 0;
		}
		this.dBytes[i] = (byte)this.cPacket;
		while (i < this.dBytes.length && i < fBytes.length){
			this.dBytes[i] = fBytes[this.fPointer];
			this.fPointer++;
			if (fPointer>=fBytes.length){
				addEOF();
				break;
			}
			i++;
		} 
	}
	
	private void addEOF() {
		this.eof = true;
	}
	
	/**
	 * Implemented from <a href="http://stackoverflow.com/questions/858980/file-to-byte-in-java">
	 * Stack Overflow</a>
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private void readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            this.fBytes = new byte[length];
            f.readFully(this.fBytes);
        } finally {
            f.close();
        }
    }

	/**
	 * Sender takes 5 arguments<br/>
	 * <ol>
	 * <li>Host Address of Receiver</li>
	 * <li>UDP Receiver Port</li>
	 * <li>UDP Sender Port</li>
	 * <li>File Name</li>
	 * <li>Reliability Number</li>
	 * </ol>
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		// for(String arg: args) System.out.println(arg);
		if (args.length != 5) {
			System.err
					.println("To run the program please enter valid parameters as:");
			System.err
					.println("prog <Host Address> <Host Port> <Local Port> <File Name> <Reliability Number>");
			System.exit(1); // exit out
		}
		// assume all is well call sender as thread...
		try {
			new StopWaitSender(InetAddress.getByName(args[0]),
					Integer.parseInt(args[1]), Integer.parseInt(args[2]),
					new File(args[3]), Integer.parseInt(args[4]))
					.start();
		} catch (UnknownHostException e) {
			System.err.println("Unable to resolve host, please try again");
			System.exit(1);
		} catch (NumberFormatException e) {
			System.err.println("Invalid port or reliability numbers, please try again");
			System.exit(1);
		}
	}
}
