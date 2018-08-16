package distroDB;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author canshi wei
 * Receiver: Each node own its own server for receiving incoming message. 
 * Receiver get the message sent and parse out and notify Logger to save the data .
 */

public class Receiver implements Runnable{
	private int port;	// the port number of the Receiver
	private Logger logger;	// reference to the logger, commnication start within hardware
	private Socket incoming; // incoming socket
	private ServerSocket server; // server socket
	
	/**
	 * Constructor: initialize the receiver and start to listen using one thread
	 * @param port
	 * @param logger
	 */
	public Receiver(int port, Logger logger) {
		this.port = port;		// port number
		this.logger = logger;	// the reference of a logger
	}
	
	/**
	 * Run the server as running other hardware in the system
	 * Whenever a new connection is made, start a new channel
	 * for listening the message using 'TaskManager'
	 */
	@Override
	public void run() {
		try {
			server = new ServerSocket(this.port);	// start a new socket for listening
			System.out.println("Reciver start to listening...");
			// open the listening for all time
			while(true) {
				// create socket and accept connection
				incoming = server.accept(); // incoming socket accept
				// Task manager could assign a channel to the commnunication
				new Thread(new Task(incoming, logger)).start();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Task Manager: assign a commnication channel to the incoming socket.
	 */
	private class Task implements Runnable{
		private Socket socket;	// check the incoming socket
		private Logger logger;	// reference of logger
		
		/**
		 * Constructor: Initialize a Runnable task manager
		 * @param socket: incoming socket
		 * @param logger: the reference to logger
		 */
		public Task(Socket socket, Logger logger) {
			this.socket = socket;
			this.logger = logger;
		}
		
		/**
		 * Start a channel for listening the incoming socket
		 */
		@Override
		public void run() {
			try {
				// input stream, output stream define
				InputStream inStream = socket.getInputStream();
				OutputStream outStream = socket.getOutputStream();
				// init scanner, out put printer
				Scanner in = new Scanner(inStream, "UTF-8");
				PrintWriter out = new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"), true);
				// waiting connection and stay in connection			
				while(in.hasNextLine()) {
					this.event(in.nextLine());	
				}
				
				// close the resources
				in.close();
				out.close();
				socket.close();
				inStream.close();
				outStream.close();
		
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		/**
		 * Parse and send the data to the logger within the hardware
		 * @param output: the message received
		 */
		public void event(String output) {
			synchronized (logger) {
				System.out.println(">>>>>" + output);
				if(output.indexOf("ASSIGN") != -1) {
					// parse the identifier and send to the logger
					String identifier = output.substring("ASSIGN".length()).trim();
					logger.joinNetwork(identifier);
				}else if(output.indexOf("ADDNODE") != -1) {
					output = output.substring("ADDNODE".length()).trim();
					int split = output.indexOf(" ");
					//get the identifier
					String identifier = output.substring(0, split).trim();
					output = output.substring(split + 1).trim();
					split = output.indexOf(" ");
					// get the dns
					String dns = output.substring(0, split).trim();
					// get the port number in string
					String str_port = output.substring(split + 1).trim();
					logger.addPeer(identifier, dns, Integer.parseInt(str_port));
				}else if(output.indexOf("WAKEUP") != -1) {
					String command = output.substring("WAKEUP".length()).trim();
					logger.wakeUp(command);
				}else if(output.indexOf("PRE-PREPARE:") != -1) {
					String command = output.substring("PRE-PREPARE:".length()).trim();
					logger.pre_prepare(command);
				}else if(output.indexOf("ACK:") != -1) {
					String line = output.substring("ACK:".length()).trim();
					int split = line.indexOf(",");
					String identifier = line.substring(0, split).trim();
					line = line.substring(split + 1).trim();
					split = line.indexOf(",");
					String hash = line.substring(0, split).trim();
					String command = line.substring(split + 1).trim();
					logger.prepare(identifier, hash, command);
				}else if(output.indexOf("COMMIT:") != -1) {
					String line = output.substring("COMMIT:".length()).trim();
					int split = line.indexOf(",");
					String identifier = line.substring(0, split).trim();
					String hash = line.substring(split + 1).trim();
					logger.commit(identifier, hash);
				}
			}
			
		}
	}
}
