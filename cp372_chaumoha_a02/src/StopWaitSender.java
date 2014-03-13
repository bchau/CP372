import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class StopWaitSender extends Thread {
	private InetAddress rAddress;
	private int rPort, sPort, rN, fPointer = 0, pPointer = 0, cPacket = 0;
	private final int MAX_BYTES = 124, PACKET_SIZE = 128;
	private File f;
	private DatagramSocket socket = null;
	private DatagramPacket packet = null;
	private byte[] fBytes, dBytes;
	private boolean eof = false, skipPrep = false, lastDropped = false;

	public StopWaitSender(InetAddress address, int rp, int sp, File fn, int rn) {
		this.rAddress = address;
		this.rPort = rp;
		this.sPort = sp;
		this.f = fn;
		this.rN = rn;
		try {
			readFile(this.f);
			this.dBytes = new byte[this.PACKET_SIZE];
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("File is invalid");
		}
	}

	public void run() {
		long start = System.currentTimeMillis(), elapsed;
		// create the new socket then send the file as packets
		try {
			this.socket = new DatagramSocket(this.sPort);
			this.socket.connect(this.rAddress, this.rPort);
			System.out.println("Client Running on Port: "
					+ this.socket.getLocalPort());
		} catch (IOException e) {
			System.err.println("Unable to create socket on port " + sPort);
			System.exit(1);
		}
		while (true) {
			// set data as either 0 or 1 sequence
			if (!this.eof && !this.skipPrep)
				prepareDByte();
			else if (this.skipPrep)
				this.skipPrep = false;
			else if (this.eof)
				break;
			if (!this.eof) {
				this.packet = new DatagramPacket(this.dBytes,
						this.dBytes.length, this.rAddress, this.rPort);
				try {
					System.out.println(new String(packet.getData(), 0, packet
							.getLength()));
					// determine if we should drop packet
					switch(this.rN){
						case 1:
							if (this.fPointer >= this.fBytes.length) {
								fPointer = 0;
								rN = 0;
								eof = false;
								break;
							} else {
								System.out.println("Dropped Packet #" + this.cPacket);
								continue;
							}
						case 2: 
							if (!lastDropped) {
								System.out.println("Dropped Packet #" + this.cPacket);
								lastDropped = true;
								this.skipPrep = true;
								continue;
							} else lastDropped = false;
							break;
						default:
					} 
					// send packet
					this.socket.send(this.packet);
				} catch (IOException e) {
					System.err.println("Unable to send datagram, retrying...");
					skipPrep = true;
				}

				// get ack from server
				packet = new DatagramPacket(this.dBytes, this.dBytes.length);
				try {
					socket.receive(packet);
					checkACK(packet.getData());
					System.out.println(new String(packet.getData(), 0, packet
							.getLength()));
				} catch (IOException e) {
					System.err.println("Error getting ACK, retrying...");
					skipPrep = true;
				}
			}
		}
		this.socket.close();
		elapsed = System.currentTimeMillis() - start;
		System.out.println("Total time to send file: " + elapsed + " miliseconds");
		System.out.println("Good Bye.");
		System.exit(0);
	}

	private void checkACK(byte[] reply) {
		byte[] ack_packet = new byte[this.PACKET_SIZE];
		byte[] ack = (this.cPacket + "ACK").getBytes();
		for (int i = 3; i < ack.length + 3; i++)
			ack_packet[i] = ack[i - 3];
		// acknowledged packet
		if (arrayEqual(reply, ack_packet)) {
			this.skipPrep = false;
			if (this.cPacket == 0)
				this.cPacket = 1;
			else
				this.cPacket = 0;
			if (fPointer >= fBytes.length)
				this.eof = true;
		} else
			this.skipPrep = true;
	}

	private boolean arrayEqual(byte[] a, byte[] b) {
		boolean equal = true;
		int i=0;
		if (a.length == b.length) {
			while (equal && i < a.length) {
				if (a[i] != b[i]) {
					equal = false;
				}
				i++;
			}
		}
		else equal = false;
		return equal;
	}

	private void prepareDByte() {
		// set sequence number
		for (this.pPointer = 0; this.pPointer < (this.PACKET_SIZE
				- this.MAX_BYTES - 1); this.pPointer++) {
			this.dBytes[this.pPointer] = 0;
		}
		this.dBytes[this.pPointer] = ("" + this.cPacket).getBytes()[0];
		// move over and then begin to add data
		this.pPointer++;
		while (this.pPointer < this.dBytes.length
				&& this.fPointer < fBytes.length) {
			this.dBytes[this.pPointer] = fBytes[this.fPointer];
			this.fPointer++;
			this.pPointer++;
			if (fPointer >= fBytes.length && this.pPointer < this.dBytes.length) {
				this.dBytes[this.pPointer] = "\\0".getBytes()[0];
				break;
			}

		}
	}

	/**
	 * Implemented from <a
	 * href="http://stackoverflow.com/questions/858980/file-to-byte-in-java">
	 * Stack Overflow</a>
	 * 
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
					new File(args[3]), Integer.parseInt(args[4])).start();
		} catch (UnknownHostException e) {
			System.err.println("Unable to resolve host, please try again");
			System.exit(1);
		} catch (NumberFormatException e) {
			System.err
					.println("Invalid port or reliability numbers, please try again");
			System.exit(1);
		}
	}
}
